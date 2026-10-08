package com.gymmembership.entity;

import com.gymmembership.entity.enums.RequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_status_history")
public class RequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referenceId;

    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RequestStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus newStatus;

    private LocalDateTime eventAt;

    public RequestStatusHistory() {
    }

    public RequestStatusHistory(String referenceId, String actorId, RequestStatus oldStatus, RequestStatus newStatus, LocalDateTime eventAt) {
        this.referenceId = referenceId;
        this.actorId = actorId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.eventAt = eventAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public RequestStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(RequestStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public RequestStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RequestStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getEventAt() {
        return eventAt;
    }

    public void setEventAt(LocalDateTime eventAt) {
        this.eventAt = eventAt;
    }
}
