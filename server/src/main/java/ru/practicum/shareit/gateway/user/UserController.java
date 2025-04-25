package ru.practicum.shareit.gateway.user;

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
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable(name = "id") Long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto userDto,
                          @PathVariable(name = "id") Long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") long userId) {
        userService.delete(userId);
    }
}