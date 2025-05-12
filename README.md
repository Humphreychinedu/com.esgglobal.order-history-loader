Order History Loader

This is a Spring Boot application for loading and managing order history data. It supports loading orders from JSON files, computing order summaries, and exposing REST APIs with Swagger documentation.

✅ Prerequisites

Before running the project, ensure the following tools are installed:

Tool

Version / Command

Java

JDK 17+ (java -version)

Maven

3.8+ (mvn -version)

Git

(git --version)

IDE (optional)

IntelliJ IDEA / VSCode

🚀 Steps to Run the Application

1. 🔁 Clone the Repository

git clone https://github.com/Humphreychinedu/com.esgglobal.order-history-loader.git
cd com.esgglobal.order-history-loader

2. 📆 Build the Project

Use Maven to build the project:

mvn clean install

This will download dependencies, compile the code, run tests, and package the application.

3. ▶️ Run the Spring Boot App

You can run the application using:

mvn spring-boot:run

OR using the compiled JAR:

java -jar target/order-history-loader-0.0.1-SNAPSHOT.jar

🌐 API Documentation (Swagger)

Once the app is running, navigate to:

http://localhost:8080/swagger-ui/index.html

You’ll find complete documentation and can test all REST endpoints interactively.

📄 In-Memory Database (H2)

You can access the H2 database console at:

http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb

Username: sa

Password: (leave empty)

📁 Sample JSON File for Upload

The orders.json file is located in:

src/main/resources/orders.json

This file is automatically loaded when calling the relevant endpoint.

🔀 Common API Endpoints

Endpoint

Description
POST /api/v1/order/load Loads data from api request

GET /api/v1/orders/file Load orders from file

GET /api/v1/order/{orderId}/details Get order items by order ID

GET /api/v1/order/{orderId}/summary Get order summary by order ID

GET /api/v1/orders/details Get all order details

GET /api/v1/orders/summary Get total orders and shipped revenue summary

🔪 Running Tests

To execute unit tests:

mvn test

📞 Support

For issues or contributions, please open an issue or a pull request on GitHub.

