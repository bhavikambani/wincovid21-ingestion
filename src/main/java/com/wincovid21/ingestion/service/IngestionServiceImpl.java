package com.wincovid21.ingestion.service;

import com.wincovid21.ingestion.entity.ResourceAvailabilityDetails;
import com.wincovid21.ingestion.entity.ResourceDetails;
import com.wincovid21.ingestion.repository.ResourceAvailabilityDetailsRepository;
import com.wincovid21.ingestion.repository.ResourceDetailsRepository;
import com.wincovid21.ingestion.util.ResourceDetailsUtil;
import com.wincovid21.ingestion.util.SheetsServiceUtil;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IngestionServiceImpl implements IngestionService {

    @Autowired
    private SheetsServiceUtil sheetsServiceUtil;
    @Autowired
    private ResourceDetailsRepository resourceDetailsRepository;
    @Autowired
    private ResourceDetailsUtil resourceDetailsUtil;
    @Autowired
    private ResourceAvailabilityDetailsRepository resourceAvailabilityDetailsRepository;

    private static final String spreadsheetId = "1v7jhxNGgWmIzxCyCLoN6izxBaDgB0_zWy7AxTW32N1o";

    @Override
    public int resourceBulkCreate() {
        try {
            Sheets sheetsService = sheetsServiceUtil.getSheetsService();
            String range = "master";
            ValueRange readResult = sheetsService.spreadsheets().values()
                    .get(spreadsheetId,range)
                    .execute();
            for(int i=1;i<readResult.getValues().size();i++) {
                List<Object> rowValue = readResult.getValues().get(i);
                ResourceDetails resourceDetails = resourceDetailsUtil.convertToEntity(rowValue);
                ResourceAvailabilityDetails resourceAvailabilityDetails = resourceDetailsUtil.convertToRADEntity(rowValue);
                resourceDetails = resourceDetailsRepository.save(resourceDetails);
                resourceAvailabilityDetails.setResourceId(resourceDetails.getId());
                resourceAvailabilityDetailsRepository.save(resourceAvailabilityDetails);
            }
            return readResult.getValues().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public DateTime fetchLastModifiedOn() {
        try {
            Drive driveService = sheetsServiceUtil.getDriveService();
            Drive.Files.Get fileRequest = driveService.files().get(spreadsheetId).setFields("id, modifiedTime");
            File file = fileRequest.execute();
            if (file != null) {
                return file.getModifiedTime();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}
