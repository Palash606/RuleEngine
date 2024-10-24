# Rule Engine

## Overview
The **Rule Engine** is a Spring Boot application that allows users to define, combine, and evaluate rules using an Abstract Syntax Tree (AST). It includes a REST API to interact with the rules, where users can create new rules, combine multiple rules into one, and evaluate them based on provided data attributes.

## Features
- **Create Rule**: Users can define rules as a string, which are parsed and converted into a tree structure (AST).
- **Combine Rules**: Multiple rules can be combined into a single rule by combining their respective ASTs.
- **Evaluate Rule**: Evaluate rules dynamically based on provided data and check if the conditions are satisfied.

## Technologies Used
- **Spring Boot**: Backend framework to develop REST APIs.
- **Jakarta Persistence (JPA)**: For persistence and ORM functionality.
- **Spring Security**: For securing the application (Basic authentication).
- **Lombok**: To reduce boilerplate code for entities.
- **Maven**: Build automation tool.
- **H2 Database**: In-memory database for development and testing.


## Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/Palash606/RuleEngine.git
   cd RuleEngine
   
2. **Build the project**

   ```bash
   mvn clean install

3. **Run the application**
    
    ```bash
   mvn spring-boot:run

## API Endpoints

### 1. **Create Rule**
- **URL**: `/api/rules/create`
- **Method**: `POST`
- **Description**: Creates a new rule from the provided rule string and returns the root node.
- **Request Body**:
  ```json
  {
    "ruleString": "your_rule_string_here"
  }
  ```
- **Response**:
    - **Status**: `200 OK`
    - **Body**:
      ```json
      {
        "node": {
          "type": "root",
          "value": "your_rule_string_here",
          "children": []
        }
      }
      ```
- **Example**:
  ```bash
  curl -X POST http://localhost:8080/api/rules/create \
  -H "Content-Type: application/json" \
  -d '{"ruleString": "your_rule_string_here"}'
  ```

---

### 2. **Combine Rules**
- **URL**: `/api/rules/combine`
- **Method**: `POST`
- **Description**: Combines multiple rules into a single root node.
- **Request Body**:
  ```json
  [
    "ruleString1",
    "ruleString2"
  ]
  ```
- **Response**:
    - **Status**: `200 OK`
    - **Body**: Returns the combined rule as a node tree.
  ```json
  {
    "node": {
      "type": "root",
      "value": "AND",
      "children": [
        { "type": "rule", "value": "ruleString1" },
        { "type": "rule", "value": "ruleString2" }
      ]
    }
  }
  ```
- **Example**:
  ```bash
  curl -X POST http://localhost:8080/api/rules/combine \
  -H "Content-Type: application/json" \
  -d '["ruleString1", "ruleString2"]'
  ```

---

### 3. **Evaluate Rule**
- **URL**: `/api/rules/evaluate`
- **Method**: `POST`
- **Description**: Evaluates the provided rule against a set of attributes and returns a boolean result.
- **Request Body**:
  ```json
  {
    "ruleString": "your_rule_string_here",
    "attributes": {
      "attribute1": "value1",
      "attribute2": "value2"
    }
  }
  ```
- **Response**:
    - **Status**: `200 OK`
    - **Body**:
      ```json
      {
        "result": true
      }
      ```
- **Example**:
  ```bash
  curl -X POST http://localhost:8080/api/rules/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "ruleString": "some_rule_string",
    "attributes": {
      "age": 30,
      "country": "USA"
    }
  }'
  ```

---

### 4. **Login (Basic Auth)**
- **URL**: `/login`
- **Method**: `POST`
- **Description**: Allows user authentication using basic authentication.
- **Headers**:
  ```bash
  Authorization: Basic base64(username:password)
  ```
- **Response**:
    - **Status**: `200 OK` on successful authentication.
    - **Example**:
  ```bash
  curl -X POST http://localhost:8080/login \
  -u user:password
  ```

---

### 5. **Access H2 Database Console**
- **URL**: `/h2-console`
- **Method**: `GET`
- **Description**: Access the H2 in-memory database console for development.
- **Note**: Available only during development. Ensure `spring.h2.console.enabled=true` is set in the `application.properties`.
- **Example**:
  Open the browser and navigate to `http://localhost:8080/h2-console`.

---

## Authentication and Authorization

- **User Authentication**: The application uses **Spring Security** for basic authentication with two predefined users:
    - Username: `user`, Password: `password` (role: USER)
    - Username: `admin`, Password: `password` (roles: ADMIN, USER)

- **Access Control**:
    - Any user can access the `/api/rules/**` endpoints after authentication.
    - Use the admin credentials for access to more secure endpoints, if applicable.


