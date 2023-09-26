package com.mediscreen.clientui.services;

import com.mediscreen.clientui.beans.PatientBean;
import com.mediscreen.clientui.proxies.NoteProxy;
import com.mediscreen.clientui.proxies.PatientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientUiService {

    private static final Logger logger = LoggerFactory.getLogger(PatientUiService.class);

    private final PatientProxy patientProxy;
    private final NoteProxy noteProxy;

    @Autowired
    public PatientUiService(PatientProxy patientProxy, NoteProxy noteProxy) {
        this.patientProxy = patientProxy;
        this.noteProxy = noteProxy;
    }


    // === FETCH ALL PATIENTS =================================================
    public List<PatientBean> fetchAllPatients(String nameSearched) {
        logger.debug("### Try to fetch all patients");

        List<PatientBean> allPatients = patientProxy.getAllPatients(nameSearched);

        if(allPatients == null) {
            logger.info("### Fetched no patients");
            return new ArrayList<>();
        }

        logger.info("### All fetched patients --> {}", allPatients);
        return allPatients;
    }

    // === FETCH ONE PATIENT ==================================================
    public PatientBean fetchPatient(Integer id) {
        logger.debug("### Try to fetch patient with id={}", id);

        PatientBean patient = patientProxy.getPatientById(id);

        logger.info("### Fetched patient --> {}", patient);
        return patient;
    }

    // === UPDATE PATIENT =====================================================
    public PatientBean updatePatient(Integer id, PatientBean patientToUpdate) {
        logger.debug("### Try to update patient with id={}", id);

        PatientBean updatedPatient = patientProxy.updatePatientById(id, patientToUpdate);

        logger.info("### Updated patient --> {}", updatedPatient);
        return updatedPatient;
    }

    // === CREATE NEW PATIENT =================================================
    public PatientBean createNewPatient(PatientBean patientToCreate) {
        logger.debug("### Try to create new patient --> {}", patientToCreate);

        PatientBean createdPatient = patientProxy.addNewPatient(patientToCreate);

        logger.info("### Created new patient --> {}", createdPatient);
        return createdPatient;
    }

    // === DELETE PATIENT =====================================================
    public void deletePatient(Integer id) {
        logger.debug("### Try to delete patient with id={}", id);

        patientProxy.deletePatientById(id);
        noteProxy.deleteAllNotesByPatientId(id);

        logger.info("### Patient deleted");
    }

}
