package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> getByOwnerId(Long userId);

    Optional<Item> getItemById(Long itemId);

    List<Item> searchItemByName(String name);

    Item create(Long userId, Item item);

    Item update(Item newItem, Long itemId);

    void delete(Long userId, Long itemId);
}