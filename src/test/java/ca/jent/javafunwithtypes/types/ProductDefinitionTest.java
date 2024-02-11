package ca.jent.javafunwithtypes.types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.UUID;
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
class ProductDefinitionTest {

    @Autowired
    JacksonTester<ProductDefinition> jacksonTester;

    @Test
    void test_serialization() throws IOException {
        String expected =  "{\n" +
                  "  \"id\" : \"5b106fd1-c059-42d1-aab8-96a1202d92df\",\n" +
                  "  \"sku\" : \"10000000\",\n" +
                  "  \"name\" : \"product name\"\n" +
                  "}";
        ProductDefinition pd = ProductDefinition.of(
                UUID.fromString("5b106fd1-c059-42d1-aab8-96a1202d92df"),
                Sku.of("10000000"),
                "product name"
        );

        JsonContent<ProductDefinition> jsonPd = jacksonTester.write(pd);

        assertEquals(expected, jsonPd.getJson());
    }

    @Test
    void test_deserialization() throws IOException {
        String jsonPd = "{\n" +
                        "  \"id\" : \"5b106fd1-c059-42d1-aab8-96a1202d92df\",\n" +
                        "  \"sku\" : \"10000000\",\n" +
                        "  \"name\" : \"product name\"\n" +
                        "}";

        ProductDefinition expectedPd = ProductDefinition.of(
                UUID.fromString("5b106fd1-c059-42d1-aab8-96a1202d92df"),
                Sku.of("10000000"),
                "product name"
        );

        ObjectContent<ProductDefinition> pd = jacksonTester.parse(jsonPd);

        assertEquals(expectedPd.id(), pd.getObject().id());
        assertEquals(expectedPd.sku(), pd.getObject().sku());
        assertEquals(expectedPd.name(), pd.getObject().name());
    }
}