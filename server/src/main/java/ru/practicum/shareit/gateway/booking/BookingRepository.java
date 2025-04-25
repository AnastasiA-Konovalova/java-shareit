package ru.practicum.shareit.gateway.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.gateway.booking.enums.BookingStatus;
import ru.practicum.shareit.gateway.booking.model.Booking;
import ru.practicum.shareit.gateway.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartBookingDesc(Long userId);

    List<Booking> findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartBookingAfterOrderByStartBookingDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndBookingStatusOrderByStartBookingDesc(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartBookingDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(Long ownerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndBookingStatusOrderByStartBookingDesc(Long ownerId, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatus(Long bookerId, Long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> findByItemInAndBookingStatusNot(List<Item> items, BookingStatus status);
}