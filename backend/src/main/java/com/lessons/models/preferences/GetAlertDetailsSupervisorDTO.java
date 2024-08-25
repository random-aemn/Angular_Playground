package com.lessons.models.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAlertDetailsSupervisorDTO {
    @JsonProperty("alertId")
    private Integer alertId;

    @JsonProperty("caseId")
    private Integer caseId;

    @JsonProperty("supervisorDispositionId")
    private Integer supervisorDispositionId;

    @JsonProperty("triggeringText")
    private String triggeringText;

    @JsonProperty("dataSourceUrl")
    private String dataSourceUrl;

    @JsonProperty("alertCreatedByFullName")
    private String alertCreatedByFullName;

    @JsonProperty("alertCreatedDate")
    private String alertCreatedDate;

    @JsonProperty("assignedToFullName")
    private String assignedToFullName;

    @JsonProperty("alertAssignmentPriorityId")
    private Integer alertAssignmentPriorityId;

    @JsonProperty("alertAssignmentPriorityLabel")
    private String alertAssignmentPriorityLabel;

    @JsonProperty("displayedGuideline")
    private String displayedGuideline;

    @JsonProperty("displayedSubGuideline")
    private String displayedSubGuideline;

    @JsonProperty("displayedRiskCategory")
    private String displayedRiskCategory;

    @JsonProperty("displayedBusinessRule")
    private String displayedBusinessRule;

    @JsonProperty("displayedSpecialistDispositionLabel")
    private String displayedSpecialistDispositionLabel;

    @JsonProperty("dataSource")
    private String dataSource;

    @JsonProperty("alertRejectionNoteId")
    private Integer alertRejectionNoteId;

    // ------------------------------- Getter & Setters ------------------------------- //

    public Integer getAlertId() {
        return alertId;
    }

    public void setAlertId(Integer alertId) {
        this.alertId = alertId;
    }

    public Integer getSupervisorDispositionId() {
        return supervisorDispositionId;
    }

    public void setSupervisorDispositionId(Integer supervisorDispositionId) {
        this.supervisorDispositionId = supervisorDispositionId;
    }

    public String getTriggeringText() {
        return triggeringText;
    }

    public void setTriggeringText(String triggeringText) {
        this.triggeringText = triggeringText;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public String getAlertCreatedByFullName() {
        return alertCreatedByFullName;
    }

    public void setAlertCreatedByFullName(String alertCreatedByFullName) {
        this.alertCreatedByFullName = alertCreatedByFullName;
    }

    public String getAlertCreatedDate() {
        return alertCreatedDate;
    }

    public void setAlertCreatedDate(String alertCreatedDate) {
        this.alertCreatedDate = alertCreatedDate;
    }

    public String getAssignedToFullName() {
        return assignedToFullName;
    }

    public void setAssignedToFullName(String assignedToFullName) {
        this.assignedToFullName = assignedToFullName;
    }

    public Integer getAlertAssignmentPriorityId() {
        return alertAssignmentPriorityId;
    }

    public void setAlertAssignmentPriorityId(Integer alertAssignmentPriorityId) {
        this.alertAssignmentPriorityId = alertAssignmentPriorityId;
    }

    public String getDisplayedGuideline() {
        return displayedGuideline;
    }

    public void setDisplayedGuideline(String displayedGuideline) {
        this.displayedGuideline = displayedGuideline;
    }

    public String getDisplayedSubGuideline() {
        return displayedSubGuideline;
    }

    public void setDisplayedSubGuideline(String displayedSubGuideline) {
        this.displayedSubGuideline = displayedSubGuideline;
    }

    public String getDisplayedRiskCategory() {
        return displayedRiskCategory;
    }

    public void setDisplayedRiskCategory(String displayedRiskCategory) {
        this.displayedRiskCategory = displayedRiskCategory;
    }

    public String getDisplayedBusinessRule() {
        return displayedBusinessRule;
    }

    public void setDisplayedBusinessRule(String displayedBusinessRule) {
        this.displayedBusinessRule = displayedBusinessRule;
    }

    public String getDisplayedSpecialistDispositionLabel() {
        return displayedSpecialistDispositionLabel;
    }

    public void setDisplayedSpecialistDispositionLabel(String displayedSpecialistDispositionLabel) {
        this.displayedSpecialistDispositionLabel = displayedSpecialistDispositionLabel;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAlertAssignmentPriorityLabel() {
        return alertAssignmentPriorityLabel;
    }

    public void setAlertAssignmentPriorityLabel(String alertAssignmentPriorityLabel) {
        this.alertAssignmentPriorityLabel = alertAssignmentPriorityLabel;
    }

    public Integer getAlertRejectionNoteId() {
        return alertRejectionNoteId;
    }

    public void setAlertRejectionNoteId(Integer alertRejectionNoteId) {
        this.alertRejectionNoteId = alertRejectionNoteId;
    }
}
