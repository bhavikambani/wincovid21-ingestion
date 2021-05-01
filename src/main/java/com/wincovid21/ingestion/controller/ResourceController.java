package com.wincovid21.ingestion.controller;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.IngestionResponse;
import com.wincovid21.ingestion.domain.ResourceStateCityDetails;
import com.wincovid21.ingestion.service.ResourceService;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller("resource")
public class ResourceController {

    private final ResourceService resourceService;
    private final CacheUtil cacheUtil;

    public ResourceController(@NonNull final ResourceService resourceService,
                              @NonNull final CacheUtil cacheUtil) {
        this.resourceService = resourceService;
        this.cacheUtil = cacheUtil;
    }

    @GetMapping("/city-states")
    @Trace
    public IngestionResponse<List<ResourceStateCityDetails>> getStateCityDetails() {
        return IngestionResponse.<List<ResourceStateCityDetails>>builder().httpStatus(HttpStatus.OK).result(resourceService.getStateCityList()).build();

    }

    @GetMapping("/invalidate-cache")
    public HttpStatus invalidateCache() {
        cacheUtil.invalidateStateCityCache();
        return HttpStatus.OK;
    }


}
