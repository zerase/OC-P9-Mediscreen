package com.mediscreen.clientui.services;

import com.mediscreen.clientui.beans.AssessmentBean;
import com.mediscreen.clientui.proxies.AssessmentProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessmentUiService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentUiService.class);

    private final AssessmentProxy assessmentProxy;

    @Autowired
    public AssessmentUiService(AssessmentProxy assessmentProxy) {
        this.assessmentProxy = assessmentProxy;
    }

    // === RETRIEVE ASSESSMENT OF ONE PATIENT =================================
    public AssessmentBean retrieveAssessmentOfPatient(Integer patientId) {
        logger.debug("### Try to retrieve assessment of patientId={}", patientId);

        AssessmentBean assessmentOfPatient = assessmentProxy.getPatientAssessment(patientId);

        logger.info("### Retrieved assessment successfully");
        return  assessmentOfPatient;
    }
}
