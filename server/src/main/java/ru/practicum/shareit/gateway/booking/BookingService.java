package ru.practicum.shareit.gateway.booking;

import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.enums.State;

import java.util.List;

public interface BookingService {

    BookingDto getById(Long bookingId, Long id);

    List<BookingDto> getAllByUser(Long id, State state);

    List<BookingDto> getAllByOwner(Long id, State state);

    BookingDto create(BookingDto bookingDto, Long userId);

    BookingDto changeBookingStatus(Long bookingId, Long id, Boolean approved);
}