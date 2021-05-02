package com.wincovid21.ingestion.controller;

import com.wincovid21.ingestion.domain.*;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.entity.UserActionAudit;
import com.wincovid21.ingestion.repository.CityRepository;
import com.wincovid21.ingestion.repository.UserActionAuditRepository;
import com.wincovid21.ingestion.service.UserActionService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import com.wincovid21.ingestion.util.monit.Profiler;
import com.wincovid21.ingestion.util.monit.ProfilerNames;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class HelloCoviMyn {

    private final UserActionAuditRepository userActionFlagRepository;
    private final UserActionService userActionService;
    private final CityRepository cityRepository;
    private final CacheUtil cacheUtil;

    private final Profiler profiler;

    public HelloCoviMyn(@NonNull final UserActionAuditRepository userActionFlagRepository,
                        @NonNull final Profiler profiler,
                        @NonNull final UserActionService userActionService,
                        @NonNull final CacheUtil cacheUtil, CityRepository cityRepository) {
        this.userActionFlagRepository = userActionFlagRepository;
        this.profiler = profiler;
        this.userActionService = userActionService;
        this.cacheUtil = cacheUtil;
        this.cityRepository = cityRepository;
    }

    @GetMapping("/")
    public IngestionResponse<List<ResourceCategoryDetails>> sayHello() {
        profiler.increment(ProfilerNames.HELLO_TOTAL);
        UserActionAudit userActionFlag = new UserActionAudit();
        userActionFlag.setResourceId(123L);
        userActionFlag.setUpdatedOn(new Date());
        userActionFlag.setFeedbackType("abc");

        userActionFlagRepository.save(userActionFlag);

        List<FeedbackType> feedbackTypes = userActionService.getFeedbackTypes();


        List<Object[]> objects = userActionFlagRepository.fetchDetails();

        List<Response> responses = new ArrayList<>();


        if (!(CollectionUtils.isEmpty(objects))) {
            objects.forEach(a -> responses.add(Response.builder().a(a[0].toString()).b(a[1].toString()).build()));
        }

        IngestionResponse.<List<Response>>builder().httpStatus(HttpStatus.OK).result(responses).build();

        Map<StateDetails, Set<CityDetails>> stateCityDetails = cacheUtil.getStateCityDetails();

        List<StateWiseConfiguredCities> stateWiseConfiguredCities = new ArrayList<>();

        stateCityDetails.forEach((s, cities) -> {
            StateWiseConfiguredCities stateWiseConfiguredCity = new StateWiseConfiguredCities(s);
            stateWiseConfiguredCity.addCity(cities);

            stateWiseConfiguredCities.add(stateWiseConfiguredCity);
        });

        Map<Category, Set<Resource>> availableResources = cacheUtil.getAvailableResources();

        List<ResourceCategoryDetails> resourceCategoryDetails = new ArrayList<>();

        availableResources.forEach((r, c) -> {
            ResourceCategoryDetails resourceCategoryDetails1 = new ResourceCategoryDetails(r);
            resourceCategoryDetails1.addResource(c);

            resourceCategoryDetails.add(resourceCategoryDetails1);
        });

        return IngestionResponse.<List<ResourceCategoryDetails>>builder().httpStatus(HttpStatus.OK).result(resourceCategoryDetails).build();
    }

}

@Builder
@Data
class Response {
    String a;
    String b;
}
