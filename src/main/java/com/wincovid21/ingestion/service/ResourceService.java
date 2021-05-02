package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.CityDetails;
import com.wincovid21.ingestion.domain.StateDetails;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResourceService {
    @Trace
    Map<StateDetails, Set<CityDetails>> getStateCityList();

    @Trace
    List<String> getAvailableResources();
}
