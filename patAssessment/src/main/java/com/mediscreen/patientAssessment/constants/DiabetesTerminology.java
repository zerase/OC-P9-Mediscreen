package com.mediscreen.patientAssessment.constants;

public enum DiabetesTerminology {

    HEMOGLOBIN_A1C("Hemoglobin A1C"),
    MICROALBUMIN("Microalbumin"),
    HEIGHT("Height"),
    WEIGHT("Weight"),
    SMOKER("Smoker"),
    ABNORMAL("Abnormal"),
    CHOLESTEROL("Cholesterol"),
    DIZZINESS("Dizziness"),
    RELAPSE("Relapse"),
    REACTION("Reaction"),
    ANTIBODIES("Antibodies"),

    // === French terms =======================================================
    HEMOGLOBINE_A1C("Hémoglobine A1C"),
    //MICROALBUMINE("Microalbumine"), // this one duplicates the other english term
    TAILLE("Taille"),
    POIDS("Poids"),
    FUMEUR("Fume"), // should work with the following trigger terms : "fumer", "fumeur", "fumeuse"
    ANORMAL("Anorma"), // should work with the following trigger terms : "anormal", "anormale", "anormales", "anormaux"
    FR_CHOLESTEROL("Cholestérol"),
    VERTIGE("Vertige"),
    RECHUTE("Rechute"),
    FR_REACTION("Réaction"),
    ANTICORPS("Anticorps");


    // ========================================================================

    private final String triggerTerm;

    DiabetesTerminology(String triggerTerm) {
        this.triggerTerm = triggerTerm;
    }

    public String getTriggerTerm() {
        return triggerTerm;
    }

}
