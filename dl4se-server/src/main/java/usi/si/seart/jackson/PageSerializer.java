package usi.si.seart.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.domain.Page;

import java.io.IOException;

public class PageSerializer extends StdSerializer<Page<?>> {

    public PageSerializer() {
        super(Page.class, false);
    }

    public static final PageSerializer INSTANCE = new PageSerializer();

    @Override
    public void serialize(Page<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("page", value.getNumber() + 1);
        gen.writeNumberField("size", value.getSize());

        gen.writeNumberField("total_pages", value.getTotalPages());
        gen.writeNumberField("total_items", value.getTotalElements());

        gen.writeBooleanField("first", value.isFirst());
        gen.writeBooleanField("last", value.isLast());

        gen.writeFieldName("sort");
        serializers.defaultSerializeValue(value.getSort(), gen);
        gen.writeFieldName("items");
        serializers.defaultSerializeValue(value.getContent(), gen);

        gen.writeEndObject();
    }
}
