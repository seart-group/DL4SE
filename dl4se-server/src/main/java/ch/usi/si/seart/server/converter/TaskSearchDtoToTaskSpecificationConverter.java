package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.model.task.Task_;
import ch.usi.si.seart.server.dto.task.TaskSearchDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.Expression;

public class TaskSearchDtoToSpecificationConverter implements Converter<TaskSearchDto, Specification<Task>> {

    @Override
    @NonNull
    public Specification<Task> convert(@NonNull TaskSearchDto source) {
        Specification<Task> specification = Specification.where(null);
        if (source.hasUuid()) {
            Specification<Task> other = withUuidContaining(source.getUuid());
            specification = specification.and(other);
        }
        return specification;
    }

    private Specification<Task> withUuidContaining(String pattern) {
        return (root, query, criteriaBuilder) -> {
            Expression<String> expression = root.get(Task_.UUID).as(String.class);
            return criteriaBuilder.like(expression, "%" + pattern + "%");
        };
    }
}
