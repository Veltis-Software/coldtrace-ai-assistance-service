@ai
Feature: Controlled generation of incident resolution plans
  As an incident owner
  I want AI assistance to be invoked only through the platform and never invent advice
  So that the operator keeps control of the corrective decision

  @US22
  Scenario: The advisory service starts without its own database
    When the health endpoint is requested
    Then the response status is 200

  @US22 @US23
  Scenario: A browser token cannot invoke the model provider
    When an incident plan is requested with a browser token
    Then the response status is 401

  @US22 @US51
  Scenario: A disabled provider returns a controlled failure without a generated plan
    When an incident plan is requested with the internal service key
    Then the failure is "ProviderDisabled"
    And no plan is generated
