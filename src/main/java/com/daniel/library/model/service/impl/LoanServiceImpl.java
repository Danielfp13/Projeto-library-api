package com.daniel.library.model.service.impl;

import com.daniel.library.model.dto.LoanFilterDTO;
import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.repository.LoanRepository;
import com.daniel.library.model.service.LoanService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.exceptions.ObjectNotFondException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

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
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO loanFilterDTO, PageRequest pageRequest) {
        return null;
    }

}
