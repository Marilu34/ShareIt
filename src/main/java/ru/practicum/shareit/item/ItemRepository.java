package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long owner);

    @Query(value = "select * " +
            "from public.items " +
            "where available and (LOWER(name) like concat('%', ?1, '%') or LOWER(description) like concat('%', ?1, '%'))",
            nativeQuery = true)
    List<Item> search(String text);
}
