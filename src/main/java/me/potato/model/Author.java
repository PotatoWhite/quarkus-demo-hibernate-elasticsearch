package me.potato.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Indexed
@Entity
public class Author extends PanacheEntity {
  @NonNull
  @FullTextField(name = "name")
  @KeywordField(name = "firstName_sort", sortable = Sortable.YES, normalizer = "sort")
  private String firstName;

  @NonNull
  @FullTextField(analyzer = "name")
  @KeywordField(name = "lastName_sort", sortable = Sortable.YES, normalizer = "sort")
  private String lastName;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @IndexedEmbedded
  private List<Book> books;
}
