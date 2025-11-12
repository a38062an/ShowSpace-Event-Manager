# ShowSpace - Event and Venue Management Platform

## Important: Running Locally vs. AWS EC2

**Local Development (Recommended):**

1. Make sure you have Java 17+ and Maven installed.
2. Clone this repository.
3. Run `mvn clean install` to build the project.
4. Start the app with `mvn spring-boot:run`.
5. Open [http://localhost:8080](http://localhost:8080) in your browser.

**Why run locally?**

- The AWS EC2 instance used for the free-tier demo is very small (t3.micro), which often causes slow startup, random crashes, or the app being offline.
- If you see errors or the demo is down, it is likely due to the EC2 instance running out of memory or being stopped by AWS.
- For best results, always test and explore the application on your own computer first.

**Troubleshooting:**

- If the app fails to start on AWS, check the ECS task logs for `OutOfMemoryError` or similar messages.
- If you want to deploy to AWS, use a larger EC2 instance (at least t3.medium or higher) for reliable performance.
- Never put real secrets, passwords, or sensitive data in this repository or in your deployment scripts.

**Contact:** If you have issues running the app, please open an issue or contact the maintainer.

## Live Demo

**Application URL:** <http://showspace-alb-1201427212.eu-west-2.elb.amazonaws.com/>

Deployed on AWS ECS (Free Tier) using Docker containers. See [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md) for full deployment details.

**Note:** Initial load may be slow (7-8 minutes) due to free tier constraints (t3.micro instance).

**Important:** The live demo link may not work reliably. The site depends on a running EC2 instance in the ECS cluster. If the instance is stopped, terminated, or the ECS agent disconnects, the site will go offline until manual recovery steps are performed (such as launching a new EC2 instance and restarting the ECS agent). This is a limitation of the EC2 launch type and manual infrastructure management.

---

## About This Project

A full-stack event and venue management web application built with Spring Boot, demonstrating enterprise-level software engineering practices including MVC architecture, RESTful API design with HATEOAS, role-based security, and external API integration.

This project originated as team coursework for COMP23412 (Software Engineering) at the University of Manchester, where I served as team lead for a 7-person development team. I was recognized as the most contributing team member for exceptional performance in coordinating the team and delivering critical features.

The codebase has been significantly refactored and enhanced for this portfolio presentation:

- Complete rebranding from EventLite to ShowSpace
- Improved UI/UX with custom CSS styling
- Enhanced code quality and removed debug statements
- Restructured package naming conventions
- Added comprehensive documentation

While the core architecture reflects collaborative coursework, the refactoring, improvements, and presentation are my independent work.

## Technical Stack

### Backend

- Java 17 with Spring Boot 3.3.6
- Spring Data JPA with Hibernate ORM
- Spring Security 6 for authentication and authorization
- Spring HATEOAS for hypermedia-driven REST API
- H2 Database (embedded, file-based persistence)
- Maven for dependency management and build automation

### Frontend

- Thymeleaf template engine for server-side rendering
- Bootstrap 5 for responsive UI components
- Font Awesome 6.7.1 for iconography
- WebJars for frontend dependency management
- Custom CSS for unique styling and UX improvements

### External Integrations

- Mapbox SDK for geolocation and address geocoding
- Mastodon API for social media integration
- Jackson for JSON serialization

### Testing

- JUnit 5 for unit testing
- Spring MockMvc for integration testing
- Spring Security Test for security testing

## Architecture and Design Patterns

### MVC Architecture

The application follows a clean separation of concerns with distinct layers:

- **Controllers**: Handle HTTP requests and responses
  - `EventsController` / `VenueController` - Web interface
  - `EventsControllerApi` / `VenuesControllerApi` - REST API endpoints

- **Service Layer**: Business logic and data validation
  - `EventService` / `VenueService` - Interface definitions
  - `EventServiceImpl` / `VenueServiceImpl` - Service implementations

- **Repository Layer**: Data access using Spring Data JPA
  - `EventRepository` / `VenueRepository` - Database operations

- **Entities**: JPA entities with Bean Validation
  - `Event` / `Venue` - Domain models

### Repository Pattern

Abstracts data access logic using Spring Data JPA repositories with custom query methods using method name conventions.

### HATEOAS (Level 3 REST Maturity)

REST API responses include hypermedia links, enabling API discoverability and reducing client coupling:

```json
{
  "name": "Spring Workshop",
  "_links": {
    "self": { "href": "/api/events/1" },
    "venue": { "href": "/api/venues/5" }
  }
}
```

## Key Features

### Security Implementation

- Role-Based Access Control (RBAC) with Spring Security
- Form-based authentication for web interface
- HTTP Basic authentication for REST API
- CSRF protection for web requests (disabled for stateless API)
- Secure password encryption using BCrypt
- Session management with configurable timeout

### Data Management

- CRUD operations for events and venues
- Search functionality with pagination
- Data validation with Jakarta Validation API
- Bidirectional JPA relationships with cascade operations
- Automatic schema generation from entity models

### API Design

- RESTful endpoints following HTTP semantics
- HATEOAS with hypermedia links
- Content negotiation (JSON/HTML)
- Resource assemblers for consistent response structure
- Exception handling with appropriate HTTP status codes

### UI/UX

- Responsive design with Bootstrap grid system
- Server-side rendering with Thymeleaf
- Template inheritance using layout dialect
- Conditional rendering based on user roles
- Custom styling for improved user experience

## Project Structure

```
showspace/
├── src/main/java/anthonynguyen/showspace/
│   ├── ShowSpaceApplication.java       # Main application entry point
│   ├── entities/                       # JPA entities
│   ├── dao/                            # Data access layer
│   │   ├── *Repository.java            # Spring Data repositories
│   │   ├── *Service.java               # Service interfaces
│   │   └── *ServiceImpl.java           # Service implementations
│   ├── controllers/                    # MVC controllers
│   ├── assemblers/                     # HATEOAS resource assemblers
│   ├── config/                         # Spring configuration
│   │   ├── Security.java               # Security configuration
│   │   ├── Persistence.java            # Database configuration
│   │   ├── Hateoas.java                # API configuration
│   │   └── data/InitialDataLoader.java # Seed data
│   └── exceptions/                     # Custom exception classes
├── src/main/resources/
│   ├── templates/                      # Thymeleaf HTML templates
│   ├── static/css/                     # Custom stylesheets
│   ├── banner.txt                      # Application banner
│   └── *.properties                    # Configuration files
├── src/test/                           # Test sources
├── db/                                 # H2 database files
└── pom.xml                             # Maven configuration
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Application

1. Clone the repository:

```bash
git clone https://github.com/a38062an/showspace.git
cd showspace
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

4. Access the application:

- Web Interface: http://localhost:8080
- REST API: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console

### Default Credentials

For demonstration purposes, the application includes pre-configured admin accounts:

| Username | Password | Role          |
|----------|----------|---------------|
| Rob      | Haines   | Administrator |
| Caroline | Jay      | Administrator |
| Markel   | Vigo     | Administrator |
| Mustafa  | Mustafa  | Administrator |
| Tom      | Carroll  | Administrator |

All users have full administrative privileges including create, update, and delete operations. Public users can view events and venues without authentication.

## Running Tests

Execute the test suite:

```bash
mvn test
```

Run with detailed output:

```bash
mvn test -X
```

## Configuration

### Database Configuration

The application uses H2 database with file-based persistence. Configuration in `Persistence.java`:

- Database file: `db/showspace-dev.mv.db`
- Console access enabled for debugging
- Connection: `jdbc:h2:./db/showspace-dev`

### External API Configuration

- Mapbox: Configure access token in `mapbox-config.properties`
- Mastodon: Configure credentials in `mastodon-config.properties`

## API Documentation

### Events Endpoints

- `GET /api/events` - List all events
- `GET /api/events/{id}` - Get event details
- `POST /api/events` - Create new event (Admin only)
- `PUT /api/events/{id}` - Update event (Admin only)
- `DELETE /api/events/{id}` - Delete event (Admin only)

### Venues Endpoints

- `GET /api/venues` - List all venues
- `GET /api/venues/{id}` - Get venue details
- `POST /api/venues` - Create new venue (Admin only)
- `PUT /api/venues/{id}` - Update venue (Admin only)
- `DELETE /api/venues/{id}` - Delete venue (Admin only)

## Key Learning Outcomes

### Technical Skills Demonstrated

- Full-stack development with Java and Spring Boot ecosystem
- RESTful API design and implementation with HATEOAS
- Authentication and authorization with Spring Security
- ORM and database design with JPA/Hibernate
- Server-side rendering with Thymeleaf
- Test-driven development with JUnit and MockMvc
- Dependency injection and IoC container patterns
- Maven project management and build automation

### Software Engineering Practices

- MVC architectural pattern
- Repository and Service patterns
- Separation of concerns
- Configuration management
- Version control with Git
- Team collaboration and leadership

## License

This project builds upon university coursework. The original coursework specifications are property of the University of Manchester. This repository demonstrates my refactoring, enhancements, and independent development work for portfolio purposes.

See Licence.md for details.


---
