package com.wincovid21.ingestion.service;

import com.wincovid21.ingestion.entity.ResourceDetails;
import com.wincovid21.ingestion.entity.ResourceRequestEntry;
import com.wincovid21.ingestion.repository.ResourceDetailsRepository;
import com.wincovid21.ingestion.util.ResourceDetailsUtil;
import com.wincovid21.ingestion.util.SheetsServiceUtil;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

public class IngestionServiceImpl implements IngestionService {

    @Autowired
    private SheetsServiceUtil sheetsServiceUtil;
    @Autowired
    private ResourceDetailsRepository resourceDetailsRepository;
    @Autowired
    private ResourceDetailsUtil resourceDetailsUtil;

    private static final String createSpreadsheetId = "1v7jhxNGgWmIzxCyCLoN6izxBaDgB0_zWy7AxTW32N1o";
    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceImpl.class);

    @Override
    public int resourceBulkCreate() {
        try {
            Sheets sheetsService = sheetsServiceUtil.getSheetsService();
            String range = "master";
            ValueRange readResult = sheetsService.spreadsheets().values()
                    .get(createSpreadsheetId,range)
                    .execute();
            for(int i=1;i<readResult.getValues().size();i++) {
                List<Object> rowValue = readResult.getValues().get(i);
                List<ResourceDetails> existingResourceList = resourceDetailsRepository.fetchResourceDetailsByPhone(String.valueOf(rowValue.get(6)));
                if(!existingResourceList.isEmpty()) {
                    for(int j=0;j<existingResourceList.size();j++) {
                        if(existingResourceList.get(j).getName().equalsIgnoreCase(String.valueOf(rowValue.get(2))) &&
                               existingResourceList.get(j).getResourceType().equalsIgnoreCase(String.valueOf(rowValue.get(4)))) {
                            continue;
                        }
                        ResourceDetails resourceDetails = resourceDetailsUtil.convertToEntity(rowValue);
                        resourceDetails = resourceDetailsRepository.save(resourceDetails);
                        ResourceRequestEntry resourceRequestEntry = resourceDetailsUtil.convertToRREntry(resourceDetails);
                        //call to search API with this entry (to be given by Gaurav)
                    }
                } else {
                    ResourceDetails resourceDetails = resourceDetailsUtil.convertToEntity(rowValue);
                    resourceDetails = resourceDetailsRepository.save(resourceDetails);
                    ResourceRequestEntry resourceRequestEntry = resourceDetailsUtil.convertToRREntry(resourceDetails);
                    //call to search API with this entry (to be given by Gaurav)
                }
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
            Drive.Files.Get fileRequest = driveService.files().get(createSpreadsheetId).setFields("id, modifiedTime");
            File file = fileRequest.execute();
            if (file != null) {
                return file.getModifiedTime();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public int resourceBulkUpdate() {
        try {
            Sheets sheetsService = sheetsServiceUtil.getSheetsService();
            String range = "master update";
            ValueRange readResult = sheetsService.spreadsheets().values()
                    .get(createSpreadsheetId,range)
                    .execute();
            for(int i=1;i<readResult.getValues().size();i++) {
                List<Object> rowValue = readResult.getValues().get(i);
                ResourceDetails existingResource = resourceDetailsRepository.fetchResourceByPrimaryKey(String.valueOf(rowValue.get(6)),String.valueOf(rowValue.get(2)),String.valueOf(rowValue.get(4)));
                if(Objects.isNull(existingResource)) {
                   logger.error("No entry present across this row {} so cannot be an update operation", rowValue);
                } else {
                   existingResource = resourceDetailsUtil.updateEntity(rowValue,existingResource);
                   resourceDetailsRepository.save(existingResource);
                }
            }
            return readResult.getValues().size();
        } catch (Exception e) {
            return 0;
        }
    }


}
