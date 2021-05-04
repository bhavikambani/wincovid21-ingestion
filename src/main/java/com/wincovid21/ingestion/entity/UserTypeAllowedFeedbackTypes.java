package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "user_allowed_feedback_types")
@Data
public class UserTypeAllowedFeedbackTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_type")
    @ToString.Exclude
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "feedback_type")
    @ToString.Exclude
    private FeedbackType feedbackType;

}
