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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({UserServiceImpl.class})
class UserServiceImplTest {

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

        assertThat(users).hasSize(2);
        assertThat(users).anyMatch(u -> u.getEmail().equals("user1@email.com"));
        assertThat(users).anyMatch(u -> u.getEmail().equals("user2@email.com"));
    }

    @Test
    void getAllTestWithNoUsers() {
        userRepository.deleteAll();
        entityManager.flush();

        List<UserDto> users = userService.getAll();

        assertThat(users).isEmpty();
    }

    @Test
    void getByIdTest() {
        UserDto userDto = userService.getById(user1.getId());

        assertThat(userDto.getId()).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user1.getId());
        assertThat(userDto.getName()).isEqualTo("User1");
        assertThat(userDto.getEmail()).isEqualTo("user1@email.com");
    }

    @Test
    void getByIdTestWithInvalidId() {
        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ошибка в получении пользователя с id 999.");
    }

    @Test
    void saveTest() {
        newUserDto.setName("New User");
        newUserDto.setEmail("newuser@email.com");

        UserDto savedUserDto = userService.save(newUserDto);

        assertThat(savedUserDto.getId()).isNotNull();
        assertThat(savedUserDto.getName()).isEqualTo("New User");
        assertThat(savedUserDto.getEmail()).isEqualTo("newuser@email.com");

        User savedUser = entityManager.find(User.class, savedUserDto.getId());
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getEmail()).isEqualTo("newuser@email.com");
    }

    @Test
    void saveTestWithDuplicateEmail() {
        newUserDto.setName("Duplicate User");
        newUserDto.setEmail("user1@email.com");

        assertThatThrownBy(() -> userService.save(newUserDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Пользователь email" + newUserDto.getEmail() + "уже существует");
    }

    @Test
    void updateTest() {
        newUserDto.setName("Updated User");
        newUserDto.setEmail("updated@email.com");

        UserDto updatedUserDto = userService.update(newUserDto, user1.getId());

        assertThat(updatedUserDto.getId()).isEqualTo(user1.getId());
        assertThat(updatedUserDto.getName()).isEqualTo("Updated User");
        assertThat(updatedUserDto.getEmail()).isEqualTo("updated@email.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void updateTestWithNullEmail() {
        newUserDto.setName("Updated Name");
        newUserDto.setEmail(null);

        UserDto updatedUserDto = userService.update(newUserDto, user1.getId());

        assertThat(updatedUserDto.getId()).isEqualTo(user1.getId());
        assertThat(updatedUserDto.getName()).isEqualTo("Updated Name");
        assertThat(updatedUserDto.getEmail()).isEqualTo("user1@email.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("user1@email.com");
    }

    @Test
    void updateTestWithDuplicateEmail() {
        newUserDto.setName("Updated User");
        newUserDto.setEmail("user2@email.com");

        assertThatThrownBy(() -> userService.update(newUserDto, user1.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Пользователь email" + newUserDto.getEmail() + "уже существует");
    }

    @Test
    void updateTestWithInvalidId() {
        newUserDto.setName("Updated User");
        newUserDto.setEmail("updated@email.com");

        assertThatThrownBy(() -> userService.update(newUserDto, 999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteTest() {
        userService.delete(user1.getId());

        User deletedUser = entityManager.find(User.class, user1.getId());
        assertThat(deletedUser == null || !userRepository.existsById(user1.getId())).isTrue();
    }

    @Test
    void deleteTestWithInvalidId() {
        userService.delete(999L);

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void updateTestWithNullName() {
        newUserDto.setName(null);
        newUserDto.setEmail("updated@email.com");

        UserDto updatedUserDto = userService.update(newUserDto, user1.getId());

        assertThat(updatedUserDto.getId()).isEqualTo(user1.getId());
        assertThat(updatedUserDto.getName()).isEqualTo("User1");
        assertThat(updatedUserDto.getEmail()).isEqualTo("updated@email.com");

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser.getName()).isEqualTo("User1");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
    }
}