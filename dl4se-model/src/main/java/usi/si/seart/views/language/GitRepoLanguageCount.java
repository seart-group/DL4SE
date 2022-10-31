package usi.si.seart.views.language;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;

@Entity
@Subselect("SELECT * FROM git_repos_by_language")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GitRepoLanguageCount extends LanguageCount {
}
