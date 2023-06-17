package com.mediscreen.clientui.controller;

import com.mediscreen.clientui.beans.PatientBean;
import com.mediscreen.clientui.proxies.PatientMicroserviceProxy;
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
public class ClientUiController {

    private static final Logger logger = LoggerFactory.getLogger(ClientUiController.class);

    private final PatientMicroserviceProxy patientProxy;

    @Autowired
    public ClientUiController(PatientMicroserviceProxy patientProxy) {
         this.patientProxy = patientProxy;
    }


    // ========================================================================

    @GetMapping("/patient/add")
    public String showAddForm(Model model) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        model.addAttribute("patient", new PatientBean());
        logger.info("### Form to add new patient loaded");

        return "/patient/add";
    }


    @PostMapping("/patient/validate")
    public String validatePatient(@Valid PatientBean patientBean, BindingResult result) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        if(result.hasErrors()) {
            return "patient/add";
        } else {
            patientProxy.addPatient(patientBean);
            return "redirect:/patient/list";
        }
    }


    // ========================================================================

    @GetMapping("/patient/list")
    public String getPatientList(Model model) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<PatientBean> patientsList = patientProxy.getAllPatients();
        model.addAttribute("patientsList", patientsList);
        logger.info("### Patients list returned successfully --> {}", patientsList);

        return "patient/list";
    }


    @GetMapping("/patient/get/{id}")
    public String getPatientById(@PathVariable("id") Integer id, Model model) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        PatientBean patient = patientProxy.getPatient(id);
        model.addAttribute("patient", patient);
        logger.info("### Patient returned successfully --> {}", patient);

        return "patient/get";
    }


    // ========================================================================

    @GetMapping("/patient/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        PatientBean patient = patientProxy.getPatient(id);
        model.addAttribute("patient", patient);
        logger.info("### Patient update page returned successfully --> {}", patient);

        return "patient/update";
    }


    @PostMapping("/patient/update/{id}")
    public String updatePatient(@PathVariable("id") Integer id, @Valid PatientBean patient, BindingResult result) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        if(result.hasErrors()){
            return "patient/update";
        } else {
            patientProxy.updatePatient(id, patient);
            logger.info("### Patient updated");
            return "redirect:/patient/list";
        }
    }

    // ========================================================================

    @GetMapping("/patient/delete/{id}")
    public String deletePatient(@PathVariable("id") Integer id) {
        logger.debug("##### Call to request --> {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        patientProxy.deletePatient(id);
        logger.info("### Patient deleted successfully");

        return "redirect:/patient/list";
    }

}
