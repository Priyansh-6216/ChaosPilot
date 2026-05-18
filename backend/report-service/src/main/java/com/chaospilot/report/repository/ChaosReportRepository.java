package com.chaospilot.report.repository;

import com.chaospilot.report.model.ChaosReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChaosReportRepository extends JpaRepository<ChaosReport, UUID> {

    Optional<ChaosReport> findByExperimentId(UUID experimentId);
}
