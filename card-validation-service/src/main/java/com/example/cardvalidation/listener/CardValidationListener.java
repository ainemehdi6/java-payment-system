package com.example.cardvalidation.listener;

import com.example.cardvalidation.dto.PaymentRequest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CardValidationListener {

    private final JmsTemplate jmsTemplate;

    public CardValidationListener(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "payment.request")
    public void onPaymentRequest(PaymentRequest request) {
        if (isValidCard(request)) {
            jmsTemplate.convertAndSend("card.validated", request);
            System.out.println("✅ Carte validée pour le paiement : " + request.getPaymentId());
        } else {
            jmsTemplate.convertAndSend("payment.failed", request);
            System.out.println("❌ Carte invalide : " + request.getPaymentId());
        }
    }

    private boolean isValidCard(PaymentRequest request) {
        if (request.getCardNumber() == null || request.getExpiryDate() == null) return false;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            LocalDate expiry = LocalDate.parse("01/" + request.getExpiryDate(), DateTimeFormatter.ofPattern("dd/MM/yy"));
            return expiry.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
