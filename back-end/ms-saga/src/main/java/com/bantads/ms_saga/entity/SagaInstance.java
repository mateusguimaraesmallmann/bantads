package com.bantads.ms_saga.entity;

import com.bantads.ms_saga.dtos.saga.SagaStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saga_instance")
@Data
@NoArgsConstructor
public class SagaInstance {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID correlationId;

    @Column(nullable = false)
    private String sagaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus currentState;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;
    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public SagaInstance(UUID correlationId, String sagaType, String payload) {
        this.id = UUID.randomUUID();
        this.correlationId = correlationId;
        this.sagaType = sagaType;
        this.payload = payload;
        this.currentState = SagaStatus.STARTED;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}