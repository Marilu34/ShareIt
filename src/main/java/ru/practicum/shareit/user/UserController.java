package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Пользователь " + userDto + " был создан");
        return userService.createUser(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Пользователь c id = " + userId + " был обновлен");
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Пользователь " + userId + " был получен");
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        log.info("Пользователь " + userId + " был удален");
        UserDto userDto = userService.deleteUser(userId);
        return userDto;
    }
}