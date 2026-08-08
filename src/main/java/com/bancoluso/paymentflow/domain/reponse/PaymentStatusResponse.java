package com.bancoluso.paymentflow.domain.reponse;

import com.bancoluso.paymentflow.domain.PaymentStatus;
import lombok.Data;

@Data
public class PaymentStatusResponse {
    private PaymentStatus status;
}
