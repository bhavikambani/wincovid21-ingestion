package com.wincovid21.ingestion.util.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.repository.FeedbackTypesRepository;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.cache.caffeine.CacheMetricsCollector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FeedbackTypeCacheUtil {
    private static final String FEEDBACK_TYPES_LIST = "FEEDBACK_TYPES_LIST";

    private final CacheConfig cacheConfig;
    private final FeedbackTypesRepository feedbackTypesRepository;
    private final CollectorRegistry meterRegistry;

    // Cache Elements
    private LoadingCache<String, List<FeedbackType>> feedbackTypesList;

    public FeedbackTypeCacheUtil(@NonNull final CacheConfig cacheConfig,
                                 @NonNull final FeedbackTypesRepository feedbackTypesRepository,
                                 @NonNull final CollectorRegistry meterRegistry) {
        this.cacheConfig = cacheConfig;
        this.feedbackTypesRepository = feedbackTypesRepository;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void buildCache() {
        final CacheMetricsCollector cacheMetrics = new CacheMetricsCollector().register(meterRegistry);
        feedbackTypesList = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCacheInvalidateTimeSeconds(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    cacheMetrics.addCache(FEEDBACK_TYPES_LIST, feedbackTypesList);
                    if (FEEDBACK_TYPES_LIST.equals(key)) {
                        Iterable<FeedbackType> allElements = feedbackTypesRepository.findAll();
                        Iterator<FeedbackType> feedbackTypeIterator = allElements.iterator();
                        List<FeedbackType> feedbackTypeList = new ArrayList<>();
                        feedbackTypeIterator.forEachRemaining(feedbackTypeList::add);
                        return feedbackTypeList;
                    }
                    return Collections.emptyList();
                });
    }

    @Trace
    public List<FeedbackType> getFeedbackList() {
        return feedbackTypesList.get(FEEDBACK_TYPES_LIST);
    }

    @Trace
    public void invalidateFeedbackListCache() {
        log.info("Invalidating cache # {}", FEEDBACK_TYPES_LIST);
        feedbackTypesList.invalidate(FEEDBACK_TYPES_LIST);
    }

}
