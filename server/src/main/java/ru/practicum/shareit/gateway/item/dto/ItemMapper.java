package ru.practicum.shareit.gateway.item.dto;

import ru.practicum.shareit.gateway.item.model.Item;
import ru.practicum.shareit.gateway.user.model.User;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());

        return itemDto;
    }

    public static ItemCreateDto toCreateDto(Item item) {
        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        return itemDto;
    }

    public static Item toEntity(Item item, ItemCreateDto itemDto, User owner) {
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        return item;
    }
}