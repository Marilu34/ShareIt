package ru.practicum.shareit.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.Status.APPROVED;


@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(ItemRepository itemRepository,
                              UserRepository userRepository,
                              BookingRepository bookingRepository,
                              CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDTO) throws ValidationException, NotFoundException {
        Comment comment = CommentMapper.fromCommentDto(commentDTO);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(""));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(""));
        LocalDateTime now = LocalDateTime.now();
        if (!(bookingRepository.findBookingByItemAndUser(itemId, userId, APPROVED.name(), now)).isEmpty()) {
            comment.setItemId(itemId);
            comment.setAuthor(user);
            comment.setCreated(now);
            return (CommentMapper.toCommentDto(commentRepository.save(comment)));
        } else throw new ValidationException("");
    }
}
