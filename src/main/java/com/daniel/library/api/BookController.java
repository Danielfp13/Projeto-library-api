package com.daniel.library.api;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.service.BookService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(modelMapper.map(entity, BookDTO.class));
    }

}
