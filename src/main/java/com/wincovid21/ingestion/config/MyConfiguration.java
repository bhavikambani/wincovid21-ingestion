package com.wincovid21.ingestion.config;

import com.wincovid21.ingestion.service.IngestionServiceImpl;
import com.wincovid21.ingestion.util.GoogleAuthorizeUtil;
import com.wincovid21.ingestion.util.ResourceDetailsUtil;
import com.wincovid21.ingestion.util.SheetsServiceUtil;
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
