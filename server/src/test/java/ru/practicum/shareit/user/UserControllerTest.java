package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exeptions.ErrorHandler;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("test");
    }

    @Test
    void getAllShouldReturnUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].name").value("test"));
    }

    @Test
    void getByIdShouldReturnUser() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void saveShouldCreateUser() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void updateShouldUpdateUser() throws Exception {
        when(userService.update(any(UserDto.class), anyLong())).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void deleteShouldDeleteUser() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllShouldReturnEmptyListIfNoUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of());

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getByIdShouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Пользователь с id 1 не найден."));

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveShouldReturnBadRequestIfInvalidEmail() throws Exception {
        UserDto invalidUserDto = new UserDto();
        invalidUserDto.setName("test");
        invalidUserDto.setEmail("invalid-email"); // Некорректный email

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void saveShouldReturnConflictIfEmailIsDuplicate() throws Exception {
        when(userService.save(any(UserDto.class)))
                .thenThrow(new IllegalStateException("Пользователь email test@test.com уже существует"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        when(userService.update(any(UserDto.class), anyLong()))
                .thenThrow(new EntityNotFoundException("Пользователь с id 1 не найден."));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnConflictIfEmailIsDuplicate() throws Exception {
        when(userService.update(any(UserDto.class), anyLong()))
                .thenThrow(new IllegalStateException("Пользователь email test@test.com уже существует"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateShouldReturnBadRequestIfInvalidEmail() throws Exception {
        UserDto invalidUserDto = new UserDto();
        invalidUserDto.setName("test");
        invalidUserDto.setEmail("invalid-email");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void saveShouldReturnBadRequestIfInvalidJson() throws Exception {
        String invalidJson = "{ \"name\": \"test\", \"email\": \"test@test.com\"";

        mvc.perform(post("/users")
                        .content(invalidJson)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deleteShouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Пользователь с id 1 не найден.")).when(userService).delete(anyLong());

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value("Пользователь с id 1 не найден."));
    }
}