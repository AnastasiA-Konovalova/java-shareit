package ru.practicum.shareit.gateway.request;

import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getById(Long requestId, Long userId);

    List<ItemRequestDto> getAll();

    List<ItemRequestDto> getUserRequest(Long userId);

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);
}