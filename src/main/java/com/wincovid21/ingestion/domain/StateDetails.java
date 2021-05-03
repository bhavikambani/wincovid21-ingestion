package com.wincovid21.ingestion.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateDetails implements Comparable<StateDetails> {

    private Long id;
    private String stateName;
    private String iconName;

    @Override
    public int compareTo(StateDetails o) {
        return this.stateName.compareTo(o.stateName);
    }
}
