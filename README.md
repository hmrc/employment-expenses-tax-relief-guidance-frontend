# employment-expenses-tax-relief-guidance-frontend

[![Build Status](https://travis-ci.org/hmrc/employment-expenses-tax-relief-guidance-frontend.svg)](https://travis-ci.org/hmrc/employment-expenses-tax-relief-guidance-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/employment-expenses-tax-relief-guidance-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/employment-expenses-tax-relief-guidance-frontend/_latestVersion)


This service helps people find out if they can claim or get tax relief on employment expenses.

The service asks questions to tell the user what they need to do. If they can claim, the service tells them if the can claim online or by post. If they cannot claim, the service provides link to guidance.

## Get started

Follow these instructions so you can get a copy of the project on your local machine. You can use this to develop and test the service. See deployment for notes on how to deploy the project on a live system.

### Before you get started

This service is written in Scala and Play 2.5. It needs:

- at least a [JRE 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to run
- [sbt](https://www.scala-sbt.org/) to test, build and run a development version
- a MongoDB instance running locally or in a docker container with open ports
- the HMRC [assets frontend service](https://github.com/hmrc/assets-frontend) running

### How to install

1. Clone this repository to your development environment.
2. Start the MongoDB instance.
3. Start the HMRC [assets frontend service](https://github.com/hmrc/assets-frontend#frontends).
4. Run sbt in interactive mode.
5. Run `start -Dhttp.port="9000"` to start the service on port 9000.
6. Open [http://localhost:9000/claim-tax-relief-expenses](http://localhost:9000/claim-tax-relief-expenses).

These steps will open the first page in the service.

## Tests

### Unit tests

1. Run sbt in interactive mode.
2. Run `test` to run unit tests

### Journey tests

There is a [separate repository for the service’s browser-driven journey tests](https://github.com/hmrc/employment-expenses-tax-relief-guidance-acceptance-tests). Follow the instructions in that repository’s README to find out how to run them.

## Licence

This project is licensed under the [Apache 2.0 License](LICENSE).