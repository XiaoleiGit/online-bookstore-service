package org.company.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookInfo {
    private String title;
    private String author;
    private Double price;
    private String category;
}
