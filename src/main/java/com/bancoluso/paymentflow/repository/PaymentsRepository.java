package com.bancoluso.paymentflow.repository;

import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentsRepository extends CrudRepository<PaymentEntity, Long> {
    PaymentEntity getPaymentByReferenceId(String referenceId);
}
