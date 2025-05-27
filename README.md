# 💰 Sample Bank

A backend-focused banking system built with **Spring Boot** that supports key operations, CRUD operations, account management, multi-currency accounts, transaction history, and balance tracking.

---

## 🚀 Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA
- **Database:** PostgreSQL (hosted on [Supabase](https://supabase.io/) ☁️)
- **Tools:** GitLab, Maven, IntelliJ
- **Tests:** JUnit
- **Deployment:** Local (can be containerized or deployed to the cloud)

## 🌍 Cloud Database

The PostgreSQL database is hosted on **Supabase**, making this project ready for cloud deployments and scalable integrations. No local DB install needed — the app interacts with a cloud-native PostgreSQL database over REST.

---

## 📦 Features

- ➕ Add a new client
- 💱 Multi-currency support (USD, EUR, GBP, CAD) under one account
- 📞 Update client phone number
- 💸 Deposit, Withdraw, and Transfer money
- 🧾 View account balance per currency
- 🕓 Show latest transactions (with timestamp)
- ❌ Remove (deactivate) a client
- 🧊 Freeze an account
- 🏦 View overall bank balance
- 🔄 Abort any operation during its process

---

## 📌 Business Constraints

- Every client must have exactly **one** account
- Supported currencies: **USD, EUR, GBP, CAD**
- Removing a client does **not** delete historical records

---

## 🛠️ Setup Instructions

### Prerequisites

- Java 17+
- Maven
- Git
- (Optional) Postman or any REST client

### Clone & Run

```bash
git clone https://gitlab.com/Mehrdad-Ghaderi/sample-bank.git
cd sample-bank
./mvnw spring-boot:run