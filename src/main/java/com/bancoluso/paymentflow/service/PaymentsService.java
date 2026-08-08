package com.bancoluso.paymentflow.service;

import com.bancoluso.paymentflow.domain.model.Payment;

public interface PaymentsService {
    Payment ingestPaymentEvent(Payment request);
    Payment getPaymentByReferenceId(String referenceId);
}
