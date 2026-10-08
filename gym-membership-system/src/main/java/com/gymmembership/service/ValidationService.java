package com.gymmembership.service;

import com.gymmembership.dto.MembershipFormDTO;
import com.gymmembership.entity.Resource;
import com.gymmembership.entity.User;
import com.gymmembership.entity.enums.RequestStatus;
import com.gymmembership.entity.enums.ResourceStatus;
import com.gymmembership.entity.enums.UserStatus;
import com.gymmembership.exception.BusinessException;
import com.gymmembership.repository.ResourceRepository;
import com.gymmembership.repository.ServiceRequestRepository;
import com.gymmembership.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ValidationService {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    public ValidationService(UserRepository userRepository,
                             ResourceRepository resourceRepository,
                             ServiceRequestRepository serviceRequestRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public void validateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found."));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User is not eligible.");
        }
    }

    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Name cannot be blank.");
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new BusinessException("Phone number must contain exactly 10 digits.");
        }
    }

    public void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new BusinessException("Description cannot be blank.");
        }
    }

    public void validateActiveRequestLimit(String userId) {
        List<RequestStatus> activeStatuses = Arrays.asList(
                RequestStatus.SUBMITTED,
                RequestStatus.APPROVED_ASSIGNED,
                RequestStatus.IN_PROGRESS
        );
        long count = serviceRequestRepository.countByUserIdAndStatusIn(userId, activeStatuses);
        if (count >= 5) {
            throw new BusinessException("Maximum 5 active requests allowed.");
        }
    }

    public void validateResourceEligibility(String resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new BusinessException("Selected resource is not available."));
        if (resource.getStatus() != ResourceStatus.ACTIVE) {
            throw new BusinessException("Selected service is inactive.");
        }
    }

    public void validateCapacity(String resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new BusinessException("Selected resource is not available."));
        if (resource.getAvailableCapacity() <= 0) {
            throw new BusinessException("Selected resource is not available.");
        }
    }

    public void validateSubmission(String userId, MembershipFormDTO form) {
        validateUser(userId);
        validateName(form.getName());
        validatePhone(form.getPhone());
        validateDescription(form.getDescription());
        validateActiveRequestLimit(userId);
        validateResourceEligibility(form.getResourceId());
        validateCapacity(form.getResourceId());
    }
}
