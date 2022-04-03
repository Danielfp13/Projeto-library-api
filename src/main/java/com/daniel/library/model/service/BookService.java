package com.daniel.library.model.service;

import com.daniel.library.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book any);
}

