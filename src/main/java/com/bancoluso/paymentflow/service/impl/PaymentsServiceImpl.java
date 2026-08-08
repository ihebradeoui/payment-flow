package com.bancoluso.paymentflow.service.impl;

import com.bancoluso.paymentflow.domain.model.PaymentsPage;
import com.bancoluso.paymentflow.exception.PaymentNotFoundException;
import com.bancoluso.paymentflow.repository.PaymentsRepository;
import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.mapper.PaymentMapper;
import com.bancoluso.paymentflow.service.PaymentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final PaymentMapper paymentMapper;

    public PaymentsServiceImpl(PaymentsRepository paymentsRepository, PaymentMapper paymentMapper) {
        this.paymentsRepository = paymentsRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Payment ingestPaymentEvent(Payment request) {
        PaymentEntity paymentEntity = paymentsRepository.getPaymentByReferenceId(request.getReferenceId());
        PaymentEntity processedPaymentEntityEntity;
        if (paymentEntity == null) {
            log.info("New Payment - Payment with reference id {} does not exist. Creating new payment.", request.getReferenceId());
            processedPaymentEntityEntity = paymentsRepository.save(paymentMapper.toPaymentEntity(request));
        }
        else
            processedPaymentEntityEntity = processPaymentUpdate(paymentMapper.toPaymentModel(paymentEntity), request);
        return paymentMapper.toPaymentModel(processedPaymentEntityEntity);

    }

    @Override
    public Payment getPaymentByReferenceId(String referenceId) {
        PaymentEntity paymentEntity = paymentsRepository.getPaymentByReferenceId(referenceId);
        if (paymentEntity == null) {
            log.warn("Payment with reference id {} does not exist.", referenceId);
            throw new PaymentNotFoundException(String.format("Payment with reference id %s does not exist", referenceId));
        }
        return paymentMapper.toPaymentModel(paymentEntity);
    }

    @Override
    public PaymentsPage getPayments(int page, int size) {
        Page<Payment> payments = paymentsRepository.findAll(Pageable.ofSize(size).withPage(page-1)).map(paymentMapper::toPaymentModel);
        return paymentMapper.pageToPaymentsPage(payments);
    }

    private PaymentEntity processPaymentUpdate(Payment existingPayment, Payment newPayment) {
        if (existingPayment.getStatus().equals(newPayment.getStatus())) {
            log.info("True Duplicate - Payment with reference id {} with same status already exists. Skipping.", existingPayment.getReferenceId());
        }
        else if(existingPayment.getEventTimestamp().isAfter(newPayment.getEventTimestamp())) {
            log.info("Out of Order - Payment with reference id {} with older event timestamp already exists. Skipping.", existingPayment.getReferenceId());
        }
        else if(existingPayment.getStatus().getOrder() > newPayment.getStatus().getOrder()) {
            log.info("Out of Order - Payment with reference id {} with higher status already exists. Skipping.", existingPayment.getReferenceId());
        }
        else {
            log.info("Updating payment with reference id {} from status {} to status {}", existingPayment.getReferenceId(), existingPayment.getStatus(), newPayment.getStatus());
            existingPayment.setEventTimestamp(newPayment.getEventTimestamp());
            existingPayment.setStatus(newPayment.getStatus());
            return paymentsRepository.save(paymentMapper.toPaymentEntity(existingPayment));
        }
        return paymentMapper.toPaymentEntity(existingPayment);
    }
}
