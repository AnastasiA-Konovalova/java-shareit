package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user);
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user);
        entityManager.persist(item2);

        comment1 = new Comment();
        comment1.setText("Comment 1 for Item 1");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());
        entityManager.persist(comment1);

        comment2 = new Comment();
        comment2.setText("Comment 2 for Item 1");
        comment2.setItem(item1);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());
        entityManager.persist(comment2);

        entityManager.flush();
    }

    @Test
    void findAllByItemShouldReturnCommentsForItem() {
        List<Comment> comments = commentRepository.findAllByItem(item1);

        assertThat(comments).hasSize(2);
        assertThat(comments).containsExactlyInAnyOrder(comment1, comment2);
        assertThat(comments).allMatch(comment -> comment.getItem().getId().equals(item1.getId()));
    }

    @Test
    void findAllByItemShouldReturnEmptyListIfNoComments() {
        List<Comment> comments = commentRepository.findAllByItem(item2);

        assertThat(comments).isEmpty();
    }

    @Test
    void findAllByItemInShouldReturnCommentsForItems() {
        Comment comment3 = new Comment();
        comment3.setText("Comment for Item 2");
        comment3.setItem(item2);
        comment3.setAuthor(user);
        comment3.setCreated(LocalDateTime.now());
        entityManager.persist(comment3);
        entityManager.flush();

        List<Item> items = List.of(item1, item2);
        List<Comment> comments = commentRepository.findAllByItemIn(items);

        assertThat(comments).hasSize(3);
        assertThat(comments).containsExactlyInAnyOrder(comment1, comment2, comment3);
        assertThat(comments).allMatch(comment -> items.contains(comment.getItem()));
    }

    @Test
    void findAllByItemInShouldReturnEmptyListIfNoComments() {
        List<Item> items = List.of(item2);

        List<Comment> comments = commentRepository.findAllByItemIn(items);

        assertThat(comments).isEmpty();
    }

    @Test
    void findAllByItemInShouldReturnEmptyListIfItemsEmpty() {
        List<Item> items = List.of();

        List<Comment> comments = commentRepository.findAllByItemIn(items);

        assertThat(comments).isEmpty();
    }
}