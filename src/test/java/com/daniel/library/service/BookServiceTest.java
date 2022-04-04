package com.daniel.library.service;

import com.daniel.library.model.entity.Book;
import com.daniel.library.model.repository.BookRepository;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setup() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = new Book(null, "Fulano", "As aventuras", "123");
        Book saveBook = new Book(1L, "Fulano", "As aventuras", "123");
        Mockito.when(repository.save(book)).thenReturn(saveBook);
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        saveBook = service.save(book);

        assertThat(saveBook.getId()).isNotNull();
        assertThat(saveBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(saveBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(saveBook.getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com ISBN duplicado.")
    public void shouldNotSaveBookWithDuplicatedISBN() {

        Book book = new Book(null, "Fulano", "As aventuras", "123");
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado.");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

}