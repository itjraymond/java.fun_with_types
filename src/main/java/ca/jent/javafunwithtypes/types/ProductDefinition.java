package ca.jent.javafunwithtypes.types;

import java.util.UUID;

public record ProductDefinition(UUID id, Sku sku, String name) {

    public static ProductDefinition of(UUID id, Sku sku, String name) {
        return new ProductDefinition(id, sku, name);
    }
}

/**
 * We don't need any custom serializer/deserializer since Jackson is able to
 * handle UUID, Sku (see our custom serializer/deserializer for Sku) and String.
 */