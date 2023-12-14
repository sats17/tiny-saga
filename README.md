# Tiny-Saga (In progress)
Proof of concept implementation of microservices saga pattern.

## Draft Version 1.0.0

## To See Technical Diagrams Check below link:
### https://sats17.github.io/tiny-saga/


## Services

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
* Send notification to user about payment.

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

### Notification Microservice
This microservice is responsible for sending notification to users

## User Journey
1) User clicks on order with selected payment mode -> Backend call will be trigger, it will take payment and place order (edge cases of insufficient fund or inventory insufficient are async flow)
2) For wallet payment mode -> Wallet payment mode will be disable, if user have insufficient fund. -> Backend will call to wallet ms to check user funds.
~~1) User clicks on order -> Backend /api/order/{id} -> After making status of order to initated, page will route to
Payment page~~
~~2) User clicks on payment -> Backend /api/payment -> API will trigger and do payment from wallet. Validation if there is no amount available.~~


## API Journeys (Choreography Pattern)

### 1) Place a order with wallet payment mode
* ~~The user initiates the order, and the request goes through the API gateway to the Order MS.~~
* The Order microservice receives a request to create an order. It creates an order with the status "Initiated" and publishes an "OrderInitiated" event.
* The Payment microservice listens for the "OrderInitiated" event. Upon receiving the event, it attempts to process the payment by calling HTTP call to the Wallet microservice. If the payment is successful, it publishes a "PaymentSucceeded" event. If the payment fails, it publishes a "PaymentFailed" event with a reason.
* The Payment MS communicates with the Wallet MS to check if the wallet has enough balance. If so, it subtracts the required amount and confirms the payment.
* Payment MS then publishes an event to Kafka, which both the Order MS and Inventory MS(Need to check for inventory) listen to.
* The Order microservice listens for both the "PaymentSucceeded" and "PaymentFailed" events. If it receives a "PaymentSucceeded" event, it updates the order status to "PaymentDone". If it receives a "PaymentFailed" event, it updates the order status to "Failed" and stores the reason for the failure.

### 2) Insufficient fund flow for wallet payment mode (Insufficient fund while placing order) -
* Wallet MS throws error to payment MS with insufficent fund
* ~~Payment MS will trigger event to kafa and order MS will receive event with insufficent flow~~
* Payment MS will just return API call to order MS.
* Order MS will update status of order. Either by email or SSE.

### 3) Inventory Reserved flow after payment success -
* When payment is done, then payment ms will create event PaymentSucceeded which will listen by order ms and inventory ms.
* The Inventory microservice listens for the "PaymentSucceeded" event. Upon receiving the event, it checks if there is enough stock for the ordered product. If there is enough stock, it reserves the items for the order and publishes an "InventoryReserved" event. If there isn't enough stock, it publishes an "InventoryInsufficient" event.
* The Order microservice listens for the "InventoryReserved", "InventoryInsufficient", and "PaymentFailed" events. It updates the order status accordingly:
"InventoryReserved": updates the order status to "Placed".
"InventoryInsufficient": updates the order status to "Failed" and stores the reason for the failure (insufficient inventory).
"PaymentFailed": updates the order status to "Failed" and stores the reason for the failure (payment-related issues).

### 4) Inventory insufficient flow and refund payment -
* When the Inventory microservice publishes the "InventoryInsufficient" event, the Payment microservice listens for this event.
* Upon receiving the "InventoryInsufficient" event, the Payment microservice initiates a refund process by calling the Wallet microservice to credit the amount back to the user's wallet. Once the refund is successful, the Payment microservice publishes a "RefundSucceeded" event. If the refund fails for any reason, it publishes a "RefundFailed" event with a reason.
* The Order microservice listens for "InventoryInsufficient" event, the Order microservice will update order with Payment Refund initiated. And Order microservice should notified via
SMS That inventory insufficient.
* The Order microservice listens for the "RefundSucceeded" and "RefundFailed" events. If it receives a "RefundSucceeded" event, it updates the order status to "Failed" with a reason indicating that the inventory was insufficient, and the refund was successful. And it should notified via SMS that refund is done. If it receives a "RefundFailed" event, it updates the order status to "Failed" and stores the reason for the failure (inventory insufficiency and refund-related issues). Refund failed is critical and it need to check.





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

## Events payload
### orderInitiated event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef15b7f6",
  "eventName": "ORDER_INITIATED",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
  "orderId": "12345",
   "userId": "67890",
   "orderStatus": "INITIATED",
   "paymentType": "WALLET",
   "productId" : "123asf-sfa-2a"
   "productQuantity": 2,
   "price": 1000
}
```
* triggered by = Order MS
* Listen by = Payment MS

### Payment_Done event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef7f6",
  "eventName": "PAYMENT_DONE",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
   "orderId": "12345",
   "userId": "67890",
   "orderStatus": "PAYMENT_DONE",
   "paymentType": "WALLET",
   "transactionId": "550sf1100-e29b-41d4-a716-446655440000",
   "productId" : "123asf-sfa-2a"
   "productQuantity": 2
}
```
* triggered by = Payment MS
* Listen by = Order MS, Inventory MS

### Payment_Fail event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef7f6",
  "eventName": "PAYMENT_FAIL",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
  "orderId": "12345",
  "userId": "67890",
  "orderStatus": "PAYMENT_FAIL",
  "paymentType": "WALLET",
  "transactionId": "550sf1100-e29b-41d4-a716-446655440000"
  "failReason": "insufficientFund" or "serverError"
}
```
* triggered by = Payment MS
* Listen by = Order MS

### Inventory Reserved event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef7f6",
  "eventName": "INVENTORY_RESERVERVED",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
  "orderId": "12345",
  "userId": "67890",
  "orderStatus": "INVENTORY_RESERVERVED",
  "paymentType": "WALLET",
  "transactionId": "550sf1100-e29b-41d4-a716-446655440000",
   "productId" : "123asf-sfa-2a",
   "productQuantity": 2
}
```
* triggered by = Inventory MS
* Listen by = Order MS

### Inventory Insufficient event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef7f6",
  "eventName": "INVENTORY_INSUFFICIENT",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
  "orderId": "12345",
  "userId": "67890",
  "orderStatus": "INVENTORY_INSUFFICIENT",
  "paymentType": "WALLET",
  "transactionId": "550sf1100-e29b-41d4-a716-446655440000",
  "productId" : "123asf-sfa-2a",
  "productQuantity": 2,
  "inventoryFailReason": "INVENTORY NOT AVAILABLE"
}
```
* triggered by = Inventory MS
* Listen by = Order MS, Payment MS

### Refund Initiated event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef7f6",
  "eventName": "REFUND_INITIATED",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
  "orderId": "12345",
  "userId": "67890",
  "orderStatus": "ORDER_FAIL",
  "paymentType": "WALLET",
  "transactionId": "550sf1100-e29b-41d4-a716-446655440000",
  "productId" : "123asf-sfa-2a",
  "productQuantity": 2,
  "isPartialOrder": false // Indicates when some amount of quantity is available
}
```
* triggered by = Payment MS
* Listen by = Order MS, Notification MS

### Refund Done event
* payload
```
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "correlationId": "8a2e2d59-9d36-4b87-8ae0-2a4eef7f6",
  "eventName": "REFUND_DONE",
  "version": "1.0",
  "timestamp": "2023-05-01T12:34:56Z",
  "orderId": "12345",
  "userId": "67890",
  "orderStatus": "ORDER_FAIL",
  "paymentType": "WALLET",
  "transactionId": "550sf1100-e29b-41d4-a716-446655440000",
  "productId" : "123asf-sfa-2a",
  "productQuantity": 2,
  "isPartialOrder": false // Indicates when some amount of quantity is available
}
```
* triggered by = Payment MS
* Listen by = Order MS, Notification MS

## Kafka design
* Single topic -> Yes order-topic. Each microservice will ignore the event that they don't want.
* Partitions ? -> Not yet decided.
* Consumer Groups for each microservice, so multiple instances/replicas of microservice can receive only one message.
* Application needs to ignore the message if they are not consumers of that message, and those eventTypes or eventName will be configurable outside code.

## Questions and Considerations
* What specific information will be included in the event published to Kafka by the Payment MS? -> Check events mentioned above.
* Once the Order MS receives the event, how will it update the order status, and what other actions will it perform? -> Check event mentioned above.
* Similarly, when the Inventory MS receives the event, how will it update the stock levels and ensure product availability?-> Using distributed locking.
* How will the system handle scenarios when the wallet does not have enough balance to complete the order? -> It will simple error throw to client and publish event with order fail.
* How you will handle system failure between payment ms and wallet ms ? -> We will use retry with exponential backoff logic, once limit of retry reached then we will send error to client. And we will mark order as fail.
* If inventory ms do not have item left -> Early fail will be bettter. Order MS will check in inventory ms if any stock available or not. (This is one option, also from user experience there will be a API call will trigger, before user click on place order. Which will tell stock is available or not.)
* Why we discarded approach of separte flow of order placed and payment ? -> To avoid incosistency of payment faliure, and get early failback. When user click on place order with payment mode frontend send request to order ms with payment type(wallet, upi). Using this we can avoid incosistency.
* How inventory can be consistent in multiple replicas of inventory MS ? -> We need to use distributed locking.

