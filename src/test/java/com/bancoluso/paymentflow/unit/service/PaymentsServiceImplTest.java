package com.bancoluso.paymentflow.unit.service;

import com.bancoluso.paymentflow.domain.PaymentStatus;
import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.mapper.PaymentMapper;
import com.bancoluso.paymentflow.repository.PaymentsRepository;
import com.bancoluso.paymentflow.service.impl.PaymentsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsServiceImplTest {

    @Mock
    private PaymentMapper paymentsMapper;

    @Mock
    private PaymentsRepository paymentsRepository;

    @InjectMocks
    private PaymentsServiceImpl paymentsService;

    private final static Instant NOW = Instant.now();

    @Test
    void ingestSettledPaymentEvent() {
        PaymentEntity existingPaymentEntity = getSettledPaymentEntity();
        Payment existingPayment = getPaymentFromEntity(existingPaymentEntity);
        Payment paymentEventRequest = preparePayment();
        when(paymentsMapper.toPaymentModel(any(PaymentEntity.class))).thenReturn(existingPayment);
        when(paymentsMapper.toPaymentEntity(any(Payment.class))).thenReturn(existingPaymentEntity);
        when(paymentsRepository.getPaymentByReferenceId(anyString())).thenReturn(existingPaymentEntity);
        Payment result = paymentsService.ingestPaymentEvent(paymentEventRequest);
        verify(paymentsRepository, never()).save(any());
        assertEquals(existingPayment.getStatus(), result.getStatus());
    }

    private static Payment getPaymentFromEntity(PaymentEntity existingPaymentEntity) {
        Payment existingPayment = new Payment();
        existingPayment.setReferenceId(existingPaymentEntity.getReferenceId());
        existingPayment.setStatus(existingPaymentEntity.getStatus());
        existingPayment.setEventTimestamp(existingPaymentEntity.getEventTimestamp());
        return existingPayment;
    }

    private static PaymentEntity getSettledPaymentEntity() {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setReferenceId("ref123");
        paymentEntity.setStatus(PaymentStatus.SETTLED);
        paymentEntity.setEventTimestamp(NOW);
        return paymentEntity;
    }

    private Payment preparePayment() {
        Payment paymentEventRequest = new Payment();
        paymentEventRequest.setReferenceId("ref123");
        paymentEventRequest.setStatus(PaymentStatus.PROCESSING);
        paymentEventRequest.setEventTimestamp(NOW.plusSeconds(10));
        return paymentEventRequest;
    }
}