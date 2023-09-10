package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;


    private Comment comment;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("Harrison")
                .email("ford@test.com")
                .build();

        userRepository.save(user);


        Item item = Item.builder()
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(user)
                .build();


        itemRepository.save(item);

        comment = Comment.builder()
                .text("Cool")
                .author(user)
                .item(item)
                .build();
        commentRepository.save(comment);
    }

    @Test
    void findAllForItems() {
        Collection<Integer> id = List.of(1);

        List<Comment> comments = new ArrayList<>(commentRepository.findAllForItems(id));

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0));
    }
}