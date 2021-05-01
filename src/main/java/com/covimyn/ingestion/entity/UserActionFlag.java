package com.covimyn.ingestion.entity;


import com.covimyn.ingestion.domain.FeedbackType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class UserActionFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "resouce_id")
    private Long resourceId;

    @Column(name = "feedback_type")
    private FeedbackType feedbackType;

    @Column(name = "updated_on")
    private Date updatedOn;

}
