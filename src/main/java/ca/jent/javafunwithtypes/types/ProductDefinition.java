package ca.jent.javafunwithtypes.types;

import java.util.UUID;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.r2dbc.core.Parameter;

@Table(name = "product_definition")
public record ProductDefinition(
        @Id UUID id,
        Sku sku,
        String name
) {

    public static ProductDefinition of(UUID id, Sku sku, String name) {
        return new ProductDefinition(id, sku, name);
    }

    @ReadingConverter
    public static class ProductDefinitionReadConverter implements Converter<Row, ProductDefinition> {

        @Override
        public ProductDefinition convert(Row row) {
            return ProductDefinition.of(
                    row.get("id", UUID.class),
                    Sku.of(row.get("sku", String.class)),
                    row.get("name", String.class)
            );
        }
    }

    @WritingConverter
    public static class ProductDefinitionWriteConverter implements Converter<ProductDefinition, OutboundRow> {

        @Override
        public OutboundRow convert(ProductDefinition pd) {
            OutboundRow row = new OutboundRow();
            if (pd.id != null) {
                row.put("id", Parameter.from(pd.id));
            }
            row.put("sku", Parameter.from(pd.sku.value()));
            row.put("name", Parameter.from(pd.name));
            return row;
        }
    }
}

/**
 * We don't need any custom serializer/deserializer since Jackson is able to
 * handle UUID, Sku (see our custom serializer/deserializer for Sku) and String.
 */
