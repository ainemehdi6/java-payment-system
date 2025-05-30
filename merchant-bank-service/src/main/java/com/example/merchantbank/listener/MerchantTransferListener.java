package com.example.merchantbank.listener;

import com.example.shared.dto.PaymentRequest;
import com.example.merchantbank.entity.Merchant;
import com.example.merchantbank.repository.MerchantRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class MerchantTransferListener {

    private final JmsTemplate jmsTemplate;
    private final MerchantRepository merchantRepository;

    public MerchantTransferListener(JmsTemplate jmsTemplate, MerchantRepository merchantRepository) {
        this.jmsTemplate = jmsTemplate;
        this.merchantRepository = merchantRepository;
    }

    @JmsListener(destination = "funds.validated")
    public void handleTransfer(PaymentRequest request) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(request.getMerchantId());

        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            BigDecimal updatedBalance = merchant.getBalance().add(BigDecimal.valueOf(request.getAmount()));
            merchant.setBalance(updatedBalance);
            merchantRepository.save(merchant);

            jmsTemplate.convertAndSend("payment.processed", request);
            System.out.println("✅ Paiement transféré au marchand : " + request.getPaymentId());
        } else {
            jmsTemplate.convertAndSend("payment.failed", request);
            System.out.println("❌ Marchand introuvable pour le paiement : " + request.getPaymentId());
        }
    }
}
