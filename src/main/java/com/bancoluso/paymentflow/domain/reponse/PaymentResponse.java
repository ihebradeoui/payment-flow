package com.bancoluso.paymentflow.domain.reponse;

import lombok.Data;

@Data
public class PaymentResponse {

    private String referenceId;

    private Double amount;

    private String currency;

    private String debtorName;

    private String debtorIban;

    private String creditorIban;

    private String valueDate;

    private String status;

    private String eventTimestamp;
}
