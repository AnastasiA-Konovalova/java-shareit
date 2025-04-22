package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getById(Long requestId, Long userId);

    List<ItemRequestDto> getAll();

    List<ItemRequestDto> getUserRequest(Long userId);

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);
}