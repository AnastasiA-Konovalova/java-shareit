package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.List;

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

        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        itemRequestDto.setItems(items.stream().map(ItemMapper::toDto).toList());
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAll() {
        List<ItemRequest> requestDtos = itemRequestRepository.findAll();

        return requestDtos.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        userService.getById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return itemRequests.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(new ItemRequest(), itemRequestDto, userId);

        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }
}