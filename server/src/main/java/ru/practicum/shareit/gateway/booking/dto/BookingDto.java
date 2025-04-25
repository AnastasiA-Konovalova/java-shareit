package ru.practicum.shareit.gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.gateway.booking.enums.BookingStatus;
import ru.practicum.shareit.gateway.item.model.Item;
import ru.practicum.shareit.gateway.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    private Long itemId;

    private User booker;

    private Item item;

    private BookingStatus status = BookingStatus.WAITING;
}