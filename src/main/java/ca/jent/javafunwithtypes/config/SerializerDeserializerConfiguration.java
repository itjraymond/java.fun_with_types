package ca.jent.javafunwithtypes.config;

import java.util.Map;
import ca.jent.javafunwithtypes.types.Sku;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class SerializerDeserializerConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializersByType(Map.of(
                        Sku.class, new Sku.SkuSerializer()
                ))
                .deserializersByType(Map.of(
                       Sku.class, new Sku.SkuDeserializer()
                ))
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}
