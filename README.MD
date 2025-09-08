# Order Microservice (OrderMS)

OrderMS handles order placement, retrieval, and cancellation in an event-driven architecture.  
It integrates with **ProductMS** for stock validation and **UserMS** for user authentication.

---

## 🚀 Endpoints

| Method | Path                        | Description                            |
|--------|-----------------------------|----------------------------------------|
| POST   | `/orders/checkout`           | Place a new order                     |
| GET    | `/orders/{orderId}`          | Retrieve order by order ID            |
| GET    | `/orders/users/{userId}`     | Retrieve all orders for a user        |
| POST   | `/orders/{orderId}/cancel`   | Cancel an existing order              |

---

## 📦 Example Requests & Responses

### 1. Place Order (Checkout)

**POST** `/orders/checkout`

**Request Body Example:**
```json
{
  "userId": 1,
  "shippingAddress": "123 Main Street, City, Country",
  "items": [
    {
      "productId": 101,
      "quantity": 2
    },
    {
      "productId": 102,
      "quantity": 1
    }
  ]
}
```

**Response Body Example:**

```json
{
  "orderId": 555,
  "userId": 1,
  "totalAmount": 250.0,
  "status": "PLACED",
  "createdAt": "2025-09-07T20:00:00Z"
}
```

### Response Example (201 Created):

```json
{
  "orderId": 555,
  "userId": 1,
  "totalAmount": 250.0,
  "status": "PLACED",
  "createdAt": "2025-09-07T20:00:00Z"
}
```

**GET** `/orders/{orderId}`

### Response Example (200 OK):

```json
{
  "orderId": 555,
  "userId": 1,
  "items": [
    {
      "productId": 101,
      "quantity": 2,
      "price": 50.0
    }
  ],
  "totalAmount": 100.0,
  "status": "PLACED",
  "shippingAddress": "123 Main Street, City, Country"
}
```

**GET** `/orders/users/{userId}`

### Response Example (200 OK):

```json
[
  {
    "orderId": 555,
    "userId": 1,
    "totalAmount": 250.0,
    "status": "PLACED"
  },
  {
    "orderId": 556,
    "userId": 1,
    "totalAmount": 80.0,
    "status": "CANCELLED"
  }
]
```

**POST** `/orders/{orderId}/cancel`

### Response Example (204 No Content):

```json
(No response body)
```

## ⚡ Future Plans

- Implement event-driven order cancellation compensation flow
- Maintain detailed order history and audit trail
- Support multiple payment provider integrations
- Implement rate limiting and retry mechanisms
- Add caching for frequently queried data
- Enable paginated order history retrieval

## ⚠️ Notes

- JWT token must be sent in the Authorization header:

    ```http
    Authorization: Bearer <token>
    ```

- Product availability is validated at checkout using ProductMS.
- Stock deduction happens via Kafka events for an event-driven architecture.

## 🛠️ Error Handling

| HTTP Status | Description                                      |
|------------|--------------------------------------------------|
| 400        | Bad Request – Invalid input data                |
| 401        | Unauthorized – Missing or invalid JWT          |
| 403        | Forbidden – Insufficient permissions           |
| 404        | Not Found – Order not found                    |
| 409        | Conflict – Out of stock or order already canceled |
| 500        | Internal Server Error – Unexpected error       |

## ⚡ Example Curl Command

```bash
curl -X POST http://localhost:8083/orders/checkout \
-H "Authorization: Bearer <token>" \
-H "Content-Type: application/json" \
-d '{
    "userId": 1,
    "shippingAddress": "123 Main Street, City, Country",
    "items": [
        { "productId": 101, "quantity": 2 },
        { "productId": 102, "quantity": 1 }
    ]
}'
