package com.bancoluso.paymentflow.domain.entity;

import com.bancoluso.paymentflow.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "debtor_name")
    private String debtorName;

    @Column(name = "debtor_iban")
    private String debtorIban;

    @Column(name = "creditor_iban")
    private String creditorIban;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "event_timestamp")
    private Instant eventTimestamp;

}
