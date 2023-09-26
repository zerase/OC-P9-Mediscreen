package com.mediscreen.patientHistory.controller;

import com.mediscreen.patientHistory.model.Note;
import com.mediscreen.patientHistory.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     * Component that handles business logic operations for notes related to patients.
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
    @Operation(
            summary = "Create a new note",
            description = "Create and save a new Note object. The response is a Note object with an automatically generated id, patient identifier, date of creation, date of modification, content of the note."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad request - The query failed due to a validation error on an input.", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/patHistories")
    public ResponseEntity<Note> addNewNote(@Valid @RequestBody Note noteToAdd) {
        logger.debug("### Request called --> POST /patHistories");

        Note addedNote = noteService.createNote(noteToAdd);

        logger.info("### New note successfully added");
        return new ResponseEntity<>(addedNote, HttpStatus.CREATED);
    }

    // === GET ALL NOTES OF ONE PATIENT BY HIS ID =============================

    /**
     * Lists all notes related to a given patient.
     *
     * @param patientId  the id of the patient to retrieve the notes from database
     * @return           the list of all notes of the patient with the given id and status of the request
     */
    @Operation(summary = "Retrieve all notes related to a specific patient")
    @Parameters({
            @Parameter(name = "patientId")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "204")
    })
    @GetMapping("/patHistories")
    public ResponseEntity<List<Note>> getAllNotesByPatientId(@RequestParam(value = "patientId", required = true) Integer patientId) {
        logger.debug("### Request called --> GET /patHistories?patientId={}", patientId);

        List<Note> patientNotes = noteService.readAllNotesByPatientId(patientId);

        if(patientNotes.isEmpty()) {
            logger.info("### Empty list of notes returned");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("### Retrieved list of notes successfully");
        return new ResponseEntity<>(patientNotes, HttpStatus.OK);
    }

    // === GET NOTE BY ID =====================================================

    /**
     * Gets note with the given id which related to a patient.
     *
     * @param noteId  the id of the note to retrieve from database
     * @return        the note with the given id and status of the request
     */
    @Operation(summary = "Retrieve a note by id")
    @Parameters({
            @Parameter(name = "id", description = "Id of a specific note", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404"),
    })
    @GetMapping("/patHistories/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") String noteId) {
        logger.debug("### Request called --> GET patHistories/{}", noteId);

        Note retrievedNote = noteService.readNoteById(noteId);

        logger.info("### Note returned successfully");
        return new ResponseEntity<>(retrievedNote, HttpStatus.OK);
    }

    // === UPDATE NOTE ========================================================

    /**
     * Updates the note with the given id.
     *
     * @param noteId  the id of the note to update
     * @param note    the note which content have to be updated in database
     * @return        the note with the given id updated and status of the request
     */
    @Operation(summary = "Update an existing note by id")
    @Parameters({
            @Parameter(name = "id", description = "Id of a specific note", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PutMapping("/patHistories/{id}")
    public ResponseEntity<Note> updateNoteById(@PathVariable("id") String noteId, @Valid @RequestBody Note note) {
        logger.debug("### Request called --> PUT /patHistories/{}", noteId);

        Note noteUpdated = noteService.updateNote(noteId, note);

        logger.info("### Note updated successfully");
        return new ResponseEntity<>(noteUpdated, HttpStatus.OK);
    }

    // === DELETE NOTE ========================================================

    /**
     * Delete note with the given id.
     *
     * @param noteId  the id of the note which data is to be deleted from database
     * @return        the status of the request
     */
    @Operation(summary = "Delete an existing note by id")
    @Parameters({
            @Parameter(name = "id", description = "Id of a specific note", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404")
    })
    @DeleteMapping("/patHistories/{id}")
    public ResponseEntity<Note> deleteNoteById(@PathVariable("id") String noteId) {
        logger.info("### Request called --> DELETE /patHistories/{}", noteId);

        noteService.deleteNote(noteId);

        logger.info("### Note deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // === DELETE ALL NOTES OF ONE PATIENT BY HIS ID ==========================
    @DeleteMapping("/patHistories")
    public ResponseEntity<List<Note>> deleteAllNotesByPatientId(@RequestParam(value = "patientId", required = true) Integer patientId) {
        logger.debug("### Request called --> DELETE /patHistories?patientId={}", patientId);

        noteService.deleteNotesByPatientId(patientId);

        logger.info("### Notes deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
