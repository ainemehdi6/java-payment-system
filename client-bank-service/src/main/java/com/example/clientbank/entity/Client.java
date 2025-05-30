package com.example.clientbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Client {

    @Id
    private String id;

    private String name;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    private BigDecimal balance;

    public Client() {
    }

    public Client(String id, String name, String cardNumber, LocalDate expiryDate, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Client{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", cardNumber='" + cardNumber + '\'' +
               ", expiryDate=" + expiryDate +
               ", balance=" + balance +
               '}';
    }
}
