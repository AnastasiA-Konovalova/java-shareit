package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.apache.catalina.User;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}