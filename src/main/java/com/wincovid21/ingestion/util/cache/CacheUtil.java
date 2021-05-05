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
    private static final String ALL_STATE_CITY_LIST = "ALL_STATE_CITY";
    private static final String RESOURCE_CITY_STATE = "RESOURCE_STATE_CITY";
    private static final String CITY_WISE_AVAILABLE_RESOURCES = "CITY_WISE_AVAILABLE_RESOURCES";
    private static final String AVAILABLE_RESOURCE_LIST = "AVAILABLE_RESOURCE_LIST";
    private static final String ALL_RESOURCE_LIST = "ALL_RESOURCE_LIST";
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
    private LoadingCache<String, Map<StateDetails, Set<CityDetails>>> allStateCityDetails;
    private LoadingCache<String, Map<Category, Set<Resource>>> availableResources;
    private LoadingCache<String, Map<Category, Set<Resource>>> allResources;
    private LoadingCache<String, Map<Long, List<FeedbackType>>> userTypeWiseAvailableFeedbackTypesList;
    private LoadingCache<Long, Map<Category, Set<Resource>>> cityWiseAvailableResourceAndCategory;

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
                    if (FEEDBACK_TYPES_LIST.equals(key)) {
                        Iterable<FeedbackType> allElements = feedbackTypesRepository.findAll();
                        Iterator<FeedbackType> feedbackTypeIterator = allElements.iterator();
                        List<FeedbackType> feedbackTypeList = new ArrayList<>();
                        feedbackTypeIterator.forEachRemaining(feedbackTypeList::add);
                        return feedbackTypeList;
                    }
                    return Collections.emptyList();
                });
        cacheMetrics.addCache(FEEDBACK_TYPES_LIST, feedbackTypesList);

        userTypeWiseAvailableFeedbackTypesList = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getFeedbackTypeCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
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
        cacheMetrics.addCache(USER_TYPE_WISE_FEEDBACK_TYPES_LIST, userTypeWiseAvailableFeedbackTypesList);

        availableResources = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getResourceListCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    if (AVAILABLE_RESOURCE_LIST.equals(key)) {
                        log.info("Loading Cache.");
                        final Map<Category, Set<Resource>> resourceCategorySetMap = Collections.synchronizedMap(new TreeMap<>());
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
                                    log.info("All # Iterating Types from DB | Category # {}, resource # {}", category, resourceType);

                                    Optional<ResourceCategory> optionalResourceCategory = resourceCategoryRepository.findById(category);
                                    Optional<ResourceSubCategory> optionalResourceSubCategory = resourceSubcategoryRepository.findById(resourceType);

                                    if (optionalResourceCategory.isPresent() && optionalResourceSubCategory.isPresent()) {

                                        Set<ResourceSubCategory> resourceSubCategories = resourceCategoryListConcurrentHashMap.get(optionalResourceCategory.get());
                                        if (CollectionUtils.isEmpty(resourceSubCategories)) {
                                            resourceSubCategories = Collections.synchronizedSet(new HashSet<>());
                                        }
                                        log.info("All # Iterating Category Entity # {}, resource # {}", optionalResourceCategory.get().getCategoryName(), optionalResourceSubCategory.get().getSubCategoryName());
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
                                    existingResources = Collections.synchronizedSet(new TreeSet<>());
                                }
                                existingResources.add(resource);
                                resourceCategorySetMap.put(categoryDetail, existingResources);
                            });
                            log.info("All # Iterating Resource # {}, resource category # {}", categoryDetail, resourceCategorySetMap.get(categoryDetail).stream().map(Resource::getResourceName).collect(Collectors.toList()));
                        });
                        return resourceCategorySetMap;
                    }
                    return Collections.emptyMap();
                });
        cacheMetrics.addCache(AVAILABLE_RESOURCE_LIST, availableResources);

        allResources = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getResourceListCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    if (ALL_RESOURCE_LIST.equals(key)) {
                        log.info("Loading All Resource Cache.");
                        final Map<Category, Set<Resource>> resourceCategorySetMap = Collections.synchronizedMap(new TreeMap<>());
                        Iterable<ResourceCategory> allResourcesEntity = resourceCategoryRepository.findAll();
                        Map<ResourceCategory, Set<ResourceSubCategory>> resourceCategoryListConcurrentHashMap = new ConcurrentHashMap<>();


                        allResourcesEntity.forEach(resourceCategory -> {
                            List<ResourceSubCategory> allSubCategories = resourceCategory.getSubCategories();
                            if (!(CollectionUtils.isEmpty(allSubCategories))) {
                                Set<ResourceSubCategory> alreadySetResources = resourceCategoryListConcurrentHashMap.get(resourceCategory);

                                if (CollectionUtils.isEmpty(alreadySetResources)) {
                                    alreadySetResources = Collections.synchronizedSet(new HashSet<>());
                                }
                                alreadySetResources.addAll(allSubCategories);
                                resourceCategoryListConcurrentHashMap.put(resourceCategory, alreadySetResources);
                            }
                        });

                        resourceCategoryListConcurrentHashMap.forEach((category, resources) -> {
                            Category categoryDetail = Category.builder().id(category.getId()).icon(category.getIconName()).categoryName(category.getCategoryName()).build();
                            resources.forEach(c -> {
                                Resource resource = Resource.builder().id(c.getId()).resourceName(c.getSubCategoryName()).icon(c.getIconName()).build();
                                Set<Resource> existingResources = resourceCategorySetMap.get(categoryDetail);
                                if (CollectionUtils.isEmpty(existingResources)) {
                                    existingResources = Collections.synchronizedSet(new TreeSet<>());
                                }
                                existingResources.add(resource);
                                resourceCategorySetMap.put(categoryDetail, existingResources);
                            });
                            log.info("Iterating all Resource # {}, resource category # {}", categoryDetail, resourceCategorySetMap.get(categoryDetail).stream().map(Resource::getResourceName).collect(Collectors.toList()));
                        });
                        return resourceCategorySetMap;
                    }
                    return Collections.emptyMap();
                });
        cacheMetrics.addCache(ALL_RESOURCE_LIST, allResources);

        allStateCityDetails = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCityStateListCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .recordStats()
                .build(key -> {
                    if (ALL_STATE_CITY_LIST.equals(key)) {

                        final Map<StateDetails, Set<CityDetails>> resourceStateCityDetailsList = new TreeMap<>();
                        Iterable<State> allStateDetails = stateRepository.findAll();
                        Map<State, List<City>> stateCityDetails = new ConcurrentHashMap<>();

                        allStateDetails.forEach(state -> {
                            if (state != null) {
                                try {
                                    final List<City> cities = stateCityDetails.get(state) != null ? stateCityDetails.get(state) : new ArrayList<>();
                                    List<City> stateCities = state.getCities();
                                    cities.addAll(stateCities);
                                    stateCityDetails.put(state, cities);
                                } catch (Exception e) {
                                    log.error("Exception while processing state # {}", state, e);
                                }
                            }
                        });

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
                            try {
                                log.info("Iterating Category # {}, resource # {}", stateDetail, resourceStateCityDetailsList.get(stateDetail).stream().map(CityDetails::getCityName).collect(Collectors.toList()));
                            } catch (Exception e) {
                                log.error("IGNORE THIS EXCEPTION #### Exception while iterating for logs", e);
                            }
                        });
                        return resourceStateCityDetailsList;
                    }
                    return Collections.emptyMap();
                });
        cacheMetrics.addCache(ALL_STATE_CITY_LIST, allStateCityDetails);


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


        cityWiseAvailableResourceAndCategory = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCityStateListCacheInvalidationTTL(), TimeUnit.SECONDS)
                .maximumSize(100000)
                .recordStats()
                .build(cityId -> {
                    final Optional<City> cityDetails = cityRepository.findById(cityId);
                    if (cityDetails.isPresent()) {
                        Map<Category, Set<Resource>> resourceDetails = Collections.synchronizedMap(new TreeMap<>());
                        List<ResourceDetails> resourceDetailsList = cityDetails.get().getResourceDetails();
//                        if (CollectionUtils.isEmpty(resourceDetailsList)) {
//                            return Collections.emptyMap();
//                        }

                        resourceDetailsList.forEach(r -> {
                            ResourceCategory category = r.getCategory();
                            ResourceSubCategory resourceType = r.getResourceType();

                            Category categoryDomain = Category.builder().categoryName(category.getCategoryName()).icon(category.getIconName()).id(category.getId()).build();
                            Resource resourceDomain = Resource.builder().icon(resourceType.getIconName()).resourceName(resourceType.getSubCategoryName()).id(resourceType.getId()).build();

                            Set<Resource> resourceForCategory = resourceDetails.get(categoryDomain);
                            if (resourceForCategory == null) {
                                resourceForCategory = new TreeSet<>();
                            }

                            resourceForCategory.add(resourceDomain);
                            resourceDetails.put(categoryDomain, resourceForCategory);
                        });
                        return resourceDetails;
                    } else {
                        return Collections.emptyMap();
                    }
                });
        cacheMetrics.addCache(CITY_WISE_AVAILABLE_RESOURCES, cityWiseAvailableResourceAndCategory);
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
    public Map<StateDetails, Set<CityDetails>> getAllStateCityDetails() {
        return allStateCityDetails.get(ALL_STATE_CITY_LIST);
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
        return availableResources.get(AVAILABLE_RESOURCE_LIST);
    }

    @Trace
    public Map<Category, Set<Resource>> getAllCategoryResources() {
        return availableResources.get(ALL_RESOURCE_LIST);
    }

    @Trace
    public Map<Category, Set<Resource>> getCityWiseAvailableResources(@NonNull final Long cityId) {
        return cityWiseAvailableResourceAndCategory.get(cityId);
    }

}
