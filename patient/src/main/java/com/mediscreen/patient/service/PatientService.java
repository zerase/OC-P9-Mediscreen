package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.PatientAlreadyExistsException;
import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;

import java.util.List;

/**
 * Manages CRUD operations of the business service layer related to patients.
 */
public interface PatientService {

    /**
     * Creates a new patient in database.
     *
     * @param patient  the patient to create
     * @return         the newly created patient
     * @throws PatientAlreadyExistsException  if the patient with same data already exists in database
     */
    Patient createPatient(Patient patient) throws PatientAlreadyExistsException;

    /**
     * Retrieves all patients from database.
     *
     * @return  a list of all patients
     */
    List<Patient> readAllPatients();

    /**
     * Retrieves all patients from database which last name contains keyword.
     * @param keyword  the string to be searched on
     * @return         a list of all patients containing the same keyword
     */
    List<Patient> readAllPatientsByLastName(String keyword);

    /**
     * Retrieves a patient by his id from database.
     *
     * @param id  the id of the patient to retrieve
     * @return    the patient with the given id
     * @throws PatientNotFoundException  if the patient doesn't exist
     */
    Patient readPatient(Integer id) throws PatientNotFoundException;

    /**
     * Updates an existing patient in database.
     *
     * @param id       the id of the patient to update
     * @param patient  the patient with the values to update
     * @return         the updated patient
     * @throws PatientNotFoundException  if the patient doesn't exist
     */
    Patient updatePatient(Integer id, Patient patient) throws PatientNotFoundException;

    /**
     * Deletes an existing patient by his id from database.
     *
     * @param id  the id of the patient to delete
     * @throws PatientNotFoundException  if the patient doesn't exist
     */
    void deletePatient(Integer id) throws PatientNotFoundException;

}
