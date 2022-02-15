package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "file")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File extends Code {

    @Column(nullable = false)
    String path;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @Singular
    List<Function> functions = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(repo, path);
    }

    public String toString() {
        return "File(id=" + this.id +
                ", repo=" + this.repo +
                ", language=" + this.language +
                ", path=" + this.path +
                ", content=" + this.content +
                ", contentHash=" + this.contentHash +
                ", ast=" + this.ast +
                ", astHash=" + this.astHash +
                ", isParsed=" + this.isParsed +
                ", tokens=" + this.tokens +
                ", lines=" + this.lines +
                ", characters=" + this.characters +
                ", isTest=" + this.isTest +
                ", containsNonAscii=" + this.containsNonAscii + ")";
    }
}
