package ca.jent.javafunwithtypes.types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import ca.jent.javafunwithtypes.config.SerializerDeserializerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.test.context.ContextConfiguration;

@JsonTest
@ContextConfiguration(classes = {SerializerDeserializerConfiguration.class})
class SkuTest {

    @Autowired
    JacksonTester<Sku> jacksonTester;

    @Test
    void test_value_object_equality() {
        Sku s1 = Sku.of("10000000");
        Sku s2 = Sku.of("10000000");

        assertEquals(s1,s2);
    }

    @Test
    void test_invalid_sku_values() {
        assertThrows(IllegalArgumentException.class, () -> Sku.of(""));
        assertThrows(IllegalArgumentException.class, () -> Sku.of("1234567"));
        assertThrows(IllegalArgumentException.class, () -> Sku.of("01234567"));
        assertThrows(NullPointerException.class, () -> Sku.of(null));
    }

    @Test
    void test_serialization() throws IOException {

        String expected = "\"10000000\"";
        Sku sku = Sku.of("10000000");

        JsonContent<Sku> jsonSku = jacksonTester.write(sku);

        assertEquals(expected, jsonSku.getJson());
    }

    @Test
    void test_deserialization() throws IOException {

        Sku expectedSku = Sku.of("10000000");
        String json = "\"10000000\"";

        ObjectContent<Sku> sku = jacksonTester.parse(json);

        assertEquals(expectedSku, sku.getObject());
    }
}