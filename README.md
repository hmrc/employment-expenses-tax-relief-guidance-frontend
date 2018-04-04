# employment-expenses-tax-relief-guidance-frontend

[![Build Status](https://travis-ci.org/hmrc/employment-expenses-tax-relief-guidance-frontend.svg)](https://travis-ci.org/hmrc/employment-expenses-tax-relief-guidance-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/employment-expenses-tax-relief-guidance-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/employment-expenses-tax-relief-guidance-frontend/_latestVersion)


The purpose of this service is to provide guidance to users who want to try and claim employment expenses.

The user is taken through numerous pages which ask questions to determine what the user needs to do next depending upon their situation.
## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

This service is written in Scala and Play 2.5, so needs:
* At least a [JRE 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to run.
* [SBT](https://www.scala-sbt.org/) to test, build and run a development version
* A MongoDB instance running locally or in a docker container with open ports
* The HMRC [assets frontend service](https://github.com/hmrc/assets-frontend) running

### Installing
* Clone this repository to your development environment
* Start the MongoDB instance
* Start the HMRC [assets frontend service](https://github.com/hmrc/assets-frontend#frontends) running
)
* run sbt in interactive mode
* run `start -Dhttp.port="9000"` to start the service on port 9000
* open [http://localhost:9000/claim-tax-relief-expenses](http://localhost:9000/claim-tax-relief-expenses)

These steps will allow you to see the fully rendered first page in the service,which can the be used to navigate through the remaining journey pages.


## Running the tests

Running the unit tests for the service
* run sbt in interactive mode
* run `test` to run unit tests

### Journey tests

There is a separate repository for the service's browser driven journey tests.  Follow the instructions in that repositorys README for instructions how to run them.

[employment-expenses-tax-relief-guidance-acceptance-tests](https://github.com/hmrc/employment-expenses-tax-relief-guidance-acceptance-tests)

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details