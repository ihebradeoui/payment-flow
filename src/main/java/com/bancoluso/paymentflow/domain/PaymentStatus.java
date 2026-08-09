package com.bancoluso.paymentflow.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    PENDING(0),
    PROCESSING(1),
    SETTLED(100),
    REJECTED(100);

    //hold the order of the status for comparison
    //both SETTLED and REJECTED have the same order, as they are both final states
    private final int order;

}
