package com.gymmembership.service;

import com.gymmembership.dto.MembershipFormDTO;
import com.gymmembership.dto.RequestDetailsDTO;
import com.gymmembership.dto.RequestSummaryDTO;
import com.gymmembership.entity.RequestStatusHistory;
import com.gymmembership.entity.Resource;
import com.gymmembership.entity.ServiceRequest;
import com.gymmembership.entity.User;
import com.gymmembership.entity.enums.RequestStatus;
import com.gymmembership.entity.enums.ResourceStatus;
import com.gymmembership.entity.enums.Role;
import com.gymmembership.entity.enums.UserStatus;
import com.gymmembership.exception.BusinessException;
import com.gymmembership.repository.RequestStatusHistoryRepository;
import com.gymmembership.repository.ResourceRepository;
import com.gymmembership.repository.ServiceRequestRepository;
import com.gymmembership.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private final ValidationService validationService;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final RequestStatusHistoryRepository requestStatusHistoryRepository;

    @Autowired
    public RequestService(ValidationService validationService,
                          UserRepository userRepository,
                          ResourceRepository resourceRepository,
                          ServiceRequestRepository serviceRequestRepository,
                          RequestStatusHistoryRepository requestStatusHistoryRepository) {
        this.validationService = validationService;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.requestStatusHistoryRepository = requestStatusHistoryRepository;
    }

    public BigDecimal calculateCharge(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (amount.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("50");
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    public ServiceRequest submitRequest(String userId, MembershipFormDTO form) {
        // Step 1: Full validation
        validationService.validateSubmission(userId, form);

        // Step 2: Re-fetch and revalidate (BR7)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found."));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User is not eligible.");
        }

        Resource resource = resourceRepository.findById(form.getResourceId())
                .orElseThrow(() -> new BusinessException("Selected resource is not available."));
        if (resource.getStatus() != ResourceStatus.ACTIVE) {
            throw new BusinessException("Selected service is inactive.");
        }
        if (resource.getAvailableCapacity() <= 0) {
            throw new BusinessException("Selected resource is not available.");
        }

        // Step 3: Calculate charge
        BigDecimal charge = calculateCharge(form.getAmount());

        // Step 4: Generate reference ID
        String referenceId = generateReferenceId();

        // Step 5: Create request with snapshots
        ServiceRequest request = new ServiceRequest();
        request.setReferenceId(referenceId);
        request.setUserId(userId);
        request.setResourceId(form.getResourceId());
        request.setNameSnapshot(form.getName());
        request.setPhoneSnapshot(form.getPhone());
        request.setDescriptionSnapshot(form.getDescription());
        request.setServiceIdSnapshot(resource.getId());
        request.setServiceNameSnapshot(resource.getServiceName());
        request.setCapacitySnapshot(resource.getCapacity());
        request.setServiceStatusSnapshot(resource.getStatus().name());
        request.setCharge(charge);
        request.setStatus(RequestStatus.SUBMITTED);
        request.setResourceReleased(false);

        // Step 6: Save request
        request = serviceRequestRepository.save(request);

        // Step 7: Allocate resource (decrease capacity)
        resource.setAvailableCapacity(resource.getAvailableCapacity() - 1);
        resourceRepository.save(resource);

        // Step 8: Create status history
        RequestStatusHistory history = new RequestStatusHistory(
                referenceId, userId, null, RequestStatus.SUBMITTED, LocalDateTime.now());
        requestStatusHistoryRepository.save(history);

        return request;
    }

    public List<RequestSummaryDTO> getUserRequests(String userId) {
        List<ServiceRequest> requests = serviceRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return requests.stream().map(r -> new RequestSummaryDTO(
                r.getReferenceId(), r.getServiceNameSnapshot(), r.getCharge(), r.getStatus(), r.getCreatedAt()
        )).collect(Collectors.toList());
    }

    public RequestDetailsDTO getRequestDetails(String referenceId, String userId, Role role) {
        ServiceRequest request = serviceRequestRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new BusinessException("Request not found."));
        if (role == Role.USER && !request.getUserId().equals(userId)) {
            throw new BusinessException("You are not authorized to access this request.");
        }
        List<RequestStatusHistory> history = requestStatusHistoryRepository.findByReferenceIdOrderByEventAtAsc(referenceId);
        
        RequestDetailsDTO dto = new RequestDetailsDTO();
        dto.setReferenceId(request.getReferenceId());
        dto.setUserId(request.getUserId());
        dto.setResourceId(request.getResourceId());
        dto.setNameSnapshot(request.getNameSnapshot());
        dto.setPhoneSnapshot(request.getPhoneSnapshot());
        dto.setDescriptionSnapshot(request.getDescriptionSnapshot());
        dto.setServiceIdSnapshot(request.getServiceIdSnapshot());
        dto.setServiceNameSnapshot(request.getServiceNameSnapshot());
        dto.setCapacitySnapshot(request.getCapacitySnapshot());
        dto.setServiceStatusSnapshot(request.getServiceStatusSnapshot());
        dto.setCharge(request.getCharge());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setCancelledAt(request.getCancelledAt());
        dto.setResourceReleased(request.isResourceReleased());
        dto.setStatusHistory(history);
        return dto;
    }

    @Transactional
    public void cancelRequest(String referenceId, String userId) {
        ServiceRequest request = serviceRequestRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new BusinessException("Request not found."));
        if (!request.getUserId().equals(userId)) {
            throw new BusinessException("You are not authorized to access this request.");
        }
        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessException("Request has already been cancelled.");
        }
        if (request.getStatus() != RequestStatus.SUBMITTED) {
            throw new BusinessException("Only SUBMITTED requests can be cancelled.");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.CANCELLED);
        request.setCancelledAt(LocalDateTime.now());

        // Release resource exactly once
        if (!request.isResourceReleased()) {
            Resource resource = resourceRepository.findById(request.getResourceId())
                    .orElseThrow(() -> new BusinessException("Resource not found."));
            resource.setAvailableCapacity(resource.getAvailableCapacity() + 1);
            resourceRepository.save(resource);
            request.setResourceReleased(true);
        }

        serviceRequestRepository.save(request);

        RequestStatusHistory history = new RequestStatusHistory(
                referenceId, userId, oldStatus, RequestStatus.CANCELLED, LocalDateTime.now());
        requestStatusHistoryRepository.save(history);
    }

    @Transactional
    public void advanceRequestStatus(String referenceId, String actorId) {
        ServiceRequest request = serviceRequestRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new BusinessException("Request not found."));
        RequestStatus currentStatus = request.getStatus();
        RequestStatus nextStatus = getNextValidStatus(currentStatus);
        if (nextStatus == null) {
            throw new BusinessException("Invalid status transition. No further action available.");
        }

        request.setStatus(nextStatus);
        serviceRequestRepository.save(request);

        RequestStatusHistory history = new RequestStatusHistory(
                referenceId, actorId, currentStatus, nextStatus, LocalDateTime.now());
        requestStatusHistoryRepository.save(history);
    }

    private RequestStatus getNextValidStatus(RequestStatus current) {
        return switch (current) {
            case SUBMITTED -> RequestStatus.APPROVED_ASSIGNED;
            case APPROVED_ASSIGNED -> RequestStatus.IN_PROGRESS;
            case IN_PROGRESS -> RequestStatus.COMPLETED;
            default -> null;
        };
    }

    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    private String generateReferenceId() {
        long count = serviceRequestRepository.count();
        String refId;
        do {
            count++;
            refId = String.format("REQ-%06d", count);
        } while (serviceRequestRepository.findByReferenceId(refId).isPresent());
        return refId;
    }
}
