# Telegram Bot Reminder

A training Telegram bot built with Spring Boot that accepts messages in the format `dd.MM.yyyy HH:mm Reminder text`,
stores tasks in PostgreSQL, and sends reminders on schedule via a scheduler. The project already separates layers into
listener, service, repository, provider, and scheduler, while cron configuration and local secrets are configured via
`application.properties` and external `env/.env.properties`.

## Features

- Handles the `/start` command and sends a welcome message.
- Accepts reminders in the format `14.05.2026 18:30 Do homework`.
- Stores tasks in PostgreSQL via Spring Data JPA.
- Runs a check for overdue/current tasks using a cron schedule.
- Sends notifications in a readable date format `dd.MM.yyyy HH:mm`.

## Stack

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- Liquibase
- PostgreSQL
- [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)
- JUnit 5 / Mockito

## Project Structure

```text
src/main/java/pro/sky/telegrambot
├── configuration     # TelegramBot configuration
├── constant          # Constants and regex patterns
├── listener          # Incoming updates handling
├── model             # JPA entity
├── provider          # Text and SendMessage building
├── repository        # Data access
├── scheduler         # Sending reminders via cron
└── service           # Business logic
```

## Message Format

The bot expects a message in the following format:

```text
14.05.2026 18:30 Do homework
```

Where:

- `14.05.2026 18:30` — reminder date and time;
- everything after the space — reminder text.

## Configuration

The main config is located in `src/main/resources/application.properties`, and local secrets are connected via
`spring.config.import=optional:file:env/.env.properties`. This approach follows externalized configuration in Spring
Boot and allows keeping tokens/passwords out of git.

### `application.properties`

```properties
spring.config.import=optional:file:env/.env.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/telegramBot
spring.liquibase.change-log=classpath:liquibase/changelog-master.yml
app.notifications.cron=0 * * * * *
app.notifications.zone=Europe/Moscow
```

### `env/.env.properties`

```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
telegram.bot.token=YOUR_TELEGRAM_BOT_TOKEN
telegram.bot.username=YOUR_TELEGRAM_BOT_USERNAME
```

### `.gitignore`

```gitignore
env/
```

## Running the Project

### 1. Start PostgreSQL

Create the database:

```sql
CREATE
DATABASE telegramBot;
```

### 2. Create a local secrets file

Create the file `env/.env.properties` and fill it with your values.

### 3. Run the application

Via Maven:

```bash
./mvnw spring-boot:run
```

Or from IntelliJ IDEA via `TelegramBotApplication`.

## How It Works

1. The user sends a message in Telegram.
2. `TelegramBotUpdatesListener` receives the update and checks the text format.
3. `NotificationTaskService` parses the date, validates it, and saves the task to the database.
4. `NotificationsScheduler` selects all tasks with `sendAt <= now` and `sent = false` using cron.
5. After successful sending, the task is marked as sent.

## Tests

The project includes unit tests for service, scheduler, and provider. To run tests, use:

```bash
./mvnw test
```

## Future Improvements

- Add custom exceptions for parsing and sending errors.
- Move the date formatter into a single shared constant.
- Add tests for `TelegramBotUpdatesListener`.
- Configure separate profiles for local/dev/prod.
- Reduce logging level in production to `WARN`/`ERROR`.
