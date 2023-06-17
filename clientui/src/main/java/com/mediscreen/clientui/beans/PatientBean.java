package com.mediscreen.clientui.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientBean {

    private Integer id;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Z a-z]*$", message = "Last name has to be text")
    @Size(min = 2, max = 30)
    private String lastName;

    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Z a-z]*$", message = "First name has to be text")
    @Size(min = 2, max = 30)
    private String firstName;

    @NotNull(message = "Date of birth is mandatory")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Date of birth should not be a future date")
    private LocalDate birthDate;

    @NotNull(message = "Gender is mandatory")
    @Pattern(regexp = "^[M|F]$")
    private String gender;

    @Size(max = 100, message = "Address should not exceed 100 characters")
    private String address;

    @Size(max = 12, message = "Phone should not exceed 12 characters")
    private String phoneNumber;

    // ========================================================================
    /*public PatientBean() {
    }

    // ========================================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // ========================================================================

    @Override
    public String toString() {
        return "PatientBean{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", birthDate=" + birthDate +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }*/
}
