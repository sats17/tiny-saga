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

### Traditional Payment Flow
* The user initiates the order, and the request goes through the API gateway to the Payment MS.
* The Payment MS communicates with the Wallet MS to check if the wallet has enough balance. If so, it subtracts the required amount and confirms the payment.
* Payment MS then publishes an event to Kafka, which both the Order MS and Inventory MS listen to.

## Questions and Considerations
* What specific information will be included in the event published to Kafka by the Payment MS?
* Once the Order MS receives the event, how will it update the order status, and what other actions will it perform?
* Similarly, when the Inventory MS receives the event, how will it update the stock levels and ensure product availability?
* How will the system handle scenarios when the wallet does not have enough balance to complete the order?
* How you will handle system failure between payment ms and wallet ms ? -> We will use retry with exponential backoff logic, once limit of retry reached then we will send error to client. And we will mark order as fail.
