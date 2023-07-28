package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEmailException;
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
    public UserDto create(User user) {
        emailValidation(user);
        return UserMapper.toUserDto(repository.create(user));
    }

    @Override
    public Collection<UserDto> getUsers() {
        return repository.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(int id) {
        return UserMapper.toUserDto(repository.get(id));

    }

    @Override
    public UserDto update(User user, int id) {
        emailValidationForUpdate(user, id);
        return UserMapper.toUserDto(repository.update(user, id));
    }

    @Override
    public boolean delete(int id) {
        return repository.delete(id);

    }

    private void emailValidation(User user) {
        for (User u : repository.getUsers()) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new InvalidEmailException("Почтовый адрес занят!");
            }
        }
    }

    private void emailValidationForUpdate(User user, int id) {
        if (repository.getUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()) && u.getId() != id)) {
            throw new InvalidEmailException("Почтовый адрес занят!");
        }
    }

}
