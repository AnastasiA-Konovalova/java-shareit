package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.model.Item;
import ru.practicum.shareit.gateway.user.dto.UserDto;
import ru.practicum.shareit.gateway.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;
    private UserDto userDto1;
    private ItemDto itemDto1;
    private Item item1;
    private User user1;
    private ItemCreateDto itemCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        LocalDateTime now = LocalDateTime.of(2025, 4, 23, 12, 0);
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        item1 = new Item();
        item1.setName("name1");
        item1.setDescription("Desc1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        user1 = new User();
        user1.setName("User name2");
        user1.setEmail("user@email2");

        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("User name1");
        userDto1.setEmail("user@email1");

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("ItemName1");
        itemCreateDto.setDescription("ItemDescription1");
        itemCreateDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setText("Great item");
        commentDto.setItem(item1);
        commentDto.setAuthorName(user1.getName());
        commentDto.setCreated(now);

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("name");
        itemDto1.setDescription("description");
        itemDto1.setAvailable(true);
        itemDto1.setComments(Collections.emptyList());

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(1L);
        commentDto1.setText("Great item");
        commentDto1.setAuthorName("User2");
        commentDto1.setCreated(LocalDateTime.of(2025, 4, 23, 12, 0));
    }

    @Test
    void getByOwnerIdTest() throws Exception {
        when(itemService.getByOwnerId(anyLong()))
                .thenReturn(List.of(itemDto1));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto1);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void searchItemByName() throws Exception {
        when(itemService.searchItemByName(anyString()))
                .thenReturn(List.of(itemDto1));

        mvc.perform(get("/items/search?text=name")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void createTest() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemCreateDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ItemName1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("ItemDescription1"));
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto1);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.id")).value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void deleteTest() throws Exception {
        doNothing().when(itemService).delete(anyLong(), anyLong());

        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveCommentTest() throws Exception {
        when(itemService.saveComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", userDto1.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item"))
                .andExpect(jsonPath("$.authorName").value("User name2"))
                .andExpect(jsonPath("$.created").value("2025-04-23T12:00:00"));
    }
}
