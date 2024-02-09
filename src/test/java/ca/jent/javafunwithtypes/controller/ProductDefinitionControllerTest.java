package ca.jent.javafunwithtypes.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import ca.jent.javafunwithtypes.service.ProductDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("tctest")
class ProductDefinitionControllerTest {

    @Autowired
    ProductDefinitionService productDefinitionService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void test_post_product_definition() throws IOException {
        File file = ResourceUtils.getFile("classpath:payload/product-definition-sku-10000000.json");
        String payload = new String(Files.readAllBytes(file.toPath()));
        webTestClient.post()
                .uri("api/v1/product-definition")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk();
    }
}