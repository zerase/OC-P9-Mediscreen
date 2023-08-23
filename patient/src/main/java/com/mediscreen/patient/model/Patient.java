package com.mediscreen.patient.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * The entity linked to the patients table of the database. Also contains the incoming data validation constraints.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 30, message = "Last name must be from 2 to 30 characters.")
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 30, message = "First name must be from 2 to 30 characters.")
    @Column(name = "first_name", length = 30, nullable = false)
    private String firstName;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be before the current date.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", length = 10, nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required.")
    @Size(max = 1, message = "Gender must be 1 character long")
    @Pattern(regexp = "M|F", message = "Gender must be letter M or F.")
    @Column(name = "gender", columnDefinition = "enum('F', 'M')", length = 1, nullable = false)
    private String gender;

    @Size(max = 100, message = "Address must be 100 characters long.")
    @Column(name = "address", length = 100)
    private String address;

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
