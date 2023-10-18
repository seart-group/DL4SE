package usi.si.seart.server.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.Task_;
import usi.si.seart.server.dto.task.TaskSearchDto;

public class TaskSearchDtoToSpecificationConverter implements Converter<TaskSearchDto, Specification<Task>> {

    @Override
    @NonNull
    public Specification<Task> convert(TaskSearchDto source) {
        Specification<Task> specification = Specification.where(null);
        if (source.hasUuid()) {
            Specification<Task> other = withUuidContaining(source.getUuid());
            specification = specification.and(other);
        }
        return specification;
    }

    private Specification<Task> withUuidContaining(String pattern) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Task_.UUID).as(String.class), "%" + pattern + "%");
    }
}
