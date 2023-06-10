package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;

import java.util.List;

public interface PatientService {

    List<Patient> readAllPatients();
    Patient readPatient(Integer id);

}
