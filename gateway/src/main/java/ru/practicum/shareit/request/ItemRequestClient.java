package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl) {
        super(
                new RestTemplateBuilder()
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestDto itemRequestDto, Long userId) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getUserRequest(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAll() {
        return get("/all");
    }
}