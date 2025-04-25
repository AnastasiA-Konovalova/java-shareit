package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.gateway.booking.BookingRepository;
import ru.practicum.shareit.gateway.booking.dto.BookingMapper;
import ru.practicum.shareit.gateway.booking.enums.BookingStatus;
import ru.practicum.shareit.gateway.booking.model.Booking;
import ru.practicum.shareit.gateway.exeptions.NotFoundException;
import ru.practicum.shareit.gateway.exeptions.ValidationException;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.CommentMapper;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemMapper;
import ru.practicum.shareit.gateway.item.model.Comment;
import ru.practicum.shareit.gateway.item.model.Item;
import ru.practicum.shareit.gateway.request.ItemRequestRepository;
import ru.practicum.shareit.gateway.request.model.ItemRequest;
import ru.practicum.shareit.gateway.user.UserRepository;
import ru.practicum.shareit.gateway.user.model.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final Clock clock;

    @Override
    public List<ItemDto> getByOwnerId(Long userId) {
        User user = checkUser(userId);
        List<Item> items = itemRepository.findByOwnerId(user.getId());
        LocalDateTime now = LocalDateTime.now(clock);

        Map<Long, List<CommentDto>> commentsByItemId = new HashMap<>();
        for (Comment comment : commentRepository.findAllByItemIn(items)) {
            commentsByItemId.computeIfAbsent(comment.getItem().getId(), i -> new ArrayList<>())
                    .add(CommentMapper.toDto(comment));
        }

        Map<Long, List<Booking>> bookingsByItemId = new HashMap<>();
        for (Booking booking : bookingRepository.findByItemInAndBookingStatusNot(items, BookingStatus.REJECTED)) {
            bookingsByItemId.computeIfAbsent(booking.getItem().getId(), i -> new ArrayList<>())
                    .add(booking);
        }

        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toDto(item);
            itemDto.setComments(commentsByItemId.getOrDefault(item.getId(), Collections.emptyList()));

            List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());
            Booking lastBooking = itemBookings.stream()
                    .filter(b -> b.getStartBooking().isBefore(now))
                    .max(Comparator.comparing(Booking::getStartBooking))
                    .orElse(null);
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toDto(lastBooking));
            }

            Booking nextBooking = itemBookings.stream()
                    .filter(b -> b.getStartBooking().isAfter(now))
                    .min(Comparator.comparing(Booking::getStartBooking))
                    .orElse(null);
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toDto(nextBooking));
            }
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        checkItemIdExists(itemId);
        checkUser(userId);
        Item item = checkItem(itemId);

        ItemDto itemDto = ItemMapper.toDto(item);
        List<Comment> comments = commentRepository.findAllByItem(item);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toDto)
                .toList();
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    @Override
    public List<ItemDto> searchItemByName(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(text);

        return items.stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemCreateDto create(ItemCreateDto itemDto, Long userId) {
        User owner = checkUser(userId);
        Item item = ItemMapper.toEntity(new Item(), itemDto, owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id " + itemDto.getRequestId() + " не найден"));
            item.setRequest(itemRequest);
        }
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toCreateDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto newItem, Long userId, Long itemId) {
        if (newItem == null) {
            throw new NotFoundException("Отсутствуют новые данные для обновления");
        }
        checkUser(userId);
        Item existingItem = checkItemIdExists(itemId);
        checkOwnerId(userId, itemId);

        if (newItem.getName() != null) {
            existingItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            existingItem.setAvailable(newItem.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(existingItem));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        checkUser(userId);
        Item item = checkItemIdExists(itemId);
        checkOwnerId(userId, itemId);

        itemRepository.delete(item);
    }

    @Transactional
    public CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = checkItem(itemId);
        User user = checkUser(userId);

        commentDto.setItem(item);
        commentDto.setAuthorName(user.getName());
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = CommentMapper.toEntity(new Comment(), commentDto);
        comment.setAuthor(user);

        List<Booking> bookings = bookingRepository
                .findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatus(
                        userId,
                        itemId,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED
                );
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не может оставить комментарий, так как не бронировал эту вещь.");
        }

        Comment saveComment = commentRepository.save(comment);
        log.info("Сохранен комментарий: itemId={}, userId={}, text={}", itemId, userId, comment.getText());

        return CommentMapper.toDto(saveComment);
    }

    private void checkOwnerId(Long userId, Long itemId) {
        Item item = checkItem(itemId);
        if (!(item.getOwner().getId().equals(userId))) {
            throw new NotFoundException("У пользователя с id " + userId + " не найден предмет с id " + itemId);
        }
    }

    private Item checkItemIdExists(Long itemId) {
        Item item = checkItem(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с  id " + itemId + " не найден");
        }

        return item;
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Неправильно введен id пользователя");
            return new NotFoundException("Пользователь не найден");
        });
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Неправильно введен id предмета");
            return new NotFoundException("Ошибка в получении предмета с id " + itemId + ".");
        });
    }
}