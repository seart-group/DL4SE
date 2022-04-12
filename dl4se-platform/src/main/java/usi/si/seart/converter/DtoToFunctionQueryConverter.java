package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.task.query.FunctionQueryDto;
import usi.si.seart.model.task.query.FunctionQuery;

public class DtoToFunctionQueryConverter implements Converter<FunctionQueryDto, FunctionQuery> {

    @Override
    @NonNull
    public FunctionQuery convert(@NonNull FunctionQueryDto source) {
        FunctionQuery.FunctionQueryBuilder<?, ?> builder = FunctionQuery.builder();
        ConverterUtil.copyToBuilder(builder, source);
        return builder
                .excludeBoilerplate(source.getExcludeBoilerplate())
                .build();
    }
}
