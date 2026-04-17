# blog-api

REST API dla bloga programistycznego. Umożliwia zarządzanie postami z pełnym cyklem życia publikacji, systemem ról użytkowników oraz uwierzytelnianiem opartym na JWT.

## 📋 Spis treści

- [Funkcjonalności](#funkcjonalności)
- [Technologie](#technologie)
- [Uruchomienie lokalne](#uruchomienie-lokalne)
- [Zmienne środowiskowe](#zmienne-środowiskowe)
- [Dokumentacja API](#dokumentacja-api-swagger)
- [Role i uprawnienia](#role-i-uprawnienia)
- [Cykl życia posta](#cykl-życia-posta)
- [Struktura projektu](#struktura-projektu)
- [Testy](#testy)

---

## Funkcjonalności

- **CRUD postów** — tworzenie, edycja, usuwanie i pobieranie postów
- **Post lifecycle** — kontrolowane przejścia między statusami posta z regułami biznesowymi
- **Role i uprawnienia** — zróżnicowany dostęp dla ról ADMIN, EDITOR i USER
- **Uwierzytelnianie JWT** — rejestracja, logowanie i odświeżanie tokenów

---

## Technologie

- **Java 17** + **Spring Boot 3**
- **Spring Security** + **JWT** — uwierzytelnianie i autoryzacja
- **Spring Data JPA** + **PostgreSQL** — warstwa danych
- **springdoc-openapi 2** — interaktywna dokumentacja API (Swagger UI)
- **Docker / docker-compose** — konteneryzacja
- **Maven** — budowanie projektu

---

## Uruchomienie lokalne

### Wymagania

- Java 17+
- Maven 3.8+
- Docker i Docker Compose

### 1. Sklonuj repozytorium

```bash
git clone https://github.com/TomosZZZ/blog-api.git
cd blog-api
```

### 2. Skonfiguruj application.yml

Sprawdź `src/main/resources/application.yml` i w razie potrzeby dostosuj dane dostępowe do bazy oraz klucz JWT.

### 3. Uruchom bazę danych przez Docker Compose

```bash
docker-compose up -d
```

Uruchamia kontener PostgreSQL 15 na porcie `5432` z bazą `blog`, użytkownikiem `user` i hasłem `password` — zgodnie z domyślną konfiguracją `application.yml`.

### 4. Uruchom aplikację

```bash
./mvnw spring-boot:run
```

Aplikacja wystartuje na `http://localhost:8080`.

> 🚧 **Roadmap:** Docelowo docker-compose będzie obejmował również backend i frontend, umożliwiając uruchomienie całego projektu jedną komendą.

---

## Konfiguracja (application.yml)

Konfiguracja aplikacji znajduje się w `src/main/resources/application.yml`. Przed uruchomieniem upewnij się, że dane dostępowe do bazy zgadzają się z konfiguracją docker-compose:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/blog
    username: user
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect

app:
  jwt:
    secret: <twój-klucz-jwt>
    accessTtlMinutes: 15
    refreshTtlDays: 14
```

---

## Docker Compose

Plik `docker-compose.yml` uruchamia bazę danych PostgreSQL:

```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    container_name: blog-db
    restart: always
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: blog
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data

volumes:
  db_data:
```

> 🚧 **Roadmap:** Docelowo docker-compose będzie obejmował również backend i frontend, umożliwiając uruchomienie całego projektu jedną komendą.

---



Po uruchomieniu aplikacji interaktywna dokumentacja dostępna jest pod adresem:

```
http://localhost:8080/swagger-ui.html
```

Specyfikacja OpenAPI w formacie JSON:

```
http://localhost:8080/v3/api-docs
```

---

## Role i uprawnienia

System zawiera trzy role użytkowników:

| Akcja | USER | EDITOR | ADMIN |
|---|:---:|:---:|:---:|
| Czytanie opublikowanych postów | ✅ | ✅ | ✅ |
| Tworzenie postów (DRAFT) | ❌ | ✅ | ✅ |
| Edycja własnych postów | ❌ | ✅ | ✅ |
| Wysyłanie posta do recenzji | ❌ | ✅ | ✅ |
| Recenzowanie postów (approve/reject) | ❌ | ❌ | ✅ |
| Publikowanie postów | ❌ | ✅ | ✅ |
| Zarządzanie użytkownikami | ❌ | ❌ | ✅ |
| Usuwanie dowolnych postów | ❌ | ❌ | ✅ |

---

## Cykl życia posta

Każdy post przechodzi przez ściśle określone statusy:

```
                        ┌─────────────────┐
                        │      DRAFT      │◄──────────────┐
                        └────────┬────────┘               │
                                 │                        │
                         autor wysyła                     │
                        do recenzji                       │
                                 │                        │
                        ┌────────▼────────┐               │
                        │    IN_REVIEW    │               │
                        └────────┬────────┘               │
                                 │                        │
                    ┌────────────┴────────────┐           │
                    │                         │           │
              EDITOR/ADMIN             EDITOR/ADMIN       │
               zatwierdza               odrzuca           │
                    │                         │           │
           ┌────────▼────────┐    ┌───────────▼─────────┐ │
           │    APPROVED     │    │   CHANGES_REQUIRED  │─┘
           └────────┬────────┘    └─────────────────────┘
                    │
             EDITOR/ADMIN
              publikuje
                    │
           ┌────────▼────────┐
           │    PUBLISHED    │
           └─────────────────┘
```

| Przejście | Kto może wykonać |
|---|---|
| `DRAFT → IN_REVIEW` | EDITOR, ADMIN (właściciel posta) |
| `IN_REVIEW → APPROVED` | ADMIN |
| `IN_REVIEW → CHANGES_REQUIRED` | ADMIN |
| `CHANGES_REQUIRED → DRAFT` | EDITOR, ADMIN (właściciel posta) |
| `APPROVED → PUBLISHED` | EDITOR, ADMIN |

---

## Struktura projektu

```
src/
├── main/
│   ├── java/com/tomcode/api/blog/
│   │   ├── auth/            # Rejestracja, logowanie, odświeżanie tokenów
│   │   ├── post/
│   │   │   ├── controller/  # Endpointy REST
│   │   │   ├── dto/         # Obiekty żądań i odpowiedzi
│   │   │   ├── entity/      # Encje JPA
│   │   │   ├── repository/  # Repozytoria Spring Data
│   │   │   └── service/     # Logika biznesowa i lifecycle
│   │   ├── user/            # Zarządzanie użytkownikami i rolami
│   │   ├── security/        # JWT, filtry, konfiguracja Spring Security
│   │   ├── config/          # Konfiguracja OpenAPI, CORS i inne
│   │   ├── common/          # Współdzielone klasy pomocnicze
│   │   └── BlogApplication.java
│   └── resources/
│       └── application.yml  # Konfiguracja aplikacji
└── test/
    └── java/com/tomcode/api/blog/
        ├── post/
        │   ├── entity/
        │   └── service/     # PostServiceTest, PostLifecycleServiceTests
        ├── user/
        │   └── service/
        └── util/
```

---

## Testy

Uruchomienie wszystkich testów:

```bash
./mvnw test
```

---

## Autor

**TomosZZZ** — [@TomosZZZ](https://github.com/TomosZZZ)
