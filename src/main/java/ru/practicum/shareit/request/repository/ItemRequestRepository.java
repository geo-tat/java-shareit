package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Collection<ItemRequest> findAllByRequesterId(int ownerId, Sort sort);

    //   Page<ItemRequest> findAllByUserIdNot(int userId, PageRequest pageRequest);

    //  Page<ItemRequest> findAllByRequesterIdNot(int userId, PageRequest pageRequest);

    Collection<ItemRequest> findAllByRequesterNot(User user, PageRequest pageRequest);
}
