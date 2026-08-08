package com.bancoluso.paymentflow.repository;

import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity getPaymentByReferenceId(String referenceId);

}
