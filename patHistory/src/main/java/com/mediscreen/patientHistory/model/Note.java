package com.mediscreen.patientHistory.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "notes") // This maps entity class objects to JSON formatted documents
public class Note {

    @Id
    private String id;

    @NotNull(message = "Patient Id is mandatory.")
    private Integer patientId;

    private LocalDateTime dateOfCreation;

    private LocalDateTime dateOfModification;

    @NotBlank(message = "Content is mandatory.")
    private String content;


    // === Other constructor ==================================================

    public Note(Integer patientId, LocalDateTime dateOfCreation, LocalDateTime dateOfModification, String content) {
        this.patientId = patientId;
        this.dateOfCreation = dateOfCreation;
        this.dateOfModification = dateOfModification;
        this.content = content;
    }
}
