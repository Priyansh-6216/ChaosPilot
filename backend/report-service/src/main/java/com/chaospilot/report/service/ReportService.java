package com.chaospilot.report.service;

import com.chaospilot.report.dto.ReportResponse;
import com.chaospilot.report.exception.NotFoundException;
import com.chaospilot.report.model.ChaosReport;
import com.chaospilot.report.repository.ChaosReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ChaosReportRepository reportRepository;

    public ReportResponse getReport(UUID experimentId) {
        ChaosReport report = reportRepository.findByExperimentId(experimentId)
                .orElseThrow(() -> new NotFoundException("Report for experiment " + experimentId + " not found"));
        return mapToDto(report);
    }

    public List<ReportResponse> listReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReportResponse mapToDto(ChaosReport report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setExperimentId(report.getExperimentId());
        response.setSummary(report.getSummary());
        response.setRootCause(report.getRootCause());
        response.setSeverity(report.getSeverity());
        response.setResilienceScore(report.getResilienceScore());
        response.setBlastRadius(report.getBlastRadius());
        response.setRecommendedFixes(report.getRecommendedFixes());
        response.setPreventionPlan(report.getPreventionPlan());
        response.setGeneratedAt(report.getGeneratedAt());
        return response;
    }
}
