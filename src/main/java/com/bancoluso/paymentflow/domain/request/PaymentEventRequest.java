package com.bancoluso.paymentflow.domain.request;

import com.bancoluso.paymentflow.domain.PaymentStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class PaymentEventRequest {

    private String referenceId;

    private Double amount;

    private String currency;

    private String debtorName;

    private String debtorIban;

    private String creditorIban;

    private LocalDate valueDate;

    private PaymentStatus status;

    private Instant eventTimestamp;

}
