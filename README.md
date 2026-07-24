The target codebase for this course is the Capstone Bank Web Application Backend, a Java-based RESTful service built using Spring Boot, Spring Data JPA/Hibernate, and MySQL.

Key Features & Architecture
User & Role Management: Manages administrative and customer credentials with role-based access control (e.g., ADMIN, BANK_ADMIN, USER).

Customer Profile Service: Handles customer records, personal details, contact information, and account status tracking (ACTIVE, INACTIVE, PENDING).

Account Management: Provides APIs to perform operations on bank accounts, including balance updates, currency formatting, branch assignments, and linking accounts to customer profiles via primary/foreign key relationships.

Database Integration: Utilizes JPA annotations, sequence generators, and relational tables (customer, account, bank_admin) to ensure data integrity and transactional consistency.
