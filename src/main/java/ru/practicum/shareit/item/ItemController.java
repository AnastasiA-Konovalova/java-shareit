package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByOwnerId(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemService.getByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByName(@RequestParam String text) {
        return itemService.searchItemByName(text);
    }

    @PostMapping
    public ItemCreateDto create(@Valid @RequestBody ItemCreateDto itemDto,
                                @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @RequestBody ItemDto newItemDto,
                          @RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                          @PathVariable("itemId") Long itemId) {
        return itemService.update(newItemDto, id, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                       @PathVariable(name = "itemId") long itemId) {
        itemService.delete(userId, itemId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                                  @PathVariable Long itemId,
                                  @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return itemService.saveComment(commentDto, itemId, userId);
    }
}