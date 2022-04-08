package com.daniel.library.model.service;

import com.daniel.library.model.dto.LoanFilterDTO;
import com.daniel.library.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface LoanService {

    public Loan save(Loan loan);

    Loan findById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO loanFilterDTO, PageRequest pageRequest);
}
