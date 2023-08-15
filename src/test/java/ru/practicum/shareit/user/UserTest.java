package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void hashCode_WhenSameId_ShouldReturnSameHashCode() {
        // Arrange
        long id = 1;
        User user1 = User.builder().id(id).build();
        User user2 = User.builder().id(id).build();

        // Act
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_WhenDifferentId_ShouldReturnDifferentHashCode() {
        // Arrange
        long id1 = 1;
        long id2 = 2;
        User user1 = User.builder().id(id1).build();
        User user2 = User.builder().id(id2).build();

        // Act
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }
}