## Pensionat – enkelt bokningssystem

Ett litet **Spring Boot 3 / Java 17**-projekt f?r att hantera bokningar p? ett pensionat.  
Systemet anv?nder **H2 in?memory**-databas, **JPA**, **Thymeleaf** och **Bootstrap**.

### Funktioner

- **Kunder**
  - Skapa, lista, uppdatera och ta bort kunder
  - Kan inte radera kund som har bokningar
- **Rum**
  - Skapa och lista rum (`SINGLE` / `DOUBLE`, extras?ngar, pris per natt)
  - En metod f?r att s?ka lediga rum givet datum och antal g?ster
- **Bokningar**
  - Skapa, lista, uppdatera och avboka bokningar
  - Kontroll mot **dubbelbokning** av samma rum (datum?verlappar inte)
  - Validering att **slutdatum ?r efter startdatum**
  - Validering av obligatoriska f?lt och minsta antal g?ster
- **Startdata (Code First)**
  - En `CommandLineRunner`-bean (`DataInitializer`) skapar n?gra kunder, rum och bokningar vid uppstart

### Teknik

- **Spr?k:** Java 17  
- **Bygg:** Maven  
- **Ramverk:** Spring Boot 3.5  
- **Databas:** H2 in?memory (ingen extern databas beh?vs)  
- **Vy:** Thymeleaf + Bootstrap  

### Komma ig?ng

Krav:

- Java 17 installerat
- Maven (om du inte anv?nder IDE:ns inbyggda st?d)

Klona projektet och k?r:

```bash
mvn spring-boot:run
```

eller i en IDE (t.ex. IntelliJ / VS Code / Eclipse):

- K?r `org.example.pensionat.PensionatApplication` som en Spring Boot?applikation.

N?r applikationen ?r ig?ng:

- G? till `http://localhost:8080/bookings` i webbl?saren.

### Viktiga URL:er

- `GET /bookings` – lista bokningar + formul?r f?r ny bokning
- `GET /bookings/edit/{id}` – redigera bokning
- `GET /bookings/delete/{id}` – avboka
- `GET /bookings/search` – s?k efter lediga rum
- `GET /customers` – lista / skapa kunder
- `GET /customers/edit/{id}` – redigera kund
- `GET /rooms` – lista / skapa rum

### Startdata (DataInitializer)

Vid uppstart k?rs `DataInitializer` (i paketet `config`) som:

- Skapar tre exempel?kunder
- Skapar fyra rum (single/double, med/utan extras?ng)
- Skapar tre bokningar med olika datum

Detta g?r att du direkt ser data i gr?nssnittet utan att beh?va l?gga in n?got manuellt.

### Validering

Exempel p? valideringsregler:

- `BookingDto`
  - `startDate` / `endDate` ?r obligatoriska
  - `numberOfGuests >= 1`
  - Metoden `isDateRangeValid()` (annoterad med `@AssertTrue`) kr?ver att **slutdatum ?r efter startdatum**  
    – felmeddelandet visas b?de vid ny bokning och vid ?ndring.
- Entiteterna (`Customer`, `Room`, `Booking`) har motsvarande valideringsannoteringar.

Valideringsfel visas:

- Som r?da texter under respektive f?lt i formul?ren
- Som en r?d alert ?verst p? sidan f?r vissa fel (t.ex. dubbelbokning av rum)

### Tester

Projektet inneh?ller enhetstester med **JUnit 5** och **Mockito** f?r:

- `BookingService`
- `RoomService`
- `CustomerService`
- En enkel `PensionatApplicationTests` f?r att teststarta Spring?kontexten

K?r testerna med:

```bash
mvn test
```

### H2?konsol

F?r att inspektera databasen under k?rning:

- G? till `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:pensionatdb`
- User: `sa`
- Password: (tom str?ng)

