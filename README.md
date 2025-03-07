# Stellar Burgers API Tests

This project contains automated tests for the Stellar Burgers API. The tests are written in Java using JUnit and RestAssured, and they are integrated with Allure for reporting.

## Technologies

The following technologies are used in this project:

| Technology         | Purpose                                                                 | Version      |
|--------------------|-------------------------------------------------------------------------|--------------|
| Java               | Programming language for writing tests                                  | 11           |
| JUnit              | Testing framework for writing and running tests                         | 5.9.2        |
| RestAssured        | Library for testing RESTful APIs                                        | 5.3.0        |
| Allure             | Test reporting framework                                                | 2.20.1       |
| JavaFaker          | Library for generating random test data (e.g., emails, passwords)       | 1.0.2        |
| Maven              | Build automation and dependency management tool                         | -            |

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven installed
- Allure command-line tool installed (for generating reports)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/hellp1ne/Diplom_2
   ```
### Running Tests
1. Building the Project and Running Tests
   To build the project and run the tests, execute the following command:

   ```bash
   mvn clean test
   ```
2. Generating the Allure Report
   To generate the Allure report, execute the following command:

   ```bash
   mvn allure:serve
   ```
3. Viewing the Allure Report
   If you want to view the report manually, follow these steps:

   Generate the report:

   ```bash
   mvn allure:report
   ```
   ### Open the generated report:

   - Navigate to the target/site/allure-maven-plugin folder.

   - Open the index.html file in your browser.