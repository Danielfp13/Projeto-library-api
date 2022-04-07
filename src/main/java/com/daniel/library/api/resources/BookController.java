package com.daniel.library.api.resources;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
@Slf4j
@Api("API Biblioteca.")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    @PostMapping
    @ApiOperation("Salvar livro.")
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        log.info("Criando um livro para no endpoint: {}", uri);
        return ResponseEntity.created(uri).body(modelMapper.map(entity, BookDTO.class));
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar livro por ID.")
    ResponseEntity<BookDTO> findById(@PathVariable Long id) {
        log.info("Obtendo um livro com identificador: {}", id);
        BookDTO bookDTO = modelMapper.map(service.findById(id), BookDTO.class);
        return ResponseEntity.ok().body(bookDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir livro.")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deletando um livro com identificador: {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ApiOperation("Alterar livro.")
    ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody @Valid BookDTO bookDTO) {
        log.info("Alterando informações do livro com identificador: {}", id);
        bookDTO = service.update(id, bookDTO);
        return ResponseEntity.ok().body(bookDTO);
    }

    @GetMapping
    @ApiOperation("Busca paginada com parâmetros.")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageRequest, result.getTotalElements());
    }
}
