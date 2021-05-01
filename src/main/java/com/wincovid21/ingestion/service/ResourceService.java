package com.wincovid21.ingestion.service;

import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.ResourceStateCityDetails;

import java.util.List;

public interface ResourceService {
    @Trace
    List<ResourceStateCityDetails> getStateCityList();
}
