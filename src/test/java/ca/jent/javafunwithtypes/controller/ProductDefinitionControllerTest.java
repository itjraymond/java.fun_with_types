package ca.jent.javafunwithtypes.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import ca.jent.javafunwithtypes.repository.ProductDefinitionRepository;
import ca.jent.javafunwithtypes.types.ProductDefinition;
import ca.jent.javafunwithtypes.types.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
@ActiveProfiles("tctest") // see pom.xml with profile tctest (for M1)
class ProductDefinitionControllerTest {

    @Autowired
    ProductDefinitionRepository productDefinitionRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void setUp(){
        productDefinitionRepository.deleteAll().block();
    }


    @Test
    void test_e2e_product_definition() throws IOException, InterruptedException {

        // Make sure the database is empty
        StepVerifier.create(productDefinitionRepository.count()).expectSubscription().expectNextMatches(c -> c == 0).verifyComplete();

        File file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000000.json");
        String payload = new String(Files.readAllBytes(file.toPath()));

        Flux<ProductDefinition> responseBody = webTestClient.post()
                .uri("/api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDefinition.class).getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(pd ->
                        pd.name().equals("Product name") && pd.sku().value().equals("10000000"))
                .verifyComplete();

        StepVerifier.create(productDefinitionRepository.count()).expectSubscription().expectNextMatches(c -> c == 1).verifyComplete();

        StepVerifier.create(productDefinitionRepository.findBySku(Sku.of("10000000")))
                .assertNext(pd -> pd.name().equals("Product name"))
                .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/product-definition/sku/10000000")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(respPayload -> {
                    assertNotNull(respPayload.getResponseBody().id());
                    assertEquals(Sku.of("10000000"), respPayload.getResponseBody().sku());
                    assertEquals("Product name", respPayload.getResponseBody().name());
                });
    }

    @Test
    void test_e2e_many_product_definition() throws IOException, InterruptedException {

        // Make sure the database is empty
        StepVerifier.create(productDefinitionRepository.count()).expectSubscription().expectNextMatches(c -> c == 0).verifyComplete();

        File file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000000.json");
        String payload = new String(Files.readAllBytes(file.toPath()));

        Flux<ProductDefinition> responseBody = webTestClient.post()
                .uri("/api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDefinition.class).getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(pd ->
                        pd.name().equals("Product name") && pd.sku().value().equals("10000000"))
                .verifyComplete();

        StepVerifier.create(productDefinitionRepository.count()).expectSubscription().expectNextMatches(c -> c == 1).verifyComplete();

        file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000001.json");
        payload = new String(Files.readAllBytes(file.toPath()));

        webTestClient.post()
                .uri("/api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(p -> {
                    assertNotNull(p.getResponseBody().id());
                    assertEquals("10000001", p.getResponseBody().sku().value());
                    assertEquals("PS5", p.getResponseBody().name());
                });

        file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000002.json");
        payload = new String(Files.readAllBytes(file.toPath()));

        webTestClient.post()
                .uri("/api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(p -> {
                    assertNotNull(p.getResponseBody().id());
                    assertEquals("10000002", p.getResponseBody().sku().value());
                    assertEquals("XBox350", p.getResponseBody().name());
                });

        file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000003.json");
        payload = new String(Files.readAllBytes(file.toPath()));

        webTestClient.post()
                .uri("/api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(p -> {
                    assertNotNull(p.getResponseBody().id());
                    assertEquals("10000003", p.getResponseBody().sku().value());
                    assertEquals("TV", p.getResponseBody().name());
                });


        StepVerifier.create(productDefinitionRepository.count()).expectSubscription().expectNextMatches(c -> c == 4).verifyComplete();


        StepVerifier.create(productDefinitionRepository.findBySku(Sku.of("10000000")))
                .assertNext(pd -> pd.name().equals("Product name"))
                .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/product-definition/sku/10000000")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(respPayload -> {
                    assertNotNull(respPayload.getResponseBody().id());
                    assertEquals(Sku.of("10000000"), respPayload.getResponseBody().sku());
                    assertEquals("Product name", respPayload.getResponseBody().name());
                });

        webTestClient.get()
                .uri("/api/v1/product-definition/sku/10000001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(respPayload -> {
                    assertNotNull(respPayload.getResponseBody().id());
                    assertEquals(Sku.of("10000001"), respPayload.getResponseBody().sku());
                    assertEquals("PS5", respPayload.getResponseBody().name());
                });

        webTestClient.get()
                .uri("/api/v1/product-definition/sku/10000003")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(respPayload -> {
                    assertNotNull(respPayload.getResponseBody().id());
                    assertEquals(Sku.of("10000003"), respPayload.getResponseBody().sku());
                    assertEquals("TV", respPayload.getResponseBody().name());
                });
    }


    @Test
    void test_get_product_definition_by_uuid_not_found() {
        // verify nothing in db
        StepVerifier.create(productDefinitionRepository.count())
                .expectSubscription()
                .expectNextMatches(c -> c == 0)
                .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/product-definition/5b106fd1-c059-42d1-aab8-96a1202d92df")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void test_get_product_definition_by_sku_not_found() {
        // verify nothing in db
        StepVerifier.create(productDefinitionRepository.count())
                .expectSubscription()
                .expectNextMatches(c -> c == 0)
                .verifyComplete();

        webTestClient.get()
                .uri("/api/v1/product-definition/sku/10000000")
                .exchange()
                .expectStatus().isNotFound();

    }

}