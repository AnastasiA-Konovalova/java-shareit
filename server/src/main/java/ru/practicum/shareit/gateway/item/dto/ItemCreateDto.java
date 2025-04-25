package ru.practicum.shareit.gateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.gateway.user.model.User;

@Data
public class ItemCreateDto {

    private Long id;

    @NotBlank(message = "имя не может быть пустым")
    private String name;

    @NotBlank(message = "описание не может быть пустым")
    private String description;

    @NotNull(message = "статус должен быть указан")
    private Boolean available;

    private User owner;

    private Long requestId;
}