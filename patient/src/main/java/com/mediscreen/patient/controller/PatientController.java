package com.mediscreen.patient.controller;

import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Manages endpoints of the Patient API.
 */
//@CrossOrigin(origins = "http://localhost:8080")
@RestController
//@RequestMapping("/api/*")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    /**
     * A component that handles business logic operations for patients.
     */
    private final PatientService patientService;

    /**
     * Instantiates a new patient controller.
     *
     * @param patientService  the service linked to patient business logic
     */
    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    // === ADD PATIENT ========================================================

    /**
     * Adds patient information to database.
     *
     * @param patientToAdd  the patient information to add to database
     * @return              the newly created patient information and status of the request
     */
    @PostMapping("/patients")
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient patientToAdd) {
        logger.debug("### Request called --> POST /patients");

        Patient addedPatient = patientService.createPatient(patientToAdd);

        logger.info("### Patient added --> {}", addedPatient);
        return new ResponseEntity<>(addedPatient, HttpStatus.CREATED);
    }

    // === GET ALL PATIENTS ===================================================

    /**
     * Lists information of all patients in database.
     *
     * @return  the list of all patients with their information and status of the request
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.debug("### Request called --> GET /patients");

        List<Patient> allPatientsList = patientService.readAllPatients();

        if (allPatientsList.isEmpty()) {
            logger.info("### Empty list of patients returned --> {}", allPatientsList);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("### List of patients returned --> {}", allPatientsList);
        return new ResponseEntity<>(allPatientsList, HttpStatus.OK);
    }

    // === GET PATIENT ========================================================

    /**
     * Gets information of the patient with the given id.
     *
     * @param patientId  the id of the patient to retrieve information from database
     * @return           the information of the patient with the given id and status of the request
     */
    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> GET /patients/{}", patientId);

        Patient patient = patientService.readPatient(patientId);

        logger.info("### Patient returned --> {}", patient);
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    // === UPDATE PATIENT =====================================================

    /**
     * Updates information of the patient with the given id.
     *
     * @param patientId  the id of the patient to update
     * @param patient    the patient whose information have to be updated in database
     * @return           the updated information of the patient with the given id and status of the request
     */
    @PutMapping("/patients/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Integer patientId, @Valid @RequestBody Patient patient) {
        logger.debug("### Request called --> PUT /patients/{id}");

        Patient patientUpdated = patientService.updatePatient(patientId, patient);

        logger.info("### Updated patient returned --> {}", patientUpdated);
        return new ResponseEntity<>(patientUpdated, HttpStatus.OK);
    }

    // === DELETE PATIENT =====================================================

    /**
     * Delete information of the patient with the given id.
     *
     * @param patientId  the id of the patient whose data is to be deleted from database
     * @return           the status of the request
     */
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Patient> deletePatient(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> DELETE /patients/{id}");

        patientService.deletePatient(patientId);

        logger.info("### Patient with id={} deleted", patientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
