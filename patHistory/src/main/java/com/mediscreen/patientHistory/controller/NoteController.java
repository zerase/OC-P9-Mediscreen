package com.mediscreen.patientHistory.controller;

import com.mediscreen.patientHistory.model.Note;
import com.mediscreen.patientHistory.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Manages endpoints of the Note API.
 */
@RestController
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    /**
     * A component that handles business logic operations for notes related to patients.
     */
    private final NoteService noteService;

    /**
     * Instantiates a new note controller.
     *
     * @param noteService  the service linked to note business logic
     */
    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    // === ADD NOTE ===========================================================

    /**
     * Adds note related to a patient to database.
     *
     * @param noteToAdd  the note related to a patient to add to database
     * @return           the newly created note and status of the request
     */
    @PostMapping("/patHistories")
    public ResponseEntity<Note> addNote(@Valid @RequestBody Note noteToAdd) {
        logger.debug("### Request called --> POST /patHistories");

        Note addedNote = noteService.createNote(noteToAdd);

        logger.info("### Note added --> {}", addedNote);
        return new ResponseEntity<>(addedNote, HttpStatus.CREATED);
    }

    // === GET ALL NOTES OF ALL PATIENTS ======================================

    /**
     * Lists all patients notes in database.
     *
     * @return  the list of all notes of all patients and status of the request
     */
    @GetMapping("/patHistories")
    public ResponseEntity<List<Note>> getAllNotes() {
        logger.debug("### Request called --> GET /patHistories");

        List<Note> allNotesList = noteService.readAllNotes();

        if(allNotesList.isEmpty()) {
            logger.info("Empty list of notes returned --> {}", allNotesList);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("### List of all notes returned --> {}", allNotesList);
        return new ResponseEntity<>(allNotesList, HttpStatus.OK);
    }

    // === GET ALL NOTES OF ONE PATIENT BY HIS ID =============================

    /**
     * Lists all notes related to a given patient.
     *
     * @param patientId  the id of the patient to retrieve the notes from database
     * @return           the list of all notes of the patient with the given id and status of the request
     */
    @GetMapping("/patHistories/patientId/{id}")
    public ResponseEntity<List<Note>> getAllNotesByPatientId(@PathVariable("id") Integer patientId) {
        logger.debug("### Request called --> GET /patHistories/patientId/{}", patientId);

        List<Note> patientNotes = noteService.readAllNotesByPatientId(patientId);

        logger.info("### List of notes of patientId={} returned --> {}", patientId, patientNotes);
        return new ResponseEntity<>(patientNotes, HttpStatus.OK);
    }

    // === GET NOTE BY ID =====================================================

    /**
     * Gets note with the given id which related to a patient.
     *
     * @param noteId  the id of the note to retrieve from database
     * @return        the note with the given id and status of the request
     */
    @GetMapping("/patHistories/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") String noteId) {
        logger.debug("### Request called --> GET patHistories/{}", noteId);

        Note note = noteService.readNoteById(noteId);

        logger.info("### Note returned --> {}", note);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    // === UPDATE NOTE ========================================================

    /**
     * Updates the note with the given id.
     *
     * @param noteId  the id of the note to update
     * @param note    the note which content have to be updated in database
     * @return        the note with the given id updated and status of the request
     */
    @PutMapping("/patHistories/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") String noteId, @Valid @RequestBody Note note) {
        logger.debug("### Request called --> PUT /patHistories/{id}");

        Note noteUpdated = noteService.updateNote(noteId, note);

        logger.info("### Updated note returned --> {}", noteUpdated);
        return new ResponseEntity<>(noteUpdated, HttpStatus.OK);
    }

    // === DELETE NOTE ========================================================

    /**
     * Delete note with the given id.
     *
     * @param noteId  the id of the note which data is to be deleted from database
     * @return        the status of the request
     */
    @DeleteMapping("/patHistories/{id}")
    public ResponseEntity<Note> deleteNote(@PathVariable("id") String noteId) {
        logger.info("### Request called --> DELETE /patHistories/{id}");

        noteService.deleteNote(noteId);

        logger.info("### Deleted note with id={}", noteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
