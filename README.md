# AI Model Lifecycle Management System

A Java-based application for managing the lifecycle of AI models using Object-Oriented Programming, MySQL database integration, and a Python-based machine-learning service.

## Project Information

- **Project Title:** AI Model Lifecycle Management System
- **Course:** Object Oriented Techniques using JAVA
- **Course Code:** CCSE0355
- **Project Type:** Capstone Project
- **Group:** 20
- **Faculty:** Ms. Shweta Singh
- **SDG Alignment:** SDG 9 — Industry, Innovation and Infrastructure

## Overview

The AI Model Lifecycle Management System is an academic software project designed to organize and manage the different stages of an AI model's lifecycle through a centralized application.

The system uses Java for lifecycle management, MySQL for persistent data storage, and Python for machine-learning operations. A local HTTP interface connects the Java application with the machine-learning service.

The project demonstrates how object-oriented programming, database management, and machine-learning integration can be combined to develop a modular software solution.

## Features

- **Dataset Management:** Create and manage dataset records.
- **AI Model Management:** Create and maintain AI model information.
- **Model Versioning:** Manage versions associated with AI models.
- **Training and Testing:** Integrate lifecycle operations with machine-learning functionality.
- **Deployment Management:** Maintain deployment-related records.
- **Monitoring:** Store and retrieve monitoring information.
- **Model Updates and Maintenance:** Support model maintenance operations.
- **Database Persistence:** Store lifecycle information in MySQL.
- **Machine-Learning Integration:** Connect Java with a Python-based ML service.
- **HTTP API:** Communicate with the ML service through a local endpoint.
- **Health Check:** Check whether the ML service is responding.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Main application and lifecycle management |
| Object-Oriented Programming | Structure and organization of application components |
| MySQL | Persistent storage of lifecycle records |
| JDBC | Java-to-database connectivity |
| MySQL Connector/J | JDBC driver for MySQL |
| Python | Machine-learning functionality |
| HTTP API | Communication between Java and the ML service |

## System Architecture

The application is organized into three principal components.

### 1. Java Application

The Java application provides the console interface and coordinates the lifecycle workflow. It manages datasets, AI models, model versions, deployments, monitoring records, and other supported operations.

### 2. MySQL Database

MySQL stores persistent information associated with the lifecycle entities. JDBC and database-access components allow the Java application to create, retrieve, and update records.

### 3. Python Machine-Learning Service

The Python component supports machine-learning operations, including model training, testing, and prediction as implemented in the project. A local HTTP interface enables communication between the Java application and the ML service.

## Object-Oriented Programming Concepts

The project demonstrates the application of Java OOP concepts to a multi-component software system.

The implementation uses classes and objects to represent lifecycle entities and organize their associated operations. It also applies encapsulation, constructors, methods, and class relationships where appropriate.

Inheritance and polymorphism are used where implemented in the relevant class hierarchy. Database-access components separate persistence operations from the main application workflow, improving modularity and maintainability.

## Database Integration

The application connects to MySQL using Java Database Connectivity (JDBC) and the MySQL Connector/J driver.

The database stores information related to AI models, datasets, model versions, deployments, and monitoring records. Persistent storage allows relevant information to remain available beyond an individual application session.

The database used during development is named `ai_model_lifecycle`.

Database setup and connection details should be configured according to the project's actual configuration files. Credentials should be kept private and should not be committed to the repository.

## Machine-Learning Integration

The project integrates a Python-based machine-learning component with the Java lifecycle management application.

The ML workflow supports the relevant stages of data preparation, model training, evaluation, and prediction. A trained model can be saved and reused according to the implementation.

The local HTTP interface allows the application to communicate with the ML service. A health endpoint is available at:

`http://127.0.0.1:8765/health`

During development, this endpoint returned a healthy status and a version identifier, confirming that the service was responding at the time of testing.

The health check confirms service availability; it does not independently establish model accuracy or prediction quality.

## Getting Started

### Prerequisites

The following software is required to run the relevant project components:

- Java Development Kit (JDK)
- Python
- MySQL Server
- MySQL Connector/J
- Python dependencies required by the ML component

Refer to the project's configuration files for the specific dependency and version requirements.

### 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_REPOSITORY_DIRECTORY>
```

Replace the placeholders with the actual GitHub repository URL and directory name.

### 2. Configure MySQL

1. Start the MySQL server.
2. Create the required database.
3. Execute the project's SQL setup script, if provided.
4. Configure the database connection details in the appropriate Java configuration.
5. Verify that the configured database user has the required permissions.

### 3. Configure Java Dependencies

Ensure that the JDK and MySQL Connector/J driver are available to the application. Follow the compilation and execution instructions appropriate to the source files and dependency configuration in the repository.

### 4. Configure the ML Service

Install the Python dependencies specified by the project and configure the ML component as required.

Start the service using the project's available startup script or documented execution procedure before testing ML-dependent operations.

### 5. Run the Application

Start the Java application using the project's execution instructions. Use the console menu to access the available lifecycle operations.

For operations involving training, testing, or prediction, ensure that the required database and ML service components are running.

## Testing and Validation

The system was tested during development for database connectivity, persistence, console-menu operation, and communication with the ML service.

Testing included:

- Verifying the connection between Java and MySQL.
- Checking that lifecycle records persist in the database.
- Confirming that the console menu displays the available operations.
- Checking the response from the ML service health endpoint.
- Testing machine-learning prediction functionality.
- Identifying and resolving database configuration issues.

The results demonstrate the integration of the principal project components in the development environment. Further testing would be required before using the system in a production environment.

## Limitations

This project is an academic prototype rather than a production-ready enterprise model-management platform.

Potential improvements include:

- A graphical user interface.
- Stronger authentication and authorization.
- Additional machine-learning algorithms and evaluation metrics.
- More comprehensive automated testing.
- Expanded monitoring and logging.
- Improved handling of concurrent operations.
- Deployment to a server or cloud environment.

## Team Members

**Group 20**

| Student | ERP ID |
|---|---|
| Om Narayan Singh | 2501331520144 |
| Janhvi Singh | 2501331520101 |
| Mohani Yadav | 2501331520129 |
| Nirali Singh | 2501331520143 |
| Harsh Kumar | 2501331520032 |

## Academic Context

This project was developed as a capstone project for Object Oriented Techniques using JAVA (CCSE0355).

It demonstrates the practical application of object-oriented programming, database integration, and machine-learning service integration. The project aligns with United Nations Sustainable Development Goal 9: Industry, Innovation and Infrastructure.

## License

No license has currently been specified for this project. Unless a license is added to the repository, permission to reuse or redistribute the source code should not be assumed.