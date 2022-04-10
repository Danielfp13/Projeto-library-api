package com.daniel.library.service;

import com.daniel.library.model.dto.LoanFilterDTO;
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
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
    public void getLoanDetaisTest() {
        //cenário
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        //execucao
        Loan result = loanService.findById(id);

        //verificacao
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.getBook()).isEqualTo(loan.getBook());
        assertThat(result.getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(loanRepository).findById(id);

    }

    @Test
    @DisplayName("Deve atualizar um empréstimo.")
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(loanRepository.save(loan)).thenReturn(loan);

        Loan updatedLoan = loanService.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar empréstimos pelas propriedades")
    public void findLoanTest(){
        //cenario
        LoanFilterDTO loanFilterDTO = new LoanFilterDTO("Fulano","321");

        Loan loan = createLoan();
        loan.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when( loanRepository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Loan> result = loanService.find( loanFilterDTO, pageRequest );

        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }


    @Test
    @DisplayName("Deve buscar livros emprestados")
    public void findLoansByBookTest(){
        //cenario
        Book book = new Book(1L, "maria", "Água limpa", "123");

        Loan loan = createLoan();
        loan.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when( loanRepository.findByBook(
                Mockito.any(Book.class),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Loan> result = loanService.findLoansByBook( book, pageRequest );

        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve buscar emprestimos com 4 ou mais dias.")
    public void findAllLateLoansTest() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);

        Loan loan = createLoan();
        loan.setLoanDate(threeDaysAgo);
        List<Loan> loanList = Arrays.asList(loan);
        BDDMockito.given(loanRepository.findByLoanDateLessThanAndNotReturned(Mockito.any(LocalDate.class)))
                        .willReturn(loanList);
        List<Loan> response = loanService.findAllLateLoans();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(1).isEqualTo(response.size());
        Assertions.assertThat(Loan.class).isEqualTo( response.get(0).getClass());
        Assertions.assertThat(loan.getId()).isEqualTo(response.get(0).getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo( response.get(0).getCustomer());
        Assertions.assertThat(loan.getCustomerEmail()).isEqualTo( response.get(0).getCustomerEmail());
        Assertions.assertThat(loan.getReturned()).isEqualTo( response.get(0).getReturned());

       Mockito.verify(loanRepository).findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }
    private Loan createLoan() {
        return new Loan(1L, "Fulano", "fulano@email.com", null,
                LocalDate.now(), false);
    }

}
