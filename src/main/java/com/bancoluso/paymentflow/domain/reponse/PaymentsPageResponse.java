package com.bancoluso.paymentflow.domain.reponse;

import lombok.Data;

import java.util.List;

@Data
public class PaymentsPageResponse {
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;
    private List<PaymentResponse> items;
}
