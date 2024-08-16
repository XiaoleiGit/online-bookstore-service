package org.company.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.company.common.BaseResponse;
import org.company.common.BusinessException;
import org.company.common.ErrorMessageEnum;
import org.company.controller.requestVO.CreateBookReq;
import org.company.data.MockData;
import org.company.entity.Book;
import org.company.enums.BookCategory;
import org.company.model.BookInfo;
import org.company.model.ShoppingCart;
import org.company.model.ShoppingItem;
import org.company.repository.BookRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Test
    void addBooks_AddNewBook_Success() {
        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(null);

        BaseResponse<Book> response = bookService.addBooks(MockData.createTestFictionBook());
        assertNotNull(response.getData());
        verify(bookRepository, times(1)).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, times(1)).save(any(Book.class));
        assertEquals(MockData.createTestFictionBook().getCount(), response.getData().getCount());
    }

    @Test
    void addBooks_AddExistingBook_Success() {
        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(MockData.fictionBookEntity());

        BaseResponse<Book> response = bookService.addBooks(MockData.createTestFictionBook());
        assertNotNull(response.getData());
        verify(bookRepository, times(1)).findByTitleAndAuthor(anyString(), anyString());
        verify(bookRepository, times(1)).save(any(Book.class));
        assertEquals(MockData.fictionBookEntity().getCount() + MockData.createTestFictionBook().getCount(), response.getData().getCount());
    }

    @Test
    void addBooks_AddExistingBookWithMismatchedPrice_BusinessException() {
        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(MockData.fictionBookEntity());

        CreateBookReq req = MockData.createTestFictionBook();
        // Check book price validation
        req.setPrice(12.30);
        BusinessException businessException = assertThrows(BusinessException.class, () -> bookService.addBooks(req));
        assertEquals(ErrorMessageEnum.BOOK_INFO_MISMATCHED.getHttpStatusCode(), businessException.getStatusCode());

        // Check book category validation
        req.setPrice(10.99);
        req.setCategory(BookCategory.EDUCATION);
        businessException = assertThrows(BusinessException.class, () -> bookService.addBooks(req));
        assertEquals(ErrorMessageEnum.BOOK_INFO_MISMATCHED.getHttpStatusCode(), businessException.getStatusCode());
    }

    @Test
    void getBookList() {
        bookService.getBookList();
        verify(bookRepository, times(1)).findAvailableBooks();
    }

    @Test
    void addBookToShoppingCart_NewItemInCart_Success() {
        int bookAmountToCart = 2;
        Book bookEntity = MockData.fictionBookEntity();
        when(bookRepository.findByBookId(anyString())).thenReturn(bookEntity);
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        BaseResponse<ShoppingCart> response = bookService.addBookToShoppingCart(bookEntity.getId(), bookAmountToCart);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getItems().size());
        assertEquals(bookEntity.getId(), response.getData().getItems().get(0).getBookId());
        assertEquals(bookAmountToCart, response.getData().getItems().get(0).getAmount());
        verify(bookRepository, times(1)).findByBookId(bookEntity.getId());
        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void addBookToShoppingCart_MoreItemInCart_Success() {
        Book bookEntity = MockData.fictionBookEntity();
        when(bookRepository.findByBookId(anyString())).thenReturn(bookEntity);

        BookInfo bookInfo1 = new BookInfo();
        BeanUtils.copyProperties(bookEntity, bookInfo1);
        BookInfo bookInfo2 = new BookInfo();
        BeanUtils.copyProperties(MockData.educationBookEntity(), bookInfo2);
        List<ShoppingItem> shoppingItems = List.of(
                ShoppingItem.builder().bookId(bookEntity.getId()).bookInfo(bookInfo1).amount(1).build()
        );
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .items(shoppingItems)
                .build();
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get("cart", ShoppingCart.class)).thenReturn(shoppingCart);

        BaseResponse<ShoppingCart> response = bookService.addBookToShoppingCart(bookEntity.getId(), 2);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getItems().size());
        assertEquals(bookEntity.getId(), response.getData().getItems().get(0).getBookId());
        assertEquals(3, response.getData().getItems().get(0).getAmount());
        verify(bookRepository, times(1)).findByBookId(bookEntity.getId());
        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void addBookToShoppingCart_BookNotFound_BusinessException() {
        when(bookRepository.findByBookId(anyString())).thenReturn(null);

        BusinessException businessException = assertThrows(BusinessException.class, () -> bookService.addBookToShoppingCart("id", 2));
        verify(bookRepository, times(1)).findByBookId(anyString());
        assertEquals(ErrorMessageEnum.BOOK_NOT_FOUND.getHttpStatusCode(), businessException.getStatusCode());
    }

    @Test
    void addBookToShoppingCart_InsufficientStock_BusinessException() {
        when(bookRepository.findByBookId(anyString())).thenReturn(MockData.fictionBookEntity());

        BusinessException businessException = assertThrows(BusinessException.class, () -> bookService.addBookToShoppingCart("id", 100));
        verify(bookRepository, times(1)).findByBookId(anyString());
        assertEquals(ErrorMessageEnum.BOOK_STOCK_INSUFFICIENT.getHttpStatusCode(), businessException.getStatusCode());
    }

    @Test
    void getTotalPrice_Normal_Success() {
        int amount1 = 2;
        int amount2 = 3;
        BookInfo bookInfo1 = new BookInfo();
        BeanUtils.copyProperties(MockData.fictionBookEntity(), bookInfo1);
        BookInfo bookInfo2 = new BookInfo();
        BeanUtils.copyProperties(MockData.educationBookEntity(), bookInfo2);

        List<ShoppingItem> shoppingItems = List.of(
                ShoppingItem.builder().bookId(MockData.fictionBookEntity().getId()).bookInfo(bookInfo1).amount(amount1).build(),
                ShoppingItem.builder().bookId(MockData.educationBookEntity().getId()).bookInfo(bookInfo2).amount(amount2).build()
        );
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .items(shoppingItems)
                .build();
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get("cart", ShoppingCart.class)).thenReturn(shoppingCart);

        BaseResponse<Double> response = bookService.getTotalPrice();
        assertNotNull(response.getData());
        assertEquals(amount1 * bookInfo1.getPrice() + amount2 * bookInfo2.getPrice(), response.getData());
    }
}