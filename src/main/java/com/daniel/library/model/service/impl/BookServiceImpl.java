package com.daniel.library.model.service.impl;

import com.daniel.library.model.entity.Book;
import com.daniel.library.model.repository.BookRepository;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    @Override
    @Transactional
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }
}