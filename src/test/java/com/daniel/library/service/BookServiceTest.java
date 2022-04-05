package com.daniel.library.service;

import com.daniel.library.model.dto.BookDTO;
import com.daniel.library.model.entity.Book;
import com.daniel.library.model.repository.BookRepository;
import com.daniel.library.model.service.BookService;
import com.daniel.library.model.service.exceptions.BusinessException;
import com.daniel.library.model.service.exceptions.ObjectNotFondException;
import com.daniel.library.model.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.beans.Beans;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void findByIdTest() {
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Book bookSalvo = service.findById(id);

        //verificacoes
        assertThat(bookSalvo).isNotNull();
        assertThat(bookSalvo.getId()).isEqualTo(id);
        assertThat(bookSalvo.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookSalvo.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(bookSalvo.getTitle()).isEqualTo(book.getTitle());
    }


    @Test
    @DisplayName("Deve retornar notfound ao obter um livro por Id quando ele não existe na base.")
    public void bookNotFoundByIdTest() {
        Long id = 1l;
        Mockito.when(repository.findById(id))
                .thenThrow(new ObjectNotFondException("Não existe livro com esse id."));

        org.junit.jupiter.api.Assertions.assertThrows(ObjectNotFondException.class,
                () -> service.findById(id));
        Mockito.verify(repository, Mockito.never()).deleteById(id);
        try {
            service.findById(id);
        } catch (Exception e) {
            assertThat(ObjectNotFondException.class).isEqualTo(e.getClass());
            assertThat("Não existe livro com esse id.").isEqualTo(e.getMessage());
        }
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        long id = 1l;
        Book book = createValidBook();

        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
        Mockito.doNothing().when(repository).deleteById(Mockito.anyLong());
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(id));
        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() {
        //cenário
        long id = 1l;

        //simulacao
        Book currentBook = new Book(1L, "repo", "title repo", "123");
        Book bookToUpdate = new Book(1L, "aut user", "title alt", "123");
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(currentBook));
        Mockito.when(repository.save(Mockito.any(Book.class))).thenReturn(bookToUpdate);

        BookDTO bookDTO = new BookDTO();
        BeanUtils.copyProperties(bookToUpdate, bookDTO);

        //exeucao
        bookDTO = service.update(id, bookDTO);

        //verificacoes
        assertThat(bookDTO.getId()).isEqualTo(bookToUpdate.getId());
        assertThat(bookDTO.getTitle()).isEqualTo(bookToUpdate.getTitle());
        assertThat(bookDTO.getIsbn()).isEqualTo(bookToUpdate.getIsbn());
        assertThat(bookDTO.getAuthor()).isEqualTo(bookToUpdate.getAuthor());

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<>(lista, pageRequest, 1);
        Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = service.find(book, pageRequest);


        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Book createValidBook() {
        return new Book(1L, "Maria", "Homem mal", "12ws");
    }

}
