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
import java.util.Optional;

/**
 * Implements the CRUD operations related to the note business logic.
 */
@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    /**
     * A component that handles database-related operations for notes of patients.
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
        logger.debug("### Try to save in repository new note --> {}", noteToCreate);

        noteToCreate.setDateOfCreation(LocalDateTime.now());
        noteToCreate.setDateOfModification(noteToCreate.getDateOfCreation());

        Note createdNote = noteRepository.insert(noteToCreate);

        logger.info("### New note saved successfully");
        return createdNote;
    }

    // === READ ALL ===========================================================
    @Override
    public List<Note> readAllNotes() {
        logger.debug("### Try to retrieve all notes of all patients from repository");

        List<Note> listOfAllNotesOfAllPatients = noteRepository.findAll();

        if(listOfAllNotesOfAllPatients.isEmpty()) {
            logger.info("### No note found from repository");
            return new ArrayList<>();
        } else {
            logger.info("### Retrieved list of all notes of all patients successfully");
            return listOfAllNotesOfAllPatients;
        }
    }

    // === READ ALL BY PATIENT ID =============================================
    @Override
    public List<Note> readAllNotesByPatientId(Integer patientId) {
        logger.debug("### Try to retrieve all notes of patient with id={}", patientId);

        List<Note> patientNotes = noteRepository.findAllByPatientIdOrderByDateOfCreationDesc(patientId);

        if(patientNotes.isEmpty()) {
            logger.info("### No notes found for patientId={} from repository", patientId);
            return new ArrayList<>();
        } else {
            logger.info("### Retrieved notes of patientId={} successfully", patientId);
            return patientNotes;
        }
    }

    // === READ ONE BY ID =====================================================
    @Override
    public Note readNoteById(String noteId) {
        logger.debug("### Try to retrieve from repository note with id={}", noteId);

        Optional<Note> note = noteRepository.findById(noteId);

        if(note.isEmpty()) {
            logger.error("### Failed to retrieve note with id={} from repository", noteId);
            throw new DataNotFoundException("Note not found with id=" + noteId);
        } else {
            logger.info("### Retrieved note with id={} successfully", noteId);
            return note.get();
        }
    }

    // === UPDATE =============================================================
    @Override
    public Note updateNote(String noteId, Note noteToUpdate) {
        logger.debug("### Try to update in repository note with id={}", noteId);

        // Checks if note with given id already exists in repository
        Note existingNote = noteRepository.findById(noteId).orElseThrow(() -> {
            logger.error("### Failed to retrieve note with id={}", noteId);
            throw new DataNotFoundException("Note not found with id=" + noteId);
        });

        LocalDateTime originalDateOfCreation = existingNote.getDateOfCreation();
        noteToUpdate.setId(noteId);
        noteToUpdate.setDateOfCreation(originalDateOfCreation);
        noteToUpdate.setDateOfModification(LocalDateTime.now());

        Note updatedNote = noteRepository.save(noteToUpdate);

        logger.info("### Updated note with id={} successfully", noteId);
        return updatedNote;
    }

    // === DELETE =============================================================
    @Override
    public void deleteNote(String noteId) {
        logger.debug("### Try to delete from repository note with id={}", noteId);

        if(noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            logger.info("### Deleted note successfully");
        } else {
            logger.error("### Failed to delete note with id={}", noteId);
            throw new DataNotFoundException("Note not found");
        }
    }

}
