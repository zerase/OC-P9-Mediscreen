package com.mediscreen.patientAssessment.beans;

import lombok.*;

/**
 * Represents the assessment of the probability that a patient will develop diabetes.
 */
//@NoArgsConstructor
@AllArgsConstructor
@Getter
//@Setter
@ToString
public class AssessmentDTO {

    private PatientBean patientDTO;
    private Integer patientAge;
    private String diabetesRiskLevelAssessment;

}
