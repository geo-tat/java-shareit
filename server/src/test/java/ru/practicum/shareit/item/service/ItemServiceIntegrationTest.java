package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig({ShareItServer.class, ItemServiceImpl.class, UserServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;
    private Item item;

    private ItemDto itemDto;
    private ItemDto itemDto2;

    private UserDto userDto;

    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 5);
        User user = User.builder()
                .id(1)
                .name("Harrison")
                .email("ford@test.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemMapper.toItemDto(item);
        itemDto2 = ItemMapper.toItemDto(item2);
        userDto = UserMapper.toUserDto(user);

        userService.create(userDto);
    }

    @AfterTestMethod
    //  @AfterEach у меня почему-то так хорошо удаляет user и item1, что метод getItems их найти не может.
    public void after() {
        itemRepository.deleteAll();
        userService.delete(userDto.getId());

    }

    @Test
    void createItem() {
        itemService.create(itemDto, 1);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item queryItem = query
                .setParameter("id", 1)
                .getSingleResult();
        assertEquals(item, queryItem);
    }

    @Test
    void getItems() {
        itemService.create(itemDto, 1);
        itemService.create(itemDto2, 1);

        List<ItemDto> items = new ArrayList<>(itemService.getItems(1, pageRequest));

        assertEquals(2, items.size());
        assertEquals(item.getName(), items.get(0).getName());
    }


}

