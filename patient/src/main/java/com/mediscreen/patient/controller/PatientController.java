package com.mediscreen.patient.controller;

import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
