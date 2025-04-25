package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void serializeShouldSerializeCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 4, 23, 12, 0);
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("test");
        commentDto.setCreated(created);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-23T12:00:00");
    }

    @Test
    void deserializeShouldDeserializeCommentDto() throws Exception {
        String jsonContent = """
                //JSON
                {
                    "id": 1,
                    "text": "Great item!",
                    "authorName": "test",
                    "created": "2025-04-23T12:00:00"
                }
                """;

        CommentDto commentDto = json.parse(jsonContent).getObject();

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("test");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2025, 4, 23, 12, 0));
    }

    @Test
    void deserializeShouldFailWhenTextIsBlank() throws Exception {
        String jsonContent = """
                //JSON
                {
                    "id": 1,
                    "text": "",
                    "authorName": "test",
                    "created": "2025-04-23T12:00:00"
                }
                """;

        CommentDto commentDto = json.parse(jsonContent).getObject();
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("text") &&
                violation.getMessage().equals("Text не может отсутствовать"));
    }

    @Test
    void deserialize_shouldFailWhenTextIsNull() throws Exception {
        String jsonContent = """
                //JSON
                {
                    "id": 1,
                    "text": null,
                    "authorName": "test",
                    "created": "2025-04-23T12:00:00"
                }
                """;

        CommentDto commentDto = json.parse(jsonContent).getObject();
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("text") &&
                violation.getMessage().equals("Text не может отсутствовать"));
    }
}