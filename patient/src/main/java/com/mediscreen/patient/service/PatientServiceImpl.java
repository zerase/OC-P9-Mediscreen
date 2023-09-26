package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.PatientAlreadyExistsException;
import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the CRUD operations related to the patient business logic.
 */
@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    /**
     * Component that handles database-related operations for patients.
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
        logger.debug("### Try to save in database new patient --> {}", patientToCreate);

        // Checks if patient with given data already exists in database
        if(patientRepository.existsByLastNameAndFirstNameAndDateOfBirth(
                patientToCreate.getLastName(),
                patientToCreate.getFirstName(),
                patientToCreate.getDateOfBirth())) {
            logger.error("### Failed to save patient {}", patientToCreate);
            throw new PatientAlreadyExistsException("Patient already exists");
        }

        Patient createdPatient = patientRepository.save(patientToCreate);

        logger.info("### New Patient saved --> {}", createdPatient);
        return createdPatient;
    }

    // === READ ALL ===========================================================
    @Override
    public List<Patient> readAllPatients() {
        logger.debug("### Try to retrieve all patients from database");

        List<Patient> listOfAllPatients = patientRepository.findAll();

        if (listOfAllPatients.isEmpty()) {
            logger.info("### No patient found from database");
            return new ArrayList<>();
        }

        logger.info("### List of patients retrieved --> {}", listOfAllPatients);
        return listOfAllPatients;
    }

    // === READ ALL FILTERED BY LAST NAME =====================================
    @Override
    public List<Patient> readAllPatientsByLastName(String keyword) {
        logger.debug("### Try to retrieve all patients from database which last name contains '{}'", keyword);

        List<Patient> listOfAllPatients = patientRepository.findAllByLastNameIgnoreCase(keyword);

        if (listOfAllPatients.isEmpty()) {
            logger.info("### No patient found containing '{}'", keyword);
            return new ArrayList<>();
        }

        logger.info("### List of patients containing '{}' as a last name retrieved --> {}", keyword, listOfAllPatients);
        return listOfAllPatients;
    }

    // === READ ===============================================================
    @Override
    public Patient readPatient(Integer patientId) {
        logger.debug("### Try to retrieve from database patient with id={}", patientId);

        Patient retrievedPatient = patientRepository.findById(patientId).orElseThrow(() -> {
            logger.error("### Failed to retrieve patient with id={} from database", patientId);
            throw new PatientNotFoundException("Patient not found or doesn't exist");
        });

        logger.info("### Retrieved patient with id={} --> {}", patientId, retrievedPatient);
        return retrievedPatient;
    }

    // === UPDATE =============================================================
    @Override
    public Patient updatePatient(Integer patientId, Patient patientToUpdate) {
        logger.debug("### Try to update in database patient with id={}", patientId);

        // Checks if patient with given id already exists in database
        patientRepository.findById(patientId).orElseThrow(() -> {
            logger.error("### Failed to retrieve patient with id={}", patientId);
            throw new PatientNotFoundException("Patient not found or doesn't exist");
        });
        // Saves updated data
        patientToUpdate.setId(patientId);
        Patient updatedPatient = patientRepository.save(patientToUpdate);

        logger.info("### Updated patient with id={} --> {}", patientId, updatedPatient);
        return updatedPatient;
    }

    // === DELETE =============================================================
    @Override
    public void deletePatient(Integer patientId) {
        logger.debug("### Try to delete from database patient with id={}", patientId);

        // Checks if given id exists in database before deleting
        if(patientRepository.existsById(patientId)) {
            patientRepository.deleteById(patientId);
            logger.info("### Deleted patient with id={}", patientId);
        } else {
            logger.error("### Failed to delete patient with id={}", patientId);
            throw new PatientNotFoundException("Patient not found or doesn't exist");
        }
    }

}
