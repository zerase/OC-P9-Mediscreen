package com.mediscreen.patientAssessment.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
//@Setter
@ToString
public class NoteBean {

    private String id;
    private Integer patientId;
    private LocalDateTime dateOfCreation;
    private LocalDateTime dateOfModification;
    private String content;

}
