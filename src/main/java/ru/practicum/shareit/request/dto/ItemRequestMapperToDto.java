package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;

@Service
public class ItemRequestMapperToDto {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        LocalDateTime localDateTime = LocalDateTime.now();
        itemRequestDto.setRequestCreate(localDateTime);
        itemRequestDto.setRequesterId(itemRequest.getRequesterId());

        return itemRequestDto;
    }

    public static ItemRequest toEntity(ItemRequest itemRequest, ItemRequestDto itemRequestDto) {
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setRequestCreate(itemRequestDto.getRequestCreate());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequesterId(itemRequestDto.getRequesterId());

        return itemRequest;
    }
}
