# UML Diagrams

## Warehouse Management System

This directory contains UML diagrams for the SEP3 project.

---

## 1. Use Case Diagram

```mermaid
graph TB
    subgraph Actors
        Admin((Admin))
        Supervisor((Supervisor))
        Operator((Warehouse<br/>Operator))
        Customer((Customer))
    end
    
    subgraph "Authentication"
        UC1[Login]
        UC2[Logout]
        UC3[Register]
    end
    
    subgraph "Inventory Management"
        UC4[View Products]
        UC5[Add Product]
        UC6[Edit Product]
        UC7[Delete Product]
        UC8[Update Stock]
        UC9[View Low Stock]
    end
    
    subgraph "Order Management"
        UC10[View Orders]
        UC11[Create Order]
        UC12[Process Order]
        UC13[Update Order Status]
        UC14[Cancel Order]
    end
    
    subgraph "Shipment Management"
        UC15[Create Shipment]
        UC16[Track Shipment]
        UC17[Update Shipment]
    end
    
    Admin --> UC1
    Admin --> UC2
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
    Admin --> UC15
    Admin --> UC16
    Admin --> UC17
    
    Supervisor --> UC1
    Supervisor --> UC2
    Supervisor --> UC4
    Supervisor --> UC5
    Supervisor --> UC6
    Supervisor --> UC8
    Supervisor --> UC9
    Supervisor --> UC10
    Supervisor --> UC12
    Supervisor --> UC13
    Supervisor --> UC14
    Supervisor --> UC15
    Supervisor --> UC16
    
    Operator --> UC1
    Operator --> UC2
    Operator --> UC4
    Operator --> UC5
    Operator --> UC6
    Operator --> UC8
    Operator --> UC9
    Operator --> UC10
    Operator --> UC12
    Operator --> UC13
    Operator --> UC15
    Operator --> UC16
    
    Customer --> UC1
    Customer --> UC2
    Customer --> UC3
    Customer --> UC4
    Customer --> UC11
    Customer --> UC10
    Customer --> UC16
```

---

## 2. Domain Model (Class Diagram)

```mermaid
classDiagram
    class User {
        +Long id
        +String username
        +String email
        +String passwordHash
        +String firstName
        +String lastName
        +String phone
        +String address
        +String city
        +String postalCode
        +String country
        +Boolean isActive
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +getFullName()
    }
    
    class Role {
        +Long id
        +String name
        +String description
        +LocalDateTime createdAt
    }
    
    class Category {
        +Long id
        +String name
        +String description
        +LocalDateTime createdAt
    }
    
    class Product {
        +Long id
        +String sku
        +String name
        +String description
        +BigDecimal price
        +BigDecimal costPrice
        +Integer quantityInStock
        +Integer minimumStockLevel
        +Integer maximumStockLevel
        +BigDecimal weightKg
        +String dimensions
        +String location
        +String barcode
        +Boolean isActive
        +isLowStock()
    }
    
    class Order {
        +Long id
        +String orderNumber
        +OrderStatus status
        +BigDecimal totalAmount
        +String shippingAddress
        +String shippingCity
        +String shippingPostalCode
        +String shippingCountry
        +String shippingPhone
        +String notes
        +LocalDateTime createdAt
        +LocalDateTime shippedAt
        +LocalDateTime deliveredAt
        +calculateTotal()
    }
    
    class OrderItem {
        +Long id
        +Integer quantity
        +BigDecimal unitPrice
        +BigDecimal totalPrice
        +calculateTotal()
    }
    
    class Shipment {
        +Long id
        +Long orderId
        +String trackingNumber
        +ShipmentStatus status
        +String recipientName
        +String recipientAddress
        +String currentLocation
        +DateTime estimatedDelivery
    }
    
    class ShipmentHistory {
        +Long id
        +ShipmentStatus status
        +String location
        +DateTime timestamp
        +String notes
    }
    
    User "1" -- "1" Role : has
    Category "1" -- "*" Product : contains
    Category "1" -- "*" Category : parent
    User "1" -- "*" Order : places
    Order "1" -- "*" OrderItem : contains
    Product "1" -- "*" OrderItem : included in
    Order "1" -- "0..1" Shipment : has
    Shipment "1" -- "*" ShipmentHistory : has
    User "1" -- "*" Order : processes
```

---

## 3. Sequence Diagram - User Login

```mermaid
sequenceDiagram
    participant C as JavaFX Client
    participant S as Java Server
    participant DB as PostgreSQL
    
    C->>S: POST /api/auth/login {username, password}
    S->>DB: SELECT user WHERE username = ?
    DB-->>S: User data
    S->>S: Validate password (BCrypt)
    S->>S: Generate JWT tokens
    S-->>C: {accessToken, refreshToken, user}
    C->>C: Store tokens
    C->>C: Navigate to main view
```

---

## 4. Sequence Diagram - Create Order

```mermaid
sequenceDiagram
    participant C as JavaFX Client
    participant S as Java Server
    participant DB as PostgreSQL
    
    C->>S: POST /api/orders {customerId, items[]}
    S->>DB: Validate customer exists
    S->>DB: Validate products exist
    S->>S: Check stock availability
    
    loop For each item
        S->>DB: Reserve stock (quantity - ordered)
    end
    
    S->>S: Calculate total
    S->>S: Generate order number
    S->>DB: INSERT order
    S->>DB: INSERT order_items
    S-->>C: Order created {orderNumber, total, status}
```

---

## 5. Sequence Diagram - Process Shipment

```mermaid
sequenceDiagram
    participant C as JavaFX Client
    participant JS as Java Server
    participant CS as C# Microservice
    participant DB as PostgreSQL
    
    C->>JS: POST /api/shipments/order/{orderId}
    JS->>JS: Validate order status (READY_FOR_SHIPMENT)
    JS->>JS: Calculate total weight
    JS->>CS: gRPC CreateShipment()
    CS->>CS: Generate tracking number
    CS->>DB: INSERT shipment
    CS->>DB: INSERT shipment_history
    CS-->>JS: ShipmentResponse
    JS->>DB: UPDATE order SET status='SHIPPED'
    JS-->>C: Shipment created {trackingNumber}
```

---

## 6. Deployment Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        JFX[JavaFX Client<br/>Java 21]
    end
    
    subgraph "Application Layer"
        JS[Java Server<br/>Spring Boot 3.2<br/>Port 8080]
        CS[C# Microservice<br/>.NET 8<br/>Port 5001]
    end
    
    subgraph "Data Layer"
        PG[(PostgreSQL<br/>Port 5432)]
    end
    
    JFX -->|REST/HTTP| JS
    JS -->|gRPC/HTTP2| CS
    JS -->|JDBC| PG
    CS -->|Npgsql| PG
```

---

## 7. Activity Diagram - Order Processing

```mermaid
stateDiagram-v2
    [*] --> PENDING: Order Created
    PENDING --> CONFIRMED: Confirm Order
    PENDING --> CANCELLED: Cancel Order
    CONFIRMED --> PROCESSING: Start Processing
    CONFIRMED --> CANCELLED: Cancel Order
    PROCESSING --> READY_FOR_SHIPMENT: Items Packed
    PROCESSING --> CANCELLED: Cancel Order
    READY_FOR_SHIPMENT --> SHIPPED: Create Shipment
    READY_FOR_SHIPMENT --> CANCELLED: Cancel Order
    SHIPPED --> DELIVERED: Delivery Confirmed
    SHIPPED --> RETURNED: Return Initiated
    DELIVERED --> RETURNED: Return Initiated
    DELIVERED --> [*]
    RETURNED --> [*]
    CANCELLED --> [*]
```

---

## 8. Component Diagram

```mermaid
graph TB
    subgraph "JavaFX Client"
        View[Views<br/>FXML]
        VM[ViewModels]
        SVC[Services]
        Model[Models]
        View --> VM
        VM --> SVC
        VM --> Model
    end
    
    subgraph "Java Server"
        Controller[Controllers]
        Service[Services]
        Repo[Repositories]
        Entity[Entities]
        DTO[DTOs]
        GRPC[gRPC Client]
        
        Controller --> Service
        Controller --> DTO
        Service --> Repo
        Service --> GRPC
        Repo --> Entity
    end
    
    subgraph "C# Microservice"
        GrpcSvc[gRPC Service]
        CSRepo[Repository]
        CSModel[Models]
        
        GrpcSvc --> CSRepo
        CSRepo --> CSModel
    end
    
    SVC -->|REST| Controller
    GRPC -->|gRPC| GrpcSvc
```

---

## Notes

- Diagrams created using Mermaid syntax
- Can be rendered in GitHub, VS Code, or online Mermaid editors
- For Astah exports, create separate .asta files
- Export diagrams as PNG/PDF for documentation
