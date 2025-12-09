# SEP3 - Warehouse Management System

## 📋 Project Overview

A distributed warehouse management system implementing a heterogeneous architecture with Java and C# components, featuring REST and gRPC communication protocols.

## 🏗️ Architecture

```
┌─────────────────┐     REST API      ┌─────────────────────┐     gRPC      ┌─────────────────────┐
│   JavaFX Client │◄─────────────────►│   Java Server       │◄─────────────►│  C# Microservice    │
│   (MVVM)        │                   │   (Spring Boot 3)   │               │  (Shipment Service) │
└─────────────────┘                   └─────────┬───────────┘               └─────────────────────┘
                                                │
                                                ▼
                                      ┌─────────────────────┐
                                      │    PostgreSQL DB    │
                                      └─────────────────────┘
```

## 📁 Project Structure

```
SEP3-Project/
├── java-server/           # Spring Boot 3 REST API + gRPC Client
├── csharp-microservice/   # ASP.NET gRPC Server (Shipment Service)
├── javafx-client/         # JavaFX GUI with MVVM pattern
├── shared-protos/         # Shared Protocol Buffer definitions
├── database/              # PostgreSQL schema and seed data
└── docs/                  # Documentation and UML diagrams
```

## 🔧 Technologies

### Java Server (Tier 2)
- **Framework**: Spring Boot 3.2
- **API**: REST Controllers
- **Database**: PostgreSQL with JPA/Hibernate
- **gRPC**: Client for C# microservice communication
- **Security**: Spring Security with JWT

### C# Microservice (Tier 3)
- **Framework**: ASP.NET Core 8
- **API**: gRPC Server
- **Service**: Shipment Tracking & Management

### JavaFX Client (Tier 1)
- **Framework**: JavaFX 21
- **Pattern**: MVVM (Model-View-ViewModel)
- **HTTP Client**: Java HttpClient for REST

## 👥 User Roles

| Role | Permissions |
|------|-------------|
| **Admin** | Full system access, user management |
| **Supervisor** | Inventory oversight, reports, order approval |
| **Warehouse Operator** | Inventory CRUD, order processing |
| **Customer** | View products, place orders, track shipments |

## 🚀 Features

### Core Functionality
- ✅ Inventory Management (CRUD operations)
- ✅ Order Processing (Create, Update, Cancel)
- ✅ Shipment Tracking (via C# microservice)
- ✅ Real-time Updates (Polling/WebSocket)
- ✅ Data Persistence (PostgreSQL)
- ✅ User Authentication & Authorization

### Network Communication
- ✅ REST API (Client ↔ Java Server)
- ✅ gRPC (Java Server ↔ C# Microservice)
- ✅ Real-time synchronization

## 📦 Prerequisites

- Java 21+
- .NET 8 SDK
- PostgreSQL 15+
- Maven 3.9+
- Node.js 18+ (optional, for tooling)

## 🛠️ Setup Instructions

### 1. Database Setup
```bash
cd database
psql -U postgres -f schema.sql
psql -U postgres -f seed.sql
```

### 2. Start C# Microservice
```bash
cd csharp-microservice/ShipmentService
dotnet restore
dotnet run
```
Service runs on: `https://localhost:5001` (gRPC)

### 3. Start Java Server
```bash
cd java-server
mvn spring-boot:run
```
Server runs on: `http://localhost:8080` (REST)

### 4. Start JavaFX Client
```bash
cd javafx-client
mvn javafx:run
```

## 📚 Documentation

- [Project Description](docs/PROJECT_DESCRIPTION.md)
- [Project Report](docs/PROJECT_REPORT.md)
- [Process Report](docs/PROCESS_REPORT.md)
- [API Documentation](docs/API.md)
- [UML Diagrams](docs/diagrams/)

## 🧪 Testing

```bash
# Java Server Tests
cd java-server
mvn test

# C# Microservice Tests
cd csharp-microservice/ShipmentService
dotnet test

# JavaFX Client Tests
cd javafx-client
mvn test
```

## 📊 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/refresh` | Refresh token |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| POST | `/api/orders` | Create order |
| PUT | `/api/orders/{id}/status` | Update order status |

### Shipments (via gRPC to C#)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/shipments/{orderId}` | Get shipment status |
| POST | `/api/shipments` | Create shipment |
| PUT | `/api/shipments/{id}/status` | Update shipment |

## 👨‍💻 Team

- Matteo Saccucci 355400
- Matteo Maria De Filippis
## 📄 License

This project is developed for educational purposes as part of SEP3 at VIA University College.
