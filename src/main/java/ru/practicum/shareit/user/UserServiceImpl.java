package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperToDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.getAll();
        if (users.isEmpty()) {
            log.warn("Список пользователей пуст");
            throw new NotFoundException("Ошибка в получении списка пользователей. Список пуст");
        }

        return users.stream()
                .map(UserMapperToDto::toDto)
                .toList();
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId).orElseThrow(() -> {
            log.warn("Неправильно введен id пользователя");
            return new NotFoundException("Ошибка в получении пользователя с id " + userId + ".");
        });

        return UserMapperToDto.toDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = UserMapperToDto.toEntity(new User(), userDto);

        return UserMapperToDto.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto newUserDto, Long userId) {
        User user = UserMapperToDto.toEntity(new User(), newUserDto);

        return UserMapperToDto.toDto(userRepository.update(user, userId));
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }
}