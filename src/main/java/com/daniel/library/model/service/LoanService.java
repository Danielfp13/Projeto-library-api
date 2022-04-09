package com.daniel.library.model.service;

import com.daniel.library.model.dto.LoanFilterDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {

    public Loan save(Loan loan);

    Loan findById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable);

    Page<Loan> findLoansByBook(Book book, Pageable pageable);

    List<Loan> findAllLateLoans();
}
