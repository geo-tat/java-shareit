package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItServer.class, ItemServiceImpl.class, UserServiceImpl.class, ItemServiceImpl.class})
public class ItemRequestIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final ItemRequestServiceImpl itemRequestService;

    private User owner;
    private User requester;

    private Item item;

    private ItemRequest request;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        requester = User.builder()
                .id(2)
                .name("Tom")
                .email("holland@test.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(owner)
                .build();

        request = ItemRequest.builder()
                .description("TEST")
                .created(LocalDateTime.of(2023,12,12,12,0))
                .requester(requester)
                .id(1)
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .request(request)
                .owner(owner)
                .build();


    }

    @Test
    void createBooking() {
        userService.create(UserMapper.toUserDto(owner));
        userService.create(UserMapper.toUserDto(requester));

        itemRequestService.addRequest(2, RequestMapper.toDto(request));
        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest queryRequest = query
                .setParameter("id", 1)
                .getSingleResult();
        assertNotNull(queryRequest);
        assertEquals("TEST", queryRequest.getDescription());
        assertEquals(request.getRequester().getId(), queryRequest.getRequester().getId());
    }

    @Test
    void getById() {
        itemService.create(ItemMapper.toItemDto(item), 1);
        ItemRequestFullDto result = itemRequestService.getById(1,2);

        assertEquals("TEST", result.getDescription());
        assertEquals(ItemMapper.toItemForRequestDto(item), result.getItems().get(0));
    }

}

