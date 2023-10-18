package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.code.Code;
import ch.usi.si.seart.server.repository.CodeRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

public interface CodeService {

    Long count(Specification<Code> specification);

    Stream<Code> streamAllDynamically(Specification<Code> specification);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class CodeServiceImpl implements CodeService {

        CodeRepository codeRepository;

        @Override
        public Long count(Specification<Code> specification) {
            return codeRepository.count(specification);
        }

        @Override
        public Stream<Code> streamAllDynamically(Specification<Code> specification) {
            return codeRepository.stream(specification, Code.class);
        }
    }
}
