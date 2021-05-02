package com.wincovid21.ingestion.controller;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.IngestionResponse;
import com.wincovid21.ingestion.domain.ResourceStateCityDetails;
import com.wincovid21.ingestion.service.ResourceService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public IngestionResponse<List<String>> availableResources() {
        return IngestionResponse.<List<String>>builder().httpStatus(HttpStatus.OK).result(cacheUtil.getAvailableResources()).build();
    }

    @GetMapping("/city-states")
    @Trace
    public IngestionResponse<List<ResourceStateCityDetails>> getStateCityDetails() {
        return IngestionResponse.<List<ResourceStateCityDetails>>builder().httpStatus(HttpStatus.OK).result(resourceService.getStateCityList()).build();

    }

    @DeleteMapping("/invalidate-cache")
    public HttpStatus invalidateCache() {
        cacheUtil.invalidateStateCityCache();
        return HttpStatus.OK;
    }


}
