package ch.usi.si.seart.views.language;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "git_repo_count_by_language")
public class GitRepoCountByLanguage extends CountByLanguage {
}
