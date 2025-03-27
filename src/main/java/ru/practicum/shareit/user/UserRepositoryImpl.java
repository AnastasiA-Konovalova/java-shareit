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

    private List<User> users = new ArrayList<>();
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
        Long id = idGenerator.getAndIncrement();
        user.setId(id);

        users.add(user);

        return user;
    }

    @Override
    public User update(User newUser, Long userId) {
        Optional<User> optionalUser = getById(userId);
        User user = optionalUser.orElseThrow(() -> new NotFoundException("Такой пользователь отсутствует в списке"));
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
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
}