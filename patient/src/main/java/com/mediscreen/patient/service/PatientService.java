package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;

import java.util.List;

public interface PatientService {

    Patient createPatient(Patient patient);
    List<Patient> readAllPatients();
    Patient readPatient(Integer id);
    Patient updatePatient(Integer id, Patient patient);
    void deletePatient(Integer id);

}
