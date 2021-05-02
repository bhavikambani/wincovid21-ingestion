package com.wincovid21.ingestion.util.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.ResourceStateCityDetails;
import com.wincovid21.ingestion.entity.FeedbackType;
import com.wincovid21.ingestion.repository.FeedbackTypesRepository;
import com.wincovid21.ingestion.repository.ResourceDetailsRepository;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.cache.caffeine.CacheMetricsCollector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CacheUtil {
    private static final String FEEDBACK_TYPES_LIST = "FEEDBACK_TYPES_LIST";
    private static final String RESOURCE_CITY_STATE = "RESOURCE_STATE_CITY";
    private static final String RESOURCE_LIST = "RESOURCE_LIST";

    private final CacheConfig cacheConfig;
    private final FeedbackTypesRepository feedbackTypesRepository;
    private final ResourceDetailsRepository resourceDetailsRepository;
    private final CollectorRegistry meterRegistry;

    // Cache Elements
    private LoadingCache<String, List<FeedbackType>> feedbackTypesList;
    private LoadingCache<String, List<ResourceStateCityDetails>> resourceStateCityDetails;
    private LoadingCache<String, List<String>> availableResources;

    public CacheUtil(@NonNull final CacheConfig cacheConfig,
                     @NonNull final FeedbackTypesRepository feedbackTypesRepository,
                     @NonNull final CollectorRegistry meterRegistry,
                     @NonNull final ResourceDetailsRepository resourceDetailsRepository) {
        this.cacheConfig = cacheConfig;
        this.feedbackTypesRepository = feedbackTypesRepository;
        this.meterRegistry = meterRegistry;
        this.resourceDetailsRepository = resourceDetailsRepository;
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

        availableResources = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCacheInvalidateTimeSeconds(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    cacheMetrics.addCache(RESOURCE_LIST, availableResources);
                    if (RESOURCE_LIST.equals(key)) {
                        return resourceDetailsRepository.getDistinctResourceTypes();
                    }
                    return Collections.emptyList();
                });

        resourceStateCityDetails = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCacheInvalidateTimeSeconds(), TimeUnit.SECONDS)
                .maximumSize(100000)
                .recordStats()
                .build(key -> {
                    cacheMetrics.addCache(RESOURCE_CITY_STATE, resourceStateCityDetails);
                    if (RESOURCE_CITY_STATE.equals(key)) {

                        final List<ResourceStateCityDetails> resourceStateCityDetailsList = new ArrayList<>();
                        List<Object[]> objects = resourceDetailsRepository.fetchStateCityDetails();
                        Map<String, List<String>> stateCityDetails = new ConcurrentHashMap<>();

                        if (!(CollectionUtils.isEmpty(objects))) {

                            List<Object[]> filteredNullObjects = objects
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            filteredNullObjects.forEach(elements -> {
                                if (elements != null && elements.length == 2 && elements[0] != null && elements[1] != null) {
                                    String state = elements[0].toString();
                                    String city = elements[1].toString();

                                    List<String> cities = stateCityDetails.get(state);
                                    if (CollectionUtils.isEmpty(cities)) {
                                        cities = Collections.synchronizedList(new ArrayList<>());
                                    }
                                    cities.add(city);
                                    stateCityDetails.put(state, cities);
                                }
                            });
                        }

                        stateCityDetails.forEach((state, cities) -> {
                            ResourceStateCityDetails resourceStateCityDetails = new ResourceStateCityDetails();
                            resourceStateCityDetails.setState(state);

                            cities.forEach(resourceStateCityDetails::addCity);

                            resourceStateCityDetailsList.add(resourceStateCityDetails);

                        });
                        return resourceStateCityDetailsList;
                    }
                    return Collections.emptyList();
                });
    }

    @Trace
    public List<ResourceStateCityDetails> getStateCityDetails() {
        return resourceStateCityDetails.get(RESOURCE_CITY_STATE);
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

    @Trace
    public void invalidateStateCityCache() {
        log.info("Invalidating cache # {}", RESOURCE_CITY_STATE);
        feedbackTypesList.invalidate(RESOURCE_CITY_STATE);
    }

    @Trace
    public List<String> getAvailableResources() {
        return availableResources.get(RESOURCE_LIST);
    }

}
