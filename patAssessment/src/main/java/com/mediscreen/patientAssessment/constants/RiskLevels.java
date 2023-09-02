package com.mediscreen.patientAssessment.constants;

public enum RiskLevels {

    NONE("None"),
    BORDERLINE("Borderline"),
    IN_DANGER("In danger"),
    EARLY_ONSET("Early onset");

    // ========================================================================

    private final String riskLevel;

    RiskLevels(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

}
