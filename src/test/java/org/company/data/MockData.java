package org.company.data;

import org.company.controller.requestVO.CreateBookReq;
import org.company.entity.Book;
import org.company.enums.BookCategory;

import java.util.UUID;

public class MockData {

    public static CreateBookReq createTestFictionBook() {
        return CreateBookReq.builder()
                .title("Book1")
                .author("Author1")
                .price(10.99)
                .category(BookCategory.FICTION)
                .count(10)
                .build();
    }
    public static CreateBookReq createTestEducationBook() {
        return CreateBookReq.builder()
                .title("Book2")
                .author("Author2")
                .price(22.99)
                .category(BookCategory.EDUCATION)
                .count(5)
                .build();
    }

    public static Book fictionBookEntity() {
        return Book.builder()
                .id(UUID.randomUUID().toString())
                .title("Book1")
                .author("Author1")
                .price(10.99)
                .category(BookCategory.FICTION.name())
                .count(10)
                .build();
    }

    public static Book educationBookEntity() {
        return Book.builder()
                .id(UUID.randomUUID().toString())
                .title("Book2")
                .author("Author2")
                .price(22.99)
                .category(BookCategory.EDUCATION.name())
                .count(5)
                .build();
    }
}
