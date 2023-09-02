package com.mediscreen.clientui.services;

import com.mediscreen.clientui.beans.PatientBean;
import com.mediscreen.clientui.proxies.PatientMicroserviceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientUiService {

    private static final Logger logger = LoggerFactory.getLogger(PatientUiService.class);

    private PatientMicroserviceProxy patientProxy;

    @Autowired
    public PatientUiService(PatientMicroserviceProxy patientProxy) {
        this.patientProxy = patientProxy;
    }


    // === RETRIEVE ALL PATIENTS ==============================================
    public List<PatientBean> retrieveAllPatients() {
        logger.debug("### Try to retrieve all patients");

        List<PatientBean> allPatients = patientProxy.getAllPatients();

        if(allPatients == null) {
            logger.info("### Retrieved no patients");
            return new ArrayList<>();
        }

        logger.info("### Retrieved all patients successfully");
        return allPatients;
    }

    // === RETRIEVE ONE PATIENT BY ID =========================================
    public PatientBean retrievePatient(Integer id) {
        logger.debug("### Try to retrieve patient with id={}", id);

        PatientBean patientBean = patientProxy.getPatient(id);

        logger.info("### Retrieved patient successfully");
        return patientBean;
    }

    // === UPDATE PATIENT =====================================================
    public PatientBean updatePatient(Integer id, PatientBean patientBean) {
        logger.debug("### Try to update patient with id={}", id);

        PatientBean patientToUpdate = patientProxy.updatePatient(id, patientBean);

        logger.info("### Update patient with id={} successfully", id);
        return patientToUpdate;
    }

    // === CREATE NEW PATIENT =================================================
    public PatientBean createPatient(PatientBean patientBean) {
        logger.debug("### Try to create new patient {}", patientBean);

        PatientBean patientToCreate = patientProxy.addPatient(patientBean);

        logger.info("### Created patient {} successfully", patientBean);
        return patientToCreate;
    }

    // === DELETE PATIENT =====================================================
    public void deletePatient(Integer id) {
        logger.debug("### Try to delete patient with id={}", id);

        patientProxy.deletePatient(id);
        logger.info("### Patient with id={} deleted successfully", id);
    }

}
