package org.zand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.zand.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitleAndAuthor(String title, String author);

    @Query(value =
            "select * from book \n" +
            "where count > 0 and deleted_at is null", nativeQuery = true)
    List<Book> findAvailableBooks();

    @Query(value =
            "select * from book \n" +
            "where id = ?1 and deleted_at is null", nativeQuery = true)
    Book findByBookId(String bookId);

}
