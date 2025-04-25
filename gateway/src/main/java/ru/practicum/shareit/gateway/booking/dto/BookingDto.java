package ru.practicum.shareit.gateway.booking.dto;

import lombok.Data;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private UserDto booker;

    private ItemDto item;

    private BookingStatus status = BookingStatus.WAITING;
}
