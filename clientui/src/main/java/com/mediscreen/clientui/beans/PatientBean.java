package com.mediscreen.clientui.beans;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PatientBean {

    private Integer id;

    @NotBlank(message = "Last name is required.")
    @Pattern(regexp = "^[A-Z a-z]*$", message = "Last name has to be text.")
    @Size(min = 2, max = 30, message = "Last name must be from 2 to 30 characters.")
    private String lastName;

    @NotBlank(message = "First name is required.")
    @Pattern(regexp = "^[A-Z a-z]*$", message = "First name has to be text.")
    @Size(min = 2, max = 30, message = "First name must be from 2 to 30 characters.")
    private String firstName;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be before the current date.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required.")
    @Pattern(regexp = "^[M|F]$")
    private String gender;

    @Size(max = 100, message = "Address should not exceed 100 characters.")
    private String address;

    @Size(max = 15, message = "Phone should not exceed 15 characters.")
    private String phoneNumber;

}
