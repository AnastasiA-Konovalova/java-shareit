package ru.practicum.shareit.gateway.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    private String text;

    private ItemDto itemId;

    private String authorName;

    private LocalDateTime created;
}