# Project Description

## Warehouse Management System - SEP3

### 1. Problem Domain

The warehouse management domain encompasses the systematic handling of inventory, order processing, and shipment tracking within a logistics environment. Modern warehouses require efficient systems to:

- Track inventory levels in real-time
- Process customer orders efficiently
- Manage shipments and deliveries
- Provide role-based access for different user types
- Ensure data consistency across distributed systems

### 2. Problem Statement

Traditional warehouse operations often suffer from:

- **Inventory Inaccuracies**: Manual tracking leads to discrepancies between recorded and actual stock levels
- **Order Processing Delays**: Lack of automation causes bottlenecks in order fulfillment
- **Limited Visibility**: Stakeholders lack real-time visibility into operations
- **Data Silos**: Information scattered across disconnected systems
- **Scalability Issues**: Monolithic systems struggle to handle growing demands

**Our Solution**: A distributed warehouse management system that:
- Automates inventory tracking and order processing
- Provides real-time visibility through a modern GUI
- Uses microservices architecture for scalability
- Implements role-based access control for security
- Enables seamless shipment tracking through integrated services

### 3. Delimitations

**In Scope:**
- Inventory management (CRUD operations)
- Order processing workflow
- Shipment tracking (delegated to microservice)
- User authentication and authorization
- Role-based access (Admin, Supervisor, Warehouse Operator, Customer)
- REST API for client communication
- gRPC for server-to-server communication

**Out of Scope:**
- Payment processing
- Supplier management
- Advanced analytics and reporting
- Mobile applications
- Integration with external ERP systems
- Barcode/RFID scanning hardware integration

### 4. Choice of Method

**Methodology**: Kanban

**Rationale:**
- **Flexibility**: Allows continuous delivery without fixed sprint cycles
- **Visual Workflow**: Board visualization helps track progress
- **WIP Limits**: Prevents team overload and ensures quality
- **Continuous Improvement**: Enables quick adaptation to changes
- **Simple to Implement**: Lower overhead than Scrum for smaller teams

**Tools:**
- **GitHub Projects**: Kanban board for task management
- **GitHub Issues**: Bug tracking and feature requests
- **Cursor/IntelliJ/Rider**: Development environments
- **Discord/Teams**: Team communication

### 5. Time Schedule

| Week | Activities |
|------|------------|
| 1-2 | Requirements gathering, System design, UML diagrams |
| 3-4 | Database design, Java Server setup, Basic REST API |
| 5-6 | C# Microservice development, gRPC integration |
| 7-8 | JavaFX Client development, MVVM implementation |
| 9-10 | Integration testing, Bug fixes |
| 11-12 | Documentation, Final testing, Presentation preparation |

### 6. Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| gRPC integration challenges | Medium | High | Early prototyping, documentation review |
| Database performance issues | Low | Medium | Proper indexing, query optimization |
| Team member unavailability | Medium | Medium | Knowledge sharing, documentation |
| Scope creep | High | Medium | Strict adherence to delimitations |
| Technology learning curve | Medium | Low | Pair programming, tutorials |
| Integration test failures | Medium | High | Continuous integration, early testing |

### 7. Technology Stack

**Java Server (Tier 2)**
- Java 21
- Spring Boot 3.2
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- gRPC (client)

**C# Microservice (Tier 3)**
- .NET 8
- ASP.NET Core
- gRPC (server)
- Entity Framework Core
- PostgreSQL/SQLite

**JavaFX Client (Tier 1)**
- Java 21
- JavaFX 21
- MVVM Pattern

**Communication**
- REST API (Client ↔ Server)
- gRPC (Server ↔ Microservice)

### 8. Team Organization

| Role | Responsibilities |
|------|-----------------|
| Project Lead | Overall coordination, documentation |
| Backend Developer | Java server, database design |
| Microservice Developer | C# shipment service, gRPC |
| Frontend Developer | JavaFX client, UI/UX |

### 9. Success Criteria

1. ✅ Distributed system with Java and C# components
2. ✅ REST and gRPC communication protocols
3. ✅ Functional GUI with MVVM pattern
4. ✅ Complete CRUD operations for products
5. ✅ Order processing workflow
6. ✅ Shipment tracking via microservice
7. ✅ Role-based access control
8. ✅ Comprehensive documentation
9. ✅ All UML diagrams completed
