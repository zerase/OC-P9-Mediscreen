# Mediscreen

Mediscreen is an application built on the Microservice Architecture Pattern and using Spring Boot, Spring Cloud and Docker.

## Functional services

Mediscreen is decomposed into three core microservices and a fourth one used to render the user interface.

### Patient service
This service manages patient demographic information. It is linked to a MySQL database.

| Method | Path           | Description                                       |
|--------|----------------|---------------------------------------------------|
 | POST   | /patients      | Register new patient                              |
| GET    | /patients      | Get patients demographic informations             |
| GET    | /patients/{id} | Get demographic information of a specific patient |
| PUT    | /patients/{id} | Save patient informations                         |

### History service
This service manages practitioner's notes related to the patients. It is linked to a MongoDB database.

| Method | Path                         | Description                           |
|--------|------------------------------|---------------------------------------|
| POST   | /patHistories                | Register new note                     |
| GET    | /patHistories?patientId={id} | Get notes of a specific patient       |
| GET    | /patHistories/{id}           | Get a specific note                   |
| PUT    | /patHistories/{id}           | Save modifications of a specific note |

### Assessment service
This service manages diabetes assessment reports.

| Method | Path                        | Description                                                    |
|--------|-----------------------------|----------------------------------------------------------------|
| GET    | /assess/id/{id}             | Get diabetes assessment report of a specific patient           |
|  GET   | /assess/lastName/{lastName} | Get diabetes assessment report of patients with same last name |

### ClientUI service
This service is handling the front-end of the application and relying on Thymeleaf.

## Run the application

### Prerequisites
This application uses many technologies to achieve its purpose, like :
- Java 11
- Maven
- SpringBoot
- Spring Data JPA
- MySQL
- MongoDB
- Docker

### Getting started
- Build each microservice with Maven :
  ```
  mvn clean package
  ```
- Then build each microservice docker image using the command :
  ```
  docker build -t NAME_OF_YOUR_IMAGE .
  ```
- Once all the docker images are built, launch the docker-compose file using the command :
  ```
  docker-compose up -d
  ```
- Connect to the application in your browser at : http://localhost:8080
