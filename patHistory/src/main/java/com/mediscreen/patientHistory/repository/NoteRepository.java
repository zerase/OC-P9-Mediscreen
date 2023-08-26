package com.mediscreen.patientHistory.repository;

import com.mediscreen.patientHistory.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Allows interaction with the note table of the NoSQL database and provides functions of CRUD operations among other things.
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Returns all instances of the given type which have the same identifier of patient ordered by latest date.
     *
     * @param patientId  the integer of the patient to be searched on
     * @return           all the entities with the same patientId
     */
    List<Note> findAllByPatientIdOrderByDateOfCreationDesc(Integer patientId);

}
