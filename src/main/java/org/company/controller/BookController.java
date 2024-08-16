package org.company.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.company.common.BaseResponse;
import org.company.controller.requestVO.AddBookToCartReq;
import org.company.controller.requestVO.CreateBookReq;
import org.company.entity.Book;
import org.company.model.ShoppingCart;
import org.company.service.BookService;

import java.util.List;

@Validated
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/books")
    @Operation(summary = "Create new book or add stock to existing book.")
    public ResponseEntity<BaseResponse<Book>> createOrAddBook(@Valid @RequestBody CreateBookReq createBookReq) {
        return ResponseEntity.ok(bookService.addBooks(createBookReq));
    }

    @GetMapping("/books")
    @Operation(summary = "Get available book list.")
    public ResponseEntity<BaseResponse<List<Book>>> getBookList() {
        return ResponseEntity.ok(bookService.getBookList());
    }

    @PostMapping("/cart")
    @Operation(summary = "Add books to shopping cart.")
    public ResponseEntity<BaseResponse<ShoppingCart>> addBookToShoppingCart(@Valid @RequestBody AddBookToCartReq addBookToCartReq) {
        return ResponseEntity.ok(bookService.addBookToShoppingCart(addBookToCartReq.getBookId(), addBookToCartReq.getAmount()));
    }

    @GetMapping("/cart")
    @Operation(summary = "Get cart total price.")
    public ResponseEntity<BaseResponse<Double>> getTotalPrice() {
        return ResponseEntity.ok(bookService.getTotalPrice());
    }

}
