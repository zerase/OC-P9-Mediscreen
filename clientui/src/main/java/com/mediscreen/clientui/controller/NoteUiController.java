package com.mediscreen.clientui.controller;

import com.mediscreen.clientui.beans.NoteDto;

import com.mediscreen.clientui.services.NoteUiService;
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
public class NoteUiController {

    private static final Logger logger = LoggerFactory.getLogger(NoteUiController.class);

    private final NoteUiService noteUiService;

    @Autowired
    public NoteUiController(NoteUiService noteUiService) {
        this.noteUiService = noteUiService;
    }


    // === LIST ALL NOTES OF ALL PATIENTS =====================================
    @GetMapping("/notes/all")
    public String displayAllNotes(Model model) {
        logger.debug("### Request called --> GET /notes/all");

        List<NoteDto> allNotesList = noteUiService.retrieveAllNotes();

        logger.info("### Notes list returned --> {}", allNotesList);
        model.addAttribute("notes", allNotesList);

        return "note/all-notes";
    }

    // === LIST ALL NOTES OF ONE PATIENT ======================================
    @GetMapping({"/notes/list/{id}"})
    public String displayNotesOfPatient(@PathVariable("id") final Integer patientId, final Model model) {
        logger.debug("### Request called --> GET /notes/list/{id}");

        List<NoteDto> allNotesOfPatientList = noteUiService.retrieveAllNotesOfPatient(patientId);

        model.addAttribute("patientId", patientId);
        model.addAttribute("notes", allNotesOfPatientList);

        logger.info("### Notes list of patientId={} returned --> {}", patientId, allNotesOfPatientList);
        return "note/list";
    }

    // === UPDATE NOTE ========================================================
    @GetMapping({"/notes/update/{id}"})
    public String displayUpdateNoteForm(@PathVariable("id") final String noteId, final Model model) {
        logger.debug("### Request called --> GET /notes/update/{id}");

        NoteDto noteDto = noteUiService.retrieveNoteById(noteId);

        model.addAttribute("noteDto", noteDto);

        logger.info("### Update form for note {} returned successfully", noteDto);
        return "note/update";
    }

    @PostMapping({"/notes/update/{id}"})
    public String validateUpdateNoteForm(
            @PathVariable("id") final String noteId,
            @Valid final NoteDto noteDto,
            final BindingResult result) {

        logger.info("### Request called --> POST /notes/update/{id}");

        if (result.hasErrors()) {
            return "note/update";
        } else {
            noteUiService.updateNote(noteId, noteDto);

            logger.info("### Note updated");
            return "redirect:/notes/list/" + noteDto.getPatientId();
        }
    }

    // === DELETE NOTE ========================================================
    @GetMapping({"/notes/delete/{id}/{patientId}"})
    public String deleteNote(
            @PathVariable("id") final String noteId,
            @PathVariable("patientId") final Integer patientId) {

        logger.debug("### Request called --> GET /notes/delete/{id}/{patientId}");

        noteUiService.deleteNote(noteId);
        logger.info("### Note deleted successfully");

        return "redirect:/notes/list/" + patientId;

    }

    // === ADD NEW NOTE =======================================================
    @GetMapping({"/notes/add/{id}"})
    public String displayAddNoteForm(@PathVariable("id") final Integer patientId, final Model model) {
        logger.debug("### Request called --> GET /notes/add/{id}");

        NoteDto noteDto = new NoteDto();
        noteDto.setPatientId(patientId);
        //model.addAttribute("date", LocalDate.now());
        model.addAttribute("noteDto", noteDto);

        logger.info("### Form to add new note loaded");

        return "note/add";
    }


    @PostMapping({"/notes/validate"})
    public String validateAddNewNoteForm(@Valid final NoteDto noteDto, final BindingResult result) {
        logger.debug("### Request called--> POST /notes/validate");

        if (result.hasErrors()) {
            return "note/add";
        } else {
            noteUiService.createNote(noteDto);

            logger.info("### New note created successfully");
            return "redirect:/notes/list/" + noteDto.getPatientId();
        }
    }

}
