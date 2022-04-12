package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.task.query.FileQueryDto;
import usi.si.seart.model.task.query.FileQuery;

public class DtoToFileQueryConverter implements Converter<FileQueryDto, FileQuery> {

    @Override
    @NonNull
    public FileQuery convert(@NonNull FileQueryDto source) {
        FileQuery.FileQueryBuilder<?, ?> builder = FileQuery.builder();
        ConverterUtil.copyToBuilder(builder, source);
        return builder
                .excludeUnparsable(source.getExcludeUnparsable())
                .build();
    }
}
