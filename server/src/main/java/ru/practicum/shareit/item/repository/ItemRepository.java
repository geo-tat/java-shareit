package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Collection<Item> findAllItemsByOwnerId(int id, PageRequest pageRequest);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    Collection<Item> search(String text, PageRequest pageRequest);

    @Query("SELECT i FROM Item i " +
            "WHERE i.request.id IN :requestsIds")
    Collection<Item> findAllItemsForRequestIds(Collection<Integer> requestsIds);

    Collection<Item> findAllByRequestId(int requestId);
}
