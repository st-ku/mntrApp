# mntrApp
## Requirements:
JDK 17, Docker, latest Gradle.

## Build & run:

```
gradlew shadowjar
docker-compose up
```
## API examples:
```
GET https://localhost:8080/api/data/ - get all data
POST https://localhost:8080/api/data/WyJOYW1lMSIsIk5hbWUyIl0= - save ["Name1","Name2"] array, encoded by base64
DELETE  https://localhost:8080/api/data/WyIyNDAyMCIsIjMwMDc0Il0= - delete data from repository where ids in array ["24020","30074"], encoded by base64 
