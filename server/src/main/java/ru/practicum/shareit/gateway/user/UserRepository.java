package ru.practicum.shareit.gateway.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.gateway.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}