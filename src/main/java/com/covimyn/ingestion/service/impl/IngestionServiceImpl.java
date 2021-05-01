package com.covimyn.ingestion.service.impl;

import com.covimyn.ingestion.service.IngestionService;
import com.covimyn.ingestion.util.SheetsServiceUtil;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

public class IngestionServiceImpl implements IngestionService {

    private final SheetsServiceUtil sheetsServiceUtil;

    @Autowired
    public IngestionServiceImpl(SheetsServiceUtil sheetsServiceUtil) {
        this.sheetsServiceUtil = sheetsServiceUtil;
    }

    @Override
    public int resourceBulkCreate() {
        try {
            String spreadsheetId = "1v7jhxNGgWmIzxCyCLoN6izxBaDgB0_zWy7AxTW32N1o";
            Sheets sheetsService = sheetsServiceUtil.getSheetsService();
            String range = "Class Data";
            ValueRange readResult = sheetsService.spreadsheets().values()
                    .get(spreadsheetId,range)
                    .execute();
            return readResult.getValues().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public DateTime fetchLastModifiedOn() {
        String spreadsheetId = "1v7jhxNGgWmIzxCyCLoN6izxBaDgB0_zWy7AxTW32N1o";
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
