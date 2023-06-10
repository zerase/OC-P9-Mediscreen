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

@RestController
@RequestMapping("/patient/*")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    // ========================================================================

    @PostMapping("/add")
    public ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient patientToAdd) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        Patient patientAdded = patientService.createPatient(patientToAdd);
        logger.info("### Added patient --> {}", patientAdded);

        return new ResponseEntity<>(patientAdded, HttpStatus.CREATED);
    }


    // ========================================================================

    @GetMapping("/list")
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.debug("##### Call to request --> {}()", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<Patient> listPatients = patientService.readAllPatients();
        logger.info("### List of patients returned --> {}", listPatients.toString());

        return new ResponseEntity<>(listPatients, HttpStatus.OK);
    }


    // ========================================================================

    @GetMapping("/get/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable("id") Integer id) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        Patient patient = patientService.readPatient(id);
        logger.info("### Patient returned --> {}", patient);

        return new ResponseEntity<>(patient, HttpStatus.OK);
    }


    // ========================================================================

    @PutMapping("/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Integer id, @Valid @RequestBody Patient patient) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        Patient patientUpdated = patientService.updatePatient(id, patient);
        logger.info("### Updated patient returned --> {}", patientUpdated);

        return new ResponseEntity<>(patientUpdated, HttpStatus.OK);
    }

}
