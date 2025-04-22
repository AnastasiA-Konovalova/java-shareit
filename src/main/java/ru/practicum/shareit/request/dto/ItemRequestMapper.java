package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        //LocalDateTime localDateTime = LocalDateTime.now();
        //itemRequestDto.setCreated(localDateTime);
        //itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        //itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(new ArrayList<>()); // Всегда пустой список по умолчанию
        return itemRequestDto;    }

    public static ItemRequestDto toDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        LocalDateTime localDateTime = LocalDateTime.now();
        itemRequestDto.setCreated(localDateTime);
        itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        itemRequestDto.setItems(items);

        return itemRequestDto;
    }

    public static ItemRequest toEntity(ItemRequest itemRequest, ItemRequestDto itemRequestDto, Long userId) {
        //itemRequest.setId(itemRequestDto.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestorId(userId);

        return itemRequest;
    }
}
