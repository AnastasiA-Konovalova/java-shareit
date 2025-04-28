package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long userId);

    List<Item> findByNameContainingIgnoreCaseAndAvailableTrue(String name);

    List<Item> findAllByRequestId(Long requestId);

    @Query("select i from Item as i where i.request.id in :ids")
    List<Item> findAllByRequestId(@Param("ids") List<Long> ids);
}