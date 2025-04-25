package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.State;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable Long bookingId,
                                          @RequestHeader(X_SHARER_USER_ID_HEADER) Long id) {
        return bookingClient.getById(bookingId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                               @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.getAllByUser(id, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                                @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.getAllByOwner(id, state);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDto bookingDto,
                                         @RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return bookingClient.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@PathVariable Long bookingId,
                                                      @RequestHeader(X_SHARER_USER_ID_HEADER) Long id,
                                                      @RequestParam Boolean approved) {
        return bookingClient.changeBookingStatus(bookingId, id, approved);
    }
}