package com.covimyn.ingestion.config;

import com.covimyn.ingestion.service.impl.IngestionServiceImpl;
import com.covimyn.ingestion.util.GoogleAuthorizeUtil;
import com.covimyn.ingestion.util.ResourceDetailsUtil;
import com.covimyn.ingestion.util.SheetsServiceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean
    public SheetsServiceUtil sheetsServiceUtil() {
        return new SheetsServiceUtil();
    }

    @Bean
    public GoogleAuthorizeUtil googleAuthorizeUtil() {
        return new GoogleAuthorizeUtil();
    }

    @Bean
    public IngestionServiceImpl ingestionService() {
        return new IngestionServiceImpl();
    }

    @Bean
    public ResourceDetailsUtil resourceDetailsUtil() {
        return new ResourceDetailsUtil();
    }


}
