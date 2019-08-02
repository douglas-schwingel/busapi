# Bus API
DataPOA based bus API

------------


### Why MongoDb?

- Schema Flexibility;
- Embedded coordinates: Using mongo, there's no need to use join in large tables containing 200+ coordinates for each bus line.One can simply put a List of coordinates in a Document and store everything in one place.
- Easy GeoNear query;

### Why Lombok?

- Easy Getters, Setters, Equals and more standard methods using annotations;
- CompileOnly: Automatically generates methods and constructors during compilation and the libraries are not added to final JAR, using a lot less space;


### Why RestAssured?

- Simple endpoint testing for Rest applications;
- Easy to read and understand requests
> Explicit dependencies for json-path and xml-path were needed due to incompatibility with SpringBoot's version

### Why JaCoCo?

- Java Code Coverage for testing
- Used to verify code coverage in SonarQube;

### Why SonarQube?

- Complete code analysis: bugs, vulnerabilities, code smells, etc.

### Why Swagger-UI?

- Auto-generated API Documentation
- Easily customized using annotations
- HTML page with requests samples
 

------------

# Running the Application
Application will automatically populate the database when start-up is finished. 

If the application is re-run, It will check if the bus line already exists and save only new ones.

### Requeriments:

	 Docker
	 Java 11
	 
##### 1. Running MongoDB
Using mongo's docker image:

> docker run -d --name mongodb -p 27017:27017 mongo

###### If you want to keep your data, run with volume using
> docker run -d --name mongodb -v ~your-local-dir:/data/db -p 27017:27017 mongo

##### 2. Running the application
Open a terminal on the source folder of the application and run:
> ./gradlew bootRun

##### 3. Access to API Documentation

> http://localhost:8080/swagger-ui.html

##### Optional: SonarQube

Run sonarqube on port 9000
> docker run -d --name sonarqube -p 9000:9000 sonarqube

When SonarQube is ready:

> ./gradlew sonarqube

> http://localhost:9000

------------

# Requests samples

- GET - Get all bus lines unpaged:
> http://localhost:8080/line-service/v1/lines

```json
{
  "content": [
    {
      "id": 5530,
      "codigo": "250-2",
      "nome": "1_DE_MAIO"
    },
      **All lines**
  ],
  "pageable": "INSTANCE",
  "totalPages": 1,
  "totalElements": 989,
  "last": true,
  "first": true,
  "number": 0,
    "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 989,
  "size": 0,
  "empty": false
}
```

- GET - Get all bus lines with pagination:
>http://localhost:8080/line-service/v1/lines?page=0&size=15

-Parameters:

	page(int) -> Page number
	size(int) -> Number of bus lines in each page
		
```json
{
  "content": [
    {
      "id": 5530,
      "codigo": "250-2",
      "nome": "1_DE_MAIO"
    },
      **14 more lines**
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageSize": 15,
    "pageNumber": 0,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 66,
  "totalElements": 15,
  "last": true,
  "first": true,
  "number": 0,
    "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 15,
  "size": 15,
  "empty": false
}
```

- POST - Insert new Bus Line

(Coordinates must be within Porto Alegre's territory)

> http://localhost:8080/line-service/v1/lines

 Request Body:    
 
```json
{
   "id": 5566,
   "coordinates": [
      [
         -30.124190568328,
         -51.223783133554
      ],
      [
         **More coordinates**
      ]
   ],
   "codigo": "266-1",
   "nome": "VILA_NOVA"
}
```
Response:
```json
{
   "id": 5566,
   "codigo": "266-1",
   "nome": "VILA_NOVA"
}  
```

  
- PUT - Update a bus line

>http://localhost:8080/line-service/v1/lines

    
*Will completely update de line with the new data*
    
    
Request Body:

```json
{
   "id": 5566,
   "coordinates": [
      [
         -30.124190568328,
         -51.223783133554
      ],
      [
         **More coordinates**
      ]
   ],
   "codigo": "266-1",
   "nome": "VILA_NOVA"
}
```

Response:
```json
{
   "id": 5566,
   "codigo": "266-1",
   "nome": "VILA_NOVA"
}  
```

- DELETE - Delete a bus line
> http://localhost:8080/line-service/v1/lines/1704

    - Status 204 - If the line was successfully deleted


- GET - Get single bus line by Id
>http://localhost:8080/line-service/v1/lines/5566


```json
{
  "id": 5566,
  "coordinates": [
    [
      -30.124190568328,
      -51.223783133554
    ],
    [
      **More coordinates**
    ]
  ],
  "codigo": "266-1",
  "nome": "VILA_NOVA"
}
```

- GET - Get single bus line by Code

>http://localhost:8080/line-service/v1/lines/code/266-1

```json
{
  "id": 5566,
  "coordinates": [
    [
      -30.124190568328,
      -51.223783133554
    ],
    [
      **More coordinates**
    ]
  ],
  "codigo": "266-1",
  "nome": "VILA_NOVA"
}
```

- GET - Search line by name (If the contains the passed string)

>http://localhost:8080/line-service/v1/lines/name/ipiranga


```json
{
  "lines": [
    {
        "id": 5128,
        "codigo": "3973-1",
        "nome": "BONSUCESSO_-_VIA_IPIRANGA"
    },
    {
        "id": 5129,
        "codigo": "3973-2",
        "nome": "BONSUCESSO_-_VIA_IPIRANGA"
    },
    {
        "id": 5112,
        "codigo": "343-1",
        "nome": "CAMPUS_-_IPIRANGA"
    },
    {
        "id": 5113,
        "codigo": "343-2",
        "nome": "CAMPUS_-_IPIRANGA"
    },
    {
        "id": 5892,
        "codigo": "G343-2",
        "nome": "GREVE_CAMPUS-IPIRANGA"
    },
    {
        "id": 5891,
        "codigo": "G343-1",
        "nome": "GREVE_CAMPUS-IPIRANGA"
    },
    {
        "id": 5610,
        "codigo": "353-1",
        "nome": "IPIRANGA_-_PUC"
    },
    {
        "id": 5611,
        "codigo": "353-2",
        "nome": "IPIRANGA_-_PUC"
    },
    {
        "id": 5626,
        "codigo": "3943-1",
        "nome": "MAPA_-_VIA_IPIRANGA"
    },
    {
        "id": 5627,
        "codigo": "3943-2",
        "nome": "MAPA_-_VIA_IPIRANGA"
    },
    {
        "id": 5630,
        "codigo": "3945-2",
        "nome": "MAPA_VIA_IPIRANGA-CIRCULAR_NO_BAIRRO"
    },
    {
        "id": 5141,
        "codigo": "3983-2",
        "nome": "PINHEIRO_-_VIA_IPIRANGA"
    },
    {
        "id": 5140,
        "codigo": "3983-1",
        "nome": "PINHEIRO_-_VIA_IPIRANGA"
    }
  ]
}
```

- GET - Find buses in range using latitude and longitude

> http://localhost:8080/line-service/v1/lines/find_near?distance=0.05&lat=-30.146200568328&lng=-51.214993133554

-Parameters:

	distance(double) -> Range in Kilometers (Km)
	lat(double) -> Latitude
	lng(double -> Longitude
	
```json
{
  "lines": [
    {
      "id": 5486,
      "codigo": "T11-2",
      "nome": "3-A_PERIMETRAL"
    },
    {
      "id": 5810,
      "codigo": "GT11-2",
      "nome": "GREVE_T11"
    }
  ]
}
```
