# Project Title

Rental Store, demo project for a tool rental store.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### This project uses the following technologies:

- Java
JDK 17 [Azul Zulu Version](https://www.azul.com/downloads/#zulu)
- Gradle 7.3 [Gradle Downloads](https://gradle.org/releases/)
- Spring Boot 3.3.1 [Spring Initializer](https://start.spring.io/), [Spring Boot Documentation](https://docs.spring.io/spring-boot/index.html)
  >The dependencies specified in the build.gradle file will result in downloading the necessary Spring-Boot, H2, etc. libraries.
- H2 Database [H2 Database](https://www.h2database.com/html/main.html)
  >This project uses an in-memory H2 database. The database is created when the application is started and is destroyed when the application is stopped. The database and related configuration is contained in the src/main/resources/application.properties file.

  >Note - The H2 Datbase is required to issue api requests (Application must be running, of course); however, the unit tests mock all of the data necessary to validate correct functionality of the application. 
- IDE - IntelliJ IDEA [IntelliJ IDEA](https://www.jetbrains.com/idea/)

### Installing, building, testing, and running the project


```bash
# Clone the repository
git clone https://github.com/yourusername/your-repo-name.git

# Navigate to the directory
cd your-repo-name

# Build the project
./gradlew build
./gradlew clean build
./gradlew clean build -x test  (to skip the tests)

# Display the project dependencies
./gradlew dependencies

# Run all of the tests, independent of the build step
./gradlew test

# Run tests in one specific test class by specifying the test class name
./gradlew test --tests com.example.demo.store.rentals.ScenarioTest

# Run a specific test withinspecific test class by specifying the test class name and the test method name
./gradlew test --tests com.example.demo.store.rentals.RentalAgreementTest.finalizeAgreementWrapperTest

# Use '--rerun-tasks' to force a re-run of the tests (regardless of whether or not Gradle has flagged them as up-to-date)
./gradlew test --rerun-tasks --tests com.example.demo.store.rentals.ScenarioTest

# Run the project (this will launch the application and make it available on http://localhost:8080)
./gradlew bootRun
```

### Overview of the source code (e.g. classes, packages, test data, etc.)
This is a Spring JPA/Hibernate project.   
> The specification did not require the use of a database, jpa, or hibernate, but I chose this approach as a personal experiment
> to demonstrate the use of JPA/Hibernate and as a convenient way for me to define the data model and related classes.  
> Using the REST api defined by the Spring controllers and associated service classes allowed me retrieve sample data from the DB 
> which made it easy to build a set of mock (test) data to use in the unit tests.

> The schema and sample data are contained in the _src/main/resources/schema.sql_ and _src/main/resources/data.sql_ files

### The project is organized as follows:
#### Packages
- **src/main/java/com.example.demo** - The main package for the project
  - **controllers** - Contains the REST controllers for the project
  - **services** - Contains the data access and manipulation related service classes for the project
  - **repository** - Contains the JPA data access classes for the project
  - **entities** - Contains the data model classes for the project
  - **json.serializers** - Contains the classes for custom serializing and deserializing JSON data
  - **store.rentals** - Contains the primary classes implementing the functionality for the rental store project
    - **exceptions** - Contains the classes defining rental store specific exceptions
  - **src/main/java/resources** - Contains the schema and sample data for the project, along with the application.properties file
    - **schema.sql** - Contains the schema for the project
    - **data.sql** - Contains the sample data for the project
    - **application.properties** - Contains the configuration for the project


- **src/test/java/com.example.demo** - The test package for the project
  - **data** - Contains the classes defining the mock data used in the unit tests
  - **store.rentals** - Contains the rental store test classes


#### Classes of note:
  - **Tool** - Represents a tool in the rental store
  - **RentalCost** - Represents charges for each category of tool
 
    > Tool and RentalCost are joined on "tool type" and uesd to calculate the rental charges for any given tool

  - **RentalRequest** - Represents a request for a tool rental
  - **RentalDateManager** - Utility class providing most of the functionality around date manipulation, and determination of holidays and weekends
  - **RentalAgreement** - Represents a finalized rental agreement, including charges and due dates.  This class contains all the logic necessary to calculate discounts, charges, and rental timeframe, etc.
  - **CheckoutService** - Responsible for taking a RentalRequest, validating the request, and producing a finalized rental agreement.

    > This class contains the logic necessary to validate the rental request; validating tool code, checkout data, number of rental days, and request discount, as well as tool lookup based on tool code.

##### Test classes of note:
  - **ScenarioTest** - Contains the primary test scenarios for the project
  - **RentalAgreementTest** - Contains the tests for the RentalAgreement class
  - **CheckoutServiceTest** - Contains the tests for the CheckoutService class
  - **RentalDateManagerTest** - Contains the tests for the RentalDateManager class
  - **RentalDemoTestBase** - Contains the method(s) necessary to load test data.
    > This class is extended only by the RentalAgreementTest and CheckoutServiceTest classes. It contains the method(s) necessary to load test data from the src/test/resources/demoTestData.json file. The test data is deserialized into an instance of the DemoTestData class.
  - **DemoTestData** - Contains the test data for the project
    > Again, this class is populated by the RentalDemoTestBase class.
  - **ScenarioTest** - Represents a test scenario.  
    > This class is used to define a test scenario, including the rental request, expected rental agreement, and expected exceptions. The test scenario is then executed by the ScenarioTest class.    > The DemoTestData object holds a collection of test scenarios, each of which represents a test case/scenario detailed in the specification.

### Running the tests
The tests can obviously be run, directly, from within the IDE (_IntelliJ in my case_), but they can also be run from the command line as shown above in the
[Installing, building, testing, and running the project](#Installing-building-testing-and-running-the-project) section.

- RentalDayManagerTest - Contains the tests that validate determination of holidays, weekends, weekdays, etc.
- RentalAgreementTest - Contains the tests that validate various calculations and logic in the RentalAgreement class:
  > - validate the calculation of the due date
  > - validate the calculation of the charge amount
  > - validate the calculation of the discount amount
  > - validate the calculation of the final charge amount
  > - validate the calculation of the final due date
  > - Etc.

  > ** There are individual RentalAgreementTest methods for each of the calculation types listed above; and there are also individual test methods covering each of these methods.
  
  > *** However, RentalAgreement.finalizeAgreement() wraps all of these calculation methods; therefore, the RentalAgreementTest.finalizeAgreementWrapperTest() method is effectively
  > a functional test which validates all capabilities of the RentalAgreement class.
  > 
#### Testing Summary
- **RentalDateManagerTest** is a unit test, particularly concerned with validating date manipulation and determination of holidays and weekends.
- **CheckoutServiceTest** is a unit test, particularly concerned with testing the CheckoutService validation of a RentalRequest.
- **RentalAgreementTest** is a functional unit test, particularly the finalizeAgreementWrapperTest() method
- **_ScenarioTest_** is a functional test whose primary function is to repeatedly run scenario tests to validate final results match the expected scenario results detailed in the specification.
  > *** This is a single test method, _testScenario()_, which is a parameterized test that iterates through all the test scenarios defined in the DemoTestData object.

## Additional Information regarding the REST APIs
- The REST APIs are defined in the _com.example.demo.controllers_ package.
- The following REST APIs are defined (**Reminder**: _By default the base url, or origin, is_ http://localhost:8080):
  - **GET /alive**  -- simply test whether he application is running (_HelloWorldController_)
  - **GET /tools**  -- retrieve all Tool objects in the database, including the RentalCost associated with each tool (_ToolController_)
    > There are other methods (endpoints) defined in the ToolController, but they are experimental, not finished, and therefore not worth mentioning.
  - **POST /rental/checkout**  -- submit a RentalRequest and perform a CheckoutService.checkout() operation (_RentalRequestController_)
    > Result is JSON string representing the finalized RentalAgreement object
  - **POST /rental/checkout/as-string**  -- submit a RentalRequest and perform a CheckoutService.checkout() operation (_RentalRequestController_)
    > Result is raw string representing the finalized RentalAgreement object generated by the RentalAgreement.toString() method (_see the toString() method for_ details)
    > 
#### Example curl statements, including results, for each of the '/rent/checkout' endpoints:
```bash
curl -X POST -H "Content-Type: application/json" http://localhost:8080/rental/checkout/as-string -d '{
  "toolCode": "LADW",
  "checkoutDate": "07/02/20",
  "numberOfRentalDays": 3,
  "discount": "10"
}' | jq -r '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   408  100   307  100   101  13041   4290 --:--:-- --:--:-- --:--:-- 17739
    Tool code: LADW
    Tool type: Ladder
    Tool brand: Werner
    Rental days: 3
    Checkout date: 07/02/20
    Due date: 07/05/20
    Daily rental charge: $1.99
    Charge days: 2
    Pre-discount charge: $3.98
    Discount percent: 10%
    Discount amount: $0.40
    Final charge: $3.58
```

```bash
curl -X POST -H "Content-Type: application/json" http://localhost:8080/rental/checkout -d '{
  "toolCode": "LADW",
  "checkoutDate": "07/02/20",
  "numberOfRentalDays": 3,
  "discount": "10"
}' | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   367    0   266  100   101   1425    541 --:--:-- --:--:-- --:--:--  1973
{
  "Tool code": "LADW",
  "Tool type": "Ladder",
  "Tool brand": "Werner",
  "Rental days": 3,
  "Checkout date": "07/02/20",
  "Due date": "07/05/20",
  "Daily rental charge": 1.99,
  "Charge days": 2,
  "Pre-discount charge": 3.98,
  "Discount percent": "10%",
  "Discount amount": 0.4,
  "Final Charge": 3.58
}
```
Finally, here is one example curl statements hitting the '/tool' endpoint:
```bash
curl -X GET http://localhost:8080/tools | jq '.'
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   798    0   798    0     0  50840      0 --:--:-- --:--:-- --:--:-- 53200
[
  {
    "id": 1,
    "brand": "Stihl",
    "rentalCosts": {
      "id": 2,
      "dailyCharge": "1.49",
      "weekdayCharge": true,
      "weekendCharge": false,
      "holidayCharge": true,
      "toolType": "Chainsaw"
    },
    "tool_code": "CHNS",
    "tool_type": "Chainsaw"
  },
  {
    "id": 2,
    "brand": "Werner",
    "rentalCosts": {
      "id": 1,
      "dailyCharge": "1.99",
      "weekdayCharge": true,
      "weekendCharge": true,
      "holidayCharge": false,
      "toolType": "Ladder"
    },
    "tool_code": "LADW",
    "tool_type": "Ladder"
  },
  {
    "id": 3,
    "brand": "DeWalt",
    "rentalCosts": {
      "id": 3,
      "dailyCharge": "2.99",
      "weekdayCharge": true,
      "weekendCharge": false,
      "holidayCharge": false,
      "toolType": "Jackhammer"
    },
    "tool_code": "JAKD",
    "tool_type": "Jackhammer"
  },
  {
    "id": 4,
    "brand": "Ridgid",
    "rentalCosts": {
      "id": 3,
      "dailyCharge": "2.99",
      "weekdayCharge": true,
      "weekendCharge": false,
      "holidayCharge": false,
      "toolType": "Jackhammer"
    },
    "tool_code": "JAKR",
    "tool_type": "Jackhammer"
  }
]
```
