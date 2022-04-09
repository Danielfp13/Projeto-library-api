package com.daniel.library.api.resources;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.dto.LoanDTO;
import com.daniel.library.model.dto.LoanFilterDTO;
import com.daniel.library.model.dto.ReturnedLoanDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
@Api("API Emprestimo.")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ApiOperation("Salvar emprestimo de livro.")
    public ResponseEntity<Long> create(@RequestBody LoanDTO dto) {
        Book book = bookService.findBookByIsbn(dto.getIsbn());
        Loan entity = new Loan(null, dto.getCustomer(), dto.getEmail(), book, LocalDate.now(), true);
        log.info("Salvando emprestimo de livro.");
        entity = loanService.save(entity);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity.getId());
    }

    @PatchMapping("{id}")
    @ApiOperation("Devolver livro.")
    public void returnBook( @PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        log.info("Devolver livro.");
        Loan loan = loanService.findById(id);
        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    @ApiOperation("Busca paginada de emprestimo com parâmetros.")
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        log.info("Busca paginada de emprestimo com parâmetros.");
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> loans = result
                .getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;

                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
}