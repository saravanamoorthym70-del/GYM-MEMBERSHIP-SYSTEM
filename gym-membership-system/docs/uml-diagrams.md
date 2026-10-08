# UML Diagrams - Gym Membership System

## 1. Use Case Diagram

The use case diagram illustrates the interactions between the primary actors (User, Administrator) and the Gym Membership System. Users can create, submit, view, and cancel requests, while validation is an included behavior. Administrators manage requests by viewing them and advancing their state through to completion.

```plantuml
@startuml
left to right direction
actor User
actor Administrator

rectangle "Gym Membership System" {
  User --> (Create Membership)
  User --> (Validate Eligibility)
  User --> (Submit Request)
  User --> (View Own Requests)
  User --> (View Request Details)
  User --> (Cancel Request)
  
  Administrator --> (View All Requests)
  Administrator --> (Approve/Assign Request)
  Administrator --> (Advance Request Status)
  Administrator --> (Complete Request)
  Administrator --> (View Request History)
  
  (Create Membership) ..> (Validate Eligibility) : <<include>>
  (Submit Request) ..> (Validate Eligibility) : <<include>>
}
@enduml
```

## 2. Class Diagram

The class diagram outlines the fundamental entities and services in the system. It demonstrates the domain model and service logic associations, detailing attributes, methods, and multiplicities like `User "1" --o "*" ServiceRequest`.

```plantuml
@startuml
class User {
  - id : String
  - name : String
  - role : Role
  - status : UserStatus
  + getId() : String
  + getName() : String
  + getRole() : Role
  + getStatus() : UserStatus
}

class Resource {
  - id : String
  - reference : String
  - serviceName : String
  - capacity : int
  - availableCapacity : int
  - status : ResourceStatus
  - createdAt : LocalDateTime
  - updatedAt : LocalDateTime
  + getId() : String
  + getAvailableCapacity() : int
  + getStatus() : ResourceStatus
}

class ServiceRequest {
  - id : Long
  - referenceId : String
  - userId : String
  - resourceId : String
  - nameSnapshot : String
  - phoneSnapshot : String
  - descriptionSnapshot : String
  - serviceIdSnapshot : String
  - serviceNameSnapshot : String
  - capacitySnapshot : int
  - serviceStatusSnapshot : String
  - charge : BigDecimal
  - status : RequestStatus
  - createdAt : LocalDateTime
  - updatedAt : LocalDateTime
  - cancelledAt : LocalDateTime
  - resourceReleased : boolean
  + getReferenceId() : String
  + getStatus() : RequestStatus
}

class RequestStatusHistory {
  - id : Long
  - referenceId : String
  - actorId : String
  - oldStatus : String
  - newStatus : String
  - eventAt : LocalDateTime
  + getReferenceId() : String
  + getNewStatus() : String
}

class ValidationService {
  + validateUser(userId : String) : void
  + validateName(name : String) : void
  + validatePhone(phone : String) : void
  + validateDescription(desc : String) : void
  + validateActiveRequestLimit(userId : String) : void
  + validateResourceEligibility(resourceId : String) : void
  + validateCapacity(resourceId : String) : void
  + validateSubmission(form : MembershipFormDTO, userId : String) : void
}

class RequestService {
  + submitRequest(userId : String, form : MembershipFormDTO) : ServiceRequest
  + calculateCharge(amount : BigDecimal) : BigDecimal
  + getUserRequests(userId : String) : List<ServiceRequest>
  + getRequestDetails(refId : String, userId : String, role : Role) : ServiceRequest
  + cancelRequest(refId : String, userId : String) : void
  + advanceRequestStatus(refId : String, adminId : String) : void
  + getAllRequests() : List<ServiceRequest>
}

User "1" --o "*" ServiceRequest
Resource "1" --o "*" ServiceRequest
ServiceRequest "1" --o "*" RequestStatusHistory

RequestService --> ValidationService
RequestService --> ServiceRequestRepository
ValidationService --> UserRepository
ValidationService --> ResourceRepository
ValidationService --> ServiceRequestRepository
@enduml
```
