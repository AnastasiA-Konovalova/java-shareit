package ru.practicum.shareit.gateway.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.gateway.item.model.Comment;
import ru.practicum.shareit.gateway.item.model.Item;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem(Item item);

    List<Comment> findAllByItemIn(List<Item> items);
}