package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ItemRequestServiceImpl.class, UserServiceImpl.class})
class ItemRequestServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestService itemRequestService;

    private User user1;
    private User owner1;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 4, 23, 12, 0);

        user1 = new User();
        user1.setName("User");
        user1.setEmail("user@email");
        entityManager.persist(user1);

        owner1 = new User();
        owner1.setName("Owner");
        owner1.setEmail("owner@email");
        entityManager.persist(owner1);

        request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setRequestorId(user1.getId());
        request1.setCreated(now.minusDays(1));
        entityManager.persist(request1);

        request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setRequestorId(user1.getId());
        request2.setCreated(now);
        entityManager.persist(request2);

        item1 = new Item();
        item1.setName("Item");
        item1.setDescription("Item description");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        item1.setRequest(request1);
        entityManager.persist(item1);

        entityManager.flush();
    }

    @Test
    void getByIdTest() {
        ItemRequestDto requestDto = itemRequestService.getById(request1.getId(), user1.getId());

        assertThat(requestDto.getId()).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(request1.getId());
        assertThat(requestDto.getDescription()).isEqualTo("Request 1");
        assertThat(requestDto.getRequestorId()).isEqualTo(user1.getId());
        assertThat(requestDto.getItems()).hasSize(1);
        assertThat(requestDto.getItems().getFirst().getId()).isEqualTo(item1.getId());
        assertThat(requestDto.getItems().getFirst().getName()).isEqualTo(item1.getName());
    }

    @Test
    void getByIdTestWithoutItems() {
        ItemRequestDto requestDto = itemRequestService.getById(request2.getId(), user1.getId());

        assertThat(requestDto.getId()).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(request2.getId());
        assertThat(requestDto.getDescription()).isEqualTo("Request 2");
        assertThat(requestDto.getRequestorId()).isEqualTo(user1.getId());
        assertThat(requestDto.getItems()).isEmpty();
    }

    @Test
    void getAllTest() {
        List<ItemRequestDto> requests = itemRequestService.getAll();

        assertThat(requests).hasSize(2);
        assertThat(requests.get(1).getId()).isEqualTo(request2.getId());
        assertThat(requests.get(0).getId()).isEqualTo(request1.getId());
        assertThat(requests.get(0).getItems()).satisfies(items -> assertThat(items == null || items.isEmpty()).isTrue());
    }

    @Test
    void getUserRequestTest() {
        List<ItemRequestDto> requests = itemRequestService.getUserRequest(user1.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getId()).isEqualTo(request2.getId());
        assertThat(requests.get(1).getId()).isEqualTo(request1.getId());
        assertThat(requests.get(0).getItems()).satisfies(items -> assertThat(items == null || items.isEmpty()).isTrue());
    }

    @Test
    void getUserRequestTestWithNoRequests() {
        User noRequestsUser = new User();
        noRequestsUser.setName("No Requests");
        noRequestsUser.setEmail("norequests@email");
        entityManager.persist(noRequestsUser);
        entityManager.flush();

        List<ItemRequestDto> requests = itemRequestService.getUserRequest(noRequestsUser.getId());

        assertThat(requests).isEmpty();
    }

    @Test
    void createTest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("New Request");

        ItemRequestDto createdRequest = itemRequestService.create(requestDto, user1.getId());

        assertThat(createdRequest.getId()).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo("New Request");
        assertThat(createdRequest.getRequestorId()).isEqualTo(user1.getId());
        assertThat(createdRequest.getCreated()).isNotNull();
        assertThat(createdRequest.getItems()).satisfies(items -> assertThat(items == null || items.isEmpty()).isTrue());

        ItemRequest savedRequest = entityManager.find(ItemRequest.class, createdRequest.getId());
        assertThat(savedRequest.getDescription()).isEqualTo("New Request");
        assertThat(savedRequest.getRequestorId()).isEqualTo(user1.getId());
    }
}