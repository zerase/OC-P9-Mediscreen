package com.mediscreen.clientui.controller;

import com.mediscreen.clientui.beans.PatientBean;
import com.mediscreen.clientui.services.PatientUiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class PatientUiController {

    private static final Logger logger = LoggerFactory.getLogger(PatientUiController.class);

    private PatientUiService patientUiService;

    @Autowired
    public PatientUiController(PatientUiService patientUiService) {
        this.patientUiService = patientUiService;
    }


    // === LIST ALL PATIENTS ==================================================
    @GetMapping("/patient/list")
    public String getPatientsList(Model model){
        logger.debug("### Request called --> GET /patient/list");

        try {
            List<PatientBean> patientsList = patientUiService.retrieveAllPatients();

            logger.info("### Patients list returned --> {}", patientsList);
            model.addAttribute("patientsList", patientsList);

        } catch (Exception e) {
            logger.error("### An error occurred --> " + e);
            model.addAttribute("message", e.getMessage());
        }

        return "patient/list";
    }

    // ==== SHOW ONE PATIENT DETAILS ==========================================
    @GetMapping("/patient/get/{id}")
    public String getPatientById(@PathVariable("id") Integer id, Model model) {
        logger.debug("### Request called --> GET /patient/get/{id}");

        PatientBean patient = patientUiService.retrievePatient(id);
        model.addAttribute("patient", patient);
        logger.info("### Patient returned successfully --> {}", patient);

        return "patient/get";
    }

    // ==== UPDATE PATIENT ====================================================
    @GetMapping("/patient/update/{id}")
    public String displayUpdatePatientForm(@PathVariable("id") Integer id, Model model) {
        logger.debug("### Request called --> GET /patient/update/{id}");

        try {
            PatientBean patient = patientUiService.retrievePatient(id);

            model.addAttribute("patientBean", patient);
            logger.info("### Update form for patient {} returned successfully", patient);

        } catch (Exception e) {
            logger.error("### An error occurred --> " + e);
            model.addAttribute("message", e.getMessage());
        }

        return "patient/update";
    }

    @PostMapping("/patient/update/{id}")
    public String updatePatient(@PathVariable("id") Integer id, @Valid PatientBean patientBean, BindingResult result) {
        logger.debug("### Request called --> POST /patient/update/{id}");

        if(result.hasErrors()){
            return "patient/update";
        } else {
            patientUiService.updatePatient(id, patientBean);
            logger.info("### Patient updated");
            return "redirect:/patient/list";
        }
    }

    // ==== ADD NEW PATIENT ===================================================
    @GetMapping("/patient/add")
    public String displayAddPatientForm(Model model) {
        logger.debug("### Request called --> GET /patient/add");

        model.addAttribute("patientBean", new PatientBean());
        logger.info("### Form to add new patient loaded");

        return "/patient/add";
    }

    @PostMapping("/patient/validate")
    public String validateNewPatientForm(@Valid PatientBean patientBean, BindingResult result) {
        logger.debug("### Request called --> POST /patient/validate");

        if(result.hasErrors()) {
            return "patient/add";
        } else {
            patientUiService.createPatient(patientBean);
            return "redirect:/patient/list";
        }
    }

    // ==== DELETE PATIENT ====================================================
    @GetMapping("/patient/delete/{id}")
    public String deletePatient(@PathVariable("id") Integer id) {
        logger.debug("### Request called --> GET /patient/delete/{id}");

        patientUiService.deletePatient(id);
        logger.info("### Patient deleted successfully");

        return "redirect:/patient/list";
    }

}
