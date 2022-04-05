package com.daniel.library.model.service;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book any);

    Book findById(Long id);

    void delete(Long id);

    BookDTO update(Long id, BookDTO bookDTO);
}

