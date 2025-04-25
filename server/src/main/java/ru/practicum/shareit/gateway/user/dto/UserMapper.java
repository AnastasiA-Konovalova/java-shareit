package ru.practicum.shareit.gateway.user.dto;

import ru.practicum.shareit.gateway.user.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        if (user.getEmail() != null) {
            userDto.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userDto.setName(user.getName());
        }
        return userDto;
    }

    public static User toEntity(User user, UserDto userDto) {
        user.setId(userDto.getId());
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return user;
    }
}