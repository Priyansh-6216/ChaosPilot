package com.chaospilot.report.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "chaos_reports")
public class ChaosReport {

    @Id
    private UUID id;

    @Column(name = "experiment_id", nullable = false, unique = true)
    private UUID experimentId;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(name = "root_cause", columnDefinition = "text")
    private String rootCause;

    @Column(columnDefinition = "varchar(50)")
    private String severity;

    @Column(name = "resilience_score")
    private Integer resilienceScore;

    @Type(JsonBinaryType.class)
    @Column(name = "blast_radius", columnDefinition = "jsonb")
    private Map<String, Object> blastRadius;

    @Type(JsonBinaryType.class)
    @Column(name = "recommended_fixes", columnDefinition = "jsonb")
    private List<String> recommendedFixes;

    @Column(name = "prevention_plan", columnDefinition = "text")
    private String preventionPlan;

    @Column(name = "generated_at")
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
