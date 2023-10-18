package ch.usi.si.seart.views.language;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "file_count_by_language")
public class FileCountByLanguage extends CountByLanguage {
}
