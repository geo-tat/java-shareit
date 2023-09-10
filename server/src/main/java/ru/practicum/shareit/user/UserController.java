package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;


@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl service;


    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        return service.create(user);
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable int id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto user, @PathVariable Integer id) {
        return service.update(user, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
         service.delete(id);
    }

}
