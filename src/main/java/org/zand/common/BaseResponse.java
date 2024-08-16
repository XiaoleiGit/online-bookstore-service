package org.zand.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public BaseResponse(T data) {
        this.code = HttpStatus.OK.value();
        this.message = "SUCCESS";
        this.data = data;
    }
}
