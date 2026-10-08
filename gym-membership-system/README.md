# GYM MEMBERSHIP SYSTEM

## 1. Problem Statement
Design and implement a Gym Membership System for managing gym memberships. The system allows users to create membership requests, track their status, and enables administrators to manage the lifecycle of these requests. Built for Software Engineering SAT II Assessment at Sri Eshwar College of Engineering.

## 2. Features
- User login simulation (no password required)
- Role-based access (USER, ADMINISTRATOR)
- Membership request creation with validation
- Automatic charge calculation (BR4)
- Request snapshot preservation (BR3)
- Active request limit enforcement (BR1, max 5)
- Status workflow management (SUBMITTED → APPROVED/ASSIGNED → IN_PROGRESS → COMPLETED)
- Cancellation with single resource release (BR9)
- Full status history tracking
- Admin dashboard for request management
- Server-side validation (phone, name, description)
- Transaction safety with @Transactional

## 3. Technology Stack
| Component | Technology |
|-----------|------------|
| Backend | Java 21, Spring Boot 3.2.5, Spring MVC, Spring Data JPA, Hibernate |
| Frontend | Thymeleaf, HTML5, CSS3, Bootstrap 5 |
| Database | MySQL |
| Validation | Jakarta Validation |
| Build | Maven |

## 4. Architecture
Layered architecture:
```
Controller → Service → Repository → MySQL Database
```
Package structure:
```
com.gymmembership
├── controller/
├── service/
├── repository/
├── entity/
│   └── enums/
├── dto/
├── validation/
├── exception/
└── config/
```

## 5. Database Design
Describe all 4 entities with their fields in tables:
- users table
- resources table
- service_requests table
- request_status_history table

## 6. Business Rules
List BR1-BR10 with brief descriptions.

## 7. Functional Requirements
List FR1-FR10.

## 8. Setup Instructions
### Prerequisites
- Java 21 (JDK)
- Maven 3.8+
- MySQL 8.0+

### Database Setup
```sql
CREATE DATABASE IF NOT EXISTS gym_membership_db;
```

### Configuration
Update src/main/resources/application.properties:
- spring.datasource.username=your_username
- spring.datasource.password=your_password

### Build and Run
```bash
mvn clean install
mvn spring-boot:run
```
Access at: http://localhost:8080

## 9. Seed Data
### Users
| ID | Name | Role | Status |
|----|------|------|--------|
| U001 | Arun Kumar | USER | ACTIVE |
| U002 | Bala Kumar | USER | ACTIVE |
| A001 | Admin | ADMINISTRATOR | ACTIVE |

### Resources
| ID | Service Name | Capacity | Available | Status |
|----|-------------|----------|-----------|--------|
| S101 | Membership A | 10 | 10 | ACTIVE |
| S102 | Membership B | 5 | 5 | ACTIVE |
| S103 | Membership C | 0 | 0 | INACTIVE |

## 10. Demo Workflow
Step-by-step guide:
1. Open http://localhost:8080
2. Login as Arun Kumar (U001)
3. Click Create Membership
4. Fill: Name=Arun Kumar, Phone=9876543210, Description=Annual gym membership, Service=Membership A, Amount=999
5. Submit → Charge should be ₹50, Status SUBMITTED
6. Logout, Login as Administrator
7. View Admin Dashboard
8. Approve the request → APPROVED/ASSIGNED
9. Move to In Progress → IN_PROGRESS
10. Complete → COMPLETED

### Rejection Demos
- Try S103 (inactive) → rejected
- Create 5 active requests, try 6th → rejected
- Submit with invalid phone → rejected

## 11. Testing
### Run Tests
```bash
mvn test
```

### Test Summary (T1-T12)
| Test | Description | Expected Result |
|------|-------------|----------------|
| T1 | Duplicate submission | No duplicate record |
| T2 | Active request counts 4,5,6 | 4 accepted, 5 accepted, 6 rejected |
| T3 | Inactive resource S103 | Rejected, state unchanged |
| T4 | Charge: 999/1000/1001 | 50/0/0 |
| T5 | Complete valid submission | Successful request, correct transitions |
| T6 | Missing/invalid details | Rejected, no partial update |
| T7 | Change eligibility before acceptance | Transaction rejected |
| T8 | Unauthorized access | Access denied |
| T9 | Valid/invalid status transitions | Valid accepted, skipping rejected |
| T10 | Cancel and repeat cancellation | First succeeds, second no effect |
| T11 | Restart application | Records persist |
| T12 | Master data change after request | Snapshot unchanged |

## 12. Limitations
- No real authentication (simulation only)
- No real payment gateway
- Single server deployment
- No file upload support
- No email notifications

## 13. Future Enhancements
- Spring Security integration
- Email notification for status changes
- Payment gateway integration
- Report generation
- REST API for mobile apps

## 14. AI-Assisted Development Note
This project was developed with AI assistance for code generation and documentation. All business logic, architecture decisions, and code implementation were reviewed for correctness and compliance with the specified requirements.
