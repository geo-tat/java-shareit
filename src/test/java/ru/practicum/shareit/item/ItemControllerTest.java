package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto itemDto2;
    private CommentDto commentDto;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        pageRequest = PageRequest.of(0, 5);
        User user = User.builder()
                .id(1)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("PlayStation")
                .description("Gaming console")
                .available(true)
                .owner(user)
                .build();


        itemDto = ItemMapper.toItemDto(item);
        itemDto2 = ItemMapper.toItemDto(item2);

        Comment comment = Comment.builder()
                .id(1)
                .author(user)
                .created(LocalDateTime.now())
                .text("Don't work")
                .build();

        commentDto = CommentMapper.toCommentDto(comment);
    }

    @Test
    void addItem() throws Exception {
        when(itemService.create(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Integer.class));

        verify(itemService, times(1)).create(itemDto, 1);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(any(ItemDto.class), anyInt(), anyInt())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Integer.class));

        verify(itemService, times(1)).update(itemDto, 1, 1);
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.get(anyInt(), anyInt())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Integer.class));

        verify(itemService, times(1)).get(1, 1);
    }

    @Test
    void getItems() throws Exception {

        when(itemService.getItems(anyInt(), any(PageRequest.class))).thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto, itemDto2))));

        verify(itemService, times(1)).getItems(1, pageRequest);
    }


    @Test
    void getItemsInvalidParameter() throws Exception {
        mvc.perform(get("/items")
                        .param("from", "-1")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка параметра 'from'!"));
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.search(anyString(), any(PageRequest.class))).thenReturn(List.of(itemDto2));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto2))));

        verify(itemService, times(1)).search("text", pageRequest);
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemService, times(1)).addComment(1, 1, commentDto);
    }

    @Test
    void deleteItem() throws Exception {

        mvc.perform(delete("/items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).delete(1);
    }
}