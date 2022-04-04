package com.daniel.library.model.dto;

import javax.validation.constraints.NotBlank;

public class BookDTO {

    private Long id;

    @NotBlank(message = "Campo author obrigatório.")
    private String author;

    @NotBlank(message = "Campo title obrigatório.")
    private String title;

    @NotBlank(message = "Campo isbn obrigatório.")
    private String isbn;

    public BookDTO(Long id, String author, String title, String isbn) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public BookDTO() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
