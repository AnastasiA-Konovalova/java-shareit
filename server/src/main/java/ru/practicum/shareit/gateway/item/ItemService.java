package ru.practicum.shareit.gateway.item;

import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getByOwnerId(Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItemByName(String name);

    ItemCreateDto create(ItemCreateDto itemDto, Long userId);

    ItemDto update(ItemDto newItem, Long userId, Long itemId);

    void delete(Long userId, Long itemId);

    CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId);
}