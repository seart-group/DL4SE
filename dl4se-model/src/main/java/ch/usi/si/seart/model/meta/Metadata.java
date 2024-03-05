package ch.usi.si.seart.model.meta;

import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.validation.constraints.SHAHash;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "metadata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Metadata {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotNull
    @Column(name = "binding_version")
    @JsonIgnore
    String bindingVersion;

    @NotNull
    @SHAHash
    @Column(name = "binding_git_sha")
    @JsonIgnore
    String bindingGitSHA;

    @NotNull
    @URL
    @Column(name = "binding_git_url")
    @JsonIgnore
    String bindingGitURL;

    @NotNull
    @Column(name = "binding_git_tag")
    @JsonIgnore
    String bindingGitTag;

    @JsonGetter("binding")
    public Map<String, Object> getBinding() {
        return Stream.of(
                Map.entry("version", bindingVersion),
                Map.entry("git_sha", bindingGitSHA),
                Map.entry("git_url", bindingGitURL),
                Map.entry("git_tag", bindingGitTag)
        ).collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, inserting) -> existing,
                        LinkedHashMap::new
                )
        );
    }

    @NotNull
    @Positive
    @Column(name = "api_version")
    @JsonIgnore
    Integer apiVersion;

    @NotNull
    @SHAHash
    @Column(name = "api_git_sha")
    @JsonIgnore
    String apiGitSHA;

    @NotNull
    @URL
    @Column(name = "api_git_url")
    @JsonIgnore
    String apiGitURL;

    @NotNull
    @Column(name = "api_git_tag")
    @JsonIgnore
    String apiGitTag;

    @JsonGetter("api")
    public Map<String, Object> getAPI() {
        return Stream.of(
                Map.entry("version", apiVersion),
                Map.entry("git_sha", apiGitSHA),
                Map.entry("git_url", apiGitURL),
                Map.entry("git_tag", apiGitTag)
        ).collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, inserting) -> existing,
                        LinkedHashMap::new
                )
        );
    }

    @NotNull
    @Positive
    @Column(name = "language_version")
    @JsonIgnore
    Integer languageVersion;

    @NotNull
    @SHAHash
    @Column(name = "language_git_sha")
    @JsonIgnore
    String languageGitSHA;

    @NotNull
    @URL
    @Column(name = "language_git_url")
    @JsonIgnore
    String languageGitURL;

    @NotNull
    @Column(name = "language_git_tag")
    @JsonIgnore
    String languageGitTag;

    @JsonGetter("language")
    public Map<String, Object> getLanguage() {
        return Stream.of(
                Map.entry("version", languageVersion),
                Map.entry("git_sha", languageGitSHA),
                Map.entry("git_url", languageGitURL),
                Map.entry("git_tag", languageGitTag)
        ).collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, inserting) -> existing,
                        LinkedHashMap::new
                )
        );
    }

    @OneToMany(mappedBy = "metadata", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonIgnore
    List<File> files = new ArrayList<>();
}
