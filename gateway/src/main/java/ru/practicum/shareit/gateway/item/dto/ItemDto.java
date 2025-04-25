package ru.practicum.shareit.gateway.item.dto;

import lombok.Data;
import org.apache.catalina.User;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User ownerId;

    private List<CommentDto> comments;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> commentsDto = new ArrayList<>();
}