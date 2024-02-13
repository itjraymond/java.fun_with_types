package ca.jent.javafunwithtypes.types;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.io.IOException;
import java.util.function.Function;

public final class Brand {
    private final String value;

    private Brand() { this.value = null; }
    private Brand(String value) {
        this.value = value.trim().toUpperCase();
    }

    public static Brand of(String brand) {
        return new Brand(brand);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    static private final Function<String,String> capitalize = s -> s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();

    public static class BrandSerializer extends StdSerializer<Brand> {

        public BrandSerializer() {
            super(Brand.class);
        }

        @Override
        public void serialize(Brand brand, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeString(brand.getValue().transform(capitalize));
        }
    }

    public static class BrandDeserializer extends StdDeserializer<Brand> {

        public BrandDeserializer() {
            super(Brand.class);
        }

        @Override
        public Brand deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return Brand.of(parser.getText());
        }
    }

    @ReadingConverter
    public static class BrandReadConverter implements Converter<String, Brand> {

        @Override
        public Brand convert(String value) {
            return Brand.of(value);
        }
    }

    @WritingConverter
    public static class BrandWriteConverter implements Converter<Brand, String> {

        @Override
        public String convert(Brand brand) {
            return brand.value;
        }
    }
}
