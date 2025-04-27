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
import ru.practicum.shareit.exeptions.ValidationException;
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
    private Item item2;
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

        item2 = new Item();
        item2.setName("Unavailable Item");
        item2.setDescription("Unavailable item description");
        item2.setAvailable(false);
        item2.setOwner(owner1);
        entityManager.persist(item2);

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
    void getByIdSuccessTest() {
        BookingDto bookingDto = bookingService.getById(pastBooking.getId(), booker1.getId());

        assertNotNull(bookingDto.getId());
        assertEquals(pastBooking.getId(), bookingDto.getId());
        assertEquals(item1.getId(), bookingDto.getItem().getId());
        assertEquals(booker1.getId(), bookingDto.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getByIdTestWithInvalidBookingId() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getById(999L, booker1.getId()));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void getAllByUserSuccessTest() {
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
    void getAllByOwnerSuccessTest() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.ALL);

        assertEquals(5, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
        assertEquals(waitingBooking.getId(), bookings.get(1).getId());
        assertEquals(futureBooking.getId(), bookings.get(2).getId());
        assertEquals(currentBooking.getId(), bookings.get(3).getId());
        assertEquals(pastBooking.getId(), bookings.get(4).getId());
    }

    @Test
    void createSuccessTest() {
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
    void changeBookingStatusSuccessTest() {
        BookingDto updatedBooking = bookingService.changeBookingStatus(waitingBooking.getId(), owner1.getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
        assertEquals(waitingBooking.getId(), updatedBooking.getId());
        assertEquals(item1.getId(), updatedBooking.getItem().getId());
        assertEquals(booker1.getId(), updatedBooking.getBooker().getId());

        Booking savedBooking = entityManager.find(Booking.class, waitingBooking.getId());
        assertEquals(BookingStatus.APPROVED, savedBooking.getBookingStatus());
    }

    @Test
    void getAllByUserTestWithWaitingState() {
        List<BookingDto> bookings = bookingService.getAllByUser(booker1.getId(), State.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByUserTestWithRejectedState() {
        List<BookingDto> bookings = bookingService.getAllByUser(booker1.getId(), State.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwnerTestWithWaitingState() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwnerTestWithRejectedState() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.REJECTED);

        assertEquals(1, bookings.size());
        assertEquals(rejectedBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwnerTestIfOwnerIsNotExist() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getAllByOwner(999L, State.ALL));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void createTestWithUnavailableItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item2.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.create(bookingDto, booker1.getId()));
        assertEquals("Бронирование этой вещи запрещено", exception.getMessage());
    }

    @Test
    void createTestWithNotExistItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(999L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.create(bookingDto, booker1.getId()));
        assertEquals("Ошибка в получении предмета с id 999.", exception.getMessage());
    }

    @Test
    void changeBookingStatusTestWithNotWaitingStatus() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.changeBookingStatus(pastBooking.getId(), owner1.getId(), true));
        assertEquals("Статус уже определен", exception.getMessage());
    }

    @Test
    void changeBookingStatusTestWithRejectStatus() {
        BookingDto updatedBooking = bookingService.changeBookingStatus(waitingBooking.getId(), owner1.getId(), false);

        assertEquals(BookingStatus.REJECTED, updatedBooking.getStatus());
        assertEquals(waitingBooking.getId(), updatedBooking.getId());
        assertEquals(item1.getId(), updatedBooking.getItem().getId());
        assertEquals(booker1.getId(), updatedBooking.getBooker().getId());

        Booking savedBooking = entityManager.find(Booking.class, waitingBooking.getId());
        assertEquals(BookingStatus.REJECTED, savedBooking.getBookingStatus());
    }

    @Test
    void changeBookingStatusTestWithNotRightOwner() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.changeBookingStatus(waitingBooking.getId(), booker1.getId(), true));
        assertEquals("Запрошенные данные о бронировании не могут быть предоставлены в силунесоответствия пользователя",
                exception.getMessage());
    }

    @Test
    void changeBookingStatusTestWithNonExistBooking() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.changeBookingStatus(999L, owner1.getId(), true));
        assertEquals("Бронирование с id = 999 не найдено", exception.getMessage());
    }

    @Test
    void changeBookingStatusTesWithNonExistUser() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.changeBookingStatus(waitingBooking.getId(), 999L, true));
        assertEquals("Указанный пользователь не может изменять статус бронирования", exception.getMessage());
    }
}