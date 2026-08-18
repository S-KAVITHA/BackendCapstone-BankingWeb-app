Capstone project is the Online Internet Banking Web Application, a Java-based backend banking application designed to provide customers with online banking services and administrators with tools to manage and authorize banking activities. The application uses Spring Boot, Java, Spring Data JPA/Hibernate, and MySQL for the backend and database layers, with a web-based frontend supporting customer and administrator portals.

**Key Features and Functionality:**

Customer Registration and Login: Customers can register for online banking, log in, and access their banking activities.

Account Management: Customers can open savings and current accounts, view account information, and check account balances.

Banking Transactions: Customers can perform deposits and withdrawals and view their account transactions.

Funds Transfers: Customers can transfer funds between accounts and manage transfer recipients.

Cheque Book Requests: Customers can submit cheque book requests for their accounts.

Customer Profile Management: Customers can view their profile information and log out of the application.

Administrative Management: Administrators can register and log in, approve or reject customer registrations, account requests, transactions, fund transfers, and cheque book requests, as well as block users when necessary.

Database Integration: The application uses MySQL with JPA/Hibernate to persist customer, account, transaction, transfer, and cheque request information and maintain relationships between application entities.

**Architecture and Code Structure:**

The backend follows a layered Spring Boot architecture, with controllers responsible for REST endpoints, services containing business logic, repositories providing database access through Spring Data JPA, and model/entity classes representing the application's relational data. The application separates customer and administrator functionality, allowing banking operations and administrative approval workflows to be developed and maintained independently.
