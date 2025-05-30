package com.example.notification.listener;

import com.example.shared.dto.PaymentRequest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class NotificationListener {

    @PostConstruct
    public void init() {
        System.out.println("🔔 NotificationListener initialisé");
    }

    @JmsListener(destination = "payment.processed")
    public void onPaymentSuccess(PaymentRequest request) {
        System.out.println("✅ Paiement effectué avec succès pour le client " + request.getClientId() +
                " vers le marchand " + request.getMerchantId() + " (Montant: " + request.getAmount() + " " + request.getCurrency() + ")");
    }

    @JmsListener(destination = "payment.failed")
    public void onPaymentFailure(PaymentRequest request) {
        System.out.println("❌ Échec du paiement pour le client " + request.getClientId() +
                " vers le marchand " + request.getMerchantId() + " (Montant: " + request.getAmount() + " " + request.getCurrency() + ")");
    }
}
