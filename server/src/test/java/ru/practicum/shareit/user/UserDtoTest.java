package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

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

    @Test
    void deserializeShouldDeserializeUserDto() throws Exception {
        String jsonContent = """
                //JSON
                {
                    "id": 1,
                    "email": "user@email.com",
                    "name": "User Name"
                }
                """;

        UserDto userDto = json.parse(jsonContent).getObject();

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getEmail()).isEqualTo("user@email.com");
        assertThat(userDto.getName()).isEqualTo("User Name");
    }

    @Test
    void deserializeShouldFailWhenEmailIsInvalid() throws Exception {
        String jsonContent = """
                //JSON
                {
                    "id": 1,
                    "email": "invalid-email",
                    "name": "User Name"
                }
                """;

        UserDto userDto = json.parse(jsonContent).getObject();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                v.getMessage().equals("Неверный формат email"));
    }
}