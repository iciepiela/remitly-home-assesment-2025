# Remitly - Home Exercise 2025
## Project Description
The application is written in Java 21 using Spring Boot. It utilizes a PostgreSQL database and is run using Docker containers. The repository includes two launch scripts, dedicated for Windows and Linux systems:

 - run-compose-windows.bat
 - run-compose-linux.sh
  
## Requirements
 - Java 21 – the Gradle configuration uses a toolchain for Java 21.
 - Docker and Docker Compose – to run the application and database containers.
 - Operating system: Windows or Linux (depending on the script used).

## Installation
 - Clone the repository:

```bash
git clone
cd
```
 - Install Docker and Docker Compose:

Ensure that Docker and Docker Compose are installed according to the Docker documentation.

 - Build the project using Gradle:

```bash
 cd .\remitly_internship\
.\gradlew build
```
(For Windows, you can use gradlew.bat)

## Configuration
### Build.gradle:
The configuration uses Spring Boot plugins and dependency management, and defines dependencies such as spring-boot-starter-web, spring-boot-starter-data-jpa, org.postgresql:postgresql, and others.

### Database Configuration:
Ensure that the application configuration file (e.g., src/main/resources/application.properties or application.yml) contains the correct database connection details. The configuration should match the settings in the docker-compose.yml file.

## Running the Application
The project is started using Docker Compose. The repository includes two scripts that set up and launch the environment:

 - For Windows:

Run the file run-compose-windows.bat (e.g., by double-clicking or via the CMD/PowerShell terminal):

```bash
 .\run-compose-windows.bat
```

 - For Linux (and macOS):

Run the file run-compose-linux.sh file:

```bash
.\run-compose-linux.sh
```

These scripts use the docker-compose.yml file, which defines the configuration for the application and PostgreSQL database containers.

## Project Structure
 - src/main/java – application source code
 - src/main/resources – application resources (including Spring Boot configuration)
 - build.gradle – Gradle configuration
 - Dockerfile – Docker image definition for the application
 - docker-compose.yml – container configuration (application + PostgreSQL database)
 - run-compose-windows.bat and run-compose-linux.sh – scripts to launch the environment

## Testing
Unit and integration tests are located in the src/test directory. To run them, execute:

```bash
cd .\remitly_internship\
.\gradlew test
```

## Building the Project
To build the project and generate an artifact, use the following command:

```bash
cd .\remitly_internship\
.\gradlew build
```


## License
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

