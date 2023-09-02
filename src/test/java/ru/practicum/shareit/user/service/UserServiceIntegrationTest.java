package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItApp.class, UserServiceImpl.class})
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;


    @Test
    void createUser() {
        UserDto user = UserDto.builder()
                .id(1)
                .name("Harrison")
                .email("ford@test.com")
                .build();

        userService.create(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User queryUser = query
                .setParameter("id", 1)
                .getSingleResult();
        assertEquals(UserMapper.toUser(user), queryUser);

    }

    @Test
    void getAllUsers() {
        UserDto user = UserDto.builder()
                .id(1)
                .name("Harrison")
                .email("ford@test.com")
                .build();
        UserDto user2 = UserDto.builder()
                .id(2)
                .name("Marlon")
                .email("brando@test.com")
                .build();

        userService.create(user);
        userService.create(user2);

        List<User> allUsers = em.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
        assertEquals(2, allUsers.size());
        assertEquals(UserMapper.toUser(user),allUsers.get(0));



    }
}
