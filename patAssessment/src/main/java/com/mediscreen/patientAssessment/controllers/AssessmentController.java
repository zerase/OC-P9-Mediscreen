package com.mediscreen.patientAssessment.controllers;

import com.mediscreen.patientAssessment.beans.AssessmentDTO;
import com.mediscreen.patientAssessment.services.AssessmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api")
public class AssessmentController {
    private static final Logger logger = LoggerFactory.getLogger(AssessmentController.class);

    private final AssessmentService assessmentService;

    @Autowired
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }


    // === GET ASSESSMENT BY PATIENT ID =======================================
    @GetMapping("/assess/{id}")
    public ResponseEntity<AssessmentDTO> getPatientAssessmentByPatientId(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> GET /assess/{}", patientId);

        AssessmentDTO patientAssessment = assessmentService.assessDiabetesRiskLevelByPatientId(patientId);

        logger.info("### Assessment report returned --> {}", patientAssessment);
        return new ResponseEntity<>(patientAssessment, HttpStatus.OK);
    }

}
