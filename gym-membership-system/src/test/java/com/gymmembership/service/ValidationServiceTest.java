package com.gymmembership.service;

import com.gymmembership.entity.ServiceRequest;
import com.gymmembership.entity.enums.RequestStatus;
import com.gymmembership.exception.BusinessException;
import com.gymmembership.repository.ResourceRepository;
import com.gymmembership.repository.ServiceRequestRepository;
import com.gymmembership.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ValidationServiceTest {

    @Autowired
    ValidationService validationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    @Test
    void testValidPhone() {
        assertDoesNotThrow(() -> validationService.validatePhone("9876543210"));
    }

    @Test
    void testInvalidPhone_9digits() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validatePhone("987654321"));
        assertTrue(exception.getMessage().contains("10 digits"));
    }

    @Test
    void testInvalidPhone_11digits() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validatePhone("98765432101"));
        assertTrue(exception.getMessage().contains("10 digits"));
    }

    @Test
    void testInvalidPhone_withLetters() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validatePhone("98765abc10"));
        assertTrue(exception.getMessage().contains("10 digits") || exception.getMessage().toLowerCase().contains("invalid"));
    }

    @Test
    void testInvalidPhone_withSpaces() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validatePhone("98765 43210"));
        assertTrue(exception.getMessage().contains("10 digits") || exception.getMessage().toLowerCase().contains("invalid"));
    }

    @Test
    void testValidName() {
        assertDoesNotThrow(() -> validationService.validateName("Arun Kumar"));
    }

    @Test
    void testBlankName() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validateName(""));
        assertTrue(exception.getMessage().contains("Name cannot be blank"));
    }

    @Test
    void testNullName() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validateName(null));
        assertTrue(exception.getMessage().contains("Name cannot be blank"));
    }

    @Test
    void testValidDescription() {
        assertDoesNotThrow(() -> validationService.validateDescription("Annual membership"));
    }

    @Test
    void testBlankDescription() {
        assertThrows(BusinessException.class, () -> validationService.validateDescription(""));
    }

    @Test
    void testInactiveResourceRejection() {
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validateResourceEligibility("S103"));
        assertTrue(exception.getMessage().toLowerCase().contains("inactive"));
    }

    @Test
    void testActiveResourceAccepted() {
        assertDoesNotThrow(() -> validationService.validateResourceEligibility("S101"));
    }

    @Test
    void testResourceCapacityCheck() {
        assertDoesNotThrow(() -> validationService.validateCapacity("S101"));
        assertThrows(BusinessException.class, () -> validationService.validateCapacity("S103"));
    }

    @Test
    void testActiveRequestLimit_under5() {
        for (int i = 0; i < 4; i++) {
            ServiceRequest req = new ServiceRequest();
            req.setReferenceId(UUID.randomUUID().toString());
            req.setUserId("U001");
            req.setResourceId("S101");
            req.setStatus(RequestStatus.SUBMITTED);
            serviceRequestRepository.save(req);
        }
        assertDoesNotThrow(() -> validationService.validateActiveRequestLimit("U001"));
    }

    @Test
    void testActiveRequestLimit_at5() {
        for (int i = 0; i < 5; i++) {
            ServiceRequest req = new ServiceRequest();
            req.setReferenceId(UUID.randomUUID().toString());
            req.setUserId("U001");
            req.setResourceId("S101");
            req.setStatus(RequestStatus.SUBMITTED);
            serviceRequestRepository.save(req);
        }
        BusinessException exception = assertThrows(BusinessException.class, () -> validationService.validateActiveRequestLimit("U001"));
        assertTrue(exception.getMessage().contains("Maximum 5"));
    }

    @Test
    void testValidUser() {
        assertDoesNotThrow(() -> validationService.validateUser("U001"));
    }

    @Test
    void testUserNotFound() {
        assertThrows(BusinessException.class, () -> validationService.validateUser("NONEXIST"));
    }

}
