package com.acme.coldtrace.platform.aiassistance.infrastructure.reports;

import com.acme.coldtrace.platform.aiassistance.application.ports.ReportEvidenceReader;
import com.acme.coldtrace.platform.aiassistance.application.ports.ReportEvidenceSnapshot;
import com.acme.coldtrace.platform.aiassistance.infrastructure.clients.OwnerClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
public class ReportServiceAdapter implements ReportEvidenceReader {
  private final OwnerClient owner;
  public ReportServiceAdapter(@Value("${coldtrace.report.url}") String url,
      @Value("${coldtrace.internal.service-key}") String key) { owner = new OwnerClient(url, key); }
  public List<ReportEvidenceSnapshot> findAllByOrganizationId(Long org) {
    return owner.read("/internal/reports/organizations/{org}/evidence", new ParameterizedTypeReference<>() {}, org);
  }
}
