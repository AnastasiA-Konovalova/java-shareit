package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserRequest(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId,
                                         @PathVariable("requestId") Long requestId) {
        return itemRequestService.getById(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return itemRequestService.create(itemRequestDto, userId);
    }
}