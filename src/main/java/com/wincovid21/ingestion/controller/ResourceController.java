package com.wincovid21.ingestion.controller;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.*;
import com.wincovid21.ingestion.service.ResourceService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;
    private final CacheUtil cacheUtil;

    @Autowired
    public ResourceController(@NonNull final ResourceService resourceService,
                              @NonNull final CacheUtil cacheUtil) {
        this.resourceService = resourceService;
        this.cacheUtil = cacheUtil;
    }

    @Trace
    @GetMapping
    public IngestionResponse<Map<Category, Set<Resource>>> availableResources() {
        return IngestionResponse.<Map<Category, Set<Resource>>>builder().httpStatus(HttpStatus.OK).result(cacheUtil.getAvailableResources()).build();
    }

    @GetMapping("/city-states")
    @Trace
    public IngestionResponse<List<StateWiseConfiguredCities>> getStateCityDetails() {
        final Map<StateDetails, Set<CityDetails>> stateCityDetails = cacheUtil.getStateCityDetails();
        final List<StateWiseConfiguredCities> stateWiseConfiguredCities = new ArrayList<>();
        stateCityDetails.forEach((s, cities) -> {
            StateWiseConfiguredCities stateWiseConfiguredCity = new StateWiseConfiguredCities(s);
            stateWiseConfiguredCity.addCity(cities);

            stateWiseConfiguredCities.add(stateWiseConfiguredCity);
        });
        return IngestionResponse.<List<StateWiseConfiguredCities>>builder().httpStatus(HttpStatus.OK).result(stateWiseConfiguredCities).build();
    }


    @DeleteMapping("/invalidate-cache")
    public HttpStatus invalidateCache() {
        cacheUtil.invalidateStateCityCache();
        return HttpStatus.OK;
    }


}
