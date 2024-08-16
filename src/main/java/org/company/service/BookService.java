package org.company.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.company.common.BaseResponse;
import org.company.common.BusinessException;
import org.company.common.ErrorMessageEnum;
import org.company.controller.requestVO.CreateBookReq;
import org.company.entity.Book;
import org.company.model.BookInfo;
import org.company.model.ShoppingCart;
import org.company.model.ShoppingItem;
import org.company.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class BookService {

    private final String SHOPPING_CART_CACHE_NAME = "shoppingCart";
    private final String SHOPPING_CART_CACHE_KEY = "cart";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheManager cacheManager;

    public BaseResponse<Book> addBooks(CreateBookReq createBookReq) {
        // Check if the book already exists
        Book bookEntity = bookRepository.findByTitleAndAuthor(createBookReq.getTitle(), createBookReq.getAuthor());
        if (bookEntity == null) {
            // Book entity does not exist, create new book record
            bookEntity = Book.builder()
                    .id(UUID.randomUUID().toString())
                    .title(createBookReq.getTitle())
                    .author(createBookReq.getAuthor())
                    .price(createBookReq.getPrice())
                    .category(createBookReq.getCategory().name())
                    .count(createBookReq.getCount())
                    .build();
        } else {
            // Book entity already exists, validate request field
            if (!Objects.equals(bookEntity.getPrice(), createBookReq.getPrice())) {
                throw new BusinessException(ErrorMessageEnum.BOOK_INFO_MISMATCHED.getHttpStatusCode(),
                        ErrorMessageEnum.BOOK_INFO_MISMATCHED.getMessage() + "price: " + bookEntity.getPrice());
            }
            if (!bookEntity.getCategory().equals(createBookReq.getCategory().name())) {
                throw new BusinessException(ErrorMessageEnum.BOOK_INFO_MISMATCHED.getHttpStatusCode(),
                        ErrorMessageEnum.BOOK_INFO_MISMATCHED.getMessage() + "category: " + bookEntity.getCategory());
            }
            // Add book stock
            bookEntity.setCount(bookEntity.getCount() + createBookReq.getCount());
        }
        bookRepository.save(bookEntity);
        log.info("New book is added.");
        return new BaseResponse<>(bookEntity);
    }

    // TODO: Add pagination query and response
    public BaseResponse<List<Book>> getBookList() {
        return new BaseResponse<>(bookRepository.findAvailableBooks());
    }

    public BaseResponse<ShoppingCart> addBookToShoppingCart(String bookId, int amount) {
        Book bookEntity = bookRepository.findByBookId(bookId);
        if (bookEntity == null) {
            throw new BusinessException(ErrorMessageEnum.BOOK_NOT_FOUND.getHttpStatusCode(), ErrorMessageEnum.BOOK_NOT_FOUND.getMessage());
        }
        if (bookEntity.getCount() < amount) {
            throw new BusinessException(ErrorMessageEnum.BOOK_STOCK_INSUFFICIENT.getHttpStatusCode(), ErrorMessageEnum.BOOK_STOCK_INSUFFICIENT.getMessage() + bookEntity.getCount());
        }
        BookInfo bookInfo = new BookInfo();
        BeanUtils.copyProperties(bookEntity, bookInfo);

        Cache cache = cacheManager.getCache(SHOPPING_CART_CACHE_NAME);
        if (cache == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get local shopping cart cache.");
        }

        ShoppingCart shoppingCartCached;

        if (cache.get(SHOPPING_CART_CACHE_KEY, ShoppingCart.class) == null) {
            shoppingCartCached = ShoppingCart.builder()
                    .items(List.of(ShoppingItem.builder().bookId(bookId).bookInfo(bookInfo).amount(amount).build()))
                    .build();
        } else {
            shoppingCartCached = cache.get(SHOPPING_CART_CACHE_KEY, ShoppingCart.class);
            if (shoppingCartCached == null) {
                shoppingCartCached = ShoppingCart.builder().items(new ArrayList<>()).build();
            }
            List<ShoppingItem> shoppingCartCachedItems = shoppingCartCached.getItems();
            AtomicBoolean newBookFlag = new AtomicBoolean(true);
            shoppingCartCachedItems.forEach(shoppingItem -> {
                if (shoppingItem.getBookId().equals(bookId)) {
                    shoppingItem.setAmount(shoppingItem.getAmount() + amount);
                    newBookFlag.set(false);
                }
            });
            if (newBookFlag.get()) {
                ShoppingItem newShoppingItem = ShoppingItem.builder().bookId(bookId).bookInfo(bookInfo).amount(amount).build();
                shoppingCartCachedItems = new ArrayList<>(shoppingCartCachedItems);
                shoppingCartCachedItems.add(newShoppingItem);
                shoppingCartCached.setItems(shoppingCartCachedItems);
            }
        }
        // Update cache
        cache.put(SHOPPING_CART_CACHE_KEY, shoppingCartCached);

        // Update Book amount in DB
        bookEntity.setCount(bookEntity.getCount() - amount);
        bookRepository.save(bookEntity);

        return new BaseResponse<>(shoppingCartCached);
    }

    public BaseResponse<Double> getTotalPrice() {
        Cache cache = cacheManager.getCache(SHOPPING_CART_CACHE_NAME);
        if (cache == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get local shopping cart cache.");
        }
        if (cache.get(SHOPPING_CART_CACHE_KEY, ShoppingCart.class) == null) {
            return new BaseResponse<>(0.0);
        }
        ShoppingCart shoppingCart = cache.get(SHOPPING_CART_CACHE_KEY, ShoppingCart.class);
        if (shoppingCart == null || shoppingCart.getItems().isEmpty()) {
            return new BaseResponse<>(0.0);
        }
        return new BaseResponse<>(shoppingCart.getItems()
                .stream()
                .mapToDouble(item -> item.getAmount() * item.getBookInfo().getPrice())
                .sum());
    }
}
