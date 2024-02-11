package ca.jent.javafunwithtypes.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;
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
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(300000))
                .build();
    }


    @Test
    void test_e2e_product_definition() throws IOException, InterruptedException {

        StepVerifier.create(productDefinitionRepository.count()).expectNextCount(0L).expectComplete();

        File file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000000.json");
        String payload = new String(Files.readAllBytes(file.toPath()));
        webTestClient.post()
                .uri("/api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDefinition.class)
                .consumeWith(resPayload -> {
                    assertNotNull(resPayload.getResponseBody().id());
                    assertEquals(Sku.of("10000000"), resPayload.getResponseBody().sku());
                    assertEquals("Product name", resPayload.getResponseBody().name());
                });

        StepVerifier.create(productDefinitionRepository.count()).expectNextCount(1L).expectComplete();

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
    void test_get_product_definition_by_uuid() {
        webTestClient.get()
                .uri("/api/v1/product-definition/5b106fd1-c059-42d1-aab8-96a1202d92df")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void test_get_product_definition_by_sku() {
        webTestClient.get()
                .uri("/api/v1/product-definition/sku/10000000")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk();
    }

}