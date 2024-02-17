package ca.jent.javafunwithtypes.types;

import ca.jent.javafunwithtypes.config.SerializerDeserializerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static ca.jent.javafunwithtypes.types.UnitOfWeight.*;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = {SerializerDeserializerConfiguration.class})
class WeightTest {

    @Autowired
    JacksonTester<Weight> jacksonTester;

    @Test
    void test_serializatioin() throws IOException {
        String expected = "{\n" +
                "  \"value\" : 1.33,\n" +
                "  \"unit\" : \"GRAM\"\n" +
                "}";
        Weight w = Weight.of(1.33F, GRAM);

        JsonContent<Weight> jsonWeight = jacksonTester.write(w);

        assertEquals(expected, jsonWeight.getJson());
    }

    @Test
    void test_deserialization() throws IOException {
        Weight expected = Weight.of(1.33F, GRAM);
        String jsonWeight = "{\n" +
                "  \"value\" : 1.33,\n" +
                "  \"unit\" : \"GRAM\"\n" +
                "}";

        ObjectContent<Weight> weight = jacksonTester.parse(jsonWeight);

        assertEquals(expected, weight.getObject());

    }

    @Test
    void test_comparison() {
        Weight w1 = Weight.of(1000.1F, GRAM);
        Weight w2 = Weight.of(1.0F, KILOGRAM);
        Weight w3 = Weight.of(1000100.0F, MILLIGRAM);

        assertTrue(w1.compareTo(w2) > 0);
        assertTrue( w2.compareTo(w1) < 0);
        assertEquals(0, w1.compareTo(w3));
    }

    @Test
    void test_imperial_metric_conversions() {
        Weight wg = Weight.of(1.0F, GRAM);
        Weight wlbs = Weight.of(0.0022046244F, LBS);
        Weight wOunce = Weight.of(0.0352739907F, OUNCE);

        assertEquals(0, wg.compareTo(wlbs));
        assertEquals(0, wlbs.compareTo(wOunce));
        assertEquals(0, wg.compareTo(wOunce));
    }
}