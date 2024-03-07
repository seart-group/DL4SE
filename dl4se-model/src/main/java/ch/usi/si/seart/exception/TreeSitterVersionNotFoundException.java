package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.meta.TreeSitterVersion;

import javax.persistence.metamodel.Attribute;

public class TreeSitterVersionNotFoundException extends EntityNotFoundException {

    public <T> TreeSitterVersionNotFoundException(Attribute<TreeSitterVersion, T> attribute, T value) {
        super(TreeSitterVersion.class, attribute, value);
    }
}
