package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader(X_SHARER_USER_ID_HEADER) Long id) {
        return bookingService.getById(bookingId, id);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                         @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingService.getAllByUser(id, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                          @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingService.getAllByOwner(id, state);
    }

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@PathVariable Long bookingId,
                                          @RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                          @RequestParam Boolean approved) {
        return bookingService.changeBookingStatus(bookingId, id, approved);
    }
}