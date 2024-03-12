package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.model.task.Task_;
import ch.usi.si.seart.model.user.Role;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.User_;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

public class UserToTaskSpecificationConverter implements Converter<User, Specification<Task>> {

    @Override
    @NonNull
    public Specification<Task> convert(@NonNull User source) {
        Role role = source.getRole();
        switch (role) {
            case ADMIN: return Specification.where(null);
            case USER: return withUserIdEquals(source.getId());
            default: throw new IllegalArgumentException("Role not supported: " + role);
        }
    }

    private Specification<Task> withUserIdEquals(Long id) {
        return (root, query, criteriaBuilder) -> {
            Join<Task, User> join = root.join(Task_.user);
            Expression<Long> expression = join.get(User_.id);
            return criteriaBuilder.equal(expression, id);
        };
    }
}
