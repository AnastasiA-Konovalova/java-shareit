package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(new ArrayList<>());
        return itemRequestDto;
    }

    public static ItemRequest toEntity(ItemRequest itemRequest, ItemRequestDto itemRequestDto, Long userId) {
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestorId(userId);

        return itemRequest;
    }
}