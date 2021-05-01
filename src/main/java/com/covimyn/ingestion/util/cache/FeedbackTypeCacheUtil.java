package com.covimyn.ingestion.util.cache;


import com.covimyn.ingestion.entity.FeedbackType;
import com.covimyn.ingestion.repository.FeedbackTypesRepository;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FeedbackTypeCacheUtil {
    private static final String FEEDBACK_TYPES_LIST = "FEEDBACK_TYPES_LIST";

    private final CacheConfig cacheConfig;
    private final FeedbackTypesRepository feedbackTypesRepository;

    private LoadingCache<String, List<FeedbackType>> feedbackTypesList;

    public FeedbackTypeCacheUtil(@NonNull final CacheConfig cacheConfig,
                                 @NonNull final FeedbackTypesRepository feedbackTypesRepository) {
        this.cacheConfig = cacheConfig;
        this.feedbackTypesRepository = feedbackTypesRepository;
    }

    @PostConstruct
    public void buildCache() {
        feedbackTypesList = Caffeine
                .newBuilder()
                .refreshAfterWrite(cacheConfig.getCacheInvalidateTimeSeconds(), TimeUnit.SECONDS)
                .maximumSize(1000)
                .build(key -> {
                    if (FEEDBACK_TYPES_LIST.equals(key)) {
                        Iterable<FeedbackType> allElements = feedbackTypesRepository.findAll();
                        Iterator<FeedbackType> feedbackTypeIterator = allElements.iterator();
                        List<FeedbackType> feedbackTypeList = new ArrayList<>();
                        feedbackTypeIterator.forEachRemaining(feedbackTypeList::add);
                        return feedbackTypeList;
                    }
                    return new ArrayList<>();
                });
    }

    public List<FeedbackType> getFeedbackList() {
        return feedbackTypesList.get(FEEDBACK_TYPES_LIST);
    }

    public void invalidateFeedbackListCache() {
        feedbackTypesList.invalidate(FEEDBACK_TYPES_LIST);
    }

}
