package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {

    List<User> users = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public Optional<User> getById(Long userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public User save(User user) {
        emailExists(user);

        Long id = idGenerator.getAndIncrement();

        User createUser = new User();
        createUser.setId(id);
        createUser.setEmail(user.getEmail());
        createUser.setName(user.getName());

        users.add(createUser);

        return createUser;
    }

    @Override
    public User update(User newUser, Long userId) {
        Optional<User> optionalUser = getById(userId);

        User user = optionalUser.orElseThrow(() -> new NotFoundException("Такой пользователь отсутствует в списке"));
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            emailExists(newUser);
            user.setEmail(newUser.getEmail());
        }

        return user;
    }

    @Override
    public void delete(Long userId) {
        Optional<User> optionalUser = getById(userId);

        User user = optionalUser.orElseThrow(() -> new NotFoundException("Такой пользователь отсутствует в списке"));
        users.remove(user);
    }

    private void emailExists(User user) {
        boolean emailExists = users.stream()
                .anyMatch(createUser -> user.getEmail().equals(createUser.getEmail()));
        if (emailExists) {
            throw new IllegalStateException("пользователь с таким email уже существует");
        }
    }
}