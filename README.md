# Tiny-Saga
Proof of concept implementation of microservices saga pattern.

## Draft Version 1.0.0

Components
### API Gateway
The API gateway acts as a single entry point for client requests, providing load balancing, authentication, and rate limiting.

### Order Microservice (Order MS)
This microservice is responsible for managing orders, including their creation, updating, and retrieval. It also handles order status updates, tracking information, and order history.

Key operations:
* Create order
* Update order
* Get order details
* Update order status

### Payment Microservice (Payment MS)
The Payment MS is responsible for processing payments, including traditional payments made using a wallet.

Key operations:
* Process payment
* Verify wallet balance

### Wallet Microservice (Wallet MS)
This microservice manages user wallets, including checking the available balance and updating the wallet upon successful payment.

Key operations:
* Get wallet balance
* Update wallet balance

### Inventory Microservice (Inventory MS)
The Inventory MS manages stock levels and product availability.

Key operations:
* Check product availability
* Update stock levels

### Orchestrator Microservice (Orchestrator MS)
This microservice is responsible for coordinating the actions of other microservices using the saga pattern.

## UI User flow
1) User clicks on order -> Backend /api/order/{id} -> After making status of order to initated, page will route to
Payment page
2) User clicks on payment -> Backend /api/payment -> API will trigger and do payment from wallet. Validation if there is no amount available.


## API User Flows

### 1) Place a order
* The user places an order, and the request goes through the API gateway to the Order MS.
* The Order MS creates a new order in the database, sets the order status to "Initiated," and returns the order details to the user.

### 2) Traditional Payment Flow
* The user initiates the order, and the request goes through the API gateway to the Payment MS.
* The Payment MS communicates with the Wallet MS to check if the wallet has enough balance. If so, it subtracts the required amount and confirms the payment.
* Payment MS then publishes an event to Kafka, which both the Order MS and Inventory MS listen to.

### 3) Insufficient fund flow
* Wallet MS throws error to payment MS with insufficent fund
* Payment MS will trigger event to kafa and order MS will receive event with insufficent flow
* Order MS will update status of order.





## Database

### 1) Order DB
* OrderId: A unique identifier for the order (Primary Key).
* UserId: A reference to the user who placed the order (Foreign Key).
* OrderStatus: An ENUM field representing the order status (e.g., InitializedOrder, PaymentDone, Placed, Failed, Delivered, On-Way, Canceled).
* ProductId: A reference to the product being ordered (Foreign Key).
* Quantity: The number of units of the product being ordered.
* Price: The cost of the product at the time the order was placed.
* createdAt: Order creation date
* updateAt: Order update date
* StatusInfo: Tells more information about status.

### 2) Inventory DB
* ProductId: A unique identifier for the product (Primary Key).
* ProductName: The name or title of the product.
* ProductType: The category or type of the product (e.g., electronics, clothing, etc.).
* AvailableProductCount: The number of available units of the product in the inventory.

### 3) Wallet DB
* UserId: A unique identifier for the user (Primary Key). This field can be a foreign key referencing the user's ID in your User table or User microservice.
* Amount: The current balance of the user's wallet.

### 4) Transaction DB
* TransactionId: A unique identifier for the transaction (Primary Key).
* UserId: The ID of the user associated with the transaction. This field can be a foreign key referencing the UserId in the User table or User microservice.
* OrderId: The ID of the associated order (if applicable). This field can be a foreign key referencing the OrderId in the Order database or can be NULL for transactions not related to orders (e.g., deposits and withdrawals).
* TransactionType: The type of transaction (e.g., Deposit, Withdrawal, Payment).
* PaymentStatus: The status of the payment transaction, if applicable (e.g., Pending, Completed, Failed, Refunded). This field can be NULL for non-payment transactions (e.g., deposits and withdrawals).
* Amount: The amount of the transaction.
* Currency: The currency used for the transaction (e.g., USD, EUR, GBP).
* Timestamp: The date and time when the transaction occurred.
* Description: A brief description or note about the transaction (optional).



## Questions and Considerations
* What specific information will be included in the event published to Kafka by the Payment MS?
* Once the Order MS receives the event, how will it update the order status, and what other actions will it perform?
* Similarly, when the Inventory MS receives the event, how will it update the stock levels and ensure product availability?
* How will the system handle scenarios when the wallet does not have enough balance to complete the order? -> It will simple error throw to client and publish event with order fail.
* How you will handle system failure between payment ms and wallet ms ? -> We will use retry with exponential backoff logic, once limit of retry reached then we will send error to client. And we will mark order as fail.
