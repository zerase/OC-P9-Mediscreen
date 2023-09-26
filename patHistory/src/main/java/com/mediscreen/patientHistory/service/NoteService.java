package com.mediscreen.patientHistory.service;

import com.mediscreen.patientHistory.exception.DataNotFoundException;
import com.mediscreen.patientHistory.model.Note;

import java.util.List;

/**
 * Manages business service layer CRUD operations related to patient notes.
 */
public interface NoteService {

    /**
     * Creates a new note in database.
     *
     * @param note  the note to create
     * @return      the newly created note
     */
    Note createNote(Note note);

    /**
     * Retrieves all notes relating to a specific patient from database.
     *
     * @param patientId  the id of the patient to retrieve notes list
     * @return           a list of notes related to the given patientId
     */
    List<Note> readAllNotesByPatientId(Integer patientId);

    /**
     * Retrieves a note by its id from database.
     *
     * @param id  the id of the note to retrieve
     * @return    the note with the given id
     * @throws DataNotFoundException  if the note doesn't exist
     */
    Note readNoteById(String id) throws DataNotFoundException;

    /**
     * Updates an existing note in database.
     *
     * @param id    the id of the note to update
     * @param note  the note with the values to update
     * @return      the updated note
     * @throws DataNotFoundException  if the note doesn't exist
     */
    Note updateNote(String id, Note note) throws DataNotFoundException;

    /**
     * Deletes an existing note by its id from database.
     *
     * @param id  the id of the note to delete
     * @throws DataNotFoundException  if the note doesn't exist
     */
    void deleteNote(String id) throws DataNotFoundException;

    /**
     * Deletes all notes related to a specific patient by its id.
     *
     * @param patientId  the id of patient to delete notes list
     */
    void deleteNotesByPatientId(Integer patientId);
}
