package com.bancoluso.paymentflow.mapper;

import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.domain.reponse.PaymentResponse;
import com.bancoluso.paymentflow.domain.request.PaymentEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPaymentModel(PaymentEntity paymentEntity);
    @Mapping(target = "id", ignore = true)
    Payment toPaymentModel(PaymentEventRequest request);
    PaymentEntity toPaymentEntity(Payment model);
    PaymentResponse toPaymentResponse(Payment payment);
}
