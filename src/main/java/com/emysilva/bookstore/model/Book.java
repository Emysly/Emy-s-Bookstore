package com.emysilva.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;

    @Column(columnDefinition = "text")
    private String description;

    private String publisher;
    private String publicationDate;
    private String language;
    private String category;
    private String format;
    private Integer numberOfPages;
    private Integer inStockNumber;
    private Integer isbn;
    private Double shippingWeight;
    private Double listPrice;
    private Double ourPrice;

    @Transient
    private MultipartFile bookImage;

    private boolean active = true;
}
