package com.wincovid21.ingestion.entity;

import com.wincovid21.ingestion.domain.AvailabilityType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "feedback_types")
@Data
public class FeedbackType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "feedback_code")
    private String feedbackCode;

    @Column(name = "feedback_message")
    private String feedbackMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationTypeEntity verificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private AvailabilityType availabilityStatus;

    @OneToMany(mappedBy = "feedbackType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<UserTypeAllowedFeedbackTypes> allowedFeedbackTypes;


}
