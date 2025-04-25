package ru.practicum.shareit.booking;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void serializeShouldSerializeBookingDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 4, 23, 12, 0);
        User user1 = new User();
        user1.setName("User name1");
        user1.setEmail("user@email1");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setBooker(user1);
        bookingDto.setStart(created);
        bookingDto.setEnd(created.plusHours(1));
        bookingDto.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User name1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("user@email1");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-04-23T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-04-23T13:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}