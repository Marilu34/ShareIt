package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void testCreateUser() {
        UserDto user = UserDto.builder().name("name").email("mail@mail.ru").build();
        when(userService.createUser(user)).thenReturn(user);

        String responseBody = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), responseBody);
    }

    @SneakyThrows
    @Test
    void testGetUserById() {
        long userId = 1;
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void testGetAllUsers() {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getAllUsers();
    }


    @SneakyThrows
    @Test
    void testUpdate() {
        long userId = 1L;
        UserDto user = UserDto.builder().id(null).email("email@yandex.ru").name("name").build();
        when(userService.updateUser(any(UserDto.class))).then(AdditionalAnswers.returnsFirstArg());

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class));
    }

    @SneakyThrows
    @Test
    void testDeleteUser() {
        long userId = 123;
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
        verifyNoMoreInteractions(userService);
    }
}