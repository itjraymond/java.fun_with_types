package ca.jent.javafunwithtypes.repository;

import ca.jent.javafunwithtypes.types.Brand;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProductDefinitionRepository extends ReactiveCrudRepository<ProductDefinition, UUID> {
    @Query("SELECT product_definition.id, product_definition.sku, product_definition.brand, product_definition.name, product_definition.category, product_definition.weight_in_gram, product_definition.original_unit_of_weight FROM product_definition WHERE product_definition.sku = :sku")
    Mono<ProductDefinition> findBySku(Sku sku);
    Flux<ProductDefinition> findByBrand(Brand brand);
}
