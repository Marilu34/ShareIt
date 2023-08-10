package ru.practicum.shareit.item;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Stream;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable page);

    @Query(value = "SELECT i FROM Item AS i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) like(concat('%', LOWER(:text), '%')) " +
            "OR (LOWER(i.description) like(concat('%', LOWER(:text), '%'))))")
    Stream<Item> findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);


}
