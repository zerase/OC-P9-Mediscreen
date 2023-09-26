package com.mediscreen.patientAssessment.beans;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@ToString
public class PatientBean {

    private Integer id;
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phoneNumber;

}
