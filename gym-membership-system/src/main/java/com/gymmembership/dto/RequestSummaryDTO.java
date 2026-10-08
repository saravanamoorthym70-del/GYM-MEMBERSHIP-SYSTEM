package com.gymmembership.dto;

import com.gymmembership.entity.enums.RequestStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RequestSummaryDTO {

    private String referenceId;
    private String serviceName;
    private BigDecimal charge;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public RequestSummaryDTO() {
    }

    public RequestSummaryDTO(String referenceId, String serviceName, BigDecimal charge, RequestStatus status, LocalDateTime createdAt) {
        this.referenceId = referenceId;
        this.serviceName = serviceName;
        this.charge = charge;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
