package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto user);

    Collection<UserDto> getUsers();

    UserDto get(int id);

    UserDto update(UserDto user, int id);

    boolean delete(int id);


}
