# Introduction

This repository is about a sample project using OpenAI, Twitter API, Selenium and other external tools, in order to help trade cryptocurrencies based on tweets and news content.

The original idea was to create a job scheduler that will be executed each X seconds to publish and reply to tweets using OpenAI. 
However, the project also supports trading cryptocurrencies by leveraging the tweets and news content.

## Prerequisites

- Java 21
- IntelliJ

### Running the application

In order to run the application locally, the following needs to be done:
- Install Docker and execute the `docker-compose-yml` by a command line, in order to create a postgres Database.
- Make sure JAVA_HOME is using Java 17
- `local` Spring profile needs to be set in the Run Configuration (in the `Active profiles` field)
- You will need the following environment variables :

```
- TWITTER_BEARER_TOKEN=
- OPENAI_BEARER_TOKEN=
- TWITTER_CLIENT_KEY=
- TWITTER_CLIENT_SECRET=
```

# Build and Test

To build and run the application using Docker, the following needs to be done:
- First check the influence's list in the `application.yml` file, make sure it's correct.
- Open a terminal, and make sure you are in the parent project where the Dockerfile located.
- Run this command, it will do everything in your behalf:

`./start_production.sh`


## Swagger

Swagger documentation can be found on **/api/swagger** and is accessible only by authenticated users.

Swagger is enabled in these environments:

- local: [http://localhost:8080/api/swagger](http://localhost:8080/api/swagger)
