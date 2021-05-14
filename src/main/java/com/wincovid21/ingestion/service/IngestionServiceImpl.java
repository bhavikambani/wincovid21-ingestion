package com.wincovid21.ingestion.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.wincovid21.ingestion.client.SearchClientHelper;
import com.wincovid21.ingestion.domain.ResourceDetailDTO;
import com.wincovid21.ingestion.entity.*;
import com.wincovid21.ingestion.exception.InvalidLeadCreationDataException;
import com.wincovid21.ingestion.repository.CityRepository;
import com.wincovid21.ingestion.repository.ResourceCategoryRepository;
import com.wincovid21.ingestion.repository.ResourceDetailsRepository;
import com.wincovid21.ingestion.repository.ResourceSubcategoryRepository;
import com.wincovid21.ingestion.util.ResourceDetailsUtil;
import com.wincovid21.ingestion.util.SheetsServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngestionServiceImpl implements IngestionService {

    @Autowired
    private SheetsServiceUtil sheetsServiceUtil;
    @Autowired
    private ResourceDetailsRepository resourceDetailsRepository;
    @Autowired
    private ResourceCategoryRepository resourceCategoryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ResourceSubcategoryRepository resourceSubcategoryRepository;
    @Autowired
    private ResourceDetailsUtil resourceDetailsUtil;
    @Autowired
    private SearchClientHelper searchClientHelper;

    private static final String createSpreadsheetId = "1JTbwbgzDle_3FiCTnex5FD1oeL47vmJoHYsELiC10a4";
    private static final String updateSpreadsheetId = "1JTbwbgzDle_3FiCTnex5FD1oeL47vmJoHYsELiC10a4";
    private static final String createRange = "master";
    private static final String updateRange = "master update";
    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceImpl.class);

    @Override
    public int resourceBulkCreate() {
        try {
            Sheets sheetsService = sheetsServiceUtil.getSheetsService();
            ValueRange readResult = sheetsService.spreadsheets().values()
                    .get(createSpreadsheetId, createRange)
                    .execute();
            for (int i = 1; i < readResult.getValues().size(); i++) {
                List<Object> rowValue = readResult.getValues().get(i);
                if (String.valueOf(rowValue.get(3)).isEmpty() || String.valueOf(rowValue.get(4)).isEmpty() || String.valueOf(rowValue.get(6)).isEmpty() || String.valueOf(rowValue.get(9)).isEmpty() || String.valueOf(rowValue.get(10)).isEmpty()) {
                    logger.error("Discarding the row as one or more of mandatory fields are missing.");
                    continue;
                }
                ResourceCategory categoryId = resourceCategoryRepository.fetchCategoryIdForName(String.valueOf(rowValue.get(3)));
                ResourceSubCategory resourceTypeId = resourceSubcategoryRepository.fetchResourceTypeIdForName(String.valueOf(rowValue.get(4)));
                if (Objects.nonNull(categoryId) && Objects.nonNull(resourceTypeId)) {
                    ResourceDetails existingResource = resourceDetailsRepository.fetchResourceByPrimaryKey(String.valueOf(rowValue.get(6)), String.valueOf(rowValue.get(2)), resourceTypeId.getId(), categoryId.getId());
                    if (Objects.nonNull(existingResource)) {
                        logger.error("Exact entry already present across {} so create operation is invalid", rowValue);
                    } else {
                        ResourceDetails resourceDetails = resourceDetailsUtil.convertToEntity(rowValue);
                        if ((resourceDetails.getName().isEmpty() && resourceDetails.getPhone1().isEmpty()) || resourceDetails.getPhone1().isEmpty()) {
                            logger.error("The phone number field is empty so dropping off the entry");
                            continue;
                        } else if (resourceDetails.getName().isEmpty()) {
                            resourceDetails.setName(resourceDetails.getPhone1());
                        }
                        try {
                            resourceDetails = resourceDetailsRepository.save(resourceDetails);
                            ResourceRequestEntry resourceRequestEntry = resourceDetailsUtil.convertToRREntry(resourceDetails);
                            searchClientHelper.makeHttpPostRequest(resourceRequestEntry);
                        } catch (Exception e) {
                            logger.error("Exception occurred so dropping current entry", e);
                        }
                    }
                } else {
                    logger.error("No category or resource exists with such names");
                }
            }
            return readResult.getValues().size();
        } catch (Exception e) {
            logger.error("Exception occurred due to.", e);
            return 0;
        }
    }

    @Override
    public Long fetchLastModifiedOn() {
        try {
            Drive driveService = sheetsServiceUtil.getDriveService();
            Drive.Files.Get fileRequest = driveService.files().get(createSpreadsheetId).setFields("id, modifiedTime");
            File file = fileRequest.execute();
            if (file != null) {
                return file.getModifiedTime().getValue();
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
            ValueRange readResult = sheetsService.spreadsheets().values()
                    .get(updateSpreadsheetId, updateRange)
                    .execute();
            for (int i = 1; i < readResult.getValues().size(); i++) {
                List<Object> rowValue = readResult.getValues().get(i);
                if (String.valueOf(rowValue.get(3)).isEmpty() || String.valueOf(rowValue.get(4)).isEmpty() || String.valueOf(rowValue.get(6)).isEmpty() || String.valueOf(rowValue.get(9)).isEmpty() || String.valueOf(rowValue.get(10)).isEmpty()) {
                    logger.error("Discarding the row as one or more of mandatory fields are missing.");
                    continue;
                }
                Long categoryId = resourceCategoryRepository.fetchCategoryIdForName(String.valueOf(rowValue.get(3))).getId();
                Long resourceTypeId = resourceSubcategoryRepository.fetchResourceTypeIdForName(String.valueOf(rowValue.get(4))).getId();
                ResourceDetails existingResource = resourceDetailsRepository.fetchResourceByPrimaryKey(String.valueOf(rowValue.get(6)), String.valueOf(rowValue.get(2)), resourceTypeId, categoryId);
                if (Objects.isNull(existingResource)) {
                    logger.error("No entry present across this row {} so cannot be an update operation", rowValue);
                } else {
                    existingResource = resourceDetailsUtil.updateEntity(rowValue, existingResource);
                    if ((existingResource.getName().isEmpty() && existingResource.getPhone1().isEmpty()) || existingResource.getPhone1().isEmpty()) {
                        logger.error("The phone number field is empty so dropping off the entry");
                        continue;
                    } else if (existingResource.getName().isEmpty()) {
                        existingResource.setName(existingResource.getPhone1());
                    }
                    try {
                        existingResource = resourceDetailsRepository.save(existingResource);
                        ResourceRequestEntry resourceRequestEntry = resourceDetailsUtil.convertToRREntry(existingResource);
                        searchClientHelper.makeHttpPostRequest(resourceRequestEntry);
                    } catch (Exception e) {
                        logger.error("Exception occurred so dropping current entry", e);
                    }
                }
            }
            return readResult.getValues().size();
        } catch (Exception e) {
            logger.error("Exception occurred due to ", e);
            return 0;
        }
    }

    @Override
    @Transactional
    public void resourceCreate(ResourceDetailDTO resourceDetailDTO) throws InvalidLeadCreationDataException, IOException {
        validate(resourceDetailDTO);
        logger.info("Payload validation success for # {}", resourceDetailDTO);
        List<ResourceDetails> resourceDetailsList = resourceDetailsUtil.transformEntryToEntity(resourceDetailDTO);
        resourceDetailsRepository.saveAll(resourceDetailsList);

        for (ResourceDetails resourceDetails : resourceDetailsList) {
            ResourceRequestEntry resourceRequestEntry = resourceDetailsUtil.convertToRREntry(resourceDetails);
            searchClientHelper.makeHttpPostRequest(resourceRequestEntry);
        }
    }

    @Override
    public void validate(final ResourceDetailDTO resourceDetailDTO) throws InvalidLeadCreationDataException {
        for (Long r : resourceDetailDTO.getResourceTypeIds()) {
            final Optional<ResourceSubCategory> resourceById = resourceSubcategoryRepository.findById(r);

            if (!(resourceById.isPresent())) {
                throw new InvalidLeadCreationDataException("Invalid Resource Type is provided.");
            }
            final ResourceSubCategory resourceSubCategoryDetails = resourceById.get();
            final ResourceCategory category = resourceSubCategoryDetails.getCategory();

            final ResourceDetails resourceDetails = resourceDetailsRepository.fetchResourceForDedup(resourceDetailDTO.getPhone1(),
                    resourceSubCategoryDetails.getId(), category.getId());

            if (resourceDetails != null) {
                throw new InvalidLeadCreationDataException("Resource details already exists");
            }

            final City resourceCity = cityRepository.findCityById(resourceDetailDTO.getCityId());

            if (!resourceCity.getState().getId().equals(resourceDetailDTO.getStateId())) {
                throw new InvalidLeadCreationDataException("City State mapping is not correct.");
            }

            final Pattern regexPhone = Pattern.compile("^(\\+\\d{1,2})?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
            Matcher regexMatcher = regexPhone.matcher(resourceDetailDTO.getPhone1());

            if (!regexMatcher.matches()) {
                throw new InvalidLeadCreationDataException("Phone Number1 is not correct.");
            }

            if (resourceDetailDTO.getPhone2() != null) {
                final Matcher regexMatcher2 = regexPhone.matcher(resourceDetailDTO.getPhone2());
                if (!regexMatcher2.matches()) {
                    throw new InvalidLeadCreationDataException("Phone Number2 is not correct.");
                }
            }
        }
    }
}
