# Expense Tracker — Backend REST API

Production-ready REST API backend for personal finance and expense tracking built with **Java 17/25**, **Spring Boot 3.2.4**, **Spring Security 6 (JWT)**, **Spring Data JPA**, and **MySQL 8.0**.

---

## 🚀 Technology Stack
* **Language**: Java 17 / 21 / 25
* **Framework**: Spring Boot 3.2.4
* **Security**: Spring Security 6 (Stateless JWT token authentication, BCrypt password hashing)
* **ORM & Database**: Hibernate 6.x, Spring Data JPA, MySQL 8.0 (with automated embedded fallback)
* **Build Tool**: Apache Maven 3.8+
* **Validation**: Spring Boot Validation (Jakarta Bean Validation)

---

## 🌟 Architecture & Features
* **Authentication**: Registration and Login endpoints with JWT Bearer Token generation
* **Multi-tenant Data Isolation**: Strict user-ownership scoping for all financial records
* **Expense Management**: Complete CRUD operations, category breakdown, date filtering, and pagination
* **Income Management**: Multi-category earnings tracking, source logging, and date filtering
* **Monthly Budgets**: Category spend limits with automated utilization and alert tracking
* **Analytics & Reports**: Monthly trend summaries, category-wise spending distributions, and CSV report export
* **Global Exception Handling**: Centralized `@RestControllerAdvice` returning structured HTTP responses

---

## 🗄️ Database Setup (MySQL 8.0)
1. Start MySQL (or XAMPP MySQL):
   ```cmd
   net start MySQL80
   ```
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS expense_tracker;
   ```
3. The SQL schema DDL script is located at:
   `src/main/resources/schema.sql`

---

## 🛠️ Running the Application

### 1. Configure Environment (Optional)
If custom credentials are required:
```powershell
$env:DB_USER = "root"
$env:DB_PASS = "your_mysql_password"
$env:JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
```

### 2. Build and Run with Maven
```bash
mvn spring-boot:run
```

API server will be available at `http://localhost:8080`.
