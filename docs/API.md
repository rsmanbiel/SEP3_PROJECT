# API Documentation

## Warehouse Management System - REST API

**Base URL**: `http://localhost:8080/api`

---

## Authentication

All endpoints except `/auth/*` require JWT authentication.

Include the token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## Endpoints

### Authentication

#### POST /auth/login
Authenticate user and receive JWT tokens.

**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Response** (200 OK):
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "string",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "role": "string"
  }
}
```

**Errors**:
- 401 Unauthorized: Invalid credentials

---

#### POST /auth/register
Register a new customer user.

**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "phone": "string",
  "address": "string",
  "city": "string",
  "postalCode": "string",
  "country": "string"
}
```

**Response** (201 Created): Same as login response

**Errors**:
- 400 Bad Request: Validation errors
- 409 Conflict: Username or email already exists

---

#### POST /auth/refresh
Refresh access token using refresh token.

**Headers**:
```
Authorization: Bearer <refresh_token>
```

**Response** (200 OK): Same as login response

---

### Products

#### GET /products
Get all products with pagination.

**Query Parameters**:
- `page` (int, default: 0): Page number
- `size` (int, default: 20): Page size
- `sort` (string): Sort field (e.g., "name,asc")

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "sku": "ELEC-001",
      "name": "Wireless Headphones",
      "description": "High-quality headphones",
      "categoryId": 1,
      "categoryName": "Electronics",
      "price": 299.99,
      "costPrice": 150.00,
      "quantityInStock": 100,
      "minimumStockLevel": 20,
      "maximumStockLevel": 500,
      "weightKg": 0.35,
      "dimensions": "20x18x8",
      "location": "A-01-01",
      "barcode": "5901234123457",
      "isActive": true,
      "isLowStock": false,
      "createdAt": "2024-12-06T10:00:00",
      "updatedAt": "2024-12-06T10:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

---

#### GET /products/{id}
Get product by ID.

**Path Parameters**:
- `id` (long): Product ID

**Response** (200 OK): Single product object

**Errors**:
- 404 Not Found: Product not found

---

#### GET /products/sku/{sku}
Get product by SKU.

**Response** (200 OK): Single product object

---

#### GET /products/search
Search products.

**Query Parameters**:
- `query` (string): Search term
- `page`, `size`: Pagination

**Response** (200 OK): Paginated product list

---

#### GET /products/category/{categoryId}
Get products by category.

**Response** (200 OK): Paginated product list

---

#### GET /products/low-stock
Get products with stock below minimum level.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Response** (200 OK): List of products

---

#### POST /products
Create a new product.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Request Body**:
```json
{
  "sku": "string (required)",
  "name": "string (required)",
  "description": "string",
  "categoryId": 1,
  "price": 99.99,
  "costPrice": 50.00,
  "quantityInStock": 100,
  "minimumStockLevel": 10,
  "maximumStockLevel": 500,
  "weightKg": 0.5,
  "dimensions": "10x10x10",
  "location": "A-01-01",
  "barcode": "1234567890"
}
```

**Response** (201 Created): Created product object

---

#### PUT /products/{id}
Update a product.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Request Body**: Same as POST (all fields optional)

**Response** (200 OK): Updated product object

---

#### PATCH /products/{id}/stock
Update product stock.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Query Parameters**:
- `quantityChange` (int): Amount to add/subtract

**Response** (200 OK): Updated product object

---

#### DELETE /products/{id}
Soft delete a product.

**Required Role**: ADMIN, SUPERVISOR

**Response** (204 No Content)

---

### Orders

#### GET /orders
Get all orders with pagination.

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "orderNumber": "ORD-20241206-000001",
      "customerId": 8,
      "customerName": "John Doe",
      "customerEmail": "john@email.com",
      "status": "PENDING",
      "totalAmount": 299.99,
      "shippingAddress": "Street 1",
      "shippingCity": "Copenhagen",
      "shippingPostalCode": "1000",
      "shippingCountry": "Denmark",
      "shippingPhone": "+45 12345678",
      "notes": "Leave at door",
      "items": [...],
      "itemCount": 3,
      "createdAt": "2024-12-06T10:00:00",
      "updatedAt": "2024-12-06T10:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 3
}
```

---

#### GET /orders/{id}
Get order by ID.

---

#### GET /orders/number/{orderNumber}
Get order by order number.

---

#### GET /orders/customer/{customerId}
Get orders by customer.

---

#### GET /orders/status/{status}
Get orders by status.

**Status values**: PENDING, CONFIRMED, PROCESSING, READY_FOR_SHIPMENT, SHIPPED, DELIVERED, CANCELLED, RETURNED

---

#### POST /orders
Create a new order.

**Request Body**:
```json
{
  "customerId": 8,
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 5, "quantity": 1 }
  ],
  "shippingAddress": "Street 1",
  "shippingCity": "Copenhagen",
  "shippingPostalCode": "1000",
  "shippingCountry": "Denmark",
  "shippingPhone": "+45 12345678",
  "notes": "Optional notes"
}
```

**Response** (201 Created): Created order object

---

#### PUT /orders/{id}/status
Update order status.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Request Body**:
```json
{
  "status": "CONFIRMED",
  "notes": "Order approved"
}
```

**Response** (200 OK): Updated order object

---

#### DELETE /orders/{id}
Cancel an order.

**Required Role**: ADMIN, SUPERVISOR

**Query Parameters**:
- `reason` (string): Cancellation reason

**Response** (200 OK): Cancelled order object

---

### Shipments

All shipment endpoints proxy to the C# gRPC microservice.

#### GET /shipments
Get all shipments.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Query Parameters**:
- `page`, `size`: Pagination
- `status`: Filter by status

---

#### GET /shipments/{id}
Get shipment by ID.

---

#### GET /shipments/order/{orderId}
Get shipment by order ID.

---

#### POST /shipments/order/{orderId}
Create shipment for an order.

**Required Role**: ADMIN, SUPERVISOR, WAREHOUSE_OPERATOR

**Response** (201 Created):
```json
{
  "id": 1,
  "orderId": 1,
  "trackingNumber": "SHP20241206120000001234",
  "status": "PENDING",
  "recipientName": "John Doe",
  "recipientAddress": "Street 1",
  "recipientCity": "Copenhagen",
  "recipientPostalCode": "1000",
  "recipientCountry": "Denmark",
  "recipientPhone": "+45 12345678",
  "weightKg": 1.5,
  "currentLocation": "Warehouse",
  "estimatedDelivery": "2024-12-10T12:00:00",
  "history": [
    {
      "status": "PENDING",
      "location": "Warehouse",
      "timestamp": "2024-12-06T12:00:00",
      "notes": "Shipment created"
    }
  ],
  "createdAt": "2024-12-06T12:00:00"
}
```

---

#### PUT /shipments/{id}/status
Update shipment status.

**Query Parameters**:
- `status` (string): New status
- `location` (string): Current location
- `notes` (string): Update notes

**Response** (200 OK): Updated shipment object

---

#### DELETE /shipments/{id}
Cancel a shipment.

**Query Parameters**:
- `reason` (string): Cancellation reason

---

## Error Responses

All errors follow this format:

```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": "2024-12-06T10:00:00"
}
```

### Common Error Codes
- 400 Bad Request: Invalid input
- 401 Unauthorized: Missing or invalid token
- 403 Forbidden: Insufficient permissions
- 404 Not Found: Resource not found
- 409 Conflict: Duplicate resource
- 500 Internal Server Error: Server error

---

## gRPC Service (C# Microservice)

The shipment microservice exposes a gRPC interface defined in `shipment.proto`.

**Host**: `localhost:5001`

### Service Definition
```protobuf
service ShipmentService {
  rpc CreateShipment (CreateShipmentRequest) returns (ShipmentResponse);
  rpc GetShipment (GetShipmentRequest) returns (ShipmentResponse);
  rpc GetShipmentByOrderId (GetShipmentByOrderIdRequest) returns (ShipmentResponse);
  rpc UpdateShipmentStatus (UpdateShipmentStatusRequest) returns (ShipmentResponse);
  rpc GetAllShipments (GetAllShipmentsRequest) returns (ShipmentListResponse);
  rpc StreamShipmentUpdates (StreamShipmentRequest) returns (stream ShipmentUpdate);
  rpc CancelShipment (CancelShipmentRequest) returns (ShipmentResponse);
}
```
