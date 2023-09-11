package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    PageRequest pageRequest;

    ItemRequest itemRequest;
    ItemRequest itemRequest2;


    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 5);
        user = User.builder()
                .name("Harrison")
                .email("ford@test.com")
                .build();

        user2 = User.builder()
                .name("Tom")
                .email("cruise@test.com")
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        itemRequest = ItemRequest.builder()
                .description("test")
                .requester(user)
                .build();

        itemRequest2 = ItemRequest.builder()
                .description("test2")
                .requester(user2)
                .build();

        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);


        item = Item.builder()
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .request(itemRequest)
                .owner(user)
                .build();

        item2 = Item.builder()

                .name("PlayStation")
                .description("Sony gaming console")
                .available(true)
                .request(itemRequest2)
                .owner(user)
                .build();
        itemRepository.save(item);
        itemRepository.save(item2);

    }

    @Test
    void search() {

        List<Item> items = new ArrayList<>(itemRepository.search("Play", pageRequest));

        assertEquals(1, items.size());
        assertEquals("PlayStation", items.get(0).getName());
    }

    @Test
    void findAllItemsForRequestIds() {
        Collection<Integer> ids = List.of(1, 2);
        List<Item> items = new ArrayList<>(itemRepository.findAllItemsForRequestIds(ids));

        assertNotNull(items);
        assertEquals(item.getRequest().getId(), items.get(0).getId());
        assertEquals(2, items.size());
    }
}