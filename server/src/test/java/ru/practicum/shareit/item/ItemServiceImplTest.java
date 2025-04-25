package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({ItemServiceImpl.class})
public class ItemServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemService itemService;

    @MockBean
    private Clock clock;

    static User user1;
    static User user2;
    static Item item1;
    static ItemDto itemDto1;
    static ItemRequest request1;
    static ItemRequest request2;
    static ItemCreateDto itemCreateDto1;
    static Comment comment;
    static Booking pastBooking;
    static Booking futureBooking;
    static Booking rejectedBooking;
    static Booking completedBooking;
    static LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 4, 23, 12, 0);

        Clock fixed = Clock.fixed(now.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixed.instant());
        when(clock.getZone()).thenReturn(fixed.getZone());

        user1 = new User();
        user1.setName("User name1");
        user1.setEmail("user@email1");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("User name2");
        user2.setEmail("user@email2");
        entityManager.persist(user2);

        item1 = new Item();
        item1.setName("Item name1");
        item1.setDescription("Item Desc1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        entityManager.persist(item1);

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("name");
        itemDto1.setDescription("description");
        itemDto1.setAvailable(true);
        itemDto1.setComments(Collections.emptyList());

        request1 = new ItemRequest();
        request1.setDescription("Test Request1");
        request1.setRequestorId(user1.getId());
        request1.setCreated(LocalDateTime.now());
        entityManager.persist(request1);

        request2 = new ItemRequest();
        request2.setDescription("Test Request2");
        request2.setRequestorId(user1.getId());
        request2.setCreated(LocalDateTime.now());
        entityManager.persist(request2);

        itemCreateDto1 = new ItemCreateDto();
        itemCreateDto1.setName("Test Item1");
        itemCreateDto1.setDescription("Test Description1");
        itemCreateDto1.setAvailable(true);
        itemCreateDto1.setRequestId(request1.getId());

        completedBooking = new Booking();
        completedBooking.setItem(item1);
        completedBooking.setBooker(user2);
        completedBooking.setStartBooking(now.minusDays(2));
        completedBooking.setEndBooking(now.minusDays(1));
        completedBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(completedBooking);

        comment = new Comment();
        comment.setText("Great item!");
        comment.setItem(item1);
        comment.setAuthor(user1);
        comment.setCreated(now.minusDays(1));
        entityManager.persist(comment);

        pastBooking = new Booking();
        pastBooking.setItem(item1);
        pastBooking.setBooker(user1);
        pastBooking.setStartBooking(now.minusDays(2));
        pastBooking.setEndBooking(now.minusDays(1));
        pastBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(pastBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item1);
        futureBooking.setBooker(user1);
        futureBooking.setStartBooking(now.plusDays(1));
        futureBooking.setEndBooking(now.plusDays(2));
        futureBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(futureBooking);

        rejectedBooking = new Booking();
        rejectedBooking.setItem(item1);
        rejectedBooking.setBooker(user1);
        rejectedBooking.setStartBooking(now.plusDays(3));
        rejectedBooking.setEndBooking(now.plusDays(4));
        rejectedBooking.setBookingStatus(BookingStatus.REJECTED);
        entityManager.persist(rejectedBooking);

        entityManager.flush();
    }

    @Test
    void createItemTest() {
        entityManager.persist(user1);
        entityManager.persist(request1);

        ItemCreateDto result = itemService.create(itemCreateDto1, user1.getId());

        assertNotNull(result.getId());
        assertEquals("Test Item1", result.getName());
        assertEquals(request1.getId(), result.getRequestId());

        Item saved = entityManager.find(Item.class, result.getId());
        assertEquals(user1.getId(), saved.getOwner().getId());
        assertEquals(request1.getId(), saved.getRequest().getId());
    }

    @Test
    void updateItemTest() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("Updated Item");
        newItemDto.setDescription("Updated Description");
        newItemDto.setAvailable(false);

        ItemDto result = itemService.update(newItemDto, user1.getId(), item1.getId());

        assertNotNull(result.getId());
        assertEquals("Updated Item", result.getName());
        assertEquals(false, result.getAvailable());

        Item updatedItem = entityManager.find(Item.class, item1.getId());
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertEquals(false, updatedItem.getAvailable());
        assertEquals(user1.getId(), updatedItem.getOwner().getId());
    }

    @Test
    void getByOwnerIdTest() {
        List<ItemDto> items = itemService.getByOwnerId(user1.getId());

        assertEquals(1, items.size());
        ItemDto itemDto1 = items.get(0);

        assertEquals("Item name1", itemDto1.getName());
        assertEquals("Item Desc1", itemDto1.getDescription());
        assertEquals(true, itemDto1.getAvailable());
        assertEquals(1, itemDto1.getComments().size());
        assertEquals("Great item!", itemDto1.getComments().get(0).getText());
        assertNotNull(itemDto1.getLastBooking());
        assertNotNull(itemDto1.getNextBooking());
    }

    @Test
    void getItemByIdTest() {
        ItemDto itemDto = itemService.getItemById(item1.getId(), user1.getId());

        assertEquals(item1.getName(), itemDto.getName());
        assertEquals(item1.getDescription(), itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(item1.getOwner(), itemDto.getOwnerId());
    }

    @Test
    void searchItemByNameTest() {
        List<ItemDto> itemDto = itemService.searchItemByName("item");

        assertEquals(1, itemDto.size());
        assertTrue(itemDto.stream().anyMatch(dto -> dto.getName().equals("Item name1")));
        assertTrue(itemDto.stream().allMatch(dto -> dto.getAvailable()));
    }

    @Test
    void searchItemIfNameNullTest() {
        List<ItemDto> items = itemService.searchItemByName(null);

        assertTrue(items.isEmpty());
    }

    @Test
    void deleteTest() {
        ItemDto itemBeforeDelete = itemService.getItemById(item1.getId(), user1.getId());
        assertEquals("Item name1", itemBeforeDelete.getName());

        itemService.delete(user1.getId(), item1.getId());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.getItemById(item1.getId(), user1.getId()));
        assertEquals("Ошибка в получении предмета с id " + item1.getId() + ".", exception.getMessage());
    }

    @Test
    void saveCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item");

        CommentDto savedComment = itemService.saveComment(commentDto, item1.getId(), user2.getId());

        assertEquals("Great item", savedComment.getText());
        assertEquals(user2.getName(), savedComment.getAuthorName());
        assertEquals(item1.getId(), savedComment.getItem().getId());

        Comment savedEntity = entityManager.find(Comment.class, savedComment.getId());
        assertEquals("Great item", savedEntity.getText());
        assertEquals(user2.getId(), savedEntity.getAuthor().getId());
        assertEquals(item1.getId(), savedEntity.getItem().getId());

    }
}