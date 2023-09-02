package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;

    private User user2;

    private UserDto userDto1;

    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        userDto1 = UserMapper.toUserDto(user1);

        user2 = User.builder()
                .id(2)
                .name("Marlon")
                .email("brando@test.com")
                .build();

        userDto2 = UserMapper.toUserDto(user2);
    }

    @Test
    void addUser() {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto userDtoTest = userService.create(userDto1);

        assertEquals(userDtoTest.getId(), userDto1.getId());
        assertEquals(userDtoTest.getName(), userDto1.getName());
        assertEquals(userDtoTest.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void updateUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        userDto1.setName("Harrison");
        userDto1.setEmail("ford@test.com");

        UserDto userDtoUpdated = userService.update(userDto1, 1);

        assertEquals(userDtoUpdated.getName(), userDto1.getName());
        assertEquals(userDtoUpdated.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void deleteUser() {
        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        UserDto userDtoTest = userService.get(1);

        assertEquals(userDtoTest.getId(), userDto1.getId());
        assertEquals(userDtoTest.getName(), userDto1.getName());
        assertEquals(userDtoTest.getEmail(), userDto1.getEmail());

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getByIdUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.get(5));
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        Collection<UserDto> userDtoList = userService.getUsers();

        assertEquals(userDtoList, List.of(userDto1, userDto2));

        verify(userRepository, times(1)).findAll();
    }
}