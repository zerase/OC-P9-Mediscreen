package com.mediscreen.patientHistory.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity linked to the notes table of the database. Also contains the incoming data validation constraints.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "notes") // Maps entity class object to JSON formatted documents
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

    /**
     * Instantiates a note.
     *
     * @param patientId           the patient id with which the note is associated
     * @param dateOfCreation      the date the note was created
     * @param dateOfModification  the date the change was made to the note
     * @param content             the content of the note provided by the practitioner
     */
    public Note(Integer patientId, LocalDateTime dateOfCreation, LocalDateTime dateOfModification, String content) {
        this.patientId = patientId;
        this.dateOfCreation = dateOfCreation;
        this.dateOfModification = dateOfModification;
        this.content = content;
    }

}
