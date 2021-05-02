package com.wincovid21.ingestion.util.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {

    @Value("${resource.list.cache.invalidate.ttl.seconds}")
    @Getter
    private int resourceListCacheInvalidationTTL;

    @Value("${city.state.list.cache.invalidate.ttl.seconds}")
    @Getter
    private int cityStateListCacheInvalidationTTL;

    @Value("${feedback.type.list.cache.invalidate.ttl.seconds}")
    @Getter
    private int feedbackTypeCacheInvalidationTTL;


}
