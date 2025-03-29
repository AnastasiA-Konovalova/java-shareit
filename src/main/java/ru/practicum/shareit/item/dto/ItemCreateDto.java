package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCreateDto {

    private Long id;

    @NotBlank(message = "имя не может быть пустым")
    private String name;

    @NotBlank(message = "описание не может быть пустым")
    private String description;

    @NotNull(message = "статус должен быть указан")
    private Boolean available;

    private Long ownerId;
}