package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable(name = "userId") Long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UserDto userDto,
                          @PathVariable(name = "userId") Long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(name = "userId") long userId) {
        userService.delete(userId);
    }
}