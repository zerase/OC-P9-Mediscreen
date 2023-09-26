package com.mediscreen.patient.repository;

import com.mediscreen.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Allows interactions with the patient table of the sql database and provides functions of CRUD operations among other things.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    /**
     * Determines if an instance containing the same input data already exists.
     *
     * @param lastName     the string to check
     * @param firstName    the string to check
     * @param dateOfBirth  the date to check
     * @return             true if input data is already present, false otherwise
     */
    boolean existsByLastNameAndFirstNameAndDateOfBirth(String lastName, String firstName, LocalDate dateOfBirth);

    /**
     * Returns all instances of the given type which have the same last name.
     * This includes cases where the last name is written in lowercase or uppercase or with accents.
     *
     * @param lastName  the string to be searched on
     * @return          all the entities with the same last name
     */
    List<Patient> findAllByLastNameIgnoreCase(String lastName);

}
