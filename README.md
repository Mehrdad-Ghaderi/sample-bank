# 💰 Sample Bank

A backend-focused banking system built with **Spring Boot** that supports key operations like account management, multi-currency transactions, and balance tracking. Ideal for training and showcasing backend skills in Java and Spring Framework.

---

## 🚀 Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA
- **Database:** PostgreSQL (hosted on [Supabase](https://supabase.io/) ☁️)
- **Tools:** GitLab, Maven, IntelliJ
- **Tests:** JUnit
- **Deployment:** Local (can be containerized or deployed to the cloud)
- 
## Requirements
this application must be able to:
- add a new client
- support different currencies on only one account number
- update a client's phone number
- deposit, withdraw, and transfer money
- show the balance of an account in each currency
- store transaction data with the date
- show desired number of recent transactions of an account
- remove (deactivate) a client
- freeze an account
- show the balance of the bank
- abort an operation at any time of the process

## Constraints
- no client without an account
- all clients have only one account
- only USD, EURO, POUND, and CAD are supported
- removal of a member does not remove their past records

