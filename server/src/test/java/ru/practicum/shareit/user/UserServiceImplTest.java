package ru.practicum.shareit.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserServiceImpl.class})
public class UserServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    static User user1;
    static User user2;
    static UserDto userDto1;
    static UserDto newUserDto;


    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@email.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@email.com");
        entityManager.persist(user2);

        userDto1 = new UserDto();
        userDto1.setName("User1");
        userDto1.setEmail("user1@email.com");

        newUserDto = new UserDto();

        entityManager.flush();
    }

    @Test
    void getAllTest() {
        List<UserDto> users = userService.getAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user1@email.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user2@email.com")));
    }

    @Test
    void getAllTestWithNoUsers() {
        userRepository.deleteAll();
        entityManager.flush();

        List<UserDto> users = userService.getAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void getByIdTest() {
        UserDto userDto = userService.getById(user1.getId());

        assertNotNull(userDto.getId());
        assertEquals(user1.getId(), userDto.getId());
        assertEquals("User1", userDto.getName());
        assertEquals("user1@email.com", userDto.getEmail());
    }

    @Test
    void getByIdTestWithInvalidId() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userService.getById(999L));
        assertEquals("Ошибка в получении пользователя с id 999.", exception.getMessage());
    }

    @Test
    void saveTest() {
        newUserDto.setName("New User");
        newUserDto.setEmail("newuser@email.com");

        UserDto savedUserDto = userService.save(newUserDto);

        assertNotNull(savedUserDto.getId());
        assertEquals("New User", savedUserDto.getName());
        assertEquals("newuser@email.com", savedUserDto.getEmail());

        User savedUser = entityManager.find(User.class, savedUserDto.getId());
        assertEquals("New User", savedUser.getName());
        assertEquals("newuser@email.com", savedUser.getEmail());
    }

    @Test
    void saveTestWithDuplicateEmail() {
        newUserDto.setName("Duplicate User");
        newUserDto.setEmail("user1@email.com");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.save(newUserDto));
        assertEquals("Пользователь email" + newUserDto.getEmail() + "уже существует", exception.getMessage());
    }

    @Test
    void updateTest() {
        newUserDto.setName("Updated User");
        newUserDto.setEmail("updated@email.com");

        UserDto updatedUserDto = userService.update(newUserDto, user1.getId());

        assertEquals(user1.getId(), updatedUserDto.getId());
        assertEquals("Updated User", updatedUserDto.getName());
        assertEquals("updated@email.com", updatedUserDto.getEmail());

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updated@email.com", updatedUser.getEmail());
    }

    @Test
    void updateTestWithNullEmail() {
        newUserDto.setName("Updated Name");
        newUserDto.setEmail(null);

        UserDto updatedUserDto = userService.update(newUserDto, user1.getId());

        assertEquals(user1.getId(), updatedUserDto.getId());
        assertEquals("Updated Name", updatedUserDto.getName());
        assertEquals("user1@email.com", updatedUserDto.getEmail());

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("user1@email.com", updatedUser.getEmail());
    }

    @Test
    void updateTestWithDuplicateEmail() {
        newUserDto.setName("Updated User");
        newUserDto.setEmail("user2@email.com");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                userService.update(newUserDto, user1.getId()));
        assertEquals("Пользователь email" + newUserDto.getEmail() + "уже существует", exception.getMessage());
    }

    @Test
    void updateTestWithInvalidId() {
        newUserDto.setName("Updated User");
        newUserDto.setEmail("updated@email.com");

        assertThrows(EntityNotFoundException.class, () ->
                userService.update(newUserDto, 999L));
    }

    @Test
    void deleteTest() {
        userService.delete(user1.getId());

        User deletedUser = entityManager.find(User.class, user1.getId());
        assertTrue(deletedUser == null || !userRepository.existsById(user1.getId()));
    }

    @Test
    void deleteTestWithInvalidId() {
        userService.delete(999L);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }


    @Test
    void updateTestWithNullName() {
        newUserDto.setName(null);
        newUserDto.setEmail("updated@email.com");

        UserDto updatedUserDto = userService.update(newUserDto, user1.getId());

        assertEquals(user1.getId(), updatedUserDto.getId());
        assertEquals("User1", updatedUserDto.getName());
        assertEquals("updated@email.com", updatedUserDto.getEmail());

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertEquals("User1", updatedUser.getName());
        assertEquals("updated@email.com", updatedUser.getEmail());
    }
}