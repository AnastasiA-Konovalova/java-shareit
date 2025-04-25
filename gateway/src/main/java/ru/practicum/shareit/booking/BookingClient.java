package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(BookingDto dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getById(Long bookingId, Long id) {
        return get("/" + bookingId, id);
    }

    public ResponseEntity<Object> getAllByUser(Long id, State state) {
        return get("?state=" + state, id);
    }

    public ResponseEntity<Object> getAllByOwner(Long id, State state) {
        return get("/owner?state=" + state, id);
    }

    public ResponseEntity<Object> changeBookingStatus(Long bookingId, Long id, Boolean approved) {
        return patch("/" + bookingId + "?approved", id);
    }
}