# RouteWeatherDashboard

## Overview
A Spring Boot application for vehicle route, distance, and weather calculation with user authentication and history tracking.

## Tech Stack
- Java 17
- Spring Boot (Web, Data JPA, Security, Validation)
- MySQL
- JWT (HTTP-only cookie)
- Bootstrap 5, HTML/CSS/JS

## Setup Instructions

### 1. Clone & Build
```shell
mvn clean package
```

### 2. Configure Database & API Keys
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yourdb
spring.datasource.username=root
spring.datasource.password=password

jwt.secret=YOUR_JWT_SECRET
google.api.key=YOUR_GOOGLE_MAPS_API_KEY
openweather.api.key=YOUR_OPENWEATHER_API_KEY
```

### 3. Database Setup
Import the provided `schema.sql` into your MySQL database (e.g., via MySQL Workbench):
```sql
-- See src/main/resources/schema.sql
```

### 4. Run the Application
```shell
java -jar target/routedashboard-0.0.1-SNAPSHOT.jar
```

### 5. Access
Visit [http://localhost:8080](http://localhost:8080). Unauthenticated users are redirected to `/login`.

---

## Folder Structure
```
RouteWeatherDashboard/
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── com.yourorg.routedashboard
        │       ├── config         # JWT Security config
        │       ├── controller     # Auth, Vehicle, Route, History controllers
        │       ├── dto            # Request/response DTOs
        │       ├── entity         # User, Vehicle, History entities
        │       ├── repository     # JPA repositories
        │       ├── service        # Services for business logic
        │       └── RouteDashboardApplication.java
        └── resources
            ├── static            # JS/CSS client-side files
            ├── templates         # HTML files: login.html, register.html, dashboard.html
            ├── application.properties
            └── schema.sql
```

---

## Coding Standards
- Java 17, Spring Boot best practices
- Lombok annotations (optional)
- Exception handling (@ControllerAdvice)
- Javadoc for public methods/services
- Descriptive naming, 4-space indentation, structured and clean codebase 