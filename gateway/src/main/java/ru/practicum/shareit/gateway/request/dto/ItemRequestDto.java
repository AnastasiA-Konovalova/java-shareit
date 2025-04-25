package ru.practicum.shareit.gateway.request.dto;

import ru.practicum.shareit.gateway.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestDto {
    private Long id;

    private Long requestorId;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}