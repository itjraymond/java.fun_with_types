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

import static ca.jent.javafunwithtypes.types.UnitOfWeight.*;

@Table(name = "product_definition")
public record ProductDefinition(
        @Id UUID id,
        Sku sku,
        Brand brand,
        String name,
        Category category,
        Weight weight
) {

    public static ProductDefinition of(UUID id, Sku sku, Brand brand, String name, Category category, Weight weight) {
        return new ProductDefinition(id, sku, brand, name, category, weight);
    }

    @ReadingConverter
    public static class ProductDefinitionReadConverter implements Converter<Row, ProductDefinition> {

        @Override
        public ProductDefinition convert(Row row) {
            return ProductDefinition.of(
                    row.get("id", UUID.class),
                    Sku.of(row.get("sku", String.class)),
                    Brand.of(row.get("brand", String.class)),
                    row.get("name", String.class),
                    Category.valueOf(row.get("category", String.class)),
                    constructWeight(row.get("weight_in_gram", Float.class), row.get("original_unit_of_weight", String.class))
            );
        }

        private Weight constructWeight(Float weightInGram, String originalUnitOfWeight) {
            UnitOfWeight originalUnit = valueOf(originalUnitOfWeight);
            return switch (originalUnit) {
                case MILLIGRAM -> Weight.of(weightInGram * 1000.0F, originalUnit);
                case GRAM -> Weight.of(weightInGram, originalUnit);
                case KILOGRAM -> Weight.of(weightInGram * 0.001F, originalUnit);
                case LBS -> Weight.of(weightInGram * ONE_GRAM_IN_LBS, originalUnit);
                case OUNCE -> Weight.of(weightInGram * ONE_GRAM_IN_OUNCE, originalUnit);
            };
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
            row.put("brand", Parameter.from(pd.brand.getValue()));
            row.put("name", Parameter.from(pd.name));
            row.put("category", Parameter.from(pd.category().name()));
            row.put("weight_in_gram", Parameter.from(pd.weight.getStandarizedWeight().value()));
            row.put("original_unit_of_weight", Parameter.from(pd.weight.unit().name()));
            return row;
        }
    }
}

/**
 * We don't need any custom serializer/deserializer since Jackson is able to
 * handle UUID, Sku (see our custom serializer/deserializer for Sku) and String.
 */
