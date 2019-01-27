# jogging-tracker

## Requirements ##

Write a REST API that tracks jogging times of users

- [COMPLETE] API Users must be able to create an account and log in.
- [COMPLETE] All API calls must be authenticated.
- [COMPLETE] Implement at least three roles with different permission levels:
  - a regular user would only be able to CRUD on their owned records
  - a user manager would be able to CRUD only users
  - an admin would be able to CRUD all records and users.
- [COMPLETE] Each time entry when entered has a date, distance, time, and location.
	- Based on the provided date and location, API should connect to a weather API provider and get the weather conditions for the run, and store that with each run.
- [COMPLETE] The API must create a report on average speed & distance per week.
- [COMPLETE] The API must be able to return data in the JSON format.
- [PARTIALLY COMPLETE] The API should provide filter capabilities for all endpoints that return a list of elements, as well should be able to support pagination.
	- The API filtering should allow using parenthesis for defining operations precedence and use any combination of the available fields. The supported operations should at least include or, and, eq (equals), ne (not equals), gt (greater than), lt (lower than).
		Example -> `(date eq '2016-05-01') AND ((distance gt 20) OR (distance lt 10)).`
- [COMPLETE] Write unit tests.
- _[Additional Items]_ Swagger and Postman support.

# Git

You can clone the project with:

	git clone https://github.com/fazdevils/simple_web_service.git


# Prerequisites

* Java 8

		
# Build

	mvn clean package


# Run

The app can be run from terminal using the Maven command:

	mvn jetty:run
