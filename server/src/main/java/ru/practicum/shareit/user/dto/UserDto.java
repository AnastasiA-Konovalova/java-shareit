package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    @Email(message = "Неверный формат email")
    private String email;

    private String name;
}