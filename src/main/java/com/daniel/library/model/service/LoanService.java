package com.daniel.library.model.service;

import com.daniel.library.model.entity.Loan;

public interface LoanService {

    public Loan save(Loan loan);

    Loan findById(Long id);

    void update(Loan loan);
}
