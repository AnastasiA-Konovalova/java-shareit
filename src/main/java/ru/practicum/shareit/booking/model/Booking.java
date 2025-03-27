package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    private Long id;

    @NotBlank(message = "Должно быть указано начало бронирования")
    private LocalDateTime beginBooking;

    @NotBlank(message = "Должен быть указан конец бронирования")
    private LocalDateTime endBooking;

    @NotBlank(message = "Должен быть передан предмет, запрашиваемый к бронированию")
    private Item item;

    @NotBlank(message = "Должен быть указан id пользователя, посылающего запрос на бронирование")
    private Long booker;

    private BookingStatus bookingStatus;
}