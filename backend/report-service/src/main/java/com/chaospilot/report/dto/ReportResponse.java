package com.chaospilot.report.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportResponse {

    private UUID id;
    private UUID experimentId;
    private String summary;
    private String rootCause;
    private String severity;
    private Integer resilienceScore;
    private Map<String, Object> blastRadius;
    private List<String> recommendedFixes;
    private String preventionPlan;
    private LocalDateTime generatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(UUID experimentId) {
        this.experimentId = experimentId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getResilienceScore() {
        return resilienceScore;
    }

    public void setResilienceScore(Integer resilienceScore) {
        this.resilienceScore = resilienceScore;
    }

    public Map<String, Object> getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(Map<String, Object> blastRadius) {
        this.blastRadius = blastRadius;
    }

    public List<String> getRecommendedFixes() {
        return recommendedFixes;
    }

    public void setRecommendedFixes(List<String> recommendedFixes) {
        this.recommendedFixes = recommendedFixes;
    }

    public String getPreventionPlan() {
        return preventionPlan;
    }

    public void setPreventionPlan(String preventionPlan) {
        this.preventionPlan = preventionPlan;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
