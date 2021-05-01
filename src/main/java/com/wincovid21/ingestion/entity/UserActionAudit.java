package com.wincovid21.ingestion.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_actions_audit")
@Data
public class UserActionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "feedback_type")
    @Enumerated(EnumType.STRING)
    private String feedbackType;

    @Column(name = "updated_on")
    private Date updatedOn;

}
