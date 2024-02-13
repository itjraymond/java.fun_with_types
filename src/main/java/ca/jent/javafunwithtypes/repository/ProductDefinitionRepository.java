package ca.jent.javafunwithtypes.repository;

import ca.jent.javafunwithtypes.types.Brand;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProductDefinitionRepository extends ReactiveCrudRepository<ProductDefinition, UUID> {
    Mono<ProductDefinition> findBySku(Sku sku);
    Flux<ProductDefinition> findByBrand(Brand brand);
}
