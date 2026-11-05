package com.acme.coldtrace.platform.aiassistance.application.ports;

import java.util.List;

public interface ReportEvidenceReader {
  List<ReportEvidenceSnapshot> findAllByOrganizationId(Long organizationId);
}
