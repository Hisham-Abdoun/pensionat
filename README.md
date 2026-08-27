## Booking Service – REST API Mikroservice

En **Spring Boot 3 / Java 17**-mikroservice som tillhandahåller ett REST API för bokningshantering.  
Systemet använder **MySQL**-databas, **JPA**, och **SpringDoc OpenAPI** för code-first API-dokumentation.

Denna service är designad för att användas av andra mikrotjänster (t.ex. customer-service) via REST API.

### Funktioner

- **Bokningar**
  - Skapa, lista, uppdatera och avboka bokningar via REST API
  - Kontroll mot **dubbelbokning** av samma rum (datumöverlappar inte)
  - Validering att **slutdatum är efter startdatum**
  - Validering av obligatoriska fält och minsta antal gäster
  - Sök efter tillgängliga rum givet datum och antal gäster
- **Rum**
  - Hämta alla rum via REST API
  - Hämta specifikt rum via ID
  - Skapa nya rum
- **Kunder**
  - Hämta alla kunder via REST API
  - Hämta specifik kund via ID
  - Skapa, uppdatera och ta bort kunder
  - Kan inte radera kund som har bokningar
- **Startdata (Code First)**
  - En `CommandLineRunner`-bean (`DataInitializer`) skapar några kunder, rum och bokningar vid uppstart
- **OpenAPI-dokumentation**
  - Automatiskt genererad API-dokumentation via Swagger UI

### Teknik

- **Språk:** Java 17  
- **Bygg:** Maven  
- **Ramverk:** Spring Boot 3.5  
- **Databas:** MySQL  
- **API-dokumentation:** SpringDoc OpenAPI (Swagger UI)

### Komma igång

#### Alternativ 1: Lokal utveckling

Krav:

- Java 17 installerat
- MySQL installerat och körs
- Maven (om du inte använder IDE:ns inbyggda stöd)

**Databas-setup:**

Skapa en MySQL-databas med namnet `booking_service`:

```sql
CREATE DATABASE booking_service;
```

Skapa `application-local.properties` i `src/main/resources/` med dina MySQL-uppgifter:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_service
spring.datasource.username=root
spring.datasource.password=ditt_lösenord
```

**Starta applikationen:**

```bash
mvn spring-boot:run
```

När applikationen är igång:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html` – Interaktiv API-dokumentation
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs` – API-specifikation i JSON-format

#### Alternativ 2: Docker Desktop

Krav:

- Docker Desktop installerat

**Starta med docker-compose:**

```bash
docker-compose up --build
```

Detta startar både MySQL-databasen och booking-service i separata containers.

När containers är igång:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **API:** `http://localhost:8080/api/bookings`

**Stoppa containers:**

```bash
docker-compose down
```

#### Alternativ 3: Kubernetes

Krav:

- Kubernetes kluster, till exempel Docker Desktop Kubernetes eller ett externt kluster
- kubectl konfigurerat

**Bygg Docker image:**

```bash
docker build -t booking-service:latest .
```

**Deploy till Kubernetes:**

```bash
# För Docker Desktop Kubernetes
kubectl config use-context docker-desktop

# Skapar secret, MySQL och booking-service
kubectl apply -f k8s/
```

**Kontrollera status:**

```bash
kubectl get pods
kubectl get services
```

**Accessa service:**

För lokal utveckling med Docker Desktop Kubernetes:

```bash
kubectl port-forward service/booking-service 8081:8081
```

För riktigt kluster med LoadBalancer, använd den externa IP-adressen som tilldelas.

### REST API Endpoints

#### Bokningar (`/api/bookings`)

- `GET /api/bookings` – Hämta alla bokningar
- `GET /api/bookings/{id}` – Hämta bokning via ID
- `POST /api/bookings` – Skapa ny bokning
- `PUT /api/bookings/{id}` – Uppdatera bokning
- `DELETE /api/bookings/{id}` – Avboka bokning
- `GET /api/bookings/search?startDate=&endDate=&numberOfGuests=` – Sök tillgängliga rum

#### Kunder (`/api/customers`)

- `GET /api/customers` – Hämta alla kunder
- `GET /api/customers/{id}` – Hämta kund via ID
- `POST /api/customers` – Skapa ny kund
- `PUT /api/customers/{id}` – Uppdatera kund
- `DELETE /api/customers/{id}` – Ta bort kund

#### Rum (`/api/rooms`)

- `GET /api/rooms` – Hämta alla rum
- `GET /api/rooms/{id}` – Hämta rum via ID
- `POST /api/rooms` – Skapa nytt rum

### Startdata (DataInitializer)

Vid uppstart körs `DataInitializer` (i paketet `config`) som:

- Skapar tre exempelkunder
- Skapar fyra rum (single/double, med/utan extrasäng)
- Skapar tre bokningar med olika datum

Detta gör att du direkt har data att arbeta med via API:et.

### Validering

Exempel på valideringsregler:

- `BookingDto`
  - `startDate` / `endDate` är obligatoriska
  - `numberOfGuests >= 1`
  - Metoden `isDateRangeValid()` (annoterad med `@AssertTrue`) kräver att **slutdatum är efter startdatum**
- Entiteterna (`Customer`, `Room`, `Booking`) har motsvarande valideringsannoteringar.

Valideringsfel returneras som HTTP 400 med detaljerad felinformation i JSON-format.

### HTTP Status Codes

- `200 OK` – Lyckat GET, PUT, DELETE
- `201 Created` – Lyckat POST
- `400 Bad Request` – Valideringsfel
- `404 Not Found` – Resurs hittades inte
- `409 Conflict` – T.ex. rum redan bokat eller kund har bokningar
- `500 Internal Server Error` – Oväntat fel

### Tester

Projektet innehåller enhetstester med **JUnit 5** och **Mockito** för:

- `BookingService`
- `RoomService`
- `CustomerService`
- En enkel `PensionatApplicationTests` för att teststarta Spring-kontexten

Kör testerna med:

```bash
mvn test
```

### Databas

För att inspektera MySQL-databasen under körning, använd ditt föredragna MySQL-klientverktyg (t.ex. MySQL Workbench, DBeaver, eller kommandoraden):

```bash
mysql -u root -p booking_service
```

### Code-First Approach

Detta projekt använder en code-first metod för API-utveckling:

1. Entiteter och DTO:er definieras först
2. REST-controllern annoteras med OpenAPI-annoteringar (`@Operation`, `@ApiResponse`, etc.)
3. SpringDoc genererar automatiskt OpenAPI-specifikation
4. Swagger UI visualiserar API:et interaktivt
5. API-klienter kan genereras från OpenAPI-specifikationen

