package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto save(UserDto userDto);

    UserDto update(UserDto newDto, Long userId);

    void delete(Long userId);
}