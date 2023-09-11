package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto1;

    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        User user1 = User.builder()
                .id(1)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        userDto1 = UserMapper.toUserDto(user1);

        User user2 = User.builder()
                .id(2)
                .name("Marlon")
                .email("brando@test.com")
                .build();

        userDto2 = UserMapper.toUserDto(user2);
    }

    @Test
    void addUser() throws Exception {
        when(userService.create(any(UserDto.class))).thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).create(userDto1);
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(any(UserDto.class), anyInt())).thenReturn(userDto1);

        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).update(userDto1, 1);
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1);
    }

    @Test
    void getUser() throws Exception {
        when(userService.get(1)).thenReturn(userDto1);

        mvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));

        verify(userService, times(1)).get(1);
    }

    @Test
    void getAllUsers() throws Exception {

        when(userService.getUsers()).thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto1, userDto2))));

        verify(userService, times(1)).getUsers();
    }
}

