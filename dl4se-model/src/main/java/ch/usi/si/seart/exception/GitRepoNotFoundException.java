package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.GitRepo;

import javax.persistence.metamodel.Attribute;

public class GitRepoNotFoundException extends EntityNotFoundException {

    public <T> GitRepoNotFoundException(Attribute<GitRepo, T> attribute, T value) {
        super(GitRepo.class, attribute, value);
    }
}
