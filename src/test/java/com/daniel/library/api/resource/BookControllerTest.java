package com.daniel.library.api.resource;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.service.BookService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um Livro com sucesso.")
    public void createBookTest() throws Exception {

        BookDTO bookDTO = createNewBook();
        Book savedBook = new Book(10L, "Artur", "As aventuras", "001");

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(10L))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()))
        ;
    }


    BookDTO createNewBook() {
        return new BookDTO(10L, "Artur", "As aventuras", "001");
    }

    @Test
    @DisplayName("Deve lançar erro de valiação quando não houver dados suficientes.")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)))
                .andExpect(jsonPath("status").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("error").value("Erro de validação."))
                .andExpect(jsonPath("message").value("Um ou mais campo(s) inválido(s)."))
                .andExpect(jsonPath("path").value(BOOK_API))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.errors[*].field", Matchers.containsInAnyOrder(
                        "isbn", "author", "title")))
                .andExpect(jsonPath("$.errors[*].message", Matchers.containsInAnyOrder(
                        "Campo title obrigatório.", "Campo author obrigatório.", "Campo isbn obrigatório.")))
        ;
    }

    @Test
    @DisplayName("Deve lançar erro ao tenta cadastrar um livro com isbn já utilizado por outros.")
    public void createBookWithDuplicatedIsbn() throws Exception{

        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Isbn já cadastrado."));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        ;
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", Matchers.is("Erro de Integridade.")))
                .andExpect(jsonPath("message").value("Isbn já cadastrado."))
        ;

    }
}
