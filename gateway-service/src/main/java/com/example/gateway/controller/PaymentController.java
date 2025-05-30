package com.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.gateway.dto.PaymentRequest;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final JmsTemplate jmsTemplate;

    @PostMapping
    public ResponseEntity<String> sendPayment(@RequestBody PaymentRequest paymentRequest) {
        jmsTemplate.convertAndSend("payment.request", paymentRequest);
        return ResponseEntity.ok("✅ Payment sent!");
    }
}
