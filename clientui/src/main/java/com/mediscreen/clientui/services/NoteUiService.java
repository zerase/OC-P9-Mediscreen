package com.mediscreen.clientui.services;

import com.mediscreen.clientui.beans.NoteBean;
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
    public NoteBean createNewNote(NoteBean noteToCreate) {
        logger.debug("### Try to create new note --> {}", noteToCreate);

        NoteBean createdNote = noteProxy.addNewNote(noteToCreate);

        logger.info("### Created new note --> {}", createdNote);
        return createdNote;
    }

    // === FETCH ALL NOTES OF ONE PATIENT =====================================
    public List<NoteBean> fetchAllNotesOfPatient(Integer patientId) {
        logger.debug("### Try to fetch all notes of patientId={}", patientId);

        List<NoteBean> allNotesOfPatient = noteProxy.getAllNotesByPatientId(patientId);

        if(allNotesOfPatient == null) {
            logger.info("### Fetched no notes");
            return new ArrayList<>();
        }

        logger.info("### All fetched notes of patient --> {}", allNotesOfPatient);
        return allNotesOfPatient;
    }

    // === FETCH ONE NOTE =====================================================
    public NoteBean fetchNoteById(String noteId) {
        logger.debug("### Try to fetch note with id={}", noteId);

        NoteBean note = noteProxy.getNoteById(noteId);

        logger.info("### Fetched note --> {}", note);
        return note;
    }

    // === UPDATE NOTE ========================================================
    public NoteBean updateNote(String noteId, NoteBean noteToUpdate) {
        logger.debug("### Try to update note with id={}", noteId);

        NoteBean updatedNote = noteProxy.updateNoteById(noteId, noteToUpdate);

        logger.info("### Updated note --> {}", updatedNote);
        return updatedNote;
    }

    // === DELETE NOTE ========================================================
    public void deleteNote(String noteId) {
        logger.debug("### Try to delete note with id={}", noteId);

        noteProxy.deleteNoteById(noteId);

        logger.info("### Note deleted");
    }
}
