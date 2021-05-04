package com.wincovid21.ingestion.util.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newrelic.api.agent.Trace;
import com.wincovid21.ingestion.domain.Category;
import com.wincovid21.ingestion.domain.CityDetails;
import com.wincovid21.ingestion.domain.Resource;
import com.wincovid21.ingestion.domain.StateDetails;
import com.wincovid21.ingestion.entity.*;
import com.wincovid21.ingestion.repository.*;
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
    private static final String USER_TYPE_WISE_FEEDBACK_TYPES_LIST = "USER_TYPE_WISE_FEEDBACK_TYPES_LIST";

    private final CacheConfig cacheConfig;
    private final FeedbackTypesRepository feedbackTypesRepository;
    private final ResourceDetailsRepository resourceDetailsRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final CollectorRegistry meterRegistry;
    private final ResourceCategoryRepository resourceCategoryRepository;
    private final ResourceSubcategoryRepository resourceSubcategoryRepository;
    private final UserTypeAllowedFeedbackTypesRepository userTypeAllowedFeedbackTypesRepository;

    // Cache Elements
    private LoadingCache<String, List<FeedbackType>> feedbackTypesList;
    private LoadingCache<String, Map<StateDetails, Set<CityDetails>>> resourceStateCityDetails;
    private LoadingCache<String, Map<Category, Set<Resource>>> availableResources;
    private LoadingCache<String, Map<Long, List<FeedbackType>>> userTypeWiseAvailableFeedbackTypesList;


    public CacheUtil(@NonNull final CacheConfig cacheConfig,
                     @NonNull final FeedbackTypesRepository feedbackTypesRepository,
                     @NonNull final CollectorRegistry meterRegistry,
                     @NonNull final ResourceDetailsRepository resourceDetailsRepository,
                     @NonNull final StateRepository stateRepository,
                     @NonNull final CityRepository cityRepository,
                     @NonNull final ResourceCategoryRepository resourceCategoryRepository,
                     @NonNull final ResourceSubcategoryRepository resourceSubcategoryRepository,
                     @NonNull final UserTypeAllowedFeedbackTypesRepository userTypeAllowedFeedbackTypesRepository) {
        this.cacheConfig = cacheConfig;
        this.feedbackTypesRepository = feedbackTypesRepository;
        this.meterRegistry = meterRegistry;
        this.resourceDetailsRepository = resourceDetailsRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.resourceCategoryRepository = resourceCategoryRepository;
        this.resourceSubcategoryRepository = resourceSubcategoryRepository;
        this.userTypeAllowedFeedbackTypesRepository = userTypeAllowedFeedbackTypesRepository;
    }

    @PostConstruct
    public void buildCache() {
        final CacheMetricsCollector cacheMetrics = new CacheMetricsCollector().register(meterRegistry);
        feedbackTypesList = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getFeedbackTypeCacheInvalidationTTL(), TimeUnit.SECONDS)
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

        userTypeWiseAvailableFeedbackTypesList = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getFeedbackTypeCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    cacheMetrics.addCache(USER_TYPE_WISE_FEEDBACK_TYPES_LIST, userTypeWiseAvailableFeedbackTypesList);
                    if (USER_TYPE_WISE_FEEDBACK_TYPES_LIST.equals(key)) {
                        log.info("Initializing cache for USER_TYPE_WISE_FEEDBACK_TYPES_LIST ");
                        Iterable<UserTypeAllowedFeedbackTypes> userTypeAllowedFeedbackTypes = userTypeAllowedFeedbackTypesRepository.findAll();
                        log.info("Allowed Feedback Types # {}", userTypeAllowedFeedbackTypes);
                        Iterator<UserTypeAllowedFeedbackTypes> feedbackTypeIterator = userTypeAllowedFeedbackTypes.iterator();
                        Map<Long, List<FeedbackType>> result = new HashMap<>();

                        feedbackTypeIterator.forEachRemaining(f -> {
                            Long userType = f.getUserType().getId();
                            log.info("Processing user type # {}", userType);
                            List<FeedbackType> feedbackTypes = result.get(userType);
                            log.info("Existing Feedback List # {}, user type # {}", feedbackTypes, userType);

                            if (CollectionUtils.isEmpty(feedbackTypes)) {
                                feedbackTypes = new ArrayList<>();
                            }
                            feedbackTypes.add(f.getFeedbackType());
                            log.info("Final Feedback List # {}, user type # {}", feedbackTypes, userType);
                            result.put(userType, feedbackTypes);
                        });
                        return result;
                    }
                    return Collections.emptyMap();
                });

        availableResources = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getResourceListCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    if (RESOURCE_LIST.equals(key)) {
                        log.info("Loading Cache.");
                        final Map<Category, Set<Resource>> resourceCategorySetMap = new ConcurrentHashMap<>();
                        final List<Object[]> categoryResourceMapping = resourceDetailsRepository.fetchCategoryResourceMapping();
                        Map<ResourceCategory, Set<ResourceSubCategory>> resourceCategoryListConcurrentHashMap = new ConcurrentHashMap<>();

                        if (!(CollectionUtils.isEmpty(categoryResourceMapping))) {

                            List<Object[]> filteredNullObjects = categoryResourceMapping
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            filteredNullObjects.forEach(elements -> {
                                if (elements != null && elements.length == 2 && elements[0] != null && elements[1] != null) {
                                    Long category = Long.parseLong(elements[0].toString());
                                    Long resourceType = Long.parseLong(elements[1].toString());
                                    log.info("Iterating Types from DB | Category # {}, resource # {}", category, resourceType);

                                    Optional<ResourceCategory> optionalResourceCategory = resourceCategoryRepository.findById(category);
                                    Optional<ResourceSubCategory> optionalResourceSubCategory = resourceSubcategoryRepository.findById(resourceType);

                                    if (optionalResourceCategory.isPresent() && optionalResourceSubCategory.isPresent()) {

                                        Set<ResourceSubCategory> resourceSubCategories = resourceCategoryListConcurrentHashMap.get(optionalResourceCategory.get());
                                        if (CollectionUtils.isEmpty(resourceSubCategories)) {
                                            resourceSubCategories = Collections.synchronizedSet(new HashSet<>());
                                        }
                                        log.info("Iterating Category Entity # {}, resource # {}", optionalResourceCategory.get().getCategoryName(), optionalResourceSubCategory.get().getSubCategoryName());
                                        resourceSubCategories.add(optionalResourceSubCategory.get());
                                        resourceCategoryListConcurrentHashMap.put(optionalResourceCategory.get(), resourceSubCategories);
                                    }
                                }
                            });
                        }

                        resourceCategoryListConcurrentHashMap.forEach((category, resources) -> {
                            Category categoryDetail = Category.builder().id(category.getId()).icon(category.getIconName()).categoryName(category.getCategoryName()).build();
                            resources.forEach(c -> {
                                Resource resource = Resource.builder().id(c.getId()).resourceName(c.getSubCategoryName()).icon(c.getIconName()).build();
                                Set<Resource> existingResources = resourceCategorySetMap.get(categoryDetail);
                                if (CollectionUtils.isEmpty(existingResources)) {
                                    existingResources = Collections.synchronizedSet(new HashSet<>());
                                }
                                existingResources.add(resource);
                                resourceCategorySetMap.put(categoryDetail, existingResources);
                            });
                            log.info("Iterating Category # {}, resource # {}", categoryDetail, resourceCategorySetMap.get(categoryDetail).stream().map(Resource::getResourceName).collect(Collectors.toList()));
                        });
                        return resourceCategorySetMap;
                    }
                    return Collections.emptyMap();
                });
        cacheMetrics.addCache(RESOURCE_LIST, availableResources);
        resourceStateCityDetails = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCityStateListCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(100000)
                .recordStats()
                .build(key -> {
                    if (RESOURCE_CITY_STATE.equals(key)) {

                        final Map<StateDetails, Set<CityDetails>> resourceStateCityDetailsList = new TreeMap<>();
                        List<Object[]> objects = resourceDetailsRepository.fetchStateCityDetails();
                        Map<State, List<City>> stateCityDetails = new ConcurrentHashMap<>();

                        if (!(CollectionUtils.isEmpty(objects))) {

                            List<Object[]> filteredNullObjects = objects
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            filteredNullObjects.forEach(elements -> {
                                if (elements != null && elements.length == 2 && elements[0] != null && elements[1] != null) {
                                    Long state = Long.parseLong(elements[0].toString());
                                    Long city = Long.parseLong(elements[1].toString());

                                    Optional<State> optionalState = stateRepository.findById(state);
                                    Optional<City> optionalCity = cityRepository.findById(city);

                                    if (optionalState.isPresent() && optionalCity.isPresent()) {

                                        List<City> cities = stateCityDetails.get(optionalState.get());
                                        if (CollectionUtils.isEmpty(cities)) {
                                            cities = Collections.synchronizedList(new ArrayList<>());
                                        }
                                        cities.add(optionalCity.get());
                                        stateCityDetails.put(optionalState.get(), cities);
                                    }
                                }
                            });
                        }

                        stateCityDetails.forEach((state, cities) -> {
                            StateDetails stateDetail = StateDetails.builder().id(state.getId()).iconName(state.getIconPath()).stateName(state.getStateName()).build();
                            cities.forEach(c -> {
                                CityDetails cityDetails = CityDetails.builder().id(c.getId()).cityName(c.getCityName()).iconName(c.getIconPath()).build();
                                Set<CityDetails> existingResources = resourceStateCityDetailsList.get(stateDetail);
                                if (CollectionUtils.isEmpty(existingResources)) {
                                    existingResources = Collections.synchronizedSet(new TreeSet<>());
                                }
                                existingResources.add(cityDetails);
                                resourceStateCityDetailsList.put(stateDetail, existingResources);
                            });
                            log.info("Iterating Category # {}, resource # {}", stateDetail, resourceStateCityDetailsList.get(stateDetail).stream().map(CityDetails::getCityName).collect(Collectors.toList()));
                        });
                        return resourceStateCityDetailsList;
                    }
                    return Collections.emptyMap();
                });
        cacheMetrics.addCache(RESOURCE_CITY_STATE, resourceStateCityDetails);
    }

    @Trace
    public Map<StateDetails, Set<CityDetails>> getStateCityDetails() {
        return resourceStateCityDetails.get(RESOURCE_CITY_STATE);
    }

    public List<FeedbackType> getUseWiseAllowedFeedback(Long userType) {
        try {
            if (userTypeWiseAvailableFeedbackTypesList == null
                    || userTypeWiseAvailableFeedbackTypesList.get(USER_TYPE_WISE_FEEDBACK_TYPES_LIST) == null
                    || userTypeWiseAvailableFeedbackTypesList.get(USER_TYPE_WISE_FEEDBACK_TYPES_LIST).get(userType) == null) {
                return Collections.emptyList();
            }
            return userTypeWiseAvailableFeedbackTypesList.get(USER_TYPE_WISE_FEEDBACK_TYPES_LIST).get(userType);
        } catch (Exception e) {
            log.error("Exception while fetching allowed feedback type for user type # {}", userType, e);
            return Collections.emptyList();
        }
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
    public Map<Category, Set<Resource>> getAvailableResources() {
        return availableResources.get(RESOURCE_LIST);
    }

}
