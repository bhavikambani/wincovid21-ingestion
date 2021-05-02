package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.CityDetails;
import com.wincovid21.ingestion.domain.StateDetails;
import com.wincovid21.ingestion.util.cache.CacheUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final CacheUtil cacheUtil;

    public ResourceServiceImpl(@NonNull final CacheUtil cacheUtil) {
        this.cacheUtil = cacheUtil;
    }

    @Override
    @Trace
    public Map<StateDetails, Set<CityDetails>> getStateCityList() {
        return cacheUtil.getStateCityDetails();
    }

    @Override
    public List<String> getAvailableResources() {
        return cacheUtil.getAvailableResources();
    }
}
