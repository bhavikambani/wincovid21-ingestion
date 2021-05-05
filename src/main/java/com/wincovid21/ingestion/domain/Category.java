package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category implements Comparable<Category> {
    private Long id;
    private String categoryName;
    private String icon;

    @Override
    public int compareTo(Category o) {
        return this.categoryName.compareTo(o.categoryName);
    }
}
