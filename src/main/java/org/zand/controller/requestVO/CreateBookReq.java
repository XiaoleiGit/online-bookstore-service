package org.zand.controller.requestVO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.zand.enums.BookCategory;

@Data
@Builder
public class CreateBookReq {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NonNull
    private Double price;

    @NonNull
    private BookCategory category;

    @NonNull
    private Integer count;
}
