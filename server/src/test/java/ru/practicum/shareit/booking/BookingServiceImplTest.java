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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({BookingServiceImpl.class})
class BookingServiceImplTest {

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

        assertThat(bookingDto.getId()).isNotNull();
        assertThat(pastBooking.getId()).isEqualTo(bookingDto.getId());
        assertThat(item1.getId()).isEqualTo(bookingDto.getItem().getId());
        assertThat(booker1.getId()).isEqualTo(bookingDto.getBooker().getId());
        assertThat(BookingStatus.APPROVED).isEqualTo(bookingDto.getStatus());
    }

    @Test
    void getByIdTestWithInvalidBookingId() {
        assertThatThrownBy(() -> bookingService.getById(999L, booker1.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование не найдено");
    }

    @Test
    void getAllByUserSuccessTest() {
        List<BookingDto> bookings = bookingService.getAllByUser(booker1.getId(), State.ALL);

        assertThat(bookings).hasSize(5);
        assertThat(bookings.get(0).getId()).isEqualTo(rejectedBooking.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(waitingBooking.getId());
        assertThat(bookings.get(2).getId()).isEqualTo(futureBooking.getId());
        assertThat(bookings.get(3).getId()).isEqualTo(currentBooking.getId());
        assertThat(bookings.get(4).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void getAllByUserIfNotExistTest() {
        assertThatThrownBy(() -> bookingService.getAllByUser(999L, State.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void getAllByOwnerSuccessTest() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.ALL);

        assertThat(bookings).hasSize(5);
        assertThat(bookings.get(0).getId()).isEqualTo(rejectedBooking.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(waitingBooking.getId());
        assertThat(bookings.get(2).getId()).isEqualTo(futureBooking.getId());
        assertThat(bookings.get(3).getId()).isEqualTo(currentBooking.getId());
        assertThat(bookings.get(4).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void createSuccessTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item1.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        BookingDto createdBooking = bookingService.create(bookingDto, booker1.getId());

        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker1.getId());
        assertThat(createdBooking.getStart()).isEqualTo(now.plusDays(1));
        assertThat(createdBooking.getEnd()).isEqualTo(now.plusDays(2));
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        Booking savedBooking = entityManager.find(Booking.class, createdBooking.getId());

        assertThat(savedBooking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker1.getId());
    }

    @Test
    void createIfNotExistTest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item1.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        assertThatThrownBy(() -> bookingService.create(bookingDto, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void changeBookingStatusSuccessTest() {
        BookingDto updatedBooking = bookingService.changeBookingStatus(waitingBooking.getId(), owner1.getId(), true);

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(updatedBooking.getId()).isEqualTo(waitingBooking.getId());
        assertThat(updatedBooking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(updatedBooking.getBooker().getId()).isEqualTo(booker1.getId());

        Booking savedBooking = entityManager.find(Booking.class, waitingBooking.getId());
        assertThat(savedBooking.getBookingStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getAllByUserTestWithWaitingState() {
        List<BookingDto> bookings = bookingService.getAllByUser(booker1.getId(), State.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(waitingBooking.getId());
    }

    @Test
    void getAllByUserTestWithRejectedState() {
        List<BookingDto> bookings = bookingService.getAllByUser(booker1.getId(), State.REJECTED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void getAllByOwnerTestWithWaitingState() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.WAITING);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(waitingBooking.getId());
    }

    @Test
    void getAllByOwnerTestWithRejectedState() {
        List<BookingDto> bookings = bookingService.getAllByOwner(owner1.getId(), State.REJECTED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void getAllByOwnerTestIfOwnerIsNotExist() {
        assertThatThrownBy(() -> bookingService.getAllByOwner(999L, State.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void createTestWithUnavailableItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item2.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        assertThatThrownBy(() -> bookingService.create(bookingDto, booker1.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Бронирование этой вещи запрещено");
    }

    @Test
    void createTestWithNotExistItem() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(999L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        assertThatThrownBy(() -> bookingService.create(bookingDto, booker1.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ошибка в получении предмета с id 999.");
    }

    @Test
    void changeBookingStatusTestWithNotWaitingStatus() {
        assertThatThrownBy(() -> bookingService.changeBookingStatus(pastBooking.getId(), owner1.getId(), true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Статус уже определен");
    }

    @Test
    void changeBookingStatusTestWithRejectStatus() {
        BookingDto updatedBooking = bookingService.changeBookingStatus(waitingBooking.getId(), owner1.getId(), false);

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(updatedBooking.getId()).isEqualTo(waitingBooking.getId());
        assertThat(updatedBooking.getItem().getId()).isEqualTo(item1.getId());
        assertThat(updatedBooking.getBooker().getId()).isEqualTo(booker1.getId());

        Booking savedBooking = entityManager.find(Booking.class, waitingBooking.getId());
        assertThat(savedBooking.getBookingStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void changeBookingStatusTestWithNotRightOwner() {
        assertThatThrownBy(() -> bookingService.changeBookingStatus(waitingBooking.getId(), booker1.getId(), true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запрошенные данные о бронировании не могут быть предоставлены в силу несоответствия пользователя");
    }

    @Test
    void changeBookingStatusTestWithNonExistBooking() {
        assertThatThrownBy(() -> bookingService.changeBookingStatus(999L, owner1.getId(), true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с id = 999 не найдено");
    }

    @Test
    void changeBookingStatusTestWithNonExistUser() {
        assertThatThrownBy(() -> bookingService.changeBookingStatus(waitingBooking.getId(), 999L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Указанный пользователь не может изменять статус бронирования");
    }
}