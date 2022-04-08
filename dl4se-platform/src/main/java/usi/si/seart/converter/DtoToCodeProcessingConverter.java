package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.task.processing.CodeProcessingDto;
import usi.si.seart.model.task.processing.CodeProcessing;

public class DtoToCodeProcessingConverter implements Converter<CodeProcessingDto, CodeProcessing> {

    @Override
    @NonNull
    public CodeProcessing convert(@NonNull CodeProcessingDto source) {
        return CodeProcessing.builder()
                .removeDocstring(source.getRemoveDocstring())
                .removeInnerComments(source.getRemoveInnerComments())
                .maskToken(source.getMaskToken())
                .maskPercentage(source.getMaskPercentage())
                .maskContiguousOnly(source.getMaskContiguousOnly())
                .idioms(source.getIdioms())
                .build();
    }
}
