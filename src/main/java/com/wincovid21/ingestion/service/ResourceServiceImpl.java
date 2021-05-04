package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.client.SearchClientHelper;
import com.wincovid21.ingestion.domain.*;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.ResourceDetails;
import com.wincovid21.ingestion.entity.ResourceRequestEntry;
import com.wincovid21.ingestion.entity.VerificationTypeEntity;
import com.wincovid21.ingestion.exception.UnAuthorizedUserException;
import com.wincovid21.ingestion.repository.ResourceDetailsRepository;
import com.wincovid21.ingestion.util.ResourceDetailsUtil;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final CacheUtil cacheUtil;
    private final ResourceDetailsRepository resourceDetailsRepository;
    private final ResourceDetailsUtil ResourceDetailsUtil;
    private final SearchClientHelper searchClientHelper;

    public ResourceServiceImpl(@NonNull final CacheUtil cacheUtil,
                               @NonNull final ResourceDetailsRepository resourceDetailsRepository,
                               @NonNull final ResourceDetailsUtil ResourceDetailsUtil,
                               @NonNull final SearchClientHelper searchClientHelper) {
        this.cacheUtil = cacheUtil;
        this.resourceDetailsRepository = resourceDetailsRepository;
        this.ResourceDetailsUtil = ResourceDetailsUtil;
        this.searchClientHelper = searchClientHelper;
    }

    @Override
    @Trace
    public Map<StateDetails, Set<CityDetails>> getStateCityList() {
        return cacheUtil.getStateCityDetails();
    }

    @Override
    public Map<Category, Set<Resource>> getAvailableResources() {
        return cacheUtil.getAvailableResources();
    }

    @Override
    public void updateWithVerified(Long resourceId, String verificationType) throws UnAuthorizedUserException, IOException {
        Optional<ResourceDetails> resourceEntityOptional = resourceDetailsRepository.findById(resourceId);
        if (resourceEntityOptional.isPresent()) {
            ResourceDetails resourceDetails = resourceEntityOptional.get();
            resourceDetails.setVerified(true);
            resourceDetailsRepository.save(resourceDetails);

            ResourceRequestEntry resourceRequestEntry = ResourceDetailsUtil.convertToRREntry(resourceDetails);
            searchClientHelper.makeHttpPostRequest(resourceRequestEntry);
        } else {
            throw new UnAuthorizedUserException("Resource id # " + resourceId + " does not exists");
        }

    }

    @Override
    public void updateWithUnVerified(Long resourceId, String verificationType) throws UnAuthorizedUserException, IOException {
        Optional<ResourceDetails> resourceEntityOptional = resourceDetailsRepository.findById(resourceId);
        if (resourceEntityOptional.isPresent()) {
            ResourceDetails resourceDetails = resourceEntityOptional.get();
            resourceDetails.setVerified(false);
            resourceDetailsRepository.save(resourceDetails);

            ResourceRequestEntry resourceRequestEntry = ResourceDetailsUtil.convertToRREntry(resourceDetails);
            searchClientHelper.makeHttpPostRequest(resourceRequestEntry);
        } else {
            throw new UnAuthorizedUserException("Resource id # " + resourceId + " does not exists");
        }

    }

    @Override
    public void updateES(Long resourceId, FeedbackType feedbackType) throws IOException {
        Optional<ResourceDetails> resourceEntityOptional = resourceDetailsRepository.findById(resourceId);
        boolean atLeastOneFlagChanges = false;

        if (resourceEntityOptional.isPresent()) {
            ResourceDetails resourceDetails = resourceEntityOptional.get();
            if (VerificationTypeEntity.NOCHANGE != feedbackType.getVerificationStatus()) {
                atLeastOneFlagChanges = true;
                resourceDetails.setVerified(feedbackType.getVerificationStatus() == VerificationTypeEntity.VERIFIED);
            }
            if (AvailabilityType.NOCHANGE != feedbackType.getAvailabilityStatus()) {
                atLeastOneFlagChanges = true;
                resourceDetails.setQuantityAvailable(feedbackType.getAvailabilityStatus().toString());
            }
            if (atLeastOneFlagChanges) {
                resourceDetailsRepository.save(resourceDetails);
                ResourceRequestEntry resourceRequestEntry = ResourceDetailsUtil.convertToRREntry(resourceDetails);
                log.info("Publishing Audit event to ES # {}", resourceRequestEntry);
                IngestionResponse<HttpEntity> earchResponse = searchClientHelper.makeHttpPostRequest(resourceRequestEntry);
                log.info("Publishing Audit event response # {}", earchResponse);
            } else {
                log.info("As not flags changed for Feedback type # {}, not pushing to ES # {}", feedbackType, resourceDetails);
            }

        }
    }
}
