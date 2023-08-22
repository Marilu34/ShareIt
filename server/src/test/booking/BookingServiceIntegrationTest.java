package booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CreationItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;


    @Test
    void approveBookingTest() {
        User owner = userService.create(new User(0, "owner name", "owner@name.org"));

        Item item1 = itemService.create(
                new CreationItemRequest("item1 name", "item1 description", true, 0),
                owner.getId());

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        User booker = userService.create(new User(0, "booker name", "booker@name.org"));
        Booking booking1 = bookingService.create(
                CreateBooking.builder().itemId(item1.getId()).start(now.plusHours(1)).end(now.plusHours(2)).build(),
                booker.getId());

        Booking approvedBooking = bookingService.approve(booking1.getId(), true, owner.getId());

        assertEquals(booking1.getId(), approvedBooking.getId());
        assertEquals(Status.APPROVED, approvedBooking.getStatus());
    }
}