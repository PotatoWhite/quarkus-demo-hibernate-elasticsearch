package me.potato.search;

import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.potato.model.Author;
import me.potato.model.Book;
import org.hibernate.search.mapper.orm.Search;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Path("library")
public class LibraryResource {

  private final EntityManager em;

  @Transactional
  void onStart(@Observes StartupEvent ev) throws InterruptedException {
    if(Book.count() > 0) {
      Search.session(em)
            .massIndexer()
            .startAndWait();
    }
  }

  @GET
  @Path("author/search")
  @Transactional
  public List<Author> searchAuthor(@QueryParam String pattern, @QueryParam Optional<Integer> size) {
    return Search.session(em)
                 .search(Author.class)
                 .where(f -> pattern == null || pattern.trim()
                                                       .isEmpty() ? f.matchAll() : f.simpleQueryString()
                                                                                    .fields("firstName", "lastName", "books.title")
                                                                                    .matching(pattern)
                       )
                 .sort(f -> f.field("lastName_sort")
                             .then()
                             .field("firstName_sort"))
                 .fetchHits(size.orElse(20));
  }

  @PUT
  @Path("book")
  @Transactional
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void addBook(@FormParam String title, @FormParam Long authorId) {
    Author author = Author.findById(authorId);
    if(author == null) return;

    Book book = new Book(title, author);
    book.persist();

    author.getBooks()
          .add(book);
    author.persist();
  }


  @DELETE
  @Path("book/{id}")
  @Transactional
  public void deleteBook(@PathParam Long id) {
    Book book = Book.findById(id);
    if(book != null) {
      book.getAuthor()
          .getBooks()
          .remove(book);
      book.delete();
    }
  }


  @POST
  @Path("author")
  @Transactional
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void addAuthor(@FormParam String firstName, @FormParam String lastName) {
    Author author = new Author(firstName, lastName);
    author.persist();
  }

  @PUT
  @Path("author/{id}")
  @Transactional
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void updateAuthor(@PathParam Long id, @FormParam String firstName, @FormParam String lastName) {
    Author author = Author.findById(id);
    if(author == null) return;

    author.setFirstName(firstName);
    author.setLastName(lastName);
    author.persist();
  }

  @DELETE
  @Path("author/{id}")
  @Transactional
  public void deleteAuthor(@PathParam Long id) {
    Author author = Author.findById(id);
    if(author != null)
      author.delete();
  }
}
