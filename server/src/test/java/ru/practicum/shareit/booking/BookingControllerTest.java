package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mvc;
    private User user1;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        LocalDateTime now = LocalDateTime.of(2025, 4, 23, 12, 0);
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        user1 = new User();
        user1.setName("User name");
        user1.setEmail("user@email");

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setBooker(user1);
        bookingDto.setStart(now);
        bookingDto.setEnd(now.plusHours(1));
        bookingDto.setStatus(BookingStatus.APPROVED);
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.booker.name").value(user1.getName()))
                .andExpect(jsonPath("$.start").value("2025-04-23T12:00:00"))
                .andExpect(jsonPath("$.end").value("2025-04-23T13:00:00"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(bookingService.getAllByUser(anyLong(), any(State.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].itemId").value(1))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].start").value("2025-04-23T12:00:00"))
                .andExpect(jsonPath("$[0].end").value("2025-04-23T13:00:00"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(State.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].itemId").value(1))
                .andExpect(jsonPath("$[0].booker.name").value(user1.getName()))
                .andExpect(jsonPath("$[0].start").value("2025-04-23T12:00:00"))
                .andExpect(jsonPath("$[0].end").value("2025-04-23T13:00:00"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void createBookingSuccessTest() throws Exception {
        when(bookingService.create(any(BookingDto.class), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.booker.name").value(user1.getName()))
                .andExpect(jsonPath("$.start").value("2025-04-23T12:00:00"))
                .andExpect(jsonPath("$.end").value("2025-04-23T13:00:00"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void changeBookingStatusTest() throws Exception {
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.booker.name").value(user1.getName()))
                .andExpect(jsonPath("$.start").value("2025-04-23T12:00:00"))
                .andExpect(jsonPath("$.end").value("2025-04-23T13:00:00"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}