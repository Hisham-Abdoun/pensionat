## Pensionat & Kundtjänst – Microservices System

Detta projekt är en vidareutveckling av pensionatssystemet från Backend 1. Applikationen har delats upp från en monolit till en microservice-arkitektur bestående av två fristående Spring Boot-tjänster som kommunicerar via REST.



## Tjänstebeskrivning

# 1. Bokningstjänsten (Pensionat)
- Beskrivning: Ansvarar för hantering av rum och bokningar samt innehåller systemets frontend (Thymeleaf + Bootstrap).
- Ändring från Backend 1: Customer-entiteten och kundtabellen har tagits bort helt från denna databas. Bokningar sparar nu endast `kundId`.
- Databas: Egen dedikerad databas.

# 2. Kundtjänsten
- Beskrivning: En helt ny och fristående Spring Boot-applikation som ansvarar för ALL kundhantering (CRUD).
- Databas: Egen dedikerad databas (Ej H2/SQLite).



## Kommunikation mellan tjänsterna (REST API)

Tjänsterna kommunicerar synkront via REST-anrop med JSON-format:

1. Vid ny bokning: Bokningstjänsten anropar Kundtjänsten (`GET /kunder/{id}`) för att verifiera att kunden existerar innan bokningen skapas.
2. Vid borttagning av kund: Kundtjänsten anropar Bokningstjänsten för att kontrollera om kunden har aktiva bokningar. Om aktiva bokningar finns avbryts borttagningen.
3. Feltolerans: Om den andra tjänsten är nere kraschar inte systemet, utan återkopplar med ett tydligt felmeddelande till användaren.



## HTTP-statuskoder

- `200 OK` – Förfrågan lyckades
- `201 Created` – Resurs skapad
- `400 Bad Request` – Valideringsfel / Felaktig inmatning
- `404 Not Found` – Kund eller rum hittades inte
- `409 Conflict` – Dubbelbokning eller försök att radera kund med aktiva bokningar



## Hur man startar hela systemet (Docker Compose)

Hela systemet startas via Docker Compose med ett enda kommando:

```bash
docker compose up --build