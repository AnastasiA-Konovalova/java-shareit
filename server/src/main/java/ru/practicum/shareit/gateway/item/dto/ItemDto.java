package ru.practicum.shareit.gateway.item.dto;

import lombok.Data;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private List<CommentDto> comments;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> commentsDto = new ArrayList<>();
}