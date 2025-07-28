# RWE Platform (Real-World Efficiency Platform)

## Overview
A Spring Boot application for vehicle route, distance, and weather calculation with user authentication and history tracking.

## Recent Fixes (Latest Update)
- ✅ **Fixed Database Schema**: Updated schema.sql to match Vehicle entity structure
- ✅ **Completed Settings Functionality**: Implemented email and password update features
- ✅ **Improved Security**: Added environment variable support for sensitive data
- ✅ **Enhanced Logging**: Replaced System.out.println with proper logging
- ✅ **Added Error Handling**: Global exception handler for better error management
- ✅ **Added Test Structure**: Basic test setup with H2 in-memory database

## Tech Stack
- Java 17
- Spring Boot (Web, Data JPA, Security, Validation)
- MySQL (Production) / H2 (Testing)
- JWT (HTTP-only cookie)
- Bootstrap 5, HTML/CSS/JS
- Thymeleaf

## Setup Instructions

### 1. Clone & Build
```shell
mvn clean package
```

### 2. Configure Database & API Keys
Edit `src/main/resources/application.properties` or set environment variables:

**Option A: Environment Variables (Recommended for Production)**
```bash
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret
export GOOGLE_MAPS_API_KEY=your_google_maps_api_key
export OPENWEATHER_API_KEY=your_openweather_api_key
```

**Option B: Direct Configuration**
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
java -jar target/realworldefficiency-0.0.1-SNAPSHOT.jar
```

### 5. Access
Visit [http://localhost:8081](http://localhost:8081).
- Unauthenticated users are redirected to `/login`.
- After login, users are redirected to `/home`.
- Navbar and authenticated UI are only visible when logged in.
- Logout invalidates the JWT and redirects to `/login`.

## Testing
Run tests with:
```shell
mvn test
```

The application includes:
- Basic context loading tests
- H2 in-memory database for testing
- Test-specific configuration

## Folder Structure
```
real-efficiency-platform/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com.yourorg.realworldefficiency
    │   │       ├── config         # JWT Security config
    │   │       ├── controller     # Auth, Vehicle, Route, History controllers
    │   │       ├── dto            # Request/response DTOs
    │   │       ├── entity         # User, Vehicle, History entities
    │   │       ├── repository     # JPA repositories
    │   │       ├── service        # Services for business logic
    │   │       └── RealWorldEfficiencyApplication.java
    │   └── resources
    │       ├── static/assets      # JS/CSS/Images
    │       ├── templates         # HTML files: login.html, register.html, home.html, dashboard.html, etc.
    │       │   └── fragments     # navbar.html, footer.html
    │       ├── application.properties
    │       └── schema.sql
    └── test
        ├── java
        │   └── com.yourorg.realworldefficiency
        │       └── RealWorldEfficiencyApplicationTests.java
        └── resources
            └── application-test.properties
```

## Coding Standards
- Java 17, Spring Boot best practices
- Lombok annotations (optional)
- Exception handling (@ControllerAdvice)
- Javadoc for public methods/services
- Descriptive naming, 4-space indentation, structured and clean codebase
- Proper logging with SLF4J
- Environment variable support for configuration 