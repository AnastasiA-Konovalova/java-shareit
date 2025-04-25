package ru.practicum.shareit.gateway.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.gateway.user.dto.UserDto;
import ru.practicum.shareit.gateway.user.dto.UserMapper;
import ru.practicum.shareit.gateway.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserMapperTest {

    static User user;
    static UserDto userDto1;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("Test User");
        userDto1.setEmail("test@example.com");
    }

    @Test
    void toDtoShouldMapUserToUserDtoWithAllFields() {
        UserDto userDto = UserMapper.toDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toDtoShouldMapUserToUserDtoWithNullNameAndEmail() {
        user.setName(null);
        user.setEmail(null);

        UserDto userDto = UserMapper.toDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void toDtoShouldMapUserToUserDtoWithNullName() {
        user.setName(null);
        user.setEmail("test@example.com");

        UserDto userDto = UserMapper.toDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toDtoShouldMapUserToUserDtoWithNullEmail() {
        user.setEmail(null);

        UserDto userDto = UserMapper.toDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void toEntityShouldMapUserDtoToUserWithAllFields() {
        User user = new User();
        User result = UserMapper.toEntity(user, userDto1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toEntity_shouldMapUserDtoToUser_withNullNameAndEmail() {
        userDto1.setName(null);
        userDto1.setEmail(null);

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        User result = UserMapper.toEntity(user, userDto1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Old Name");
        assertThat(result.getEmail()).isEqualTo("old@example.com");
    }

    @Test
    void toEntityShouldMapUserDtoToUserWithNullName() {
        userDto1.setName(null);
        userDto1.setEmail("test@example.com");

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        User result = UserMapper.toEntity(user, userDto1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Old Name");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toEntityShouldMapUserDtoToUserWithNullEmail() {
        userDto1.setName("Test User");
        userDto1.setEmail(null);

        user.setName("Old Name");
        user.setEmail("old@example.com");

        User result = UserMapper.toEntity(user, userDto1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("old@example.com");
    }
}