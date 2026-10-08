package com.gymmembership.dto;

import com.gymmembership.entity.RequestStatusHistory;
import com.gymmembership.entity.enums.RequestStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RequestDetailsDTO {

    private String referenceId;
    private String userId;
    private String resourceId;
    private String nameSnapshot;
    private String phoneSnapshot;
    private String descriptionSnapshot;
    private String serviceIdSnapshot;
    private String serviceNameSnapshot;
    private int capacitySnapshot;
    private String serviceStatusSnapshot;
    private BigDecimal charge;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private boolean resourceReleased;
    private List<RequestStatusHistory> statusHistory;

    public RequestDetailsDTO() {
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getNameSnapshot() {
        return nameSnapshot;
    }

    public void setNameSnapshot(String nameSnapshot) {
        this.nameSnapshot = nameSnapshot;
    }

    public String getPhoneSnapshot() {
        return phoneSnapshot;
    }

    public void setPhoneSnapshot(String phoneSnapshot) {
        this.phoneSnapshot = phoneSnapshot;
    }

    public String getDescriptionSnapshot() {
        return descriptionSnapshot;
    }

    public void setDescriptionSnapshot(String descriptionSnapshot) {
        this.descriptionSnapshot = descriptionSnapshot;
    }

    public String getServiceIdSnapshot() {
        return serviceIdSnapshot;
    }

    public void setServiceIdSnapshot(String serviceIdSnapshot) {
        this.serviceIdSnapshot = serviceIdSnapshot;
    }

    public String getServiceNameSnapshot() {
        return serviceNameSnapshot;
    }

    public void setServiceNameSnapshot(String serviceNameSnapshot) {
        this.serviceNameSnapshot = serviceNameSnapshot;
    }

    public int getCapacitySnapshot() {
        return capacitySnapshot;
    }

    public void setCapacitySnapshot(int capacitySnapshot) {
        this.capacitySnapshot = capacitySnapshot;
    }

    public String getServiceStatusSnapshot() {
        return serviceStatusSnapshot;
    }

    public void setServiceStatusSnapshot(String serviceStatusSnapshot) {
        this.serviceStatusSnapshot = serviceStatusSnapshot;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public boolean isResourceReleased() {
        return resourceReleased;
    }

    public void setResourceReleased(boolean resourceReleased) {
        this.resourceReleased = resourceReleased;
    }

    public List<RequestStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<RequestStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
