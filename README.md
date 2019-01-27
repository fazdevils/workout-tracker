# jogging-tracker

## Requirements ##

Write a REST API that tracks jogging times of users

- [COMPLETE - NOTE: initial password are `password`] API Users must be able to create an account and log in.
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

	git clone https://git.toptal.com/screening/vincent-fazio.git


# Prerequisites

* Java 8

		
# Build

	mvn clean package


# Run

The app can be run from terminal using the Maven command:

	mvn jetty:run

	Options:

		ENV: the environment settings file to read (i.e. specify -DENV=[ENV] to read properties from
			 src/main/resources/application-[ENV].properties).  Any of the values in these files can
			 be individually overridden by -D[PROPERTY]=[VALUE]

# Release

Mostly follow [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow "GitFlow") as implemented by [JGit Flow](https://bitbucket.org/atlassian/jgit-flow/wiki/goals.wiki)

## Create a TEST release

- `mvn gitflow:release-start`
- update the Jenkins "release" target to build from the release branch.
- verify the release
- `mvn gitflow:release-finish`

## Patch a TEST release

- Create a feature branch off of the release branch for the fix.
- Apply the fix to the feature branch.
- Merge back to the release branch and then merge to develop.

## HotFix a PRODUCTION Release

- prune to remove any old remote hotfix references (git fetch -p)
- switch to master and pull to update
- `mvn gitflow:hotfix-start`
- Apply the fix.
- `mvn gitflow:hotfix-finish`
	