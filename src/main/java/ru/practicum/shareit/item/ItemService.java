package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getByOwnerId(Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItemByName(String name);

    ItemCreateDto create(ItemCreateDto itemDto, Long userId);

    ItemDto update(ItemDto newItem, Long userId, Long itemId);

    void deleteItem(Long userId, Long itemId);
}