
Test Scenario :

Search by Customer ID	GET /api/transactions?customerId=222

Search by Account Number	GET /api/transactions?accountNumber=8872838283

Search by Description	GET /api/transactions?description=FUND TRANSFER

Combined Filters	GET /api/transactions?customerId=222&description=BILL PAYMENT




Database Schema Design:

DDL- SQL:

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    accountNumber VARCHAR(255) NOT NULL,
    trxAmount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    trxDate DATE NOT NULL,
    trxTime TIME NOT NULL,
    customerId VARCHAR(255) NOT NULL,
    version BIGINT DEFAULT 0  -- For optimistic locking
);



id: Primary key, auto-generated.
accountNumber: Account number (String).
trxAmount: Transaction amount (Decimal).
description: Transaction description (String).
trxDate: Transaction date (Date).
trxTime: Transaction time (Time).
customerId: Customer ID (String).
version: Version field for optimistic locking (Integer). This is crucial for handling concurrent updates.










Design Patterns Used.

1. Repository Pattern :
* The Repository Pattern abstracts the data access logic. It provides a clean interface for interacting with the database, shielding the rest of the application (like the TransactionService) from the complexities of database queries, connections, and specific persistence mechanisms (like JPA).
* It promotes loose coupling and testability.
* Spring Data JPA provides a convenient implementation of this pattern, generating basic CRUD (Create, Read, Update, Delete) methods automatically. 


2. Service Layer Pattern
* The TransactionService class implements the Service Layer Pattern.
* The Service Layer sits between the controller (TransactionController) and the repository (TransactionRepository). It encapsulates the application's business logic.

* The Service Layer can handle:
 	- Data validation
	- Business rules
	- Transaction management (using @Transactional)
	- Coordination of multiple repository calls
	- Exception handling related to business logic

This pattern improves the organization, testability, and maintainability of the application.

3. Front Controller Pattern (Spring MVC)

 	- Spring MVC's DispatcherServlet acts as the Front Controller.
 	- The Front Controller handles all incoming requests to the application.
	- It dispatches requests to the appropriate controllers (TransactionController in our case).
	- It provides common services like request parsing, authentication, and view resolution.
	- Spring MVC's DispatcherServlet is a central component of this pattern, simplifying request handling.

4. Dependency Injection (DI)

	- DI is a fundamental principle in Spring. Instead of objects creating their dependencies, those dependencies are "injected" into the objects.
	- @Autowired is Spring's way of marking a constructor, field, or setter method where a dependency should be injected.
     


Class Descriptions :- 
Transaction: Entity class with fields from datasource.txt and version for optimistic locking.
TransactionRepository: Spring Data JPA interface for querying transactions.
TransactionService: Contains business logic for fetching and updating transactions.
TransactionController: REST controller handling GET/PUT requests.
BatchConfig: Configures the Spring Batch job
SecurityConfig: Configures security (Basic Auth, H2 console access).
GlobalExceptionHandler: Handles ConcurrentUpdateException and returns 409 Conflict. 

 
Activity Descriptions:
Batch Job:
Reads datasource.txt.
Processes and writes transactions to the database.
PUT Request:
Checks authentication.
Fetches and updates the transaction.
Uses optimistic locking (version field) to handle concurrent updates.
Returns appropriate HTTP status codes (200 OK, 409 Conflict, 404 Not Found, 401 Unauthorized).
