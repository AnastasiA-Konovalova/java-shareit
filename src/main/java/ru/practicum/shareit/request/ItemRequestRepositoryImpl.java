package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {

    private List<ItemRequest> requests = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public Optional<ItemRequest> getById(Long requestId) {
        return requests.stream()
                .filter(itemRequest -> itemRequest.getId().equals(requestId))
                .findFirst();
    }

    @Override
    public List<ItemRequest> getAll() {
        return requests;
    }

    @Override
    public List<ItemRequest> getUserRequest(Long userId) {
        return requests.stream()
                .filter(request -> request.getRequesterId().equals(userId))
                .toList();
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest, Long userId) {
        Long id = idGenerator.getAndIncrement();
        ItemRequest createItemRequest = new ItemRequest();
        createItemRequest.setId(id);
        createItemRequest.setRequesterId(userId);
        createItemRequest.setDescription(itemRequest.getDescription());
        createItemRequest.setRequestCreate(itemRequest.getRequestCreate());
        createItemRequest.setRequestCreate(itemRequest.getRequestCreate());

        requests.add(createItemRequest);

        return createItemRequest;
    }
}