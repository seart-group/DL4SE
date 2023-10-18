package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.User_;
import ch.usi.si.seart.server.dto.user.UserSearchDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

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
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(User_.UID), "%" + pattern + "%");
    }

    private Specification<User> withEmailContaining(String pattern) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(User_.EMAIL), "%" + pattern + "%");
    }

    private Specification<User> withOrganisationContaining(String pattern) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(User_.ORGANISATION), "%" + pattern + "%");
    }
}
