package ca.jent.javafunwithtypes.config;


import java.util.List;
import ca.jent.javafunwithtypes.types.ProductDefinition.ProductDefinitionWriteConverter;
import ca.jent.javafunwithtypes.types.ProductDefinition.ProductDefinitionReadConverter;
import ca.jent.javafunwithtypes.types.Sku;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;

@Configuration
public class ConertersConfiguration {

    @Bean
    public R2dbcCustomConversions conversions(ConnectionFactory connectionFactory, ObjectMapper mapper) {
        var dialect = DialectResolver.getDialect(connectionFactory);
        var converters = List.of(
                new ProductDefinitionReadConverter(),
                new ProductDefinitionWriteConverter(),
                new Sku.SkuReadConverter(),
                new Sku.SkuWriteConverter()
        );
        return R2dbcCustomConversions.of(dialect, converters);
    }
}
