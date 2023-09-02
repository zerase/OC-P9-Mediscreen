package com.mediscreen.clientui.controller;

import com.mediscreen.clientui.beans.AssessmentBean;
import com.mediscreen.clientui.services.AssessmentUiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AssessmentUiController {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentUiController.class);

    private final AssessmentUiService assessmentUiService;

    @Autowired
    public AssessmentUiController(AssessmentUiService assessmentUiService) {
        this.assessmentUiService = assessmentUiService;
    }

    // === REPORT ASSESSMENT OF PATIENT =======================================
    @GetMapping({"/assess/{id}"})
    public String getPatientAssessment(@PathVariable("id") Integer patientId, Model model) {
        logger.debug("### Request called --> GET /assess/{}", patientId);

        AssessmentBean assessment = assessmentUiService.retrieveAssessmentOfPatient(patientId);

        model.addAttribute("assessment", assessment);

        logger.info("### Assessment report of patientId={} returned --> {}", patientId, assessment);
        return "assessment/assess";
    }

}
