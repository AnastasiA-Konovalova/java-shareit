package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapperToDto {

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