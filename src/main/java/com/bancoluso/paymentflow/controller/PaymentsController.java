package com.bancoluso.paymentflow.controller;

import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.domain.reponse.PaymentResponse;
import com.bancoluso.paymentflow.domain.reponse.PaymentStatusResponse;
import com.bancoluso.paymentflow.domain.reponse.PaymentsPageResponse;
import com.bancoluso.paymentflow.domain.request.PaymentEventRequest;
import com.bancoluso.paymentflow.mapper.PaymentMapper;
import com.bancoluso.paymentflow.service.PaymentsService;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
    public static final String EVENTS_PATH = "/events";
    public static final String PAYMENT_STATUS_PATH = EVENTS_PATH + "/{referenceId}/status";
    private final PaymentsService paymentsService;
    private final PaymentMapper paymentMapper;

    public PaymentsController(PaymentsService paymentsService, PaymentMapper paymentMapper) {
        this.paymentsService = paymentsService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping(EVENTS_PATH)
    public PaymentResponse ingestPaymentEvent(@RequestBody PaymentEventRequest request) {
        Payment ingestionResult = paymentsService.ingestPaymentEvent(paymentMapper.toPaymentModel(request));
        return paymentMapper.toPaymentResponse(ingestionResult);
    }

    @GetMapping(PAYMENT_STATUS_PATH)
    public PaymentStatusResponse getPaymentByReferenceId(@PathVariable String referenceId) {
        Payment payment = paymentsService.getPaymentByReferenceId(referenceId);
        return paymentMapper.toPaymentStatusResponse(payment);
    }

    @GetMapping
    public PaymentsPageResponse getPayments(@RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "10") @Min(1) int size) {
        return paymentMapper.toPaymentsPageResponse(paymentsService.getPayments(page, size));
    }
}
