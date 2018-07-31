# Testing project

This project tests web pages for page https://www.mortgageloan.com/calculator#mortgage-payment-calculator with Selenium framework.

It uses Gradle as a build system. For normal application, `src/main` would contain application code. This directory has been left empty. `src\test` will contain unit tests for testing classes if there will be any. `src\integrationTest` is where code for this task is.

Test can be run with command `gradle build`.

Unchecked exceptions are preferred as in most cases it does not make sense to recover or it is not possible.

## Libraries used
- Chrome and Gecko Web Drivers
- Selenium
- JUnit
- AssertJ (for elegant soft assertions)
