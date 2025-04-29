package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return ItemRequestMapper.toDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAll() {
        List<ItemRequest> request = itemRequestRepository.findAll();

        return request.stream()
                .map(itemRequest -> ItemRequestMapper.toDto(itemRequest, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        userService.getById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> ids = itemRequests.stream().map(ItemRequest::getId).toList();

        List<Item> items = itemRepository.findAllByRequestId(ids);

        Map<Long, List<Item>> requestMap = new HashMap<>();
        for (Item item : items) {
            Long requestId = item.getRequest().getId();
            if (!requestMap.containsKey(requestId)) {
                requestMap.put(requestId, new ArrayList<>());
            }
            requestMap.get(requestId).add(item);
        }

        return ItemRequestMapper.toDto(itemRequests, requestMap);
    }

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(new ItemRequest(), itemRequestDto, userId);

        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }
}