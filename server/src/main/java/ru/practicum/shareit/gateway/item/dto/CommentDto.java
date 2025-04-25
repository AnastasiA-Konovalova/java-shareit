package ru.practicum.shareit.gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.gateway.item.model.Item;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    @NotBlank(message = "Text не может отсутствовать")
    private String text;

    private Item item;

    private String authorName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
}