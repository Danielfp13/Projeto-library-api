package com.daniel.library.model.service.impl;

import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.repository.LoanRepository;
import com.daniel.library.model.service.LoanService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.exceptions.ObjectNotFondException;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Livro já emprestado.");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFondException("Não existe emprestimo com esse id."));
    }

    @Override
    public void update(Loan loan) {
        loanRepository.save(loan);
    }

}
