package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getAll();

    Optional<User> getById(Long userId);

    User save(User user);

    User update(User newUser, Long userId);

    void delete(Long userId);
}