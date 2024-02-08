package ca.jent.javafunwithtypes.types;

import java.io.IOException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.NonNull;

public record Sku(@NonNull String value) {

    public Sku {
        if (value.trim().length() != 8 || value.trim().startsWith("0")) throw new IllegalArgumentException("Sku must be made of 8 digits and not start with zero");
        // we can add verification to be only digits
    }

    // Typical "of" factory method
    public static Sku of(String value) {
        return new Sku(value);
    }

    // Nice to have:  e.g. System.out.println(sku); it will call the Sku.toString();
    @Override
    public String toString() {
        return value;
    }

    public static class SkuSerializer extends StdSerializer<Sku> {

        public SkuSerializer() {
            super(Sku.class);
        }

        @Override
        public void serialize(Sku sku, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeString(sku.value);
        }
    }

    public static class SkuDeserializer extends StdDeserializer<Sku> {

        public SkuDeserializer() {
            super(Sku.class);
        }

        @Override
        public Sku deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return Sku.of(parser.getText());
        }
    }
}

/**
 * Note: When we serialize/deserialize such record (without custom serializer/deserializer) we get something like:
 *
 * {
 *     "sku": {
 *         "value" : "10000000"
 *     }
 * }
 *
 * We prefer to flatten this structure to be more like:
 *
 * {
 *     "sku" : "10000000"
 * }
 *
 * Hence we add custom serializer/deserializer.  We need to register those serializer/deserializer
 * - see @Configuration SerializerDeserializerConfiguration.java
 */
