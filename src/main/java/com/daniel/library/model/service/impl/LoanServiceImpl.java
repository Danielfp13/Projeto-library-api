package com.daniel.library.model.service.impl;

import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.repository.LoanRepository;
import com.daniel.library.model.service.LoanService;
import com.daniel.library.model.service.exceptions.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Livro já emprestado.");
        }
        return repository.save(loan);
    }

    @Override
    public Loan findById(Long id) {
        return null;
    }

    @Override
    public void update(Loan loan) {

    }

}
