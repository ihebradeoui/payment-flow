package com.bancoluso.paymentflow.integration.service;

import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import com.bancoluso.paymentflow.repository.PaymentsRepository;
import com.bancoluso.paymentflow.service.PaymentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Testcontainers
class PaymentServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("payment-flow-test")
            .withUsername("postgres")
            .withPassword("123");

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");  // given it's a fresh db
    }

    @Test
    void testCreateAndRetrievePayment() {
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(100D);
        payment.setCurrency("EUR");

        PaymentEntity saved = paymentsRepository.save(payment);

        assertThat(saved.getId()).isNotNull();
        assertThat(paymentsRepository.findById(saved.getId())).isPresent();
    }
}