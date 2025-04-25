package ru.practicum.shareit.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingMapper;
import ru.practicum.shareit.gateway.booking.enums.BookingStatus;
import ru.practicum.shareit.gateway.booking.enums.State;
import ru.practicum.shareit.gateway.booking.model.Booking;
import ru.practicum.shareit.gateway.exeptions.NotFoundException;
import ru.practicum.shareit.gateway.exeptions.ValidationException;
import ru.practicum.shareit.gateway.item.ItemRepository;
import ru.practicum.shareit.gateway.item.model.Item;
import ru.practicum.shareit.gateway.user.UserRepository;
import ru.practicum.shareit.gateway.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        checkBookingByOwnerAndBooker(booking, userId);

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(Long id, State state) {
        checkUser(id);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartBookingDesc(id);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(id, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(id, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartBookingAfterOrderByStartBookingDesc(id, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndBookingStatusOrderByStartBookingDesc(id, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndBookingStatusOrderByStartBookingDesc(id, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Неизвестное условие сортировки бронирований");
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long id, State state) {
        checkUser(id);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartBookingDesc(id);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(id, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(id, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartBookingAfterOrderByStartBookingDesc(id, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartBookingDesc(id, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartBookingDesc(id, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Неизвестное условие сортировки бронирований");
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookingDto create(BookingDto bookingDto, Long userId) {
        User booker = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        if (item.getAvailable().equals(false)) {
            throw new ValidationException("Бронирование этой вещи запрещено");
        }

        Booking booking = BookingMapper.toEntity(new Booking(), bookingDto, booker, item);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto changeBookingStatus(Long bookingId, Long id, Boolean approved) {
        userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Указанный пользователь не может изменять статус бронирования"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        checkBookingByOwner(booking, id);

        if (!BookingStatus.WAITING.equals(booking.getBookingStatus())) {
            throw new ValidationException("Статус уже определен");
        } else if (Objects.equals(true, approved)) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toDto(booking);
    }

    private void checkBookingByOwnerAndBooker(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Запрошенные данные о бронировании не могут быть предоставлены в силу " +
                    " несоответствия пользователя");
        }
    }

    private void checkBookingByOwner(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Запрошенные данные о бронировании не могут быть предоставлены в силу" +
                    "несоответствия пользователя");
        }
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