package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(User user);

    Collection<UserDto> getUsers();

    UserDto get(int id);

    UserDto update(User user, int id);

    boolean delete(int id);


}
