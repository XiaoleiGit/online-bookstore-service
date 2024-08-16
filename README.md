# Online bookstore service

## Get project up and running
### Option 1: Use IntelliJ IDEA
1. Load project as maven project in IntelliJ IDEA
2. Run command: mvn clean install
3. Run command: mvn spring-boot:run

### Option 2: run jar
1. Go to jar file directory
2. Run command: java -jar online-bookstore-service-1.0-SNAPSHOT.jar

## API Design

### Option 1 (Preferred): Swagger address
1. Run application
2. check API doc via swagger address: http://localhost:8080/swagger-ui/index.html#
3. Try APIs by filling request body values

### Option 2: online Swagger Editor
1. Open this link: https://editor.swagger.io/
2. Copy the json content in APIDOC.json file and paste into Swagger Editor