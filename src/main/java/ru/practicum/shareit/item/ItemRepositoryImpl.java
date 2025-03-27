package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private List<Item> items = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public List<Item> getByOwnerId(Long userId) {
        return items.stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public List<Item> searchItemByName(String text) {
        return items.stream()
                .filter(item -> item.getName() != null &&
                        item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    @Override
    public Item create(Long userId, Item item) {
        Long id = idGenerator.getAndIncrement();
        item.setId(id);
        items.add(item);

        return item;
    }

    @Override
    public Item update(Item newItem, Long itemId) {
        Optional<Item> optionalItem = getItemById(itemId);

        Item item = optionalItem.orElseThrow(() -> new NotFoundException("Такой предмет отсутствует в списке"));
        item.setName(newItem.getName());
        item.setDescription(newItem.getDescription());
        item.setAvailable(newItem.getAvailable());
        item.setOwnerId(newItem.getOwnerId());

        return item;
    }

    @Override
    public void delete(Long userId, Long itemId) {
        Optional<Item> optionalItem = getItemById(itemId);

        Item item = optionalItem.orElseThrow(() -> new NotFoundException("Такой предмет отсутствует в списке"));
        items.remove(item);
    }
}