package com.mediscreen.patientAssessment.beans;

import lombok.*;

/**
 * Represents the assessment of the probability that a patient will develop diabetes.
 */
@AllArgsConstructor
@Getter
@ToString
public class AssessmentDTO {

    private PatientBean patientBean;
    private Integer patientAge;
    private String diabetesRiskLevelAssessment;

}
