package org.zand.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorMessageEnum {
    BOOK_INFO_MISMATCHED(HttpStatus.BAD_REQUEST, "Book already exists but failed to add book stock, price or category is mismatched. "),
    BOOK_STOCK_INSUFFICIENT(HttpStatus.INTERNAL_SERVER_ERROR, "Book stock is insufficient. Current stock: "),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "Book is not available.")
    ;

    private final HttpStatusCode httpStatusCode;
    private final String message;

    ErrorMessageEnum(HttpStatusCode httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

}
