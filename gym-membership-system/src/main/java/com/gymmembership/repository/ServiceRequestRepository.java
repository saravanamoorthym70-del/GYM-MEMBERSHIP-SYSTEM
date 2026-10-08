package com.gymmembership.repository;

import com.gymmembership.entity.ServiceRequest;
import com.gymmembership.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByUserId(String userId);
    List<ServiceRequest> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<ServiceRequest> findByReferenceId(String referenceId);
    long countByUserIdAndStatusIn(String userId, List<RequestStatus> statuses);
    List<ServiceRequest> findAllByOrderByCreatedAtDesc();
}
