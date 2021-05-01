package com.wincovid21.ingestion.util.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {

    @Value("${cache.invalidate.time.seconds}")
    @Getter
    private int cacheInvalidateTimeSeconds;


}
