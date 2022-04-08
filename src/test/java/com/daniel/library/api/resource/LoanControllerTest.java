package com.daniel.library.api.resource;

import com.daniel.library.api.resources.LoanController;
import com.daniel.library.model.dto.LoanDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.entity.Loan;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.LoanService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception {

        LoanDTO dto = new LoanDTO(null, "123", "Fulano", "customer@email.com", null);
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = new Book(1L, null, null, "123");
        BDDMockito.given(bookService.findBookByIsbn("123")).willReturn(book);

        Loan loan = new Loan(1L, "Fulano", null, book, LocalDate.now(), true);
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"))
        ;

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado.")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {

        LoanDTO dto = new LoanDTO(1L, "123", "Fulano", "fulano.@email.com", null);
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = new Book(1L, "Maria", "Ana", "123");
        BDDMockito.given(bookService.findBookByIsbn("123")).willReturn(book);

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Livro já emprestado."));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", Matchers.is("Erro de Integridade.")))
                .andExpect(jsonPath("message").value("Livro já emprestado."))
        ;
    }

}
