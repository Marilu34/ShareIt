package validators;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationValidatorTest {
    @Test
    void validatePaginationNormalWay() {
        Pageable page =  PageRequest.of(20, 20);
        assertEquals(1, page.getPageNumber());
        assertEquals(20, page.getPageSize());
    }
}