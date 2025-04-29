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
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({ItemServiceImpl.class, UserServiceImpl.class})
class ItemServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private Clock clock;

    static User user1;
    static User user2;
    static Item item1;
    static ItemDto newItemDto;
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

        newItemDto = new ItemDto();
        newItemDto.setName("Updated Item");
        newItemDto.setDescription("Updated Description");
        newItemDto.setAvailable(false);

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
    void createItemSuccessTest() {
        entityManager.persist(user1);
        entityManager.persist(request1);

        ItemCreateDto result = itemService.create(itemCreateDto1, user1.getId());

        assertThat(result.getId()).isNotNull();
        assertThat("Test Item1").isEqualTo(result.getName());
        assertThat(request1.getId()).isEqualTo(result.getRequestId());

        Item saved = entityManager.find(Item.class, result.getId());
        assertThat(user1.getId()).isEqualTo(saved.getOwner().getId());
        assertThat(request1.getId()).isEqualTo(saved.getRequest().getId());
    }

    @Test
    void createShouldNotCreateItemNotUser() {
        assertThatThrownBy(() -> {
            itemService.create(itemCreateDto1, 999L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItemSuccessTest() {
        ItemDto result = itemService.update(newItemDto, user1.getId(), item1.getId());

        assertThat(result.getId()).isNotNull();
        assertThat("Updated Item").isEqualTo(result.getName());
        assertThat(false).isEqualTo(result.getAvailable());

        Item updatedItem = entityManager.find(Item.class, item1.getId());
        assertThat("Updated Item").isEqualTo(updatedItem.getName());
        assertThat("Updated Description").isEqualTo(updatedItem.getDescription());
        assertThat(false).isEqualTo(updatedItem.getAvailable());
        assertThat(user1.getId()).isEqualTo(updatedItem.getOwner().getId());
    }

    @Test
    void updateShouldNotUpdateNoItem() {
        User user = userRepository.save(user1);

        final Long userId = user.getId();
        final Long nonExistentItemId = 999L;

        assertThatThrownBy(() -> {
            itemService.update(new ItemDto(), userId, nonExistentItemId);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateShouldUpdateOnlyName() {
        User savedUser = userRepository.save(user1);
        Item savedItem = itemRepository.save(item1);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");

        ItemDto updatedItem = itemService.update(updateDto, savedUser.getId(), savedItem.getId());

        assertThat(updatedItem.getName()).isEqualTo("New Name");
        assertThat(updatedItem.getDescription()).isEqualTo(savedItem.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(savedItem.getAvailable());
    }

    @Test
    void updateShouldUpdateOnlyDescription() {
        User savedUser = userRepository.save(user1);

        Item savedItem = itemRepository.save(item1);

        ItemDto updateDto = new ItemDto();
        updateDto.setDescription("new description");

        ItemDto updatedItem = itemService.update(updateDto, savedUser.getId(), savedItem.getId());

        assertThat(updatedItem.getDescription()).isEqualTo("new description");
        assertThat(updatedItem.getName()).isEqualTo(savedItem.getName());
        assertThat(updatedItem.getAvailable()).isEqualTo(savedItem.getAvailable());
    }

    @Test
    void updateShouldUpdateOnlyAvailable() {
        User savedUser = userRepository.save(user1);

        Item savedItem = itemRepository.save(item1);

        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.update(updateDto, savedUser.getId(), savedItem.getId());

        assertThat(updatedItem.getAvailable()).isFalse();
        assertThat(updatedItem.getDescription()).isEqualTo(savedItem.getDescription());
        assertThat(updatedItem.getName()).isEqualTo(savedItem.getName());
    }

    @Test
    void getByOwnerIdSuccessTest() {
        List<ItemDto> items = itemService.getByOwnerId(user1.getId());

        assertThat(1).isEqualTo(items.size());
        ItemDto itemDto1 = items.getFirst();

        assertThat("Item name1").isEqualTo(itemDto1.getName());
        assertThat("Item Desc1").isEqualTo(itemDto1.getDescription());
        assertThat(true).isEqualTo(itemDto1.getAvailable());
        assertThat(1).isEqualTo(itemDto1.getComments().size());
        assertThat("Great item!").isEqualTo(itemDto1.getComments().getFirst().getText());
        assertThat(itemDto1.getLastBooking()).isNotNull();
        assertThat(itemDto1.getNextBooking()).isNotNull();
    }

    @Test
    void getItemByIdSuccessTest() {
        ItemDto itemDto = itemService.getItemById(item1.getId(), user1.getId());

        assertThat(itemDto.getName()).isEqualTo(item1.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwner()).isEqualTo((item1.getOwner()));
    }

    @Test
    void findByIdShouldNotFindItemById() {
        assertThatThrownBy(() -> {
            itemService.getByOwnerId(999L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByOwnerIdShouldFindItemByOwnerId() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setOwner(savedUser);

        ItemCreateDto savedItem = itemService.create(itemDto, savedUser.getId());

        List<ItemDto> items = itemService.getByOwnerId(savedUser.getId());

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getId()).isEqualTo(savedItem.getId());
    }

    @Test
    void searchItemByNameTest() {
        List<ItemDto> itemDto = itemService.searchItemByName("item");

        assertThat(1).isEqualTo(itemDto.size());

        assertThat(itemDto).anyMatch(dto -> dto.getName().equals("Item name1"));
        assertThat(itemDto).allMatch(dto -> dto.getAvailable());
    }

    @Test
    void searchItemIfNameNullTest() {
        List<ItemDto> items = itemService.searchItemByName(null);

        assertThat(items).isEmpty();
    }

    @Test
    void deleteSuccessTest() {
        ItemDto itemBeforeDelete = itemService.getItemById(item1.getId(), user1.getId());
        assertThat(itemBeforeDelete.getName()).isEqualTo("Item name1");

        itemService.delete(user1.getId(), item1.getId());
        assertThatThrownBy(() -> itemService.getItemById(item1.getId(), user1.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ошибка в получении предмета с id " + item1.getId() + ".");
    }

    @Test
    void saveCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item");

        CommentDto savedComment = itemService.saveComment(commentDto, item1.getId(), user2.getId());

        assertThat(savedComment.getText()).isEqualTo("Great item");
        assertThat(savedComment.getAuthorName()).isEqualTo(user2.getName());
        assertThat(savedComment.getItem().getId()).isEqualTo(item1.getId());

        Comment savedEntity = entityManager.find(Comment.class, savedComment.getId());
        assertThat(savedEntity.getText()).isEqualTo("Great item");
        assertThat(savedEntity.getAuthor().getId()).isEqualTo(user2.getId());
        assertThat(savedEntity.getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    void addCommentShouldNotAddCommentNoBooking() {
        User savedOwner = userRepository.save(user1);

        item1.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item1);

        User commenter = new User();
        commenter.setName("Commenter");
        commenter.setEmail("commenter@example.com");
        User savedCommenter = userRepository.save(commenter);

        CommentDto commentDtoRequest = new CommentDto();
        commentDtoRequest.setText("Nice item!");

        assertThatThrownBy(() -> itemService.saveComment(commentDtoRequest, savedItem.getId(), savedCommenter.getId()))
                .isInstanceOf(ValidationException.class);
    }
}