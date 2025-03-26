package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    private Long id;

    @Email(message = "неверный формат email")
    private String email;

    private String name;
}
