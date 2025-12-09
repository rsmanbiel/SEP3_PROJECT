# Project Report

## Warehouse Management System - SEP3

---

## 1. Introduction

### 1.1 Background
Warehouse management is a critical component of supply chain operations. Efficient inventory tracking, order processing, and shipment management directly impact customer satisfaction and operational costs.

### 1.2 Purpose
This project implements a distributed warehouse management system using a heterogeneous architecture with Java and C# components, demonstrating modern software engineering practices and distributed systems concepts.

### 1.3 Scope
The system provides:
- Inventory management with CRUD operations
- Order processing workflow
- Shipment tracking through a dedicated microservice
- Role-based access control
- Modern GUI for user interaction

---

## 2. Analysis

### 2.1 Requirements

#### Functional Requirements
| ID | Requirement | Priority |
|----|-------------|----------|
| FR1 | Users can login with username/password | Must |
| FR2 | System supports 4 user roles | Must |
| FR3 | Users can view product inventory | Must |
| FR4 | Authorized users can add/edit/delete products | Must |
| FR5 | Customers can place orders | Must |
| FR6 | Operators can process orders | Must |
| FR7 | System tracks shipments via microservice | Must |
| FR8 | Real-time inventory updates | Should |
| FR9 | Low stock alerts | Should |
| FR10 | Order history for customers | Should |

#### Non-Functional Requirements
| ID | Requirement | Priority |
|----|-------------|----------|
| NFR1 | System uses REST for client-server communication | Must |
| NFR2 | System uses gRPC for server-microservice communication | Must |
| NFR3 | Response time < 2 seconds for normal operations | Should |
| NFR4 | System handles concurrent users | Should |
| NFR5 | Data persisted in PostgreSQL | Must |
| NFR6 | JWT-based authentication | Must |

### 2.2 Use Cases

#### UC1: User Login
- **Actor**: All users
- **Precondition**: User has valid credentials
- **Main Flow**:
  1. User enters username and password
  2. System validates credentials
  3. System returns JWT token
  4. User gains access based on role
- **Alternative Flow**: Invalid credentials → Error message

#### UC2: Manage Products
- **Actor**: Warehouse Operator, Supervisor, Admin
- **Precondition**: User is authenticated
- **Main Flow**:
  1. User navigates to Products
  2. User can view, add, edit, or delete products
  3. System updates database
  4. Inventory updated in real-time

#### UC3: Process Order
- **Actor**: Warehouse Operator
- **Precondition**: Order exists in PENDING status
- **Main Flow**:
  1. Operator views pending orders
  2. Operator changes status to PROCESSING
  3. Operator prepares items
  4. Operator marks as READY_FOR_SHIPMENT
  5. System notifies shipment service

#### UC4: Track Shipment
- **Actor**: Customer, All Staff
- **Precondition**: Order has been shipped
- **Main Flow**:
  1. User views order details
  2. System queries shipment microservice via gRPC
  3. Shipment status and tracking info displayed

### 2.3 Domain Model

```
+----------------+       +----------------+       +----------------+
|     User       |       |    Product     |       |    Category    |
+----------------+       +----------------+       +----------------+
| - id           |       | - id           |       | - id           |
| - username     |       | - sku          |       | - name         |
| - email        |       | - name         |       | - description  |
| - passwordHash |       | - price        |       | - parentId     |
| - firstName    |       | - quantity     |       +----------------+
| - lastName     |       | - categoryId   |
| - roleId       |       +----------------+
+----------------+              |
       |                        |
       |                        |
+----------------+       +----------------+
|     Order      |------>|   OrderItem    |
+----------------+       +----------------+
| - id           |       | - id           |
| - orderNumber  |       | - orderId      |
| - customerId   |       | - productId    |
| - status       |       | - quantity     |
| - totalAmount  |       | - unitPrice    |
+----------------+       +----------------+
       |
       |
+----------------+
|   Shipment     | (C# Microservice)
+----------------+
| - id           |
| - orderId      |
| - trackingNum  |
| - status       |
+----------------+
```

---

## 3. System Design

### 3.1 Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              JavaFX Application (MVVM)               │   │
│  │  ┌──────┐  ┌───────────┐  ┌─────────────────────┐  │   │
│  │  │ View │──│ ViewModel │──│ Service (HTTP)      │  │   │
│  │  └──────┘  └───────────┘  └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ REST API (HTTP/JSON)
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      JAVA SERVER                             │
│  ┌────────────┐  ┌──────────┐  ┌────────────────────────┐  │
│  │ Controller │──│ Service  │──│ Repository             │  │
│  └────────────┘  └──────────┘  └────────────────────────┘  │
│                        │                     │              │
│                  ┌─────┴─────┐               │              │
│                  │gRPC Client│               │              │
│                  └───────────┘               │              │
└─────────────────────────────────────────────────────────────┘
          │                                    │
          │ gRPC (HTTP/2)                      │ JDBC
          ▼                                    ▼
┌────────────────────────┐           ┌─────────────────────┐
│   C# MICROSERVICE      │           │    PostgreSQL       │
│  ┌─────────────────┐   │           │    Database         │
│  │ gRPC Server     │   │           └─────────────────────┘
│  │ ShipmentService │   │
│  └─────────────────┘   │
└────────────────────────┘
```

### 3.2 Component Design

#### Java Server Components
- **Controllers**: REST endpoints, request validation
- **Services**: Business logic, transaction management
- **Repositories**: Data access layer
- **DTOs**: Data transfer objects
- **Entities**: JPA entities
- **Security**: JWT authentication, role-based access

#### C# Microservice Components
- **gRPC Service**: Handles shipment operations
- **Repository**: Data access
- **Models**: Domain entities

#### JavaFX Client Components
- **Views (FXML)**: UI layouts
- **ViewModels**: UI state, business logic
- **Services**: HTTP client, API calls
- **Models**: Client-side data models

---

## 4. Implementation

### 4.1 Database Implementation
- PostgreSQL database with normalized schema
- Proper indexing for performance
- Triggers for automatic timestamps
- Views for common queries

### 4.2 Java Server Implementation
- Spring Boot 3.2 with Java 21
- RESTful API design
- JWT authentication
- gRPC client for microservice communication

### 4.3 C# Microservice Implementation
- .NET 8 with ASP.NET Core
- gRPC server implementation
- Entity Framework Core for data access
- Async operations for scalability

### 4.4 JavaFX Client Implementation
- MVVM architecture
- Property bindings for reactive UI
- Async HTTP calls
- Modern CSS styling

---

## 5. Testing

### 5.1 Unit Tests
- Service layer tests
- Repository tests
- ViewModel tests

### 5.2 Integration Tests
- REST API endpoints
- gRPC communication
- Database operations

### 5.3 System Tests
- End-to-end workflows
- User acceptance testing

---

## 6. Results & Discussion

### 6.1 Achieved Goals
- ✅ Distributed system architecture
- ✅ Heterogeneous technology stack (Java + C#)
- ✅ Dual network protocols (REST + gRPC)
- ✅ Functional inventory management
- ✅ Order processing workflow
- ✅ Shipment tracking microservice
- ✅ Modern GUI with MVVM

### 6.2 Challenges Faced
- gRPC integration between Java and C#
- Proto file compilation across platforms
- JWT token handling in JavaFX

### 6.3 Lessons Learned
- Importance of interface definitions (proto files)
- Benefits of layered architecture
- Value of early integration testing

---

## 7. Conclusion

The project successfully demonstrates a distributed warehouse management system using modern software engineering practices. The heterogeneous architecture with Java and C# components, combined with REST and gRPC communication, provides a scalable and maintainable solution.

---

## 8. Future Work

1. **Mobile Application**: React Native or Flutter client
2. **Advanced Analytics**: Dashboard with charts and reports
3. **Barcode Integration**: Scanner support for inventory
4. **Notification System**: Email/SMS alerts
5. **Performance Optimization**: Caching layer
6. **Containerization**: Docker deployment

---

## Appendices

### A. API Documentation
See [API.md](API.md)

### B. UML Diagrams
See [diagrams/](diagrams/)

### C. Installation Guide
See [README.md](../README.md)
