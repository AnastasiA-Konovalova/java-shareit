package ru.practicum.shareit.gateway.request.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "request_create")
    private LocalDateTime created;
}