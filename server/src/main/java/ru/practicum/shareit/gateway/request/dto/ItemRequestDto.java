package ru.practicum.shareit.gateway.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {

    private Long id;

    private Long requestorId;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    private List<ItemDto> items;
}