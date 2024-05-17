# jm0524

Simulation of a Tool Rental Checkout System CLI. Uses Spring Data JPA to interact with a PostgreSQL database.

## Prerequisites

Ensure you have Docker installed.

## Clone the repository

```bash
git clone https://github.com/squidmin/jm0524.git
cd /Users/username/path/to/jm0524
```

> Replace the above path with the actual location of your clone of the project.

## Run tests

```bash
./gradlew cleanTest test -DAPP_PROFILE=test
```

![Run the tests](docs%2Fimg%2Fgradle_test.gif)

## View Test Coverage Report

![View test coverage report](docs%2Fimg%2Fview_test_coverage_report.gif)

### macOS

```bash
open build/reports/jacoco/test/html/index.html
```

### Windows

```bash
start build\reports\jacoco\test\html\index.html
```

### Linux

```bash
xdg-open build/reports/jacoco/test/html/index.html
```

## Build and run the containers

```bash
docker-compose run --rm -it app ./gradlew bootRun -DAPP_PROFILE=local
```

![Build and run the containers](docs%2Fimg%2Fbuild_and_run_containers.gif)

## Stop and remove all containers

```bash
docker-compose down
```

![Stop and remove all containers](docs%2Fimg%2Fdocker_compose_down.gif)

## Remove existing containers, volumes, and images

```bash
docker-compose down -v --rmi all
```

![Remove existing containers, volumes, and images](docs%2Fimg%2Fremove_existing_containers_volumes_and_images.gif)
