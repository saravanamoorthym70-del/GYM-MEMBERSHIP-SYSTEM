# Requirement Traceability Matrix

| Requirement | Description | UML Element | Implementation (Class.Method) | Test ID |
|------------|-------------|-------------|-------------------------------|--------|
| FR1 | Create and display memberships | UC: Create Membership | MembershipController.createMembershipPage(), Resource entity | T5 |
| FR2 | Validate active-request limit and eligibility | UC: Validate Eligibility | ValidationService.validateActiveRequestLimit(), validateResourceEligibility() | T2, T3 |
| FR3 | Display charge, summary, status | UC: Submit Request | RequestService.calculateCharge(), RequestController.requestDetails() | T4 |
| FR4 | Validate user, details, eligibility | UC: Validate Eligibility | ValidationService.validateSubmission() | T6 |
| FR5 | Create SUBMITTED request with snapshot | UC: Submit Request | RequestService.submitRequest() | T5, T12 |
| FR6 | Display logged-in user's requests | UC: View Own Requests | RequestController.myRequests(), RequestService.getUserRequests() | T8 |
| FR7 | Administrator advances status | UC: Advance Request Status | AdminController.advanceStatus(), RequestService.advanceRequestStatus() | T9 |
| FR8 | Cancellation with resource release | UC: Cancel Request | RequestService.cancelRequest() | T10 |
| FR9 | Clear error messages | All UCs | GlobalExceptionHandler, BusinessException | T6, T8 |
| FR10 | Persistent storage | Class: All entities | JPA entities, MySQL database | T11 |
