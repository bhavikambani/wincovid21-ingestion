package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "city_details")
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "icon_path")
    private String iconPath;

    @ManyToOne
    @JoinColumn(name = "state_id")
    @ToString.Exclude
    private State state;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<ResourceDetails> resourceDetails;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private Date updatedOn;

}

