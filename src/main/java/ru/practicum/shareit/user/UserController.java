package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        UserDto dto = userService.createUser(userDto);
        log.info("Пользователь создан {}", dto);
        return dto;
    }


    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        UserDto dto = userService.updateUser(userDto);
        log.info("Пользователь обновлен {}", dto);
        return dto;
    }


    @GetMapping
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> all = userService.getAllUsers();
        log.info("Получены все Пользователи {}", all.size());
        return all;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        UserDto dto = userService.getUserById(userId);
        log.info("Получен Пользователь {}", dto);
        return dto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Пользователь удален {}", userId);
    }
}
