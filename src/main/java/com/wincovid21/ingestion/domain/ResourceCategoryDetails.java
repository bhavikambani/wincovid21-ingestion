package com.wincovid21.ingestion.domain;

import lombok.Data;
import lombok.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Data
public class ResourceCategoryDetails {
    private final Category category;
    private final Set<Resource> resources;

    public ResourceCategoryDetails(@NonNull final Category c) {
        this.category = c;
        resources = Collections.synchronizedSet(new TreeSet<>());
    }

    public void addResource(@NonNull final Resource resource) {
        resources.add(resource);
    }

    public void addResource(@NonNull final Set<Resource> resources) {
        this.resources.addAll(resources);
    }

}
