package com.bancoluso.paymentflow.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class PaymentsPage {
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;
    private List<Payment> items;
}
