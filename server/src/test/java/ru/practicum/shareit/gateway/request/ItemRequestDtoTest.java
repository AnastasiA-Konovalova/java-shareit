package ru.practicum.shareit.gateway.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void serializeShouldSerializeItemRequestDto() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User1");
        user1.setEmail("user1@email.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setOwner(user1);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setRequestorId(1L);
        requestDto.setDescription("Desc");
        requestDto.setCreated(LocalDateTime.of(2025, 4, 23, 12, 0, 0));
        requestDto.setItems(Collections.singletonList(itemDto));
        requestDto.setRequestorId(user1.getId());

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Desc");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-23T12:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.items[0].lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.items[0].nextBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.items[0].comments").isNull();
    }
}