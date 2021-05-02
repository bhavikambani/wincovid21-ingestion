package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {
    private Long id;
    private String categoryName;
    private String icon;
}
