package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private List<Item> items = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public List<Item> getByOwnerId(Long userId) {
        return items.stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public List<Item> searchItemByName(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .filter(item -> item.getName() != null &&
                        item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable())
                .toList();
    }

    @Override
    public Item create(Long userId, Item item) {
        Long id = idGenerator.getAndIncrement();

        Item createItem = new Item();
        createItem.setId(id);
        createItem.setName(item.getName());
        createItem.setDescription(item.getDescription());
        createItem.setOwnerId(userId);
        createItem.setAvailable(item.getAvailable());

        items.add(createItem);

        return createItem;
    }

    @Override
    public Item update(Item newItem, Long itemId) {
        Optional<Item> optionalItem = getItemById(itemId);

        Item item = optionalItem.orElseThrow(() -> new NotFoundException("Такой предмет отсутствует в списке"));
        item.setName(newItem.getName());
        item.setDescription(newItem.getDescription());
        item.setAvailable(newItem.getAvailable());

        return item;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Optional<Item> optionalItem = getItemById(itemId);

        Item item = optionalItem.orElseThrow(() -> new NotFoundException("Такой предмет отсутствует в списке"));
        items.remove(item);
    }
}