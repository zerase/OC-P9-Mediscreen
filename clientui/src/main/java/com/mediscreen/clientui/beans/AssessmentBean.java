package com.mediscreen.clientui.beans;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AssessmentBean {

    private PatientBean patientBean;
    private int patientAge;
    private String diabetesRiskLevelAssessment;

}
