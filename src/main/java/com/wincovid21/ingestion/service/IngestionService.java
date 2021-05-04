package com.wincovid21.ingestion.service;

import com.wincovid21.ingestion.domain.ResourceDetailDTO;

public interface IngestionService {

   int resourceBulkCreate();

   Long fetchLastModifiedOn();

   int resourceBulkUpdate();

   void resourceCreate(ResourceDetailDTO resourceDetailDTO) throws Exception;

}
