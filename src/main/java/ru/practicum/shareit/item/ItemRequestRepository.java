package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(long requesterId, Sort created);

    Page<ItemRequest> findAllByRequesterIdNot(long userId, Pageable page);
}
