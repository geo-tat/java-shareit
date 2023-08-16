package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(int id) {
        return UserMapper.toUserDto(repository.findById(id).stream()
                .findFirst().orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + id + " не найден")));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, int id) {
        User user = UserMapper.toUser(userDto);
        emailValidationForUpdate(user, id);
        User userToUpdate = repository.findById(id).stream()
                .findFirst().orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + id + " не найден"));
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(repository.save(userToUpdate));      // теперь не передаю id из эндпоинта
    }

    @Override
    @Transactional
    public void delete(int id) {
        repository.deleteById(id);

    }

    @Transactional(readOnly = true)
    private void emailValidationForUpdate(User user, int id) {
        if (repository.findAll().stream()
                .anyMatch(u -> u.getId() != id && u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new InvalidEmailException("Почтовый адрес занят!");
        }
    }

}
