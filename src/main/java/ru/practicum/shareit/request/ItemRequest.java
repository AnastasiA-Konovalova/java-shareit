package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {

    private Long id;

    //@NotBlank(message = "Должен быть указан id пользователя, создавшего запрос")
    private Long requesterId;

    @NotBlank(message = "Должно быть указано описание вещи")
    private String description;

    private LocalDateTime requestCreate;
}