package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }


    // ======= CREATE =========================================================

    @Override
    public Patient createPatient(Patient patientToAdd) {
        logger.debug("##### Call to method --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        Patient addedPatient = patientRepository.save(patientToAdd);

        logger.info("### Created new patient successfully");

        return addedPatient;
    }


    // ======= READ ALL =======================================================

    @Override
    public List<Patient> readAllPatients() {
        logger.debug("##### Call to method --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<Patient> listOfAllPatients = patientRepository.findAll();

        if(listOfAllPatients.isEmpty()) {
            logger.info("### No patient in database");
            return new ArrayList<>();
        }

        logger.info("### Retrieved list of all patients successfully");

        return listOfAllPatients;
    }


    // ======= READ ===========================================================

    @Override
    public Patient readPatient(Integer id) {
        logger.debug("##### Call to method --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        Patient existingPatient = patientRepository.findById(id).orElseThrow(() -> {
            logger.error("# Failed to retrieve patient --> id={} is unknown", id);
            throw new PatientNotFoundException("Patient not found");
        });

        logger.info("### Retrieved patient successfully");

        return existingPatient;
    }


    // ======= UPDATE =========================================================

    @Override
    public Patient updatePatient(Integer id, Patient patientToUpdate) {
        logger.debug("##### Call to method --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        // Checks if patient with this id already exists in db
        patientRepository.findById(id).orElseThrow(() -> {
            logger.error("# Failed to retrieve patient --> id={} is unknown", id);
            throw new PatientNotFoundException("Patient not found");
        });
        patientToUpdate.setId(id);
        Patient updatedPatient = patientRepository.save(patientToUpdate);

        logger.info("### Updated patient successfully");

        return updatedPatient;
    }

}
