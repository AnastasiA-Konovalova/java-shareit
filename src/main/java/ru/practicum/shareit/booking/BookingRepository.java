package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //@Query("select b from bookings as b where b.item.id :itemsIds")
    List<Booking> findByBookerIdOrderByStartBookingDesc(Long userId);
    //all
    List<Booking> findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end);
    //current

    List<Booking> findByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(Long bookerId, LocalDateTime start);
    //past

    List<Booking> findByBookerIdAndStartBookingAfterOrderByStartBookingDesc(Long bookerId, LocalDateTime start);
    //future

    List<Booking> findByBookerIdAndBookingStatusOrderByStartBookingDesc(Long userId, BookingStatus status);
    //waiting
    //rejected

    List<Booking> findByItemOwnerIdOrderByStartBookingDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(Long ownerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartBookingAfterOrderByStartBookingDesc(Long ownerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndBookingStatusOrderByStartBookingDesc(Long ownerId, BookingStatus status);

    Booking findTopByItemIdAndStartBookingBeforeOrderByStartBookingDesc(Long itemId, LocalDateTime now);

    Booking findTopByItemIdAndStartBookingAfterOrderByStartBookingAsc(Long itemId, LocalDateTime now);

    List<Booking> findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatus(Long bookerId, Long itemId, LocalDateTime time, BookingStatus status);
}