package ca.jent.javafunwithtypes.service;

import java.util.UUID;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductDefinitionService {

    public Mono<ProductDefinition> save(ProductDefinition pd) {
        return Mono.just(ProductDefinition.of(
                UUID.fromString("5b106fd1-c059-42d1-aab8-96a1202d92df"),
                Sku.of("10000000"),
                "Product Name"
        ));
    }

    public Mono<ProductDefinition> findById(UUID uuid) {
        return Mono.just(ProductDefinition.of(
                uuid,
                Sku.of("10000000"),
                "Product Name"
        ));
    }

    public Mono<ProductDefinition> findBySku(Sku sku) {
        return Mono.just(ProductDefinition.of(
                UUID.fromString("5b106fd1-c059-42d1-aab8-96a1202d92df"),
                Sku.of("10000000"),
                "Product Name"
        ));
    }

}
