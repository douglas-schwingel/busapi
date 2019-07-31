# Bus API
DataPOA based bus API

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

### Why Swagger-UI?

- Auto-generated API Documentation
- Easily customized using annotations
- HTML page with requests samples