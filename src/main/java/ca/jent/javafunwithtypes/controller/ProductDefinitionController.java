package ca.jent.javafunwithtypes.controller;

import java.util.UUID;
import ca.jent.javafunwithtypes.service.ProductDefinitionService;
import ca.jent.javafunwithtypes.types.Brand;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/product-definition")
@RequiredArgsConstructor
public class ProductDefinitionController {

    private final ProductDefinitionService service;

    @GetMapping("/{uuid}")
    public Mono<ProductDefinition> findById(@PathVariable UUID uuid) {
        return service.findById(uuid)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/sku/{sku}")
    public Mono<ProductDefinition> findBySku(@PathVariable Sku sku) {
        return service.findBySku(sku)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/brand/{brand}")
    public Flux<ProductDefinition> findByBrand(@PathVariable Brand brand) {
        return service.findByBrand(brand)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    public Mono<ProductDefinition> save(@RequestBody ProductDefinition productDefinition) {
        Mono<ProductDefinition> saved = service.save(productDefinition);
        saved.log();
        return saved;
    }
}
