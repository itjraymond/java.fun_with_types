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
                "  \"brand\" : \"Sony\",\n" +
                "  \"name\" : \"product name\",\n" +
                "  \"category\" : \"TVS\",\n" +
                "  \"weight\" : {\n" +
                "    \"value\" : 1.0,\n" +
                "    \"unit\" : \"GRAM\"\n" +
                "  }\n" +
                "}";
        ProductDefinition pd = ProductDefinition.of(
                UUID.fromString("5b106fd1-c059-42d1-aab8-96a1202d92df"),
                Sku.of("10000000"),
                Brand.of("sony"),
                "product name",
                Category.TVS,
                Weight.of(1.0F, UnitOfWeight.GRAM)
        );

        JsonContent<ProductDefinition> jsonPd = jacksonTester.write(pd);

        assertEquals(expected, jsonPd.getJson());
    }

    @Test
    void test_deserialization() throws IOException {
        String jsonPd = "{\n" +
                "  \"id\" : \"5b106fd1-c059-42d1-aab8-96a1202d92df\",\n" +
                "  \"sku\" : \"10000000\",\n" +
                "  \"brand\" : \"Sony\",\n" +
                "  \"name\" : \"product name\",\n" +
                "  \"category\" : \"TVS\",\n" +
                "  \"weight\" : {\n" +
                "    \"value\" : 1.0,\n" +
                "    \"unit\" : \"GRAM\"\n" +
                "  }\n" +
                "}";

        ProductDefinition expectedPd = ProductDefinition.of(
                UUID.fromString("5b106fd1-c059-42d1-aab8-96a1202d92df"),
                Sku.of("10000000"),
                Brand.of("sony"),
                "product name",
                Category.TVS,
                Weight.of(1.0F, UnitOfWeight.GRAM)
        );

        ObjectContent<ProductDefinition> pd = jacksonTester.parse(jsonPd);

        assertEquals(expectedPd.id(), pd.getObject().id());
        assertEquals(expectedPd.sku(), pd.getObject().sku());
        assertEquals(expectedPd.name(), pd.getObject().name());
        assertEquals(expectedPd.category(), pd.getObject().category());
        assertEquals(expectedPd.weight().getStandarizedWeight(), pd.getObject().weight().getStandarizedWeight());
    }
}