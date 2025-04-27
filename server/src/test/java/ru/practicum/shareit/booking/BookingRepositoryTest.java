package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item1;
    private Item item2;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        now = LocalDateTime.of(2025, 4, 25, 12, 0);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        entityManager.persist(item2);

        pastBooking = new Booking();
        pastBooking.setItem(item1);
        pastBooking.setBooker(booker);
        pastBooking.setStartBooking(now.minusDays(3));
        pastBooking.setEndBooking(now.minusDays(1));
        pastBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(pastBooking);

        currentBooking = new Booking();
        currentBooking.setItem(item1);
        currentBooking.setBooker(booker);
        currentBooking.setStartBooking(now.minusDays(1));
        currentBooking.setEndBooking(now.plusDays(1));
        currentBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(currentBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item1);
        futureBooking.setBooker(booker);
        futureBooking.setStartBooking(now.plusDays(1));
        futureBooking.setEndBooking(now.plusDays(3));
        futureBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(futureBooking);

        waitingBooking = new Booking();
        waitingBooking.setItem(item1);
        waitingBooking.setBooker(booker);
        waitingBooking.setStartBooking(now.plusDays(5));
        waitingBooking.setEndBooking(now.plusDays(7));
        waitingBooking.setBookingStatus(BookingStatus.WAITING);
        entityManager.persist(waitingBooking);

        rejectedBooking = new Booking();
        rejectedBooking.setItem(item2);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStartBooking(now.plusDays(10));
        rejectedBooking.setEndBooking(now.plusDays(12));
        rejectedBooking.setBookingStatus(BookingStatus.REJECTED);
        entityManager.persist(rejectedBooking);

        entityManager.flush();
    }

    @Test
    void findByBookerIdOrderByStartBookingDesc() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartBookingDesc(booker.getId());

        assertThat(bookings).hasSize(5);
        assertThat(bookings).containsExactly(rejectedBooking, waitingBooking, futureBooking, currentBooking, pastBooking);
    }

    @Test
    void findByBookerIdOrderByStartBookingDescReturnEmptyListIfNoBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartBookingDesc(owner.getId());

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDescShouldReturnCurrentBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
                booker.getId(), now, now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(currentBooking);
    }

    @Test
    void findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDescShouldReturnEmptyListIfNoCurrentBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
                owner.getId(), now, now);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByBookerIdAndEndBookingBeforeOrderByStartBookingDescShouldReturnPastBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(booker.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(pastBooking);
    }

    @Test
    void findByBookerIdAndEndBookingBeforeOrderByStartBookingDescShouldReturnEmptyListIfNoPastBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(owner.getId(), now);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByBookerIdAndStartBookingAfterOrderByStartBookingDescShouldReturnEmptyListIfNoFutureBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBookingAfterOrderByStartBookingDesc(owner.getId(), now);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByBookerIdAndBookingStatusOrderByStartBookingDescShouldReturnBookingsByStatus() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndBookingStatusOrderByStartBookingDesc(booker.getId(), BookingStatus.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(waitingBooking);
    }

    @Test
    void findByBookerIdAndBookingStatusOrderByStartBookingDescShouldReturnEmptyListIfNoBookingsWithStatus() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndBookingStatusOrderByStartBookingDesc(owner.getId(), BookingStatus.WAITING);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByItemOwnerIdOrderByStartBookingDescShouldReturnAllOwnerBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartBookingDesc(owner.getId());

        assertThat(bookings).hasSize(5);
        assertThat(bookings).containsExactly(rejectedBooking, waitingBooking, futureBooking, currentBooking, pastBooking);
    }

    @Test
    void findByItemOwnerIdOrderByStartBookingDescShouldReturnEmptyListIfNoOwnerBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartBookingDesc(booker.getId());

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDescShouldReturnCurrentOwnerBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
                owner.getId(), now, now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(currentBooking);
    }

    @Test
    void findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDescShouldReturnEmptyListIfNoCurrentOwnerBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
                booker.getId(), now, now);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDescShouldReturnPastOwnerBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(owner.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(pastBooking);
    }

    @Test
    void findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDescShouldReturnEmptyListIfNoPastOwnerBookings() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndBookingBeforeOrderByStartBookingDesc(booker.getId(), now);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByItemOwnerIdAndBookingStatusOrderByStartBookingDescShouldReturnOwnerBookingsByStatus() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartBookingDesc(owner.getId(), BookingStatus.REJECTED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(rejectedBooking);
    }

    @Test
    void findByItemOwnerIdAndBookingStatusOrderByStartBookingDescShouldReturnEmptyListIfNoOwnerBookingsWithStatus() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartBookingDesc(booker.getId(), BookingStatus.REJECTED);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatusShouldReturnBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatus(
                booker.getId(), item1.getId(), now, BookingStatus.APPROVED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(pastBooking);
    }

    @Test
    void findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatusShouldReturnEmptyListIfNoBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBookingBeforeAndBookingStatus(
                booker.getId(), item2.getId(), now, BookingStatus.APPROVED);

        assertThat(bookings).isEmpty();
    }

    @Test
    void findByItemInAndBookingStatusNotShouldReturnBookings() {
        List<Item> items = List.of(item1, item2);
        List<Booking> bookings = bookingRepository.findByItemInAndBookingStatusNot(items, BookingStatus.REJECTED);

        assertThat(bookings).hasSize(4);
        assertThat(bookings).containsExactlyInAnyOrder(pastBooking, currentBooking, futureBooking, waitingBooking);
    }

    @Test
    void findByItemInAndBookingStatusNotShouldReturnEmptyListIfItemsEmpty() {
        List<Item> items = List.of();
        List<Booking> bookings = bookingRepository.findByItemInAndBookingStatusNot(items, BookingStatus.REJECTED);

        assertThat(bookings).isEmpty();
    }
}