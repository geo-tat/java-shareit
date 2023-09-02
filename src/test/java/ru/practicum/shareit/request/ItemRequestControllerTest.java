package ru.practicum.shareit.request;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;
    private PageRequest pageRequest;

    private ItemRequestDto itemRequestDto;
    private ItemRequestFullDto itemRequestFullDto;


    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 5, Sort.by("created").descending());
        User user = User.builder()
                .id(1)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        User user2 = User.builder()
                .id(2)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        User user3 = User.builder()
                .id(3)
                .name("Jamie")
                .email("fox@test.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1)
                .description("Nintendo")
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2)
                .description("Sega")
                .created(LocalDateTime.now())
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
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();
        Item item3 = Item.builder()
                .id(3)
                .name("Nintendo")
                .description("fyi")
                .available(true)
                .owner(user3)
                .request(itemRequest)
                .build();

        itemRequestDto = RequestMapper.toDto(itemRequest);
        itemRequestFullDto = RequestMapper.toDtoFull(itemRequest);

    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(anyInt(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).addRequest(1, itemRequestDto);
    }

    @Test
    void getOwnRequests() throws Exception {
        when(itemRequestService.getOwnRequests(anyInt())).thenReturn(List.of(itemRequestFullDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestFullDto))));     //???

        verify(itemRequestService, times(1)).getOwnRequests(3);
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyInt(), any(PageRequest.class))).thenReturn(List.of(itemRequestFullDto));

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestFullDto))));

        verify(itemRequestService, times(1)).getAllRequests(1, pageRequest);
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getById(anyInt(), anyInt())).thenReturn(itemRequestFullDto);

        mvc.perform(get("/requests/{requestId}", itemRequestFullDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestFullDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).getById(1, 1);
    }

}