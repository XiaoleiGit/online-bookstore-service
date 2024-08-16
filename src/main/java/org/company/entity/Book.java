package org.company.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "book")
@Entity
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private Double price;
    private String category;
    private Integer count;
}
