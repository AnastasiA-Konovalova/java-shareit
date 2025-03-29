package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
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

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId).orElseThrow(() -> {
            log.warn("Неправильно введен id пользователя");
            return new NotFoundException("Ошибка в получении пользователя с id " + userId + ".");
        });

        return UserMapper.toDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        uniqueEmailValidate(userDto);
        User user = UserMapper.toEntity(new User(), userDto);

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto newDto, Long userId) {
        if (newDto.getEmail() != null) {
            uniqueEmailValidate(newDto);
        }
        getById(userId);
        User user = UserMapper.toEntity(new User(), newDto);
        user.setId(userId);
        return UserMapper.toDto(userRepository.update(user));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    private void uniqueEmailValidate(UserDto userDto) {
        List<User> userList = userRepository.getAll();
        boolean emailExists = userList.stream()
                .anyMatch(user -> userDto.getEmail().equals(user.getEmail()));
        if (emailExists) {
            throw new IllegalStateException("пользователь с таким email уже существует");
        }
    }
}