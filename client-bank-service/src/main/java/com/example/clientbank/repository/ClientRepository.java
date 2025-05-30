package com.example.clientbank.repository;

import com.example.clientbank.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByCardNumber(String cardNumber);
}
