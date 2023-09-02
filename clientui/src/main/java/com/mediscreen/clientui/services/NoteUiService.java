package com.mediscreen.clientui.services;

import com.mediscreen.clientui.beans.NoteDto;
import com.mediscreen.clientui.proxies.NoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteUiService {

    private static final Logger logger = LoggerFactory.getLogger(NoteUiService.class);

    private final NoteProxy noteProxy;

    @Autowired
    public NoteUiService(NoteProxy noteProxy) {
        this.noteProxy = noteProxy;
    }


    // === CREATE NEW NOTE ====================================================
    public NoteDto createNote(NoteDto note) {
        logger.debug("### Try to create new note {}", note);

        NoteDto noteToCreate = noteProxy.addNote(note);

        logger.info("### Created note {} successfully", note);
        return noteToCreate;
    }

    // === RETRIEVE ALL NOTES OF ALL PATIENTS =================================
    public List<NoteDto> retrieveAllNotes() {
        logger.debug("### Try to retrieve all notes");

        List<NoteDto> allNotesOfAllPatients = noteProxy.getAllNotes();

        if(allNotesOfAllPatients == null) {
            logger.info("### Retrieved no notes");
            return new ArrayList<>();
        }

        logger.info("### Retrieved all notes of all patients successfully");
        return allNotesOfAllPatients;
    }

    // === RETRIEVE ALL NOTES OF ONE PATIENT ==================================
    public List<NoteDto> retrieveAllNotesOfPatient(Integer patientId) {
        logger.debug("### Try to retrieve all notes of patientId={}", patientId);

        List<NoteDto> allNotesOfPatient = noteProxy.getAllNotesByPatientId(patientId);

        if(allNotesOfPatient == null) {
            logger.info("### Retrieved no notes");
            return new ArrayList<>();
        }

        logger.info("### Retrieved all notes of patient successfully");
        return allNotesOfPatient;
    }

    // === RETRIEVE NOTE BY ITS ID ============================================
    public NoteDto retrieveNoteById(String noteId) {
        logger.debug("### Try to retrieve note with id={}", noteId);

        NoteDto note = noteProxy.getNoteById(noteId);

        logger.info("### Retrieved note successfully");
        return note;
    }

    // === UPDATE NOTE ========================================================
    public NoteDto updateNote(String noteId, NoteDto note) {
        logger.debug("### Try to update note with id={}", noteId);

        NoteDto noteToUpdate = noteProxy.updateNote(noteId, note);

        logger.info("### Update note successfully");
        return noteToUpdate;
    }

    // === DELETE NOTE ========================================================
    public void deleteNote(String noteId) {
        logger.debug("### Try to delete note with id={}", noteId);

        noteProxy.deleteNote(noteId);
        logger.info("### Note deleted successfully");
    }
}
