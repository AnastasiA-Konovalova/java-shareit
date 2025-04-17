package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStart(booking.getStartBooking());
        bookingDto.setEnd(booking.getEndBooking());
        bookingDto.setStatus(booking.getBookingStatus());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(booking.getItem());
        bookingDto.setItemId(booking.getItem().getId());

        return bookingDto;
    }

    public static Booking toEntity(Booking booking, BookingDto bookingDto, User booker, Item item) {
        booking.setId(bookingDto.getId());
        booking.setItem(item);
        booking.setStartBooking(bookingDto.getStart());
        booking.setEndBooking(bookingDto.getEnd());
        booking.setBookingStatus(bookingDto.getStatus());
        booking.setBooker(booker);

        return booking;
    }
}