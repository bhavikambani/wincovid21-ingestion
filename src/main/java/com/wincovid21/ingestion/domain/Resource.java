package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resource implements Comparable<Resource> {
    private Long id;
    private String resourceName;
    private String icon;

    @Override
    public int compareTo(Resource o) {
        return this.resourceName.compareTo(o.resourceName);
    }

}
