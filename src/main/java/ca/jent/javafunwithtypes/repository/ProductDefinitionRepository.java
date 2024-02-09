package ca.jent.javafunwithtypes.repository;

import java.util.UUID;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductDefinitionRepository extends ReactiveCrudRepository<ProductDefinition, UUID> {
    Mono<ProductDefinition> findBySku(Sku sku);
}
