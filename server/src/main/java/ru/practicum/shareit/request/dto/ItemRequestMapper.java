package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(items);

        return itemRequestDto;
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> itemRequest, List<Item> items) {
        List<ItemRequestDto> itemRequestDtoResult = new ArrayList<>();
        for (ItemRequest itemRequestDto : itemRequest) {
            List<Item> itemsForRequest = items.stream()
                    .filter(item -> itemRequestDto.getId().equals(item.getRequest().getId()))
                    .toList();
            itemRequestDtoResult.add(toDto(itemRequestDto, itemsForRequest));
        }

        return itemRequestDtoResult;
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> requests, Map<Long, List<Item>> items) {
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> related = items.getOrDefault(request.getId(), List.of());
            ItemRequestDto itemRequestDto = toDto(request, related);
            result.add(itemRequestDto);
        }
        return result;
    }

    public static ItemRequest toEntity(ItemRequest itemRequest, ItemRequestDto itemRequestDto, Long userId) {
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestorId(userId);

        return itemRequest;
    }
}