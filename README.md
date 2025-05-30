# 💳 Distributed Payment Microservices System

Ce projet est une application distribuée de gestion de paiements développée en Java avec Spring Boot. Il repose sur une architecture orientée microservices et utilise ActiveMQ pour la communication asynchrone via JMS.

---

## 🎯 Objectif pédagogique

Comprendre et implémenter une architecture microservices en utilisant ActiveMQ comme système de messagerie pour coordonner les différents services dans un scénario de paiement en ligne.

Le rendu attendu :

- Un rapport de projet : justifications, captures d’écran, schéma d’architecture.  
- Un fichier ZIP avec l’ensemble du code et configuration.  
- Une présentation orale avec démo et questions individuelles.

---

## 📦 Microservices inclus & Flux général

Chaque service communique via une queue ou un topic ActiveMQ. Voici le cheminement avec les queues/topics suggérés :

| Étape | Micro-service          | Description                              | File ActiveMQ                   |
|-------|-----------------------|----------------------------------------|--------------------------------|
| 1     | `gateway-service`      | Reçoit les paiements                    | `payment.request`              |
| 2     | `card-validation-service` | Vérifie la validité de la carte       | `card.validated` / `payment.failed` |
| 3     | `client-bank-service`  | Vérifie que le client a les fonds      | `funds.validated` / `payment.failed`  |
| 4     | `merchant-bank-service`| Effectue le transfert                   | `payment.processed`            |
| 5     | `notification-service` | Informe client et boutique              | `payment.processed` / `payment.failed`|

---

## 🔧 Détails techniques par microservice

### 1. `gateway-service`

- Envoie le message JSON dans `payment.request` via ActiveMQ.  
- Exemple de payload JSON :  
```json
{
  "paymentId": "abc123",
  "amount": 100.50,
  "currency": "EUR",
  "cardNumber": "1234-5678-9012-3456",
  "expiryDate": "12/26",
  "clientId": "cli001",
  "merchantId": "mer001"
}
```

### 2. `card-validation-service`

- Consomme les messages de `payment.request`.  
- Vérifie la carte (format, date d’expiration).  
- Si valide, publie dans `card.validated`.  
- Sinon, publie dans `payment.failed`.

### 3. `client-bank-service`

- Consomme `card.validated`.  
- Vérifie dans la base SQL que le client a assez de fonds.  
- Si ok, publie dans `funds.validated`.  
- Sinon, publie dans `payment.failed`.

### 4. `merchant-bank-service`

- Consomme `funds.validated`.  
- Effectue débit/crédit en base de données.  
- Publie `payment.processed`.

### 5. `notification-service`

- Consomme `payment.processed` et `payment.failed`.  
- Affiche notification dans un log ou envoie un message fictif.

---

## 🗃️ Base de données SQL

Services concernés : `client-bank-service` et `merchant-bank-service`.

Structure de la base de données (MySQL) :

### Table `clients`

| Champ       | Type    | Description                      |
|-------------|---------|--------------------------------|
| id          | VARCHAR | Identifiant client (ex : cli001)|
| name        | VARCHAR | Nom du client                   |
| card_number | VARCHAR | Numéro de carte                 |
| expiry_date | DATE    | Date d’expiration               |
| balance     | DECIMAL | Solde disponible                |

### Table `merchants`

| Champ   | Type    | Description                      |
|---------|---------|--------------------------------|
| id      | VARCHAR | Identifiant marchand (ex : mer001) |
| name    | VARCHAR | Nom de la boutique              |
| balance | DECIMAL | Solde du marchand               |

---

## 🔧 Technologies utilisées

- Java 17  
- Spring Boot 3.x  
- Spring Web  
- Spring JMS  
- Apache ActiveMQ  
- Maven  
- Lombok  
- MySQL

---

## 🛠️ Installation & Lancement

### 1. Prérequis

- Java 17+  
- Maven 3.x  
- Docker (pour ActiveMQ et Mysql)  

Lancer ActiveMQ et MYSQL avec Docker :  
```bash
docker run -d --name activemq -p 61616:61616 -p 8161:8161 rmohr/activemq
```

Interface ActiveMQ Web : [http://localhost:8161](http://localhost:8161)  
Identifiants par défaut : `admin` / `admin`

sur la racine du projet lancer la cmd
```bash
docker compose up -d
```

### 2. Démarrer les services

Chaque microservice est indépendant et peut être lancé avec :  
```bash
cd <nom-du-service>
mvn spring-boot:run
```

Par exemple :  
```bash
cd gateway-service
mvn spring-boot:run
```

Répéter pour :  
- `payment-service`  
- `notification-service`  
- `card-validation-service`  
- `client-bank-service`  
- `merchant-bank-service`

---

## 📨 Exemple de requête

Envoie une requête `POST` vers le `gateway-service` :  
```bash
curl -X POST http://localhost:8089/api/payments   -H "Content-Type: application/json"   -d '{
    "paymentId": "abc123",
    "amount": 100.5,
    "currency": "EUR",
    "cardNumber": "1234-5678-9012-3456",
    "expiryDate": "12/26",
    "clientId": "cli001",
    "merchantId": "mer001"
}'
```

Réponse attendue :  
```json
{
  "status": 200,
  "message": "✅ Payment sent!"
}
```

---

## 🔁 Architecture de communication JMS

- `gateway-service` publie un message dans la file `payment.request`.  
- `card-validation-service` consomme `payment.request` et publie dans `card.validated` ou `payment.failed`.  
- `client-bank-service` consomme `card.validated` et publie dans `funds.validated` ou `payment.failed`.  
- `merchant-bank-service` consomme `funds.validated` et publie `payment.processed`.  
- `notification-service` consomme `payment.processed` et `payment.failed` pour notifier.

---

## 📂 Arborescence typique

```bash
├── gateway-service/
│   ├── src/main/java/com/example/gateway/controller/PaymentController.java
│   └── src/main/java/com/example/gateway/model/PaymentRequest.java
├── card-validation-service/
│   └── src/main/java/com/example/cardvalidation/listener/CardValidationListener.java
├── client-bank-service/
│   └── src/main/java/com/example/clientbank/listener/ClientBankListener.java
├── merchant-bank-service/
│   └── src/main/java/com/example/merchantbank/listener/MerchantBankListener.java
├── notification-service/
│   └── src/main/java/com/example/notification/listener/NotificationListener.java
├── docker-compose.yml (optionnel)
└── README.md
```

---

## 🧪 Améliorations futures

- Ajouter Swagger/OpenAPI  
- Authentification via OAuth2 / Keycloak  
- Centralisation des logs avec ELK ou Grafana  
- Déploiement sur Docker Compose ou Kubernetes  
- Tests unitaires & d’intégration  

---

## 👨‍💻 Auteur

Développé par **El Mehdi**
Développeur backend
