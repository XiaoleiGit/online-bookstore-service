package org.zand.controller.requestVO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBookToCartReq {

    @NotBlank
    private String bookId;

    @NonNull
    private Integer amount;
}
