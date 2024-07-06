# Scala 3.4.0 Repository

![Build Status](https://codebuild.eu-west-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiVmJyME1kd2o2eTN0QUNQbEs2YVZ5aVlZam4zNGRXMC9SSUNyT1o1ajR2MW5XK2ozc25VcXQ2SFVXdFg1Yzg3aWNQMG1uNGFlK01mS0FFOE9kUk81M2owPSIsIml2UGFyYW1ldGVyU3BlYyI6IkdsRVA2Yk4wK0NoR1BhNGIiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)


This repository contains a collection of Scala 3.4.0 projects, including five interview assignments and Scala 3 Coding Gym materials that I created during my time at previous companies.

## Interview Assignments

Each interview assignment includes a set of test codes to verify the functionality of the implemented components.

### 1. Binary Adder

The Binary Adder component adds one to a sequence of binary numbers representing a single binary number. For example:
- `1010` => `1011`
- `1110` => `1111`
- `111` => `1000`

### 2. Order Book

*Since this was an interview assignment, I would prefer not to disclose the specific business logic. However, test cases are provided to describe the logic for each class. The core algorithm is complex, as the business requirement was in a new domain that I had not previously worked on.*

**Please note that the Cucumber tests for this component have been temporarily disabled due to the absence of the original text file required for these tests. It was likely meant to be supplied as part of the examination process.*

### 3. Cashier

The Cashier component simulates a cashier system with various functionalities.

### 4. Supermarket

The Supermarket component calculates the total price (with promotions applied) for a given list of items.

### 5. Transaction

The Transaction component validates a single transaction or a list of transactions and yields the validation result.

## Scala 3 Coding Gym Materials

The repository also includes a set of Scala 3 Coding Gym materials covering various Scala 3 features and concepts. These materials were prepared for my colleagues to learn and practice Scala 3 programming techniques and best practices. The topics covered include:

- Call-by-Name Demonstration
- Context Functions
- Dependent Function Types
- Deriving
- Effect Error Handling
- Effect Technics
- Inline Examples
- Intersection and Union Types
- Open Classes
- Upper Bounds
- Covariance, Contravariance, and Invariance

Feel free to explore the code and materials in this repository.

Happy coding with Scala 3.4.0!

## Father Picture Report Producer

This repository also includes the 'father-picture-report-producer' project, a practical application of Scala 3 concepts. This project is an Image Document Generator designed to simplify the creation of photo albums, especially for users who may not be technically inclined. Key features include:

- Automated document generation using image file names as captions
- Custom document titling via a user-friendly GUI
- Grid layout for image arrangement
- LibreOffice Writer compatibility for optimal formatting
- Support for multiple image formats and robust file handling
- Bilingual interface (English and Korean)

This project showcases real-world application of Scala 3, demonstrating GUI development, file I/O operations, and document generation using Apache POI. It serves as an excellent example of how Scala can be used to create practical, user-friendly applications.