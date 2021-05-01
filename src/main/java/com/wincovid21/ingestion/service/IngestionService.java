package com.wincovid21.ingestion.service;

import com.google.api.client.util.DateTime;

public interface IngestionService {

   int resourceBulkCreate();

   DateTime fetchLastModifiedOn();

   int resourceBulkUpdate();
}
