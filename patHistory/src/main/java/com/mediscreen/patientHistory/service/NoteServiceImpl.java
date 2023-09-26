package com.mediscreen.patientHistory.service;

import com.mediscreen.patientHistory.exception.DataNotFoundException;
import com.mediscreen.patientHistory.model.Note;
import com.mediscreen.patientHistory.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the CRUD operations related to the note business logic.
 */
@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    /**
     * Component that manages database operations for patient notes.
     */
    private final NoteRepository noteRepository;

    /**
     * Instantiates a new note service.
     *
     * @param noteRepository  the repository linked to note data
     */
    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }


    // === CREATE =============================================================
    @Override
    public Note createNote(Note noteToCreate) {
        logger.debug("### Try to save in database new note --> {}", noteToCreate);

        noteToCreate.setDateOfCreation(LocalDateTime.now());
        noteToCreate.setDateOfModification(noteToCreate.getDateOfCreation());

        Note createdNote = noteRepository.insert(noteToCreate);

        logger.info("### New note saved --> {}", createdNote);
        return createdNote;
    }

    // === READ ALL BY PATIENT ID =============================================
    @Override
    public List<Note> readAllNotesByPatientId(Integer patientId) {
        logger.debug("### Try to retrieve all notes with patientId={} from database", patientId);

        List<Note> patientNotes = noteRepository.findAllByPatientIdOrderByDateOfCreationDesc(patientId);

        if(patientNotes.isEmpty()) {
            logger.info("### No notes found from database");
            return new ArrayList<>();
        } else {
            logger.info("### List of notes retrieved --> {}", patientNotes);
            return patientNotes;
        }
    }

    // === READ ONE BY ID =====================================================
    @Override
    public Note readNoteById(String noteId) {
        logger.debug("### Try to retrieve from database note with id={}", noteId);

        Note retrievedNote = noteRepository.findById(noteId).orElseThrow(() -> {
            logger.error("### Failed to retrieve note with id={} from database", noteId);
            throw new DataNotFoundException("Note not found or doesn't exist");
        });

        logger.info("### Retrieved note with id={} --> {}", noteId, retrievedNote);
        return retrievedNote;
    }

    // === UPDATE =============================================================
    @Override
    public Note updateNote(String noteId, Note noteToUpdate) {
        logger.debug("### Try to update in database note with id={}", noteId);

        // Checks if note with given id already exists in database ...
        Note existingNote = noteRepository.findById(noteId).orElseThrow(() -> {
            logger.error("### Failed to retrieve note with id={}", noteId);
            throw new DataNotFoundException("Note not found or doesn't exist");
        });
        // ... then saves modifications
        LocalDateTime originalDateOfCreation = existingNote.getDateOfCreation();
        noteToUpdate.setId(noteId);
        noteToUpdate.setDateOfCreation(originalDateOfCreation);
        noteToUpdate.setDateOfModification(LocalDateTime.now());
        Note updatedNote = noteRepository.save(noteToUpdate);

        logger.info("### Updated note with id={} --> {}", noteId, updatedNote);
        return updatedNote;
    }

    // === DELETE =============================================================
    @Override
    public void deleteNote(String noteId) {
        logger.debug("### Try to delete from database note with id={}", noteId);

        // Checks if given id exists in database before deleting
        if(noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            logger.info("### Deleted note with id={}", noteId);
        } else {
            logger.error("### Failed to delete note with id={}", noteId);
            throw new DataNotFoundException("Note not found or doesn't exist");
        }
    }

    // === DELETE NOTES BY PATIENT ID =========================================
    @Override
    public void deleteNotesByPatientId(Integer patientId) {
        logger.debug("### Try to delete from database all notes with patientId={}", patientId);

        noteRepository.deleteAllByPatientId(patientId);

        logger.info("### Deleted notes with patientId={}", patientId);
    }
}
