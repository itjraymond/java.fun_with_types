package ca.jent.javafunwithtypes.service;

import java.util.UUID;
import ca.jent.javafunwithtypes.repository.ProductDefinitionRepository;
import ca.jent.javafunwithtypes.types.Brand;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductDefinitionService {

    private final ProductDefinitionRepository repository;

    public Mono<ProductDefinition> save(ProductDefinition pd) {
        return repository.save(pd);
    }

    public Mono<ProductDefinition> findById(UUID uuid) {
        return repository.findById(uuid);
    }

    public Mono<ProductDefinition> findBySku(Sku sku) {
        return repository.findBySku(sku);
    }

    public Flux<ProductDefinition> findByBrand(Brand brand) {
        return repository.findByBrand(brand);
    }

}
