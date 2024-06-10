package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.User_;
import ch.usi.si.seart.server.dto.user.UserSearchDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.metamodel.SingularAttribute;

public class UserSearchDtoToSpecificationConverter implements Converter<UserSearchDto, Specification<User>> {
    
    @Override
    @NonNull
    public Specification<User> convert(UserSearchDto source) {
        Specification<User> specification = Specification.where(null);
        if (source.hasUid()) {
            Specification<User> other = withUidContaining(source.getUid());
            specification = specification.and(other);
        }
        if (source.hasEmail()) {
            Specification<User> other = withEmailContaining(source.getEmail());
            specification = specification.and(other);
        }
        if (source.hasOrganisation()) {
            Specification<User> other = withOrganisationContaining(source.getOrganisation());
            specification = specification.and(other);
        }
        return specification;
    }

    private Specification<User> withUidContaining(String pattern) {
        return withStringAttributeContaining(User_.uid, pattern);
    }

    private Specification<User> withEmailContaining(String pattern) {
        return withStringAttributeContaining(User_.email, pattern);
    }

    private Specification<User> withOrganisationContaining(String pattern) {
        return withStringAttributeContaining(User_.organisation, pattern);
    }

    private Specification<User> withStringAttributeContaining(
            SingularAttribute<User, String> attribute, String pattern
    ) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(attribute), "%" + pattern + "%");
    }
}
