package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BookingServiceImpl.class})
public class BookingServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingService bookingService;

    private User owner1;
    private User booker1;
    private Item item1;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 4, 23, 12, 0);

        owner1 = new User();
        owner1.setName("Owner");
        owner1.setEmail("owner@email");
        entityManager.persist(owner1);

        booker1 = new User();
        booker1.setName("Booker");
        booker1.setEmail("booker@email");
        entityManager.persist(booker1);

        item1 = new Item();
        item1.setName("Item");
        item1.setDescription("Item description");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        entityManager.persist(item1);

        pastBooking = new Booking();
        pastBooking.setItem(item1);
        pastBooking.setBooker(booker1);
        pastBooking.setStartBooking(now.minusDays(3));
        pastBooking.setEndBooking(now.minusDays(1));
        pastBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(pastBooking);

        currentBooking = new Booking();
        currentBooking.setItem(item1);
        currentBooking.setBooker(booker1);
        currentBooking.setStartBooking(now.minusDays(1));
        currentBooking.setEndBooking(now.plusDays(1));
        currentBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(currentBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item1);
        futureBooking.setBooker(booker1);
        futureBooking.setStartBooking(now.plusDays(1));
        futureBooking.setEndBooking(now.plusDays(3));
        futureBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(futureBooking);

        waitingBooking = new Booking();
        waitingBooking.setItem(item1);
        waitingBooking.setBooker(booker1);
        waitingBooking.setStartBooking(now.plusDays(5));
        waitingBooking.setEndBooking(now.plusDays(7));
        waitingBooking.setBookingStatus(BookingStatus.WAITING);
        entityManager.persist(waitingBooking);

        rejectedBooking = new Booking();
        rejectedBooking.setItem(item1);
        rejectedBooking.setBooker(booker1);
        rejectedBooking.setStartBooking(now.plusDays(10));
        rejectedBooking.setEndBooking(now.plusDays(12));
        rejectedBooking.setBookingStatus(BookingStatus.REJECTED);
        entityManager.persist(rejectedBooking);

        entityManager.flush();
    }

    @Test
    void getByIdTest() {
        BookingDto bookingDto = bookingService.getById(pastBooking.getId(), booker1.getId());

        assertNotNull(bookingDto.getId());
        assertEquals(pastBooking.getId(), bookingDto.getId());
        assertEquals(item1.getId(), bookingDto.getItem().getId());
        assertEquals(booker1.getId(), bookingDto.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getByIdTest_withInvalidBookingId_shouldThrowNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getById(999L, booker1.getId()));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void getAllByUserTest() {
        List<BookingDto> bookings = bookingService.getAllByUser(booker1.getId(), State.ALL);

        assertEquals(5, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
        assertEquals(waitingBooking.getId(), bookings.get(1).getId());
        assertEquals(futureBooking.getId(), bookings.get(2).getId());
        assertEquals(currentBooking.getId(), bookings.get(3).getId());
        assertEquals(pastBooking.getId(), bookings.get(4).getId());
    }

    @Test
    void getAllByUserIfNotExistTest() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getAllByUser(999L, State.ALL));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getAllByOwnerTest() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.ALL);

        assertEquals(5, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
        assertEquals(waitingBooking.getId(), bookings.get(1).getId());
        assertEquals(futureBooking.getId(), bookings.get(2).getId());
        assertEquals(currentBooking.getId(), bookings.get(3).getId());
        assertEquals(pastBooking.getId(), bookings.get(4).getId());
    }

    @Test
    void createTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item1.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        BookingDto createdBooking = bookingService.create(bookingDto, booker1.getId());

        assertNotNull(createdBooking.getId());
        assertEquals(item1.getId(), createdBooking.getItem().getId());
        assertEquals(booker1.getId(), createdBooking.getBooker().getId());
        assertEquals(now.plusDays(1), createdBooking.getStart());
        assertEquals(now.plusDays(2), createdBooking.getEnd());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());

        Booking savedBooking = entityManager.find(Booking.class, createdBooking.getId());
        assertEquals(item1.getId(), savedBooking.getItem().getId());
        assertEquals(booker1.getId(), savedBooking.getBooker().getId());
    }

    @Test
    void createIfNotExistTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item1.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.create(bookingDto, 999L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void changeBookingStatusTest() {
        BookingDto updatedBooking = bookingService.changeBookingStatus(waitingBooking.getId(), owner1.getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
        assertEquals(waitingBooking.getId(), updatedBooking.getId());
        assertEquals(item1.getId(), updatedBooking.getItem().getId());
        assertEquals(booker1.getId(), updatedBooking.getBooker().getId());

        Booking savedBooking = entityManager.find(Booking.class, waitingBooking.getId());
        assertEquals(BookingStatus.APPROVED, savedBooking.getBookingStatus());
    }
}
