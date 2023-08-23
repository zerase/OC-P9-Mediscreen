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
import java.util.Optional;

/**
 * Implements the CRUD operations related to the patient business logic.
 */
@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    /**
     * A component that handles database-related operations for patients.
     */
    private final PatientRepository patientRepository;

    /**
     * Instantiates a new patient service.
     *
     * @param patientRepository  the repository linked to patient data
     */
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }


    // === CREATE =============================================================
    @Override
    public Patient createPatient(Patient patientToCreate) {
        logger.debug("### Try to save in repository new patient --> {}", patientToCreate);

        Patient createdPatient = patientRepository.save(patientToCreate);

        logger.info("### New patient saved successfully");
        return createdPatient;
    }

    // === READ ALL ===========================================================
    @Override
    public List<Patient> readAllPatients() {
        logger.debug("### Try to retrieve all patients from repository");

        List<Patient> listOfAllPatients = patientRepository.findAll();

        if (listOfAllPatients.isEmpty()) {
            logger.info("### No patient found from repository");
            return new ArrayList<>();
        }

        logger.info("### Retrieved list of all patients successfully");
        return listOfAllPatients;
    }

    // === READ ===============================================================
    @Override
    public Patient readPatient(Integer patientId) {
        logger.debug("### Try to retrieve from repository patient with id={}", patientId);

        Optional<Patient> patient = patientRepository.findById(patientId);

        if(patient.isEmpty()) {
            logger.error("### Failed to retrieve patient with id={} from repository", patientId);
            throw new PatientNotFoundException("Patient not found with id=" + patientId);
        }

        logger.info("### Retrieved patient with id={} successfully", patientId);
        return patient.get();
    }

    // === UPDATE =============================================================
    @Override
    public Patient updatePatient(Integer patientId, Patient patientToUpdate) {
        logger.debug("### Try to update in repository patient with id={}", patientId);

        // Checks if patient with given id already exists in repository
        patientRepository.findById(patientId).orElseThrow(() -> {
            logger.error("### Failed to retrieve patient with id={}", patientId);
            throw new PatientNotFoundException("Patient not found with id=" + patientId);
        });
        patientToUpdate.setId(patientId);
        Patient updatedPatient = patientRepository.save(patientToUpdate);

        logger.info("### Updated patient with id={} successfully", patientId);
        return updatedPatient;
    }

    // ======= DELETE =========================================================
    @Override
    public void deletePatient(Integer patientId) {
        logger.debug("### Try to delete from repository patient with id={}", patientId);

        Optional<Patient> patientToDelete = patientRepository.findById(patientId);
        if(patientToDelete.isPresent()) {
            patientRepository.delete(patientToDelete.get());
            logger.info("### Deleted patient successfully");
        } else {
            logger.error("### Failed to delete patient with id={}", patientId);
            throw new PatientNotFoundException("Patient not found");
        }
    }

}
