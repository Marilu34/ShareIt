package ru.practicum.shareit.comment;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.Status.APPROVED;


@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Autowired
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDTO) throws ValidationException, NotFoundException {
        Comment comment = CommentMapper.fromCommentDto(commentDTO);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(""));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException(""));


        if (!(bookingRepository.findBookingByItemAndUser(itemId, userId, APPROVED.name(), LocalDateTime.now())).isEmpty()) {
            comment.setItemId(itemId);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return (CommentMapper.toCommentDto(commentRepository.save(comment)));
        } else throw new ValidationException("");
    }
}
