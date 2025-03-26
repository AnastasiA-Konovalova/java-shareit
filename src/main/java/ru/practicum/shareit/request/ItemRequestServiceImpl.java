package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperToDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto getById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.getById(requestId).orElseThrow(() -> {
            log.warn("Неправильно введен id запроса вещи");
            return new NotFoundException("Ошибка в получении запроса с id " + requestId + ".");
        });

        return ItemRequestMapperToDto.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAll() {
        List<ItemRequest> requestDtos = itemRequestRepository.getAll();
        if (requestDtos.isEmpty()) {
            log.warn("Список запросов пуст");
            throw new NotFoundException("Ошибка в получении списка запросов. Список пуст");
        }

        return requestDtos.stream()
                .map(ItemRequestMapperToDto::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.getUserRequest(userId);
        if (itemRequests.isEmpty()) {
            log.warn("Список запросов пуст");
            throw new NotFoundException("Ошибка в получении списка запросов с id пользователя " + userId + ". Список пуст");
        }

        return itemRequests.stream()
                .map(ItemRequestMapperToDto::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequestMapperToDto.toEntity(new ItemRequest(), itemRequestDto);

        return ItemRequestMapperToDto.toDto(itemRequestRepository.create(itemRequest, userId));
    }
}