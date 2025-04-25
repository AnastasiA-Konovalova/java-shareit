package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    static User owner;
    static Item item;
    static ItemRequest request;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("User name1");
        owner.setEmail("user@email1");

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test Request1");
        request.setRequestorId(owner.getId());
        request.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(2L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
    }

    @Test
    void toDto_shouldMapItemToItemDto() {
//        User owner = new User();
//        owner.setId(1L);
//        owner.setName("Owner");
//        owner.setEmail("owner@example.com");


        ItemDto itemDto = ItemMapper.toDto(item);

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(2L);
        assertThat(itemDto.getName()).isEqualTo("Item");
        assertThat(itemDto.getDescription()).isEqualTo("Description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
        assertThat(itemDto.getOwner()).isEqualTo(owner);
    }

    @Test
    void toCreateDto_shouldMapItemToItemCreateDto_withRequest() {
        ItemCreateDto itemCreateDto = ItemMapper.toCreateDto(item);
        assertThat(itemCreateDto).isNotNull();
        assertThat(itemCreateDto.getId()).isEqualTo(2L);
        assertThat(itemCreateDto.getName()).isEqualTo("Item");
        assertThat(itemCreateDto.getDescription()).isEqualTo("Description");
        assertThat(itemCreateDto.getAvailable()).isTrue();
        assertThat(itemCreateDto.getOwner().getId()).isEqualTo(1L);
        assertThat(itemCreateDto.getRequestId()).isEqualTo(1L);
    }

    @Test
    void toEntity_shouldMapItemCreateDtoToItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item");
        itemCreateDto.setDescription("Description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setOwner(ItemMapperTest.owner);
        itemCreateDto.setRequestId(2L);

        Item item = new Item();
        item.setId(1L);

        Item result = ItemMapper.toEntity(item, itemCreateDto, owner);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getRequest()).isNull();
    }

    @Test
    void toEntity_shouldUpdateExistingItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old Item");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(false);
        existingItem.setOwner(new User());

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Updated Item");
        itemCreateDto.setDescription("Updated Description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setOwner(ItemMapperTest.owner);

        Item result = ItemMapper.toEntity(existingItem, itemCreateDto, owner);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Item");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getRequest()).isNull();
    }
}

