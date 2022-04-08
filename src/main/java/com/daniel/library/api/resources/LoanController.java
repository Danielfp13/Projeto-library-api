package com.daniel.library.api.resources;

import com.daniel.library.model.dto.LoanDTO;
import com.daniel.library.model.dto.ReturnedLoanDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody LoanDTO dto) {
        Book book = bookService.findBookByIsbn(dto.getIsbn());
        Loan entity = new Loan(null, dto.getCustomer(), dto.getEmail(), book, LocalDate.now(), true);
        entity = loanService.save(entity);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity.getId());
    }
    @PatchMapping("{id}")
    public void returnBook( @PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = loanService.findById(id);
        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }
}