package com.mediscreen.clientui.controller;

import com.mediscreen.clientui.beans.AssessmentBean;
import com.mediscreen.clientui.beans.NoteBean;
import com.mediscreen.clientui.beans.PatientBean;
import com.mediscreen.clientui.services.AssessmentUiService;
import com.mediscreen.clientui.services.NoteUiService;
import com.mediscreen.clientui.services.PatientUiService;
import feign.FeignException;
import feign.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ClientUiController {

    private static final Logger logger = LoggerFactory.getLogger(ClientUiController.class);

    private final PatientUiService patientUiService;
    private final NoteUiService noteUiService;
    private final AssessmentUiService assessmentUiService;

    @Autowired
    public ClientUiController(PatientUiService patientUiService, NoteUiService noteUiService, AssessmentUiService assessmentUiService) {
        this.patientUiService = patientUiService;
        this.noteUiService = noteUiService;
        this.assessmentUiService = assessmentUiService;
    }


    // === HOMEPAGE ===========================================================
    @GetMapping("/")
    public String displayLandingPage() {
        logger.debug("### Request called --> GET /");
        logger.info("### Display landing page");
        return "home";
    }

    @GetMapping("/home")
    public String displayHomePage() {
        logger.debug("### Request called --> GET /home");
        return "redirect:/";
    }

    // === LIST ALL PATIENTS PAGE =============================================
    @GetMapping("/patients")
    public String displayAllPatients(@Param("search") String search, Model model){
        logger.debug("### Request called --> GET /patients?search={}", search);

        try {
            List<PatientBean> patientsList = patientUiService.fetchAllPatients(search);

            model.addAttribute("patientsList", patientsList);
            model.addAttribute("nameSearched", search);

            logger.info("### List of all patients returned successfully. Display patients list page");
            return "patients-list";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // === PATIENT DETAILS PAGE ===============================================
    @GetMapping("/patients/details/{id}")
    public String displayDetailedPatientInformation(@PathVariable("id") Integer id, Model model) {
        logger.debug("### Request called --> GET /patients/details/{}", id);

        try {
            PatientBean patient = patientUiService.fetchPatient(id);
            List<NoteBean> allNotesOfPatientList = noteUiService.fetchAllNotesOfPatient(id);
            AssessmentBean assessment = assessmentUiService.retrieveAssessmentOfPatient(id);

            model.addAttribute("patient", patient);
            model.addAttribute("patientId", id);
            model.addAttribute("notes", allNotesOfPatientList);
            model.addAttribute("assessment", assessment);

            logger.info("### Patient, Notes list and Diabetes assessment returned successfully. Display patient details page");
            return "patient-details";

        } catch (FeignException.NotFound e) {
            logger.error("### Exception thrown --> {}", e.getMessage());
            logger.info("### Display error 404 page");
            return "404";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // === UPDATE PATIENT PAGE ================================================
    @GetMapping("/patients/update/{id}")
    public String displayUpdatePatientForm(@PathVariable("id") Integer id, Model model) {
        logger.debug("### Request called --> GET /patients/update/{}", id);

        try {
            PatientBean patientToUpdate = patientUiService.fetchPatient(id);

            model.addAttribute("patientBean", patientToUpdate);

            logger.info("### Data of patient to update returned successfully. Display form to update patient");
            return "patient-update-form";

        } catch (FeignException.NotFound e) {
            logger.error("### Exception thrown --> {}", e.getMessage());
            logger.info("### Display error 404 page");
            return "404";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    @PostMapping("/patients/update/{id}/validate")
    public String validateUpdatePatientForm(@PathVariable("id") Integer id, @Valid PatientBean patientToUpdate, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.debug("### Request called --> POST /patients/update/{}/validate", id);

        try {
            if (result.hasErrors()) {
                logger.info("### Display form to update patient with {} error(s)", result.getErrorCount());
                return "patient-update-form";
            } else {
                patientUiService.updatePatient(id, patientToUpdate);

                String message = "Patient has been updated successfully";
                redirectAttributes.addFlashAttribute("msgAlert", message);

                logger.info("### Successful message returned --> {}", message);
                return "redirect:/patients/details/{id}";
            }
        } catch (FeignException.NotFound e) {
            logger.error("### Exception thrown --> {}", e.getMessage());
            logger.info("### Display error 404 page");
            return "404";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // ==== ADD NEW PATIENT PAGE ==============================================
    @GetMapping("/patients/new")
    public String displayAddNewPatientForm(Model model) {
        logger.debug("### Request called --> GET /patients/new");

        model.addAttribute("patientBean", new PatientBean());

        logger.info("### Display form to add new patient");
        return "patient-add-form";
    }

    @PostMapping("/patients/new/validate")
    public String validateAddNewPatientForm(@Valid PatientBean patientToCreate, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("### Request called --> POST /patients/new/validate");

        try {
            if (result.hasErrors()) {
                logger.info("### Display form to add new patient with {} error(s)", result.getErrorCount());
                return "patient-add-form";
            } else {
                patientUiService.createNewPatient(patientToCreate);

                String message = "New patient has been saved successfully";
                redirectAttributes.addFlashAttribute("msgAlert", message);

                logger.info("### Successful message returned --> {}", message);
                return "redirect:/patients";
            }
        } catch (FeignException.Conflict e) {
            logger.error("### Exception thrown --> {}", e.getMessage());

            String message = "This patient already exists";
            model.addAttribute("msgAlert", message);

            logger.info("### Display form to add new patient with error message --> {}", message);
            return "patient-add-form";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // === DELETE PATIENT =====================================================
    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        logger.debug("### Request called --> GET /patients/delete/{}", id);

        try {
            patientUiService.deletePatient(id);

            String message = "Patient has been deleted successfully";
            redirectAttributes.addFlashAttribute("msgAlert", message);

            logger.info("### Successful message returned --> {}", message);
            return "redirect:/patients";

        } catch (FeignException.NotFound e) {
            logger.error("### Exception thrown --> {}", e.getMessage());
            logger.info("### Display error 404 page");
            return "404";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // === ADD NEW NOTE PAGE ==================================================
    @GetMapping("/patients/{id}/notes/new")
    public String displayAddNewNoteForm(@PathVariable("id") Integer patientId, Model model) {
        logger.debug("### Request called --> GET /patients/{}/notes/new", patientId);

        NoteBean noteBean = new NoteBean();
        noteBean.setPatientId(patientId);
        model.addAttribute("noteBean", noteBean);

        logger.info("### Display form to add new note");
        return "note-add-form";
    }

    @PostMapping("/notes/new/validate")
    public String validateAddNewNoteForm(@Valid NoteBean noteToCreate, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.debug("### Request called--> POST /notes/new/validate");

        try {
            if (result.hasErrors()) {
                logger.info("### Display form to add new note with {} error(s)", result.getErrorCount());
                return "note-add-form";
            } else {
                noteUiService.createNewNote(noteToCreate);

                String message = "New note has been saved successfully";
                redirectAttributes.addFlashAttribute("msgAlert", message);

                logger.info("### Successful message returned --> {}", message);
                return "redirect:/patients/details/" + noteToCreate.getPatientId();
            }
        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // === UPDATE NOTE PAGE ===================================================
    @GetMapping("/notes/update/{id}")
    public String displayUpdateNoteForm(@PathVariable("id") String noteId, Model model) {
        logger.debug("### Request called --> GET /notes/update/{}", noteId);

        try {
            NoteBean noteBean = noteUiService.fetchNoteById(noteId);

            model.addAttribute("noteBean", noteBean);

            logger.info("### Data of note to update returned successfully. Display form to update note");
            return "note-update-form";

        } catch (FeignException.NotFound e) {
            logger.error("### Exception thrown --> {}", e.getMessage());
            logger.info("### Display error 404 page");
            return "404";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    @PostMapping({"/notes/update/{id}"})
    public String validateUpdateNoteForm(@PathVariable("id") String noteId, @Valid NoteBean noteBean, BindingResult result, RedirectAttributes redirectAttributes) {
        logger.info("### Request called --> POST /notes/update/{id}");

        try {
            if (result.hasErrors()) {
                logger.info("### Display form to update note with {} error(s)", result.getErrorCount());
                return "note-update-form";
            } else {
                noteUiService.updateNote(noteId, noteBean);

                String message = "Note has been updated successfully";
                redirectAttributes.addFlashAttribute("msgAlert", message);

                logger.info("### Successful message returned --> {}", message);
                return "redirect:/patients/details/" + noteBean.getPatientId();
            }
        } catch (FeignException.NotFound e) {
            logger.error("### Exception thrown --> {}", e.getMessage());
            logger.info("### Display error 404 page");
            return "404";

        } catch (FeignException.InternalServerError e) {
            logger.error("### An error occurred --> {}", e.getMessage());
            logger.info("### Display error 500 page");
            return "500";
        }
    }

    // === DELETE NOTE PAGE ===================================================
    @GetMapping("/notes/delete/{id}/{patientId}")
    public String deleteNote(@PathVariable("id") String noteId, @PathVariable("patientId") Integer patientId, RedirectAttributes redirectAttributes) {
        logger.debug("### Request called --> GET /notes/delete/{}", noteId);

        noteUiService.deleteNote(noteId);

        String message = "Note has been deleted successfully";
        redirectAttributes.addFlashAttribute("msgAlert", message);

        logger.info("### Successful message returned --> {}", message);
        return "redirect:/patients/details/" + patientId;

    }
}
