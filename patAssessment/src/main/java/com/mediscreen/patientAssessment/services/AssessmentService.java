package com.mediscreen.patientAssessment.services;

import com.mediscreen.patientAssessment.beans.AssessmentDTO;
import com.mediscreen.patientAssessment.beans.NoteBean;
import com.mediscreen.patientAssessment.beans.PatientBean;
import com.mediscreen.patientAssessment.constants.DiabetesTerminology;
import com.mediscreen.patientAssessment.constants.RiskLevels;
import com.mediscreen.patientAssessment.exceptions.PatientNotFoundException;
import com.mediscreen.patientAssessment.proxies.MicroserviceNoteProxy;
import com.mediscreen.patientAssessment.proxies.MicroservicePatientProxy;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements the operations related to the assessment business logic.
 */
@Service
public class AssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    /**
     * Component that manages operations related to the retrieval of patient personal data.
     */
    private final MicroservicePatientProxy microservicePatientProxy;

    /**
     * Component that manages operations related to the retrieval of patient notes history.
     */
    private final MicroserviceNoteProxy microserviceNoteProxy;

    /**
     * Instantiates a new assessment service.
     *
     * @param microservicePatientProxy  the proxy linked to patient data
     * @param microserviceNoteProxy     the proxy linked to patient notes history
     */
    @Autowired
    public AssessmentService(MicroservicePatientProxy microservicePatientProxy, MicroserviceNoteProxy microserviceNoteProxy) {
        this.microservicePatientProxy = microservicePatientProxy;
        this.microserviceNoteProxy = microserviceNoteProxy;
    }


    // ========================================================================

    /**
     * Creates the object that evaluates and determines the likelihood that a patient will develop diabetes.
     *
     * @param patientId  the id of the patient we are seeking to evaluate
     * @return           the object that represents the assessment of a patient probability of developing diabetes
     */
    public AssessmentDTO assessDiabetesRiskLevelByPatientId(Integer patientId) {
        logger.debug("### Try to assess level of risk of patient with id={}", patientId);
        PatientBean patientInfo = retrievePatientById(patientId);

        Integer patientAge = calculateAgeFromDateOfBirth(patientInfo.getDateOfBirth());

        List<NoteBean> patientNotes = retrieveNotesByPatientId(patientId);
        Integer patientTriggers = countTriggerTermsPresentInList(patientNotes);

        String diabetesRiskLevel = determineDiabetesRiskLevel(patientInfo.getGender(), patientAge, patientTriggers);

        AssessmentDTO assessmentDTO = new AssessmentDTO(patientInfo, patientAge, diabetesRiskLevel);

        logger.info("### Assessment returned --> {}", assessmentDTO);
        return assessmentDTO;
    }

    // === RETRIEVE PATIENT BY PATIENT ID =====================================

    /**
     * Retrieves information of the patient with the given id.
     *
     * @param patientId  the id of the patient whose information we wish to retrieve
     * @return           the information of the patient with the given id
     */
    public PatientBean retrievePatientById(Integer patientId) {
        logger.debug("### Try to retrieve patient with id={}", patientId);

        PatientBean patientToAssess;

        try {
            patientToAssess = microservicePatientProxy.getPatientById(patientId);
        } catch (FeignException e) {
            logger.error("### Failed to retrieve patient with id={}", patientId);
            throw new PatientNotFoundException("Patient not found with id=" + patientId);
        }
        logger.info("### Retrieved patient with id={} successfully", patientId);
        return patientToAssess;
    }

    // === CALCULATE AGE ======================================================

    /**
     * Calculate the age based on the date of birth given as a parameter.
     *
     * @param dateOfBirth  the date from which the calculation must be made
     * @return             the integer corresponding to the patient age
     */
    public Integer calculateAgeFromDateOfBirth(LocalDate dateOfBirth) {
        logger.debug("### Try to calculate age from {}", dateOfBirth);

        LocalDate currentDate = LocalDate.now();

        Period age = Period.between(dateOfBirth, currentDate);

        logger.info("### Age returned --> {}", age.getYears());
        return age.getYears();
    }

    // === RETRIEVE NOTES OF PATIENT BY PATIENT ID ============================

    /**
     * Retrieves all notes related to a given patient.
     *
     * @param patientId  the id of the patient whose notes we wish to retrieve
     * @return           the list of all notes of the patient with the given id
     */
    public List<NoteBean> retrieveNotesByPatientId(final Integer patientId) {
        logger.debug("### Try to retrieve notes of patient with id={}", patientId);

        List<NoteBean> patientNotes;

        try {
            patientNotes = microserviceNoteProxy.getAllNotesByPatientId(patientId);
        } catch (FeignException e) {
            logger.error("### Failed to retrieve notes of patient with id={}", patientId);
            throw new PatientNotFoundException("Patient not found with id=" + patientId);
        }

        logger.info("### Notes returned --> {}", patientNotes);
        return patientNotes;
    }

    // === COUNT TRIGGER TERMS ================================================

    /**
     * Counts the number of trigger terms found in a list of notes.
     *
     * @param allNotesOfPatient  the list on which the count is performed
     * @return                   the number of terms found
     */
    public int countTriggerTermsPresentInList(List<NoteBean> allNotesOfPatient) {
        logger.debug("### Try to count number of trigger terms present in list of notes {}", allNotesOfPatient);

        Set<String> allTerminology = Stream.of(DiabetesTerminology.values())
                .map(DiabetesTerminology::getTriggerTerm)
                .collect(Collectors.toSet());
        Set<String> triggerTermsFound = new TreeSet<>();

        // For each note among all the notes of the patient ...
        for (NoteBean noteOfPatient : allNotesOfPatient) {
            String contentOfTheNote = noteOfPatient.getContent().toLowerCase();
            for(String triggerTerm : allTerminology) {
                // ... we check whether a trigger term among all the terms of the terminology is present ...
                if (contentOfTheNote.contains(triggerTerm.toLowerCase())){
                    // ... and we add it to the final list of terms found
                    triggerTermsFound.add(triggerTerm);
                }
            }
        }
        //logger.info("### Terminology --> {}", allTerminology);
        //logger.info("### Terminology size --> {}", allTerminology.size());
        logger.info("### Trigger terms found --> {}", triggerTermsFound);
        logger.info("### Number of trigger terms returned --> {}", triggerTermsFound.size());
        return triggerTermsFound.size();
    }

    // === DETERMINE DIABETES RISK LEVEL ======================================

    /**
     * Determines and assigns a risk level value based on the patient gender, age and the number of trigger terms.
     * Can be set to None, Borderline, In danger, Early onset or Undefined if no cases meet the specified conditions
     *
     * @param triggerTermCount  the number of trigger terms found in the list of notes of the patient to assess
     * @param patientAge        the age of the patient to assess
     * @param gender            the gender of the patient to assess
     * @return                  the value of the risk level
     */
    public String determineDiabetesRiskLevel(String gender, Integer patientAge, Integer triggerTermCount) {
        logger.debug("### Try to determine diabetes risk level when gender={}, age={} and trigger terms={}", gender, patientAge, triggerTermCount);

        final int PIVOTAL_AGE = 30;
        String riskLevel;

        switch (gender) {
            case "M":
                if(patientAge >= PIVOTAL_AGE) {
                    switch (triggerTermCount) {
                        case 0: case 1:
                            riskLevel = RiskLevels.NONE.getRiskLevel();
                            break;
                        case 2: case 3: case 4: case 5:
                            riskLevel = RiskLevels.BORDERLINE.getRiskLevel();
                            break;
                        case 6: case 7:
                            riskLevel = RiskLevels.IN_DANGER.getRiskLevel();
                            break;
                        case 8: case 9: case 10: case 11:
                            riskLevel = RiskLevels.EARLY_ONSET.getRiskLevel();
                            break;
                        default:
                            riskLevel = "Undefined";
                    }
                } else {
                    switch (triggerTermCount) {
                        case 0: case 1: case 2:
                            riskLevel = RiskLevels.NONE.getRiskLevel();
                            break;
                        case 3: case 4:
                            riskLevel = RiskLevels.IN_DANGER.getRiskLevel();
                            break;
                        case 5: case 6: case 7: case 8: case 9: case 10: case 11:
                            riskLevel = RiskLevels.EARLY_ONSET.getRiskLevel();
                            break;
                        default:
                            riskLevel = "Undefined";
                    }
                }
                break;
            case "F":
                if(patientAge >= PIVOTAL_AGE) {
                    switch (triggerTermCount) {
                        case 0: case 1:
                            riskLevel = RiskLevels.NONE.getRiskLevel();
                            break;
                        case 2: case 3: case 4: case 5:
                            riskLevel = RiskLevels.BORDERLINE.getRiskLevel();
                            break;
                        case 6: case 7:
                            riskLevel = RiskLevels.IN_DANGER.getRiskLevel();
                            break;
                        case 8: case 9: case 10: case 11:
                            riskLevel = RiskLevels.EARLY_ONSET.getRiskLevel();
                            break;
                        default:
                            riskLevel = "Undefined";
                    }
                } else {
                    switch (triggerTermCount) {
                        case 0: case 1: case 2: case 3:
                            riskLevel = RiskLevels.NONE.getRiskLevel();
                            break;
                        case 4: case 5: case 6:
                            riskLevel = RiskLevels.IN_DANGER.getRiskLevel();
                            break;
                        case 7: case 8: case 9: case 10: case 11:
                            riskLevel = RiskLevels.EARLY_ONSET.getRiskLevel();
                            break;
                        default:
                            riskLevel = "Undefined";
                    }
                }
                break;
            default:
                riskLevel = "Undefined";
        }
        logger.info("### Diabetes risk level returned --> {}", riskLevel);
        return riskLevel;
    }

    // NOTE: The method below is more compact and works just as well as the one above,
    // but I find the different possible cases less easy to decipher at first glance.
    // Let's see if we use it later instead of the one used above.
    /*public String determineDiabetesRiskLevel(String gender, Integer patientAge, Integer triggerTermCount) {
        logger.debug("### Try to determine diabetes risk level when gender={}, age={} and trigger terms={}", gender, patientAge, triggerTermCount);

        String riskLevel = RiskLevels.NONE.getRiskLevel();

        if (((gender.equals("M") && patientAge < 30 && triggerTermCount >= 5))
                || ((gender.equals("F") && patientAge < 30 && triggerTermCount >= 7))
                || ((patientAge >= 30 && triggerTermCount >= 8))) {
            riskLevel = RiskLevels.EARLY_ONSET.getRiskLevel();
        } else if ((gender.equals("M") && patientAge < 30 && triggerTermCount >= 3)
                || ((gender.equals("F")) && patientAge < 30 && triggerTermCount >= 4)
                || (patientAge >= 30 && triggerTermCount >= 6)) {
            riskLevel = RiskLevels.IN_DANGER.getRiskLevel();
        } else if ((patientAge >= 30 && triggerTermCount >= 2)) {
            riskLevel = RiskLevels.BORDERLINE.getRiskLevel();
        }
        logger.info("### Diabetes risk level returned --> {}", riskLevel);
        return riskLevel;
    }*/
}
