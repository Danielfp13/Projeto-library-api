package com.daniel.library.api.resource;

import com.daniel.library.api.resources.BookController;
import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.LoanService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.exceptions.ObjectNotFondException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Deve criar um Livro com sucesso.")
    public void createBookTest() throws Exception {

        BookDTO bookDTO = createNewBookDTO();
        Book savedBook = new Book(10L, "Artur", "As aventuras", "001");

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

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
        Mockito.verify(bookService, Mockito.times(0)).save(savedBook);
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
    public void createBookWithDuplicatedIsbn() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Isbn já cadastrado."));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        final ResultActions resultActions = mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", Matchers.is("Erro de Integridade.")))
                .andExpect(jsonPath("message").value("Isbn já cadastrado."));
    }

    @Test
    @DisplayName("Deve obter as informações de um livro")
    public void getBookDetailsTest() throws Exception {

        Book book = new Book(1L, "Lucas", "Poder", "123");
        Long id = 1L;
        System.out.println(BOOK_API.concat("/" + id));
        BDDMockito.given(bookService.findById(book.getId())).willReturn(book);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);


        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar um not found se não existe book com id informado.")
    public void bookNotFound() throws Exception {
        Long id = 1L;

        BDDMockito.given(bookService.findById(Mockito.anyLong()))
                .willThrow(new ObjectNotFondException("Não existe book com esse id."));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", Matchers.is("Não existe.")))
                .andExpect(jsonPath("message").value("Não existe book com esse id."));
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        long id = 1L;
        Book book = new Book(1L, "Luana", "Até", "77rr");
        BDDMockito.given(bookService.findById(Mockito.anyLong())).willReturn(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBookDTO());

        BDDMockito.given(bookService.update(Mockito.anyLong(), Mockito.any(BookDTO.class)))
                .willReturn(createNewBookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
              .perform(request)
              .andExpect(status().isOk())
              .andExpect(jsonPath("id").value(1L))
              .andExpect(jsonPath("title").value(createNewBook().getTitle()))
              .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
              .andExpect(jsonPath("isbn").value("001"));
    }


    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception {

        Long id = 1L;

        Book book = new Book(id, createNewBook().getTitle(), createNewBook().getAuthor()
                , createNewBook().getIsbn());

        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0))
        ;
    }


    BookDTO createNewBookDTO() {
        return new BookDTO(1L, "Artur", "As aventuras", "001");
    }

    Book createNewBook() {
        return new Book(1L, "Artur", "As aventuras", "001");
    }
}
