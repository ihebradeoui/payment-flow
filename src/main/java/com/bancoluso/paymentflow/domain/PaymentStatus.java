package com.bancoluso.paymentflow.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    PENDING(0),
    PROCESSING(1),
    SETTLED(2),
    REJECTED(2);

    private final int order;

}
