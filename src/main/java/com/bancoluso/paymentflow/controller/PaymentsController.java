package com.bancoluso.paymentflow.controller;

import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.domain.reponse.PaymentResponse;
import com.bancoluso.paymentflow.domain.request.PaymentEventRequest;
import com.bancoluso.paymentflow.mapper.PaymentMapper;
import com.bancoluso.paymentflow.service.PaymentsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
    private final PaymentsService paymentsService;
    private final PaymentMapper paymentMapper;

    public PaymentsController(PaymentsService paymentsService, PaymentMapper paymentMapper) {
        this.paymentsService = paymentsService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/events")
    public PaymentResponse ingestPaymentEvent(@RequestBody PaymentEventRequest request) {
        Payment ingestionResult = paymentsService.ingestPaymentEvent(paymentMapper.toPaymentModel(request));
        return paymentMapper.toPaymentResponse(ingestionResult);
    }
}
