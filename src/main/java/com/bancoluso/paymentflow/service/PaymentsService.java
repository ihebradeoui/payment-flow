package com.bancoluso.paymentflow.service;

import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.domain.model.PaymentsPage;

public interface PaymentsService {
    Payment ingestPaymentEvent(Payment request);
    Payment getPaymentByReferenceId(String referenceId);
    PaymentsPage getPayments(int page, int size);
}
