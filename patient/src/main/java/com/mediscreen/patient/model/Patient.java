package com.mediscreen.patient.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * The entity linked to the patients table of the database. Also contains the incoming data validation constraints.
 */
@Schema(description = "Patient model information")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "patients")
public class Patient {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Patient id", example = "3")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Schema(description = "Patient's last name", example = "TestInDanger")
    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 30, message = "Last name must be from 2 to 30 characters.")
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @Schema(description = "Patient's first name", example = "Test")
    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 30, message = "First name must be from 2 to 30 characters.")
    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @Schema(description = "Patient's date of birth", example = "2004-06-18")
    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be before the current date.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", length = 10, nullable = false)
    private LocalDate dateOfBirth;

    @Schema(description = "Patient's gender", example = "M")
    @NotBlank(message = "Gender is required.")
    @Size(max = 1, message = "Gender must be 1 character long")
    @Pattern(regexp = "^[M|F]$", message = "Gender must be letter M or F.")
    @Column(name = "gender", columnDefinition = "enum('F', 'M')", length = 1, nullable = false)
    private String gender;

    @Schema(description = "Patient's address", example = "3 Club Road")
    @Size(max = 100, message = "Address must be 100 characters long.")
    @Column(name = "address", length = 100)
    private String address;

    @Schema(description = "Patient's phone number", example = "300-444-5555")
    @Size(max = 15, message = "Phone number must be 15 characters long.")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;


    // === Other constructor ==================================================

    /**
     * Instantiates a patient.
     *
     * @param lastName     the last name of the patient
     * @param firstName    the first name of the patient
     * @param dateOfBirth  the date of birth of the patient
     * @param gender       the gender of the patient
     * @param address      the address of the patient
     * @param phoneNumber  the phone number of the patient
     */
    public Patient(String lastName, String firstName, LocalDate dateOfBirth, String gender, String address, String phoneNumber) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

}
