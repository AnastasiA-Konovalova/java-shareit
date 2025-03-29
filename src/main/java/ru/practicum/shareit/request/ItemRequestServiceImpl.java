package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperToDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

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

        return requestDtos.stream()
                .map(ItemRequestMapperToDto::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getUserRequest(Long userId) {
        userService.getById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.getUserRequest(userId);

        return itemRequests.stream()
                .map(ItemRequestMapperToDto::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapperToDto.toEntity(new ItemRequest(), itemRequestDto);

        return ItemRequestMapperToDto.toDto(itemRequestRepository.create(itemRequest));
    }
}