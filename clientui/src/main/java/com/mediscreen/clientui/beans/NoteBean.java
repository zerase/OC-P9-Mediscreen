package com.mediscreen.clientui.beans;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoteBean {

    private String id;

    private Integer patientId;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime dateOfCreation;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime dateOfModification;

    @NotBlank(message = "Note is required.")
    private String content;

}
