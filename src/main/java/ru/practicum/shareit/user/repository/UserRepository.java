package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    int idCount = 0;

    public User create(User user) {
        idCount++;
        user.setId(idCount);
        users.put(user.getId(), user);
        return user;
    }

    public User get(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new UserNotFoundException("Не существует пользователя с id=" + id);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public User update(User user,User userToUpdate, int id) {
            if (user.getName() != null) {
                userToUpdate.setName(user.getName());
            }
            if (user.getEmail() != null) {
                userToUpdate.setEmail(user.getEmail());
            }
            users.put(id, userToUpdate);
            return userToUpdate;
        }


    public boolean delete(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        }
        throw new UserNotFoundException("Не существует пользователя с id=" + id);
    }

}
