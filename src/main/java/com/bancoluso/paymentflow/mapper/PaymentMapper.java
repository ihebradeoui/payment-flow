package com.bancoluso.paymentflow.mapper;

import com.bancoluso.paymentflow.domain.entity.PaymentEntity;
import com.bancoluso.paymentflow.domain.model.Payment;
import com.bancoluso.paymentflow.domain.model.PaymentsPage;
import com.bancoluso.paymentflow.domain.reponse.PaymentResponse;
import com.bancoluso.paymentflow.domain.reponse.PaymentStatusResponse;
import com.bancoluso.paymentflow.domain.reponse.PaymentsPageResponse;
import com.bancoluso.paymentflow.domain.request.PaymentEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPaymentModel(PaymentEntity paymentEntity);

    @Mapping(target = "id", ignore = true)
    Payment toPaymentModel(PaymentEventRequest request);

    PaymentEntity toPaymentEntity(Payment model);

    PaymentResponse toPaymentResponse(Payment payment);

    PaymentStatusResponse toPaymentStatusResponse(Payment payment);

    @Mapping(target = "page", expression = "java(page.getNumber() + 1)")
    @Mapping(source = "content", target = "items", defaultExpression = "java(java.util.Collections.emptyList())")
    PaymentsPage pageToPaymentsPage(Page<Payment> page);

    PaymentsPageResponse toPaymentsPageResponse(PaymentsPage paymentsPage);

}
