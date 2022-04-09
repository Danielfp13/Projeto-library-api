package com.daniel.library.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String author;
    private String title;
    private String isbn;
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    List<Loan> loans = new ArrayList<>();

    public Book(Long id, String author, String title, String isbn) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }
}

