# mntrApp
## Description:
This project is an example of implementation crud rest api, based on [metacfg](https://github.com/akarazhev/metacfg4j) web engine.

## Requirements:
JDK 17, Docker, latest Gradle.

## Build & run:

```
gradlew shadowjar
docker-compose up
```
## REST API examples:
```
GET https://localhost:8080/api/data/ - get all data
POST https://localhost:8080/api/data/WyJOYW1lMSIsIk5hbWUyIl0= - save ["Name1","Name2"] array, encoded by base64
DELETE  https://localhost:8080/api/data/WyIyNDAyMCIsIjMwMDc0Il0= - delete data from repository where ids in array ["24020","30074"], encoded by base64
```
## REST API description:

## Get all data

### Request

`GET https://localhost:8080/api/data/`
```
curl -i -k -H 'Accept: application/json' https://localhost:8080/api/data/
```
### Response
```
HTTP/1.1 200 OK
Date: Thu, 27 Oct 2022 09:13:57 GMT
Content-type: application/json
Content-length: 41

{"result":[],"success":true,"error":null}
```
## Save data

### Request

`POST https://localhost:8080/api/data/`
```
curl -i -k -H 'Accept: application/json' https://localhost:8080/api/data/WyJOYW1lMSIsIk5hbWUyIl0=
```
### Response
```
HTTP/1.1 200 OK
Date: Thu, 27 Oct 2022 09:18:19 GMT
Content-type: application/json
Content-length: 41

{"result":[],"success":true,"error":null}
```
## Delete data by data ids

### Request

`DELETE https://localhost:8080/api/data/`
```
curl -i -k -H 'Accept: application/json' https://localhost:8080/api/data/WyIyNDAyMCIsIjMwMDc0Il0=
```
### Response
```
HTTP/1.1 200 OK
Date: Thu, 27 Oct 2022 09:18:19 GMT
Content-type: application/json
Content-length: 41

{"result":[],"success":true,"error":null}
```

