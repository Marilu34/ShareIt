package java.ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import static org.junit.jupiter.api.Assertions.assertNull;

class UserDtoMapperTest {

    @Test
    void toUserWhenNull() {
        User user = UserDtoMapper.toUser(null);
        assertNull(user);
    }

    @Test
    void toUserDtoWhenNull() {
        UserDto userDto = UserDtoMapper.toUserDto(null);
        assertNull(userDto);
    }
}