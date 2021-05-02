package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resource {
    private Long id;
    private String resourceName;
    private String icon;
}
