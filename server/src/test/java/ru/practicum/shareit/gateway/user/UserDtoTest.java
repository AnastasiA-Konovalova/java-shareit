package ru.practicum.shareit.gateway.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void serializeShouldSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user@email.com");
        userDto.setName("User Name");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@email.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("User Name");
    }
}