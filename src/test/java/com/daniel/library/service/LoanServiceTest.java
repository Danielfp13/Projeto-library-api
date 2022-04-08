package com.daniel.library.service;

import com.daniel.library.model.entity.Book;
import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.repository.LoanRepository;
import com.daniel.library.model.service.LoanService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.exceptions.ObjectNotFondException;
import com.daniel.library.model.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService loanService;

    @MockBean
    LoanRepository loanRepository;

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        Book book = new Book(1L, "Ana", "O Código", "123");
        String customer = "Fulano";

        Loan savingLoan = new Loan(1L, customer, "fulano@email.com", book, LocalDate.now(), false);


        Loan savedLoan = new Loan(1L, customer, "fulano@email.com", book, LocalDate.now(), false);


        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
    public void loanedBookSaveTest() {
        Book book = new Book(1L, "Ana", "O Código", "123");
        String customer = "Fulano";

        Loan savingLoan = new Loan(1L, customer, "fulano@email.com", book, LocalDate.now(), false);

        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> loanService.save(savingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Livro já emprestado.");

        Mockito.verify(loanRepository, Mockito.never()).save(savingLoan);

    }

    @Test
    @DisplayName("Deve retornar notfound tentar um emprestimo por id quando ele não existe na base.")
    public void loanNotFoundByIdTest() {
        Long id = 1L;

        org.junit.jupiter.api.Assertions.assertThrows(ObjectNotFondException.class,
                () -> loanService.findById(id));
        try {
            loanService.findById(id);
        } catch (Exception e) {
            assertThat(ObjectNotFondException.class).isEqualTo(e.getClass());
            assertThat("Não existe emprestimo com esse id.").isEqualTo(e.getMessage());
        }
    }

    @Test
    @DisplayName(" Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDetaisTest(){
        //cenário
        Long id = 1l;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when( loanRepository.findById(id) ).thenReturn(Optional.of(loan));

        //execucao
        Loan result = loanService.findById(id);

        //verificacao
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.getBook()).isEqualTo(loan.getBook());
        assertThat(result.getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify( loanRepository ).findById(id);

    }

    private Loan createLoan() {
        return  new Loan(1L, "Fulano","fulano@email.com", null, LocalDate.now(),false);
    }

}
