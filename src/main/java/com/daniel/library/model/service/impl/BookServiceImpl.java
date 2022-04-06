package com.daniel.library.model.service.impl;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.repository.BookRepository;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.exceptions.ObjectNotFondException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    @Override
    @Transactional
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }

    @Override
    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ObjectNotFondException("Não existe book com esse id."));
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public BookDTO update(Long id, BookDTO bookDTO) {
        Book book = findById(id);
        book.setAuthor(bookDTO.getAuthor());
        book.setTitle(bookDTO.getTitle());
        BeanUtils.copyProperties(repository.save(book), bookDTO);
        return bookDTO;

    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return repository.findAll(example, pageRequest);
    }
}