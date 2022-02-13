package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "function")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Function {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="repo_id", nullable=false)
    GitRepo repo;

    @ManyToOne(optional = false)
    @JoinColumn(name="file_id", nullable=false)
    File file;

    @ManyToOne(optional = false)
    @JoinColumn(name="lang_id", nullable=false)
    Language language;

    @Column(nullable = false)
    String content;

    @Column(name = "content_hash", nullable = false)
    String contentHash;

    @Column(nullable = false)
    String ast;

    @Column(name = "ast_hash", nullable = false)
    String astHash;

    @Column(name = "is_parsed", nullable = false, columnDefinition = "boolean default false")
    Boolean isParsed = false;

    @Column(nullable = false)
    Long tokens;

    @Column(nullable = false)
    Long lines;

    @Column(nullable = false)
    Long characters;

    @Column(name = "is_test", nullable = false)
    Boolean isTest;

    @Column(name = "is_boilerplate", nullable = false)
    Boolean isBoilerPlate;

    @Column(name = "contains_non_ascii", nullable = false)
    Boolean containsNonAscii;
}
