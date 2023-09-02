package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    private PageRequest pageRequest;
    private User user;
    private User user2;
    private User user3;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private Item item;
    private Item item2;
    private Item item3;

    private ItemRequestDto itemRequestDto;


    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 5);
        user = User.builder()
                .id(1)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        user2 = User.builder()
                .id(2)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        user3 = User.builder()
                .id(3)
                .name("Jamie")
                .email("fox@test.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Nintendo")
                .created(LocalDateTime.now())
                .build();
        itemRequest2 = ItemRequest.builder()
                .id(2)
                .description("Sega")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .build();

        item2 = Item.builder()
                .id(2)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();
        item3 = Item.builder()
                .id(3)
                .name("Nintendo")
                .description("fyi")
                .available(true)
                .owner(user3)
                .request(itemRequest)
                .build();

          itemRequestDto = RequestMapper.toDto(itemRequest);


    }

    @Test
    void addRequest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.addRequest(user.getId(), itemRequestDto);

        assertEquals(itemRequestDtoTest.getId(), itemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getOwnRequests() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user3));
        when(itemRequestRepository.findAllByRequesterId(anyInt(), any(Sort.class))).thenReturn(new ArrayList<>(List.of(itemRequest)));
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findAllItemsForRequestIds(any(Collection.class))).thenReturn(List.of(item3));

        ItemRequestFullDto test = new ArrayList<>(itemRequestService.getOwnRequests(user.getId())).get(0);


        assertEquals(test.getItems().get(0).getId(), item3.getId());
        assertEquals(test.getItems().get(0).getName(), item3.getName());
        assertEquals(test.getItems().get(0).getDescription(), item3.getDescription());
        assertEquals(test.getItems().get(0).getAvailable(), item3.getAvailable());

        verify(itemRequestRepository, times(1)).findAllByRequesterId(anyInt(), any(Sort.class));
    }

    @Test
    void getAllRequests() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterNot(any(User.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(itemRequest)));
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findAllItemsForRequestIds(any(Collection.class))).thenReturn(List.of(item3));

        ItemRequestFullDto test = new ArrayList<>(itemRequestService.getAllRequests(user.getId(),pageRequest)).get(0);
        assertEquals(test.getItems().get(0).getId(), item3.getId());
        assertEquals(test.getItems().get(0).getName(), item3.getName());
        assertEquals(test.getItems().get(0).getDescription(), item3.getDescription());
        assertEquals(test.getItems().get(0).getAvailable(), item3.getAvailable());

        verify(itemRequestRepository, times(1)).findAllByRequesterNot(any(User.class),any(PageRequest.class));
    }


    @Test
    void getRequestById() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(List.of(item3));
        when(itemRequestRepository.existsById(anyInt())).thenReturn(true);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemRequestFullDto test = itemRequestService.getById(itemRequest.getId(), user.getId());

        assertEquals(test.getId(), itemRequest.getId());
        assertEquals(test.getDescription(), itemRequest.getDescription());
        assertEquals(test.getItems().get(0).getId(), item3.getId());
        assertEquals(test.getItems().get(0).getRequestId(), user.getId());

        verify(itemRequestRepository, times(1)).findById(anyInt());
    }

}
