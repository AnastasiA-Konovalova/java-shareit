package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto save(UserDto userDto);

    UserDto update(UserDto newUserDto, Long userId);

    void delete(long userId);
}