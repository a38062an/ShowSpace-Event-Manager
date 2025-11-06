# ShowSpace - Event and Venue Management Platform# ShowSpace - Event and Venue Management Platform# ShowSpace - Event and Venue Management Platform# ShowSpace - Event and Venue Management Platform# ShowSpace - Event and Venue Management Platform# COMP23412 EventLite



A full-stack event and venue management web application built with Spring Boot, demonstrating enterprise-level software engineering practices including MVC architecture, RESTful API design with HATEOAS, role-based security, and external API integration.



## About This ProjectA full-stack event and venue management web application built with Spring Boot, demonstrating enterprise-level software engineering practices including MVC architecture, RESTful API design with HATEOAS, role-based security, and external API integration.



This project originated as team coursework for COMP23412 (Software Engineering) at the University of Manchester, where I served as **team lead for a 7-person development team**. I was recognized as the **most contributing team member** for exceptional performance in coordinating the team and delivering critical features.



The codebase has been significantly refactored and enhanced for this portfolio presentation:## About This ProjectA full-stack event and venue management web application built with Spring Boot, demonstrating enterprise-level software engineering practices including MVC architecture, RESTful API design with HATEOAS, role-based security, and external API integration.A full-stack event and venue management web application built with Spring Boot, demonstrating enterprise-level software engineering practices including MVC architecture, RESTful API design with HATEOAS, role-based security, and external API integration.## COMP23412 Software Engineering, University of Manchester, UK

- Complete rebranding from EventLite to ShowSpace

- Improved UI/UX with custom CSS styling

- Enhanced code quality and removed debug statements

- Restructured package naming conventionA professional event and venue management system developed as part of a team project. The application showcases modern Java development practices, Spring Framework expertise, and full-stack web application architecture. The system handles event scheduling, venue management, user authentication, and provides both web and REST API interfaces.

- Added comprehensive documentation



While the core architecture reflects collaborative coursework, the refactoring, improvements, and presentation are my independent work.

## Technical Stack## About This Project## About This ProjectA full-stack web application for managing events and venues, built with Spring Boot and modern Java technologies. This project demonstrates enterprise-level software engineering practices including MVC architecture, RESTful API design with HATEOAS, role-based security, and external API integration.### Robert Haines, Markel Vigo, Mustafa Mustafa, Tom Carroll, Caroline Jay

## Technical Stack



### Backend

- Java 17 with Spring Boot 3.3.6### Backend

- Spring Data JPA with Hibernate ORM

- Spring Security 6 for authentication and authorization- Java 17 with Spring Boot 3.3.6

- Spring HATEOAS for hypermedia-driven REST API

- H2 Database (embedded, file-based persistence)- Spring Data JPA with Hibernate ORMThis project originated as coursework for COMP23412 at the University of Manchester, where I served as team lead for a 7-person development team. I was recognized as the most contributing team member and received credit for exceptional performance in coordinating the team and delivering critical features. The codebase has been refactored and rebranded to align with professional portfolio standards while preserving the architectural patterns and technical implementations developed during the coursework.This project is inspired by team coursework from COMP23412 (Software Engineering) at the University of Manchester, where I served as **team lead for a 7-person development team** and was recognized as the **most contributing team member** for exceptional performance in coordinating the team and delivering critical features. ## About This Project*This is the skeleton lab code for the team-based exercise.*

- Maven for dependency management and build automation

- Spring Security 6 for authentication and authorization

### Frontend

- Thymeleaf template engine for server-side rendering- Spring HATEOAS for hypermedia-driven REST API

- Bootstrap 5 for responsive UI components

- Font Awesome 6.7.1 for iconography- H2 Database (embedded, file-based persistence)

- WebJars for frontend dependency management

- Custom CSS for unique styling and UX improvements- Maven for dependency management and build automation## Technical StackThe codebase has been refactored, rebranded, and enhanced with additional features while preserving the core architectural patterns and technical implementations from the original coursework.This project originated as coursework for COMP23412 at the University of Manchester, where I served as **team lead for a 7-person development team**. I was recognized as the **most contributing team member** and received credit for exceptional performance in coordinating the team and delivering critical features. The codebase has been refactored and rebranded to align with professional portfolio standards while preserving the architectural patterns and technical implementations developed during the coursework.See the instructions in Blackboard for more details.



### External Integrations

- Mapbox SDK for geolocation and address geocoding

- Mastodon API for social media integration### Frontend

- Jackson for JSON serialization

- Thymeleaf template engine for server-side rendering

### Testing

- JUnit 5 for unit testing- Bootstrap 5 for responsive UI components### Backend## Technical Stack## Technical Stack#### Licence

- Spring MockMvc for integration testing

- Spring Security Test for security testing- Font Awesome 6.7.1 for iconography



## Architecture and Design Patterns- WebJars for frontend dependency management- Java 17 with Spring Boot 3.3.6



### MVC Architecture- Custom CSS for unique styling and UX improvements



The application follows a clean separation of concerns with distinct layers:- Spring Data JPA with Hibernate ORM### Backend### BackendBSD licenced. See Licence.md



- **Controllers**: Handle HTTP requests and responses### External Integrations

  - `EventsController` / `VenueController` - Web interface

  - `EventsControllerApi` / `VenuesControllerApi` - REST API endpoints- Mapbox SDK for geolocation and address geocoding- Spring Security 6 for authentication and authorization

- **Service Layer**: Business logic and data validation

  - `EventService` / `VenueService` - Interface definitions- Mastodon API for social media integration

  - `EventServiceImpl` / `VenueServiceImpl` - Service implementations

- **Repository Layer**: Data access using Spring Data JPA- Jackson for JSON serialization- Spring HATEOAS for hypermedia-driven REST API- **Java 17** with Spring Boot 3.3.6

  - `EventRepository` / `VenueRepository` - Database operations

- **Entities**: JPA entities with Bean Validation

  - `Event` / `Venue` - Domain models

### Testing- H2 Database (embedded, file-based persistence)

### Repository Pattern

- JUnit 5 for unit testing

Abstracts data access logic using Spring Data JPA repositories with custom query methods using method name conventions.

- Spring MockMvc for integration testing- Maven for dependency management and build automation- **Spring Data JPA** with Hibernate ORM- **Java 17** with Spring Boot 3.3.6

### HATEOAS (Level 3 REST Maturity)

- Spring Security Test for security testing

REST API responses include hypermedia links, enabling API discoverability and reducing client coupling:



```json

{## Architecture and Design Patterns

  "name": "Spring Workshop",

  "_links": {### Frontend- **Spring Security 6** for authentication and authorization- **Spring Data JPA** with Hibernate ORM

    "self": { "href": "/api/events/1" },

    "venue": { "href": "/api/venues/5" }### MVC Architecture

  }

}- Thymeleaf template engine for server-side rendering

```

The application follows a clean separation of concerns with distinct layers:

## Key Features

- Bootstrap 5 for responsive UI components- **Spring HATEOAS** for hypermedia-driven REST API- **Spring Security 6** for authentication and authorization

### Security Implementation

- Role-Based Access Control (RBAC) with Spring Security- Controllers: Handle HTTP requests and responses

- Form-based authentication for web interface

- HTTP Basic authentication for REST API  - EventsController / VenueController - Web interface- Font Awesome 6.7.1 for iconography

- CSRF protection for web requests (disabled for stateless API)

- Secure password encryption using BCrypt  - EventsControllerApi / VenuesControllerApi - REST API endpoints

- Session management with configurable timeout

- Service Layer: Business logic and data validation- WebJars for frontend dependency management- **H2 Database** (embedded, file-based persistence)- **Spring HATEOAS** for hypermedia-driven REST API

### Data Management

- CRUD operations for events and venues  - EventService / VenueService - Interface definitions

- Search functionality with pagination

- Data validation with Jakarta Validation API  - EventServiceImpl / VenueServiceImpl - Service implementations- Custom CSS for unique styling and UX improvements

- Bidirectional JPA relationships with cascade operations

- Automatic schema generation from entity models- Repository Layer: Data access using Spring Data JPA



### API Design  - EventRepository / VenueRepository - Database operations- **Maven** for dependency management and build automation- **H2 Database** (embedded, file-based persistence)

- RESTful endpoints following HTTP semantics

- HATEOAS with hypermedia links- Entities: JPA entities with Bean Validation

- Content negotiation (JSON/HTML)

- Resource assemblers for consistent response structure  - Event / Venue - Domain models### External Integrations

- Exception handling with appropriate HTTP status codes



### UI/UX

- Responsive design with Bootstrap grid system### Repository Pattern- Mapbox SDK for geolocation and address geocoding- **Maven** for dependency management and build automation

- Server-side rendering with Thymeleaf

- Template inheritance using layout dialect

- Conditional rendering based on user roles

- Custom styling for improved user experienceAbstracts data access logic using Spring Data JPA repositories with custom query methods using method name conventions.- Mastodon API for social media integration



## Project Structure



```### HATEOAS (Level 3 REST Maturity)- Jackson for JSON serialization### Frontend

showspace/

├── src/main/java/anthonynguyen/showspace/

│   ├── ShowSpaceApplication.java       # Main application entry point

│   ├── entities/                       # JPA entitiesREST API responses include hypermedia links, enabling API discoverability and reducing client coupling:

│   ├── dao/                            # Data access layer

│   │   ├── *Repository.java            # Spring Data repositories

│   │   ├── *Service.java               # Service interfaces

│   │   └── *ServiceImpl.java           # Service implementations```json### Testing- **Thymeleaf** template engine for server-side rendering### Frontend

│   ├── controllers/                    # MVC controllers

│   ├── assemblers/                     # HATEOAS resource assemblers{

│   ├── config/                         # Spring configuration

│   │   ├── Security.java               # Security configuration  "name": "Spring Workshop",- JUnit 5 for unit testing

│   │   ├── Persistence.java            # Database configuration

│   │   ├── Hateoas.java                # API configuration  "_links": {

│   │   └── data/InitialDataLoader.java # Seed data

│   └── exceptions/                     # Custom exception classes    "self": { "href": "/api/events/1" },- Spring MockMvc for integration testing- **Bootstrap 5** for responsive UI components

├── src/main/resources/

│   ├── templates/                      # Thymeleaf HTML templates    "venue": { "href": "/api/venues/5" }

│   ├── static/css/                     # Custom stylesheets

│   ├── banner.txt                      # Application banner  }- Spring Security Test for security testing

│   └── *.properties                    # Configuration files

├── src/test/                           # Test sources}

├── db/                                 # H2 database files

└── pom.xml                             # Maven configuration```- **Font Awesome 6.7.1** for iconography- **Thymeleaf** template engine for server-side rendering

```



## Getting Started

## Key Features## Architecture and Design Patterns

### Prerequisites

- Java 17 or higher

- Maven 3.6+

### Security Implementation- **WebJars** for frontend dependency management- **Bootstrap 5** for responsive UI components

### Running the Application

- Role-Based Access Control (RBAC) with Spring Security

1. Clone the repository:

- Form-based authentication for web interface### MVC Architecture

```bash

git clone https://github.com/a38062an/showspace.git- HTTP Basic authentication for REST API

cd showspace

```- CSRF protection for web requests (disabled for stateless API)- **Custom CSS** for unique styling and UX improvements- **Font Awesome 6.7.1** for iconography



2. Build the project:- Secure password encryption using BCrypt



```bash- Session management with configurable timeoutThe application follows a clean separation of concerns with distinct layers:

mvn clean install

```



3. Run the application:### Data Management- **WebJars** for frontend dependency management



```bash- CRUD operations for events and venues

mvn spring-boot:run

```- Search functionality with pagination- Controllers: Handle HTTP requests and responses



4. Access the application:- Data validation with Jakarta Validation API

- Web Interface: http://localhost:8080

- REST API: http://localhost:8080/api- Bidirectional JPA relationships with cascade operations  - EventsController / VenueController - Web interface### External Integrations

- H2 Console: http://localhost:8080/h2-console

- Automatic schema generation from entity models

### Default Credentials

  - EventsControllerApi / VenuesControllerApi - REST API endpoints

For demonstration purposes, the application includes pre-configured admin accounts:

### API Design

| Username | Password | Role          |

|----------|----------|---------------|- RESTful endpoints following HTTP semantics- Service Layer: Business logic and data validation- **Mapbox SDK** for geolocation and address geocoding### External Integrations

| Rob      | Haines   | Administrator |

| Caroline | Jay      | Administrator |- HATEOAS with hypermedia links

| Markel   | Vigo     | Administrator |

| Mustafa  | Mustafa  | Administrator |- Content negotiation (JSON/HTML)  - EventService / VenueService - Interface definitions

| Tom      | Carroll  | Administrator |

- Resource assemblers for consistent response structure

All users have full administrative privileges including create, update, and delete operations. Public users can view events and venues without authentication.

- Exception handling with appropriate HTTP status codes  - EventServiceImpl / VenueServiceImpl - Service implementations- **Mastodon API** for social media integration

## Running Tests



Execute the test suite:

### UI/UX- Repository Layer: Data access using Spring Data JPA

```bash

mvn test- Responsive design with Bootstrap grid system

```

- Server-side rendering with Thymeleaf  - EventRepository / VenueRepository - Database operations- **Jackson** for JSON serialization- **Mapbox SDK** for geolocation and address geocoding

Run with detailed output:

- Template inheritance using layout dialect

```bash

mvn test -X- Conditional rendering based on user roles- Entities: JPA entities with Bean Validation

```

- Custom styling for improved user experience

## Configuration

  - Event / Venue - Domain models- **Mastodon API** for social media integration

### Database Configuration

## Project Structure

The application uses H2 database with file-based persistence. Configuration in `Persistence.java`:

- Database file: `db/showspace-dev.mv.db`

- Console access enabled for debugging

- Connection: `jdbc:h2:./db/showspace-dev````



### External API Configurationshowspace/### Repository Pattern### Testing- **Jackson** for JSON serialization

- **Mapbox**: Configure access token in `mapbox-config.properties`

- **Mastodon**: Configure credentials in `mastodon-config.properties`├── src/main/java/anthonynguyen/showspace/



## API Documentation│   ├── ShowSpaceApplication.java       # Main application entry point



### Events Endpoints│   ├── entities/                       # JPA entities

- `GET /api/events` - List all events

- `GET /api/events/{id}` - Get event details│   ├── dao/                            # Data access layerAbstracts data access logic using Spring Data JPA repositories with custom query methods using method name conventions.- **JUnit 5** for unit testing

- `POST /api/events` - Create new event (Admin only)

- `PUT /api/events/{id}` - Update event (Admin only)│   │   ├── *Repository.java            # Spring Data repositories

- `DELETE /api/events/{id}` - Delete event (Admin only)

│   │   ├── *Service.java               # Service interfaces

### Venues Endpoints

- `GET /api/venues` - List all venues│   │   └── *ServiceImpl.java           # Service implementations

- `GET /api/venues/{id}` - Get venue details

- `POST /api/venues` - Create new venue (Admin only)│   ├── controllers/                    # MVC controllers### HATEOAS (Level 3 REST Maturity)- **Spring MockMvc** for integration testing### Testing

- `PUT /api/venues/{id}` - Update venue (Admin only)

- `DELETE /api/venues/{id}` - Delete venue (Admin only)│   ├── assemblers/                     # HATEOAS resource assemblers



## Key Learning Outcomes│   ├── config/                         # Spring configuration



### Technical Skills Demonstrated│   │   ├── Security.java               # Security configuration

- Full-stack development with Java and Spring Boot ecosystem

- RESTful API design and implementation with HATEOAS│   │   ├── Persistence.java            # Database configurationREST API responses include hypermedia links, enabling API discoverability and reducing client coupling:- **Spring Security Test** for security testing

- Authentication and authorization with Spring Security

- ORM and database design with JPA/Hibernate│   │   ├── Hateoas.java                # API configuration

- Server-side rendering with Thymeleaf

- Test-driven development with JUnit and MockMvc│   │   └── data/InitialDataLoader.java # Seed data

- Dependency injection and IoC container patterns

- Maven project management and build automation│   └── exceptions/                     # Custom exception classes



### Software Engineering Practices├── src/main/resources/```json- **JUnit 5** for unit testing

- MVC architectural pattern

- Repository and Service patterns│   ├── templates/                      # Thymeleaf HTML templates

- Separation of concerns

- Configuration management│   ├── static/css/                     # Custom stylesheets{

- Version control with Git

- Team collaboration and leadership│   ├── banner.txt                      # Application banner



## License│   └── *.properties                    # Configuration files  "name": "Spring Workshop",## Architecture and Design Patterns- **Spring MockMvc** for integration testing



This project builds upon university coursework. The original coursework specifications are property of the University of Manchester. This repository demonstrates my refactoring, enhancements, and independent development work for portfolio purposes.├── src/test/                           # Test sources



## Contact├── db/                                 # H2 database files  "_links": {



**Anthony Nguyen**└── pom.xml                             # Maven configuration

- GitHub: [@a38062an](https://github.com/a38062an)

- University: University of Manchester, BSc Computer Science```    "self": { "href": "/api/events/1" },- **Spring Security Test** for security testing



---



*Originally developed as part of COMP23412 team coursework, significantly refactored and enhanced for portfolio presentation. Demonstrates proficiency in enterprise Java development, Spring Framework, and full-stack web application architecture.*## Getting Started    "venue": { "href": "/api/venues/5" }




### Prerequisites  }### MVC Architecture

- Java 17 or higher

- Maven 3.6+}



### Running the Application```The application follows a clean separation of concerns with distinct layers:## Architecture and Design Patterns



1. Clone the repository:



```bash## Key Features- **Controllers**: Handle HTTP requests and responses### MVC Architecture

git clone https://github.com/a38062an/showspace.git

cd showspace

```

### Security Implementation  - `EventsController` / `VenueController` - Web interface

2. Build the project:

- Role-Based Access Control (RBAC) with Spring Security

```bash

mvn clean install- Form-based authentication for web interface  - `EventsControllerApi` / `VenuesControllerApi` - REST API endpointsThe application follows a clean separation of concerns with distinct layers:

```

- HTTP Basic authentication for REST API

3. Run the application:

- CSRF protection for web requests (disabled for stateless API)- **Service Layer**: Business logic and data validation- **Controllers**: Handle HTTP requests and responses

```bash

mvn spring-boot:run- Secure password encryption using BCrypt

```

- Session management with configurable timeout  - `EventService` / `VenueService` - Interface definitions  - `EventsController` / `VenueController` - Web interface

4. Access the application:

- Web Interface: http://localhost:8080

- REST API: http://localhost:8080/api

- H2 Console: http://localhost:8080/h2-console### Data Management  - `EventServiceImpl` / `VenueServiceImpl` - Service implementations  - `EventsControllerApi` / `VenuesControllerApi` - REST API endpoints



### Default Credentials- CRUD operations for events and venues



For demonstration purposes, the application includes pre-configured admin accounts:- Search functionality with pagination- **Repository Layer**: Data access using Spring Data JPA- **Service Layer**: Business logic and data validation



| Username | Password | Role          |- Data validation with Jakarta Validation API

|----------|----------|---------------|

| Rob      | Haines   | Administrator |- Bidirectional JPA relationships with cascade operations  - `EventRepository` / `VenueRepository` - Database operations  - `EventService` / `VenueService` - Interface definitions

| Caroline | Jay      | Administrator |

| Markel   | Vigo     | Administrator |- Automatic schema generation from entity models

| Mustafa  | Mustafa  | Administrator |

| Tom      | Carroll  | Administrator |    - `EventServiceImpl` / `VenueServiceImpl` - Service implementations



All users have full administrative privileges including create, update, and delete operations. Public users can view events and venues without authentication.### API Design



## Running Tests- RESTful endpoints following HTTP semantics- **Entities**: JPA entities with Bean Validation  



Execute the test suite:- HATEOAS with hypermedia links



```bash- Content negotiation (JSON/HTML)  - `Event` / `Venue` - Domain models- **Repository Layer**: Data access using Spring Data JPA

mvn test

```- Resource assemblers for consistent response structure



Run with detailed output:- Exception handling with appropriate HTTP status codes  - `EventRepository` / `VenueRepository` - Database operations



```bash

mvn test -X

```### UI/UX### Repository Pattern  



## Configuration- Responsive design with Bootstrap grid system



### Database Configuration- Server-side rendering with ThymeleafAbstracts data access logic using Spring Data JPA repositories with custom query methods using method name conventions.- **Entities**: JPA entities with Bean Validation



The application uses H2 database with file-based persistence. Configuration in Persistence.java:- Template inheritance using layout dialect

- Database file: db/showspace-dev.mv.db

- Console access enabled for debugging- Conditional rendering based on user roles- `Event` / `Venue` - Domain models

- Connection: jdbc:h2:./db/showspace-dev

- Custom styling for improved user experience

### External API Configuration

- Mapbox: Configure access token in mapbox-config.properties### HATEOAS (Level 3 REST Maturity)

- Mastodon: Configure credentials in mastodon-config.properties

## Project Structure

## API Documentation

REST API responses include hypermedia links, enabling API discoverability and reducing client coupling:### Repository Pattern

### Events Endpoints

- GET /api/events - List all events```

- GET /api/events/{id} - Get event details

- POST /api/events - Create new event (Admin only)showspace/```jsonAbstracts data access logic using Spring Data JPA repositories with custom query methods using method name conventions.

- PUT /api/events/{id} - Update event (Admin only)

- DELETE /api/events/{id} - Delete event (Admin only)├── src/main/java/anthonynguyen/showspace/



### Venues Endpoints│   ├── ShowSpaceApplication.java       # Main application entry point{

- GET /api/venues - List all venues

- GET /api/venues/{id} - Get venue details│   ├── entities/                       # JPA entities

- POST /api/venues - Create new venue (Admin only)

- PUT /api/venues/{id} - Update venue (Admin only)│   ├── dao/                            # Data access layer  "name": "Spring Workshop",### HATEOAS (Level 3 REST Maturity)

- DELETE /api/venues/{id} - Delete venue (Admin only)

│   │   ├── *Repository.java            # Spring Data repositories

## Key Learning Outcomes

│   │   ├── *Service.java               # Service interfaces  "_links": {

### Technical Skills Demonstrated

- Full-stack development with Java and Spring Boot ecosystem│   │   └── *ServiceImpl.java           # Service implementations

- RESTful API design and implementation with HATEOAS

- Authentication and authorization with Spring Security│   ├── controllers/                    # MVC controllers    "self": { "href": "/api/events/1" },REST API responses include hypermedia links, enabling API discoverability and reducing client coupling:

- ORM and database design with JPA/Hibernate

- Server-side rendering with Thymeleaf│   ├── assemblers/                     # HATEOAS resource assemblers

- Test-driven development with JUnit and MockMvc

- Dependency injection and IoC container patterns│   ├── config/                         # Spring configuration    "venue": { "href": "/api/venues/5" }

- Maven project management and build automation

│   │   ├── Security.java               # Security configuration

### Software Engineering Practices

- MVC architectural pattern│   │   ├── Persistence.java            # Database configuration  }```json

- Repository and Service patterns

- Separation of concerns│   │   ├── Hateoas.java                # API configuration

- Configuration management

- Version control with Git│   │   └── data/InitialDataLoader.java # Seed data}{

- Team collaboration and leadership

│   └── exceptions/                     # Custom exception classes

## Contact

├── src/main/resources/```  "name": "Spring Workshop",

Anthony Nguyen

- GitHub: @a38062an│   ├── templates/                      # Thymeleaf HTML templates

- University: University of Manchester, BSc Computer Science

│   ├── static/css/                     # Custom stylesheets  "_links": {

---

│   ├── banner.txt                      # Application banner

Built with Spring Boot and modern Java technologies. Demonstrates proficiency in enterprise Java development, Spring Framework, and full-stack web application architecture.

│   └── *.properties                    # Configuration files## Key Features    "self": { "href": "/api/events/1" },

├── src/test/                           # Test sources

├── db/                                 # H2 database files    "venue": { "href": "/api/venues/5" }

└── pom.xml                             # Maven configuration

```### Security Implementation  }



## Getting Started- **Role-Based Access Control (RBAC)** with Spring Security}



### Prerequisites- Form-based authentication for web interface```

- Java 17 or higher

- Maven 3.6+- HTTP Basic authentication for REST API



### Running the Application- CSRF protection for web requests (disabled for stateless API)## Key Features



1. Clone the repository:- Secure password encryption using BCrypt



```bash- Session management with configurable timeout### Security Implementation

git clone https://github.com/a38062an/showspace.git

cd showspace

```

### Data Management- **Role-Based Access Control (RBAC)** with Spring Security

2. Build the project:

- CRUD operations for events and venues- Form-based authentication for web interface

```bash

mvn clean install- Search functionality with pagination- HTTP Basic authentication for REST API

```

- Data validation with Jakarta Validation API- CSRF protection for web requests (disabled for stateless API)

3. Run the application:

- Bidirectional JPA relationships with cascade operations- Secure password encryption using BCrypt

```bash

mvn spring-boot:run- Automatic schema generation from entity models- Session management with configurable timeout

```



4. Access the application:

- Web Interface: http://localhost:8080### API Design### Data Management

- REST API: http://localhost:8080/api

- H2 Console: http://localhost:8080/h2-console- RESTful endpoints following HTTP semantics



### Default Credentials- HATEOAS with hypermedia links- CRUD operations for events and venues



For demonstration purposes, the application includes pre-configured admin accounts:- Content negotiation (JSON/HTML)- Search functionality with pagination



| Username | Password | Role          |- Resource assemblers for consistent response structure- Data validation with Jakarta Validation API

|----------|----------|---------------|

| Rob      | Haines   | Administrator |- Exception handling with appropriate HTTP status codes- Bidirectional JPA relationships with cascade operations

| Caroline | Jay      | Administrator |

| Markel   | Vigo     | Administrator |- Automatic schema generation from entity models

| Mustafa  | Mustafa  | Administrator |

| Tom      | Carroll  | Administrator |### UI/UX



All users have full administrative privileges including create, update, and delete operations. Public users can view events and venues without authentication.- Responsive design with Bootstrap grid system### API Design



## Running Tests- Server-side rendering with Thymeleaf



Execute the test suite:- Template inheritance using layout dialect- RESTful endpoints following HTTP semantics



```bash- Conditional rendering based on user roles- HATEOAS with hypermedia links

mvn test

```- Custom styling for improved user experience- Content negotiation (JSON/HTML)



Run with detailed output:- Resource assemblers for consistent response structure



```bash## Project Structure- Exception handling with appropriate HTTP status codes

mvn test -X

```



## Configuration```### UI/UX



### Database Configurationshowspace/



The application uses H2 database with file-based persistence. Configuration in Persistence.java:├── src/main/java/anthonynguyen/showspace/- Responsive design with Bootstrap grid system

- Database file: db/showspace-dev.mv.db

- Console access enabled for debugging│   ├── ShowSpaceApplication.java       # Main application entry point- Server-side rendering with Thymeleaf

- Connection: jdbc:h2:./db/showspace-dev

│   ├── entities/                       # JPA entities- Template inheritance using layout dialect

### External API Configuration

- Mapbox: Configure access token in mapbox-config.properties│   ├── dao/                            # Data access layer- Conditional rendering based on user roles

- Mastodon: Configure credentials in mastodon-config.properties

│   │   ├── *Repository.java            # Spring Data repositories- Font Awesome icons for visual enhancement

## API Documentation

│   │   ├── *Service.java               # Service interfaces

### Events Endpoints

- GET /api/events - List all events│   │   └── *ServiceImpl.java           # Service implementations## Project Structure

- GET /api/events/{id} - Get event details

- POST /api/events - Create new event (Admin only)│   ├── controllers/                    # MVC controllers

- PUT /api/events/{id} - Update event (Admin only)

- DELETE /api/events/{id} - Delete event (Admin only)│   ├── assemblers/                     # HATEOAS resource assemblers```



### Venues Endpoints│   ├── config/                         # Spring configurationshowspace/

- GET /api/venues - List all venues

- GET /api/venues/{id} - Get venue details│   │   ├── Security.java               # Security configuration├── src/main/java/anthonynguyen/showspace/

- POST /api/venues - Create new venue (Admin only)

- PUT /api/venues/{id} - Update venue (Admin only)│   │   ├── Persistence.java            # Database configuration│   ├── ShowSpaceApplication.java       # Main application entry point

- DELETE /api/venues/{id} - Delete venue (Admin only)

│   │   ├── Hateoas.java                # API configuration│   ├── entities/                       # JPA entities

## Key Learning Outcomes

│   │   └── data/InitialDataLoader.java # Seed data│   ├── dao/                            # Data access layer

### Technical Skills Demonstrated

- Full-stack development with Java and Spring Boot ecosystem│   └── exceptions/                     # Custom exception classes│   │   ├── *Repository.java            # Spring Data repositories

- RESTful API design and implementation with HATEOAS

- Authentication and authorization with Spring Security├── src/main/resources/│   │   ├── *Service.java               # Service interfaces

- ORM and database design with JPA/Hibernate

- Server-side rendering with Thymeleaf│   ├── templates/                      # Thymeleaf HTML templates│   │   └── *ServiceImpl.java           # Service implementations

- Test-driven development with JUnit and MockMvc

- Dependency injection and IoC container patterns│   ├── static/                         # CSS, JS, images│   ├── controllers/                    # MVC controllers

- Maven project management and build automation

│   ├── banner.txt                      # Application banner│   ├── assemblers/                     # HATEOAS resource assemblers

### Software Engineering Practices

- MVC architectural pattern│   └── *.properties                    # Configuration files│   ├── config/                         # Spring configuration

- Repository and Service patterns

- Separation of concerns├── src/test/                           # Test sources│   │   ├── Security.java               # Security configuration

- Configuration management

- Version control with Git├── db/                                 # H2 database files│   │   ├── Persistence.java            # Database configuration

- Team collaboration and leadership

└── pom.xml                             # Maven configuration│   │   ├── Hateoas.java                # API configuration

## License

```│   │   └── data/InitialDataLoader.java # Seed data

This project is a refactored version of university coursework. The original coursework specifications and requirements are property of the University of Manchester. This repository is intended for portfolio demonstration purposes.

│   └── exceptions/                     # Custom exception classes

## Contact

## Getting Started├── src/main/resources/

Anthony Nguyen

- GitHub: @a38062an│   ├── templates/                      # Thymeleaf HTML templates

- University: University of Manchester, BSc Computer Science

### Prerequisites│   ├── banner.txt                      # Application banner

---

- Java 17 or higher│   └── *.properties                    # Configuration files

Developed as part of COMP23412 coursework at the University of Manchester, refactored and rebranded for portfolio presentation. Demonstrates proficiency in enterprise Java development, Spring Framework, and full-stack web application architecture.

- Maven 3.6+├── src/test/                           # Test sources

├── db/                                 # H2 database files

### Running the Application└── pom.xml                             # Maven configuration

```

1. Clone the repository:

```bash## Getting Started

git clone https://github.com/a38062an/showspace.git

cd showspace### Prerequisites

```

- Java 17 or higher

2. Build the project:- Maven 3.6+

```bash

mvn clean install### Running the Application

```

1. Clone the repository:

3. Run the application:

```bash```bash

mvn spring-boot:rungit clone <https://github.com/a38062an/showspace.git>

```cd showspace

```

4. Access the application:

- **Web Interface**: <http://localhost:80802>. Build the project:

- **REST API**: <http://localhost:8080/api>

- **H2 Console**: <http://localhost:8080/h2-console```bash>

mvn clean install

### Login Credentials```

The application includes pre-configured administrator accounts for demonstration:3. Run the application:

| Username | Password | Role |```bash

|----------|----------|------|mvn spring-boot:run

| Rob | Haines | Administrator |```

| Caroline | Jay | Administrator |

| Markel | Vigo | Administrator |4. Access the application:

| Mustafa | Mustafa | Administrator |

| Tom | Carroll | Administrator |- Web Interface: <http://localhost:8080>

- REST API: <http://localhost:8080/api>

**Note**: All users have full administrative privileges including create, update, and delete operations. Public users can view events and venues without authentication.- H2 Console: <http://localhost:8080/h2-console>

## Running Tests### Default Credentials

Execute the test suite:For demonstration purposes, the application includes pre-configured admin accounts:

```bash

mvn test- Username: `Rob` / Password: `Haines`

```- Additional accounts available in `Security.java`



Run with detailed output:## Running Tests

```bash

mvn test -XExecute the test suite:

```

```bash

**Note**: Some unit tests may fail on Java 25 due to Mockito compatibility issues. The application compiles and runs correctly. Integration tests pass successfully.mvn test

```

## Configuration

Run with detailed output:

### Database Configuration

The application uses H2 database with file-based persistence. Configuration in `Persistence.java`:```bash

- Database file: `db/showspace-dev.mv.db`mvn test -X

- Console access enabled for debugging```

- Connection: `jdbc:h2:./db/showspace-dev`

## Configuration

### External API Configuration

- **Mapbox**: Configure access token in `mapbox-config.properties`### Database Configuration

- **Mastodon**: Configure credentials in `mastodon-config.properties`

The application uses H2 database with file-based persistence. Configuration in `Persistence.java`:

## API Documentation

- Database file: `db/showspace-dev.mv.db`

### Events Endpoints- Console access enabled for debugging

- `GET /api/events` - List all events

- `GET /api/events/{id}` - Get event details### External API Configuration

- `POST /api/events` - Create new event (Admin only)

- `PUT /api/events/{id}` - Update event (Admin only)- **Mapbox**: Configure access token in `mapbox-config.properties`

- `DELETE /api/events/{id}` - Delete event (Admin only)- **Mastodon**: Configure credentials in `mastodon-config.properties`

### Venues Endpoints## API Documentation

- `GET /api/venues` - List all venues

- `GET /api/venues/{id}` - Get venue details### Events Endpoints

- `POST /api/venues` - Create new venue (Admin only)

- `PUT /api/venues/{id}` - Update venue (Admin only)- `GET /api/events` - List all events

- `DELETE /api/venues/{id}` - Delete venue (Admin only)- `GET /api/events/{id}` - Get event details

- `POST /api/events` - Create new event (Admin only)

## Key Learning Outcomes- `PUT /api/events/{id}` - Update event (Admin only)

- `DELETE /api/events/{id}` - Delete event (Admin only)

### Technical Skills Demonstrated

- Full-stack development with Java and Spring Boot ecosystem### Venues Endpoints

- RESTful API design and implementation with HATEOAS

- Authentication and authorization with Spring Security- `GET /api/venues` - List all venues

- ORM and database design with JPA/Hibernate- `GET /api/venues/{id}` - Get venue details

- Server-side rendering with Thymeleaf- `POST /api/venues` - Create new venue (Admin only)

- Test-driven development with JUnit and MockMvc- `PUT /api/venues/{id}` - Update venue (Admin only)

- Dependency injection and IoC container patterns- `DELETE /api/venues/{id}` - Delete venue (Admin only)

- Maven project management and build automation

## Key Learning Outcomes

### Software Engineering Practices

- MVC architectural pattern### Technical Skills Demonstrated

- Repository and Service patterns

- Separation of concerns- Full-stack development with Java and Spring Boot ecosystem

- Configuration management- RESTful API design and implementation with HATEOAS

- Version control with Git- Authentication and authorization with Spring Security

- Team collaboration and leadership- ORM and database design with JPA/Hibernate

- Server-side rendering with Thymeleaf

## License- Test-driven development with JUnit and MockMvc

- Dependency injection and IoC container patterns

This project is inspired by university coursework. The original coursework specifications and requirements are property of the University of Manchester. This repository is intended for portfolio demonstration purposes.- Maven project management and build automation

## Contact### Software Engineering Practices

**Anthony Nguyen**- MVC architectural pattern

- GitHub: [@a38062an](https://github.com/a38062an)- Repository and Service patterns

- University: University of Manchester, BSc Computer Science- Separation of concerns

- Configuration management

---- Version control with Git

- Team collaboration and leadership

*Inspired by COMP23412 coursework at the University of Manchester. Refactored and enhanced for portfolio presentation. Demonstrates proficiency in enterprise Java development, Spring Framework, and full-stack web application architecture.*

## Future Enhancements

Potential improvements for production deployment:

- Migration to PostgreSQL/MySQL for production use
- OAuth2/OpenID Connect for social login
- JWT tokens for stateless API authentication
- Docker containerization
- CI/CD pipeline with automated testing
- Frontend migration to React/Vue for SPA
- WebSocket integration for real-time updates
- Enhanced monitoring and logging
- API documentation with OpenAPI/Swagger

## License

This project is a refactored version of university coursework. The original coursework specifications and requirements are property of the University of Manchester. This repository is intended for portfolio demonstration purposes.

## Contact

**Anthony Nguyen**

- GitHub: [@a38062an](https://github.com/a38062an)
- University: University of Manchester, BSc Computer Science
- LinkedIn: [Add your LinkedIn profile]

---

*Developed as part of COMP23412 coursework at the University of Manchester, refactored and rebranded for portfolio presentation. Demonstrates proficiency in enterprise Java development, Spring Framework, and full-stack web application architecture.*
