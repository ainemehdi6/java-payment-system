package com.example.clientbank.listener;

import com.example.clientbank.dto.PaymentRequest;
import com.example.clientbank.entity.Client;
import com.example.clientbank.repository.ClientRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class FundsVerificationListener {

    private final JmsTemplate jmsTemplate;
    private final ClientRepository clientRepository;

    public FundsVerificationListener(JmsTemplate jmsTemplate, ClientRepository clientRepository) {
        this.jmsTemplate = jmsTemplate;
        this.clientRepository = clientRepository;
    }

    @JmsListener(destination = "card.validated")
    public void handleValidatedCard(PaymentRequest request) {
        Optional<Client> clientOpt = clientRepository.findByCardNumber(request.getCardNumber());

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (client.getBalance().compareTo(BigDecimal.valueOf(request.getAmount())) >= 0) {
                jmsTemplate.convertAndSend("funds.validated", request);
                System.out.println("✅ Fonds validés pour : " + request.getPaymentId());
            } else {
                jmsTemplate.convertAndSend("payment.failed", request);
                System.out.println("❌ Fonds insuffisants pour : " + request.getPaymentId());
            }
        } else {
            jmsTemplate.convertAndSend("payment.failed", request);
            System.out.println("❌ Client non trouvé pour : " + request.getPaymentId());
        }
    }
}
