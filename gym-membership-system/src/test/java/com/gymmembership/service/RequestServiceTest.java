package com.gymmembership.service;

import com.gymmembership.dto.MembershipFormDTO;
import com.gymmembership.entity.Resource;
import com.gymmembership.entity.ServiceRequest;
import com.gymmembership.entity.enums.RequestStatus;
import com.gymmembership.entity.enums.Role;
import com.gymmembership.exception.BusinessException;
import com.gymmembership.repository.RequestStatusHistoryRepository;
import com.gymmembership.repository.ResourceRepository;
import com.gymmembership.repository.ServiceRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RequestServiceTest {

    @Autowired
    RequestService requestService;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    RequestStatusHistoryRepository requestStatusHistoryRepository;

    private MembershipFormDTO createValidForm() {
        MembershipFormDTO form = new MembershipFormDTO();
        form.setName("Arun Kumar");
        form.setPhone("9876543210");
        form.setDescription("Annual gym membership");
        form.setResourceId("S101");
        form.setAmount(new BigDecimal("999"));
        return form;
    }

    @Test
    void testChargeCalculation_999() {
        assertEquals(new BigDecimal("50"), requestService.calculateCharge(new BigDecimal("999")));
    }

    @Test
    void testChargeCalculation_1000() {
        assertEquals(BigDecimal.ZERO, requestService.calculateCharge(new BigDecimal("1000")));
    }

    @Test
    void testChargeCalculation_1001() {
        assertEquals(BigDecimal.ZERO, requestService.calculateCharge(new BigDecimal("1001")));
    }

    @Test
    void testChargeCalculation_null() {
        assertEquals(BigDecimal.ZERO, requestService.calculateCharge(null));
    }

    @Test
    void testChargeCalculation_zero() {
        assertEquals(BigDecimal.ZERO, requestService.calculateCharge(BigDecimal.ZERO));
    }

    @Test
    void testSuccessfulSubmission() {
        MembershipFormDTO form = createValidForm();
        Resource before = resourceRepository.findById("S101").get();
        int capacityBefore = before.getAvailableCapacity();
        
        ServiceRequest request = requestService.submitRequest("U001", form);
        
        assertNotNull(request);
        assertNotNull(request.getReferenceId());
        assertTrue(request.getReferenceId().startsWith("REQ-"));
        assertEquals(RequestStatus.SUBMITTED, request.getStatus());
        assertEquals(new BigDecimal("50"), request.getCharge());
        assertEquals("Arun Kumar", request.getNameSnapshot());
        assertEquals("9876543210", request.getPhoneSnapshot());
        assertEquals("Membership A", request.getServiceNameSnapshot());
        
        Resource after = resourceRepository.findById("S101").get();
        assertEquals(capacityBefore - 1, after.getAvailableCapacity());
    }

    @Test
    void testSubmissionWithInactiveResource() {
        MembershipFormDTO form = createValidForm();
        form.setResourceId("S103");
        assertThrows(BusinessException.class, () -> requestService.submitRequest("U001", form));
    }

    @Test
    void testValidStatusTransition_SubmittedToApproved() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        ServiceRequest updated = serviceRequestRepository.findByReferenceId(req.getReferenceId()).get();
        assertEquals(RequestStatus.APPROVED_ASSIGNED, updated.getStatus());
    }

    @Test
    void testValidStatusTransition_FullCycle() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        ServiceRequest updated = serviceRequestRepository.findByReferenceId(req.getReferenceId()).get();
        assertEquals(RequestStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void testInvalidStatusTransition_CompletedCantAdvance() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        assertThrows(BusinessException.class, () -> requestService.advanceRequestStatus(req.getReferenceId(), "A001"));
    }

    @Test
    void testCancelSubmittedRequest() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        Resource before = resourceRepository.findById("S101").get();
        int capBefore = before.getAvailableCapacity();
        
        requestService.cancelRequest(req.getReferenceId(), "U001");
        
        ServiceRequest cancelled = serviceRequestRepository.findByReferenceId(req.getReferenceId()).get();
        assertEquals(RequestStatus.CANCELLED, cancelled.getStatus());
        assertNotNull(cancelled.getCancelledAt());
        assertTrue(cancelled.isResourceReleased());
        
        Resource after = resourceRepository.findById("S101").get();
        assertEquals(capBefore + 1, after.getAvailableCapacity());
    }

    @Test
    void testRepeatedCancellation() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        requestService.cancelRequest(req.getReferenceId(), "U001");
        assertThrows(BusinessException.class, () -> requestService.cancelRequest(req.getReferenceId(), "U001"));
    }

    @Test
    void testSnapshotPreservation() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        assertEquals(10, req.getCapacitySnapshot());
        assertEquals("Membership A", req.getServiceNameSnapshot());
        
        Resource resource = resourceRepository.findById("S101").get();
        resource.setCapacity(7);
        resourceRepository.save(resource);
        
        ServiceRequest reloaded = serviceRequestRepository.findByReferenceId(req.getReferenceId()).get();
        assertEquals(10, reloaded.getCapacitySnapshot());
        assertEquals("Membership A", reloaded.getServiceNameSnapshot());
    }

    @Test
    void testUnauthorizedAccess() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        assertThrows(BusinessException.class, () -> requestService.getRequestDetails(req.getReferenceId(), "U002", Role.USER));
    }

    @Test
    void testUnauthorizedCancel() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        assertThrows(BusinessException.class, () -> requestService.cancelRequest(req.getReferenceId(), "U002"));
    }

    @Test
    void testCancelNonSubmittedRequest() {
        ServiceRequest req = requestService.submitRequest("U001", createValidForm());
        requestService.advanceRequestStatus(req.getReferenceId(), "A001");
        assertThrows(BusinessException.class, () -> requestService.cancelRequest(req.getReferenceId(), "U001"));
    }

    @Test
    void testSixthActiveRequestRejected() {
        for (int i = 0; i < 5; i++) {
            MembershipFormDTO form = createValidForm();
            form.setDescription("Request " + (i + 1));
            requestService.submitRequest("U001", form);
        }
        MembershipFormDTO sixthForm = createValidForm();
        sixthForm.setDescription("Request 6");
        assertThrows(BusinessException.class, () -> requestService.submitRequest("U001", sixthForm));
    }
}
