package com.chaospilot.report.controller;

import com.chaospilot.report.dto.ReportResponse;
import com.chaospilot.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportResponse>> listReports() {
        return ResponseEntity.ok(reportService.listReports());
    }

    @GetMapping("/{experimentId}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable UUID experimentId) {
        return ResponseEntity.ok(reportService.getReport(experimentId));
    }
}
