package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository {

    Optional<ItemRequest> getById(Long requestId);

    List<ItemRequest> getAll();

    List<ItemRequest> getUserRequest(Long userId);

    ItemRequest create(ItemRequest itemRequest, Long userId);
}