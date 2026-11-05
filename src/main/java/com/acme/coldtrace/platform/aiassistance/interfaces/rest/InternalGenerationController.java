package com.acme.coldtrace.platform.aiassistance.interfaces.rest;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.*;
import com.acme.coldtrace.platform.aiassistance.application.model.*;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.*;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.web.bind.annotation.*;

/** Generates advisory content; incident decisions remain transactional in Alert. */
@RestController
@RequestMapping("/internal/ai/v1/generations")
public class InternalGenerationController {
  private final AiAssistanceCommandService ai;
  public InternalGenerationController(AiAssistanceCommandService ai) { this.ai = ai; }
  private static <T> AiGenerationReply<T> reply(Result<AiGeneratedResponse<T>, AiAssistanceFailure> result) {
    return result.fold(value -> new AiGenerationReply<>(value, null),
        failure -> new AiGenerationReply<>(null, failure.getClass().getSimpleName()));
  }
  @PostMapping("/incident-plan")
  public AiGenerationReply<IncidentResolutionPlanDraft> incident(@RequestBody GenerateIncidentResolutionPlanDraftCommand command) {
    return reply(ai.handle(command));
  }
  @PostMapping("/dashboard")
  public AiGenerationReply<DashboardInterpretationDraft> dashboard(@RequestBody GenerateDashboardInterpretationCommand command) {
    return reply(ai.handle(command));
  }
  @PostMapping("/compliance-summary")
  public AiGenerationReply<ComplianceSummaryDraft> compliance(@RequestBody GenerateComplianceSummaryCommand command) {
    return reply(ai.handle(command));
  }
}
