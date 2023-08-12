package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void testCreate() {
        long id = 1L;
        ItemDto item = ItemDto.builder().name("name").available(true).build();
        long userId = 999L;
        when(itemService.createItem(userId, item)).thenReturn(item.toBuilder().id(id).build());
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(item))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.available", Matchers.is(true)));
    }


    @SneakyThrows
    @Test
    void testUpdate() {
        long requestId = 1L;
        ItemDto item = ItemDto.builder().requestId(requestId).build();
        long itemId = 2L;
        long userId = 3L;
        when(itemService.updateItem(userId, item.toBuilder().id(itemId).build()))
                .then(returnsSecondArg());
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(item))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.requestId", is(requestId), Long.class));
    }

    @SneakyThrows
    @Test
    void testGetById() {
        long itemId = 1L;
        long userId = 2L;
        ItemCommentsDto expected = new ItemCommentsDto();
        expected.setId(itemId);
        expected.setDescription("description");
        expected.setComments(List.of(
                new CommentDto(1, "text1", "name1", LocalDateTime.now().minusNanos(100)),
                new CommentDto(2, "text2", "name2", LocalDateTime.now())
        ));
        when(itemService.getByItemId(itemId, userId)).thenReturn(expected);
        String responseBody = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(expected), responseBody);
    }


    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("size", "30")
                        .queryParam("from", "60"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    void testGetByText() {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("text", "anyText"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void testPostComment() {
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"anyText\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void testBadPostComment() {
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(" ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void testBadCreate() throws Exception {
        ItemDto dto = ItemDto.builder().name("name").available(true).build();
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenThrow(NotFoundException.class);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound());
    }
}