package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.Category;
import com.wincovid21.ingestion.domain.CityDetails;
import com.wincovid21.ingestion.domain.Resource;
import com.wincovid21.ingestion.domain.StateDetails;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.exception.UnAuthorizedUserException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface ResourceService {
    @Trace
    Map<StateDetails, Set<CityDetails>> getStateCityList();

    @Trace
    Map<StateDetails, Set<CityDetails>> getAllStateCityList();

    @Trace
    Map<Category, Set<Resource>> getAllAvailableResources();

    @Trace
    Map<Category, Set<Resource>> getAvailableResourcesForCity(Long cityId);

    @Trace
    void updateWithVerified(Long resourceId, String verificationType) throws UnAuthorizedUserException, IOException;

    @Trace
    void updateWithUnVerified(Long resourceId, String verificationType) throws UnAuthorizedUserException, IOException;

    @Trace
    void updateES(Long resourceId, FeedbackType feedbackType) throws IOException;

    @Trace
    Map<Category, Set<Resource>> getAllCategoryResources();
}
