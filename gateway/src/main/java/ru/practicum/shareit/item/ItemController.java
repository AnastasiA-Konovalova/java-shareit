package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemClient.getByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByName(@RequestParam String text) {
        return itemClient.searchItemByName(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemCreateDto itemDto,
                                         @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto newItemDto,
                                         @RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                         @PathVariable("itemId") Long itemId) {
        return itemClient.update(newItemDto, id, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                       @PathVariable(name = "itemId") long itemId) {
        itemClient.delete(userId, itemId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> saveComment(@Valid @RequestBody CommentDto commentDto,
                                              @PathVariable Long itemId,
                                              @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return itemClient.saveComment(commentDto, itemId, userId);
    }
}