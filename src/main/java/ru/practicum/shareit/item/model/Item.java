package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Item {

    private Long id;

    @NotBlank(message = "имя не может быть пустым")
    private String name;

    @NotBlank(message = "описание не может быть пустым")
    private String description;

    @NotNull(message = "статус должен быть указан")
    private Boolean available;

    @Positive(message = "id не может быть отрицательным")
    @NotNull(message = "id должен быть указан")
    private Long ownerId;
}