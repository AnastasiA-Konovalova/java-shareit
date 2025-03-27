package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getByOwnerId(Long userId) {
        userService.getById(userId);
        List<Item> items = itemRepository.getByOwnerId(userId);

        return items.stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        checkItemIdExists(itemId);
        userService.getById(userId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> {
            log.warn("Неправильно введен id предмета");
            return new NotFoundException("Ошибка в получении предмета с id " + itemId + ".");
        });

        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> searchItemByName(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.searchItemByName(text);

        return items.stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public ItemCreateDto create(ItemCreateDto itemDto, Long userId) {
        userService.getById(userId);
        itemDto.setOwnerId(userId);
        Item item = ItemMapper.toEntity(new Item(), itemDto);

        return ItemMapper.toCreateDto(itemRepository.create(userId, item));
    }

    @Override
    public ItemDto update(ItemDto newItem, Long userId, Long itemId) {
        if (newItem == null) {
            throw new NotFoundException("Отсутствуют новые данные для обновления");
        }
        newItem.setOwnerId(userId);
        Item updateItem = ItemMapper.toEntity(new Item(), newItem);
        userService.getById(userId);
        checkItemIdExists(itemId);
        checkOwnerId(userId, itemId);

        return ItemMapper.toDto(itemRepository.update(updateItem, itemId));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        userService.getById(userId);
        checkItemIdExists(itemId);
        checkOwnerId(userId, itemId);

        itemRepository.delete(userId, itemId);
    }

    private void checkOwnerId(Long userId, Long itemId) {
        Optional<Item> item = itemRepository.getItemById(itemId);
        if (!(item.get().getOwnerId().equals(userId))) {
            throw new NotFoundException("У пользователя с id " + userId + " не найден предмет с id " + itemId);
        }
    }

    private void checkItemIdExists(Long itemId) {
        Optional<Item> optionalItem = itemRepository.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с  id " + itemId + " не найден");
        }
    }
}