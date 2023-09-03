package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.NotYourItemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private ItemDto itemDto;
    private ItemDto itemDto2;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Booking firstBooking;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        pageRequest = PageRequest.of(0, 5);
        user = User.builder()
                .id(1)
                .name("Harrison")
                .email("ford@test.com")
                .build();

         user2 = User.builder()
                .id(2)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("ItemRequest 1")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        item2 = Item.builder()
                .id(2)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();


        itemDto = ItemMapper.toItemDto(item);
        itemDto2 = ItemMapper.toItemDto(item2);

        comment = Comment.builder()
                .id(1)
                .author(user)
                .created(LocalDateTime.now())
                .text("Don't work!")
                .build();

        commentDto = CommentMapper.toCommentDto(comment);

        firstBooking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void createItemTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.create(itemDto, user.getId());

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findAllItemsByOwnerId(anyInt(), any(PageRequest.class))).thenReturn(List.of(item));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.update(itemDto, user.getId(), item.getId());

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemNotBelongUser() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item2));

        when(itemRepository.findAllItemsByOwnerId(anyInt(), any(PageRequest.class))).thenReturn(Collections.emptyList());

        assertThrows(NotYourItemException.class, () -> itemService.update(itemDto2, user.getId(), item2.getId()));
    }

    @Test
    void deleteItem() {
        itemService.delete(1);
        verify(itemRepository, times(1)).deleteById(1);
    }

    @Test
    void getItemById() {
        Booking lastBooking = Booking.builder()
                .id(2)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(Status.APPROVED)
                .item(item)
                .booker(user2)
                .build();

        Booking nextBooking = Booking.builder()
                .id(3)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(Status.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findFirstByItemAndStartBeforeAndStatus(
                eq(item), any(LocalDateTime.class), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemAndStartAfterAndStatus(
                eq(item), any(LocalDateTime.class), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(nextBooking);
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.get(item.getId(), user.getId());

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).findById(anyInt());
    }

    @Test
    void getItems() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findAllItemsByOwnerId(anyInt(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(item)));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = new ArrayList<>(itemService.getItems(user.getId(), pageRequest)).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).findAllItemsByOwnerId(anyInt(), any(PageRequest.class));
    }

    @Test
    void searchItem() {
        when(itemRepository.search(anyString(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(item)));

        ItemDto itemDtoTest = new ArrayList<>(itemService.search("text", pageRequest)).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void searchItemEmptyText() {
        Collection<ItemDto> itemDtoTest = itemService.search("", pageRequest);

        assertTrue(itemDtoTest.isEmpty());

        verify(itemRepository, times(0)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void addComment() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        List<Integer> bookingIds = Collections.singletonList(firstBooking.getId());
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                anyInt(), anyInt(), any(Status.class), any(LocalDateTime.class)
        )).thenAnswer(invocation -> bookingIds);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDtoTest = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertEquals(commentDtoTest.getId(), comment.getId());
        assertEquals(commentDtoTest.getText(), comment.getText());
        assertEquals(commentDtoTest.getAuthorName(), comment.getAuthor().getName());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentNotAvailable() {

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                anyInt(), anyInt(), any(Status.class), any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        assertThrows(AvailableException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }
}