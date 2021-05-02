package com.wincovid21.ingestion.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "resource_category")
@Data
public class ResourceSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sub_category_name", nullable = false)
    private String subCategoryName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ResourceCategory category;

    @OneToMany(mappedBy = "resourceType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ResourceDetails> resourceDetailsList;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private Date updatedOn;

}
