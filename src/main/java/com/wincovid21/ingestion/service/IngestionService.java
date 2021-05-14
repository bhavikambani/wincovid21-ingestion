package com.wincovid21.ingestion.service;

import com.wincovid21.ingestion.domain.ResourceDetailDTO;
import com.wincovid21.ingestion.exception.InvalidLeadCreationDataException;

public interface IngestionService {

    int resourceBulkCreate();

    Long fetchLastModifiedOn();

    int resourceBulkUpdate();

    void resourceCreate(ResourceDetailDTO resourceDetailDTO) throws Exception;

    void validate(ResourceDetailDTO resourceDetailDTO) throws InvalidLeadCreationDataException, InvalidLeadCreationDataException;

}
