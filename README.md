# Telegram Bot Reminder

Учебный Telegram-бот на Spring Boot, который принимает сообщения в формате `dd.MM.yyyy HH:mm Текст напоминания`,
сохраняет задачу в PostgreSQL и отправляет напоминание по расписанию через scheduler. В проекте уже вынесены слои
listener, service, repository, provider и scheduler, а cron-настройка и локальные секреты конфигурируются через
`application.properties` и внешний `env/.env.properties`.[web:342][web:249][web:415]

## Возможности

- Обрабатывает команду `/start` и отправляет приветственное сообщение.
- Принимает напоминания в формате `14.05.2026 18:30 Сделать домашку`.
- Сохраняет задачи в PostgreSQL через Spring Data JPA.
- Запускает проверку просроченных/актуальных задач по cron-расписанию.
- Отправляет уведомление в читаемом формате даты `dd.MM.yyyy HH:mm`.

## Стек

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- Liquibase
- PostgreSQL
- [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)
- JUnit 5 / Mockito

## Структура проекта

```text
src/main/java/pro/sky/telegrambot
├── configuration     # Конфигурация TelegramBot
├── constant          # Константы и regex Pattern
├── listener          # Обработка входящих update'ов
├── model             # JPA entity
├── provider          # Формирование текстов и SendMessage
├── repository        # Доступ к данным
├── scheduler         # Отправка напоминаний по cron
└── service           # Бизнес-логика
```

## Формат сообщения

Бот ожидает сообщение вида:

```text
14.05.2026 18:30 Сделать домашнюю работу
```

Где:

- `14.05.2026 18:30` — дата и время напоминания;
- всё после пробела — текст напоминания.

## Конфигурация

Основной конфиг лежит в `src/main/resources/application.properties`, а локальные секреты подключаются через
`spring.config.import=optional:file:env/.env.properties`. Такой способ соответствует externalized configuration в Spring
Boot и позволяет не хранить токены/пароли в git.[web:415][web:416]

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

## Запуск проекта

### 1. Поднять PostgreSQL

Создать базу данных:

```sql
CREATE
DATABASE telegramBot;
```

### 2. Создать локальный файл с секретами

Создать файл `env/.env.properties` и заполнить его своими значениями.

### 3. Запустить приложение

Через Maven:

```bash
./mvnw spring-boot:run
```

Или из IntelliJ IDEA через `TelegramBotApplication`.

## Как это работает

1. Пользователь отправляет сообщение в Telegram.
2. `TelegramBotUpdatesListener` принимает update и проверяет формат текста.
3. `NotificationTaskService` парсит дату, валидирует её и сохраняет задачу в БД.
4. `NotificationsScheduler` по cron выбирает все задачи с `sendAt <= now` и `sent = false`.
5. После успешной отправки задача помечается как отправленная.[web:249][web:342]

## Тесты

В проекте есть unit-тесты для service, scheduler и provider. Для запуска тестов использовать:

```bash
./mvnw test
```

## Дальнейшие улучшения

- Добавить кастомные исключения для ошибок парсинга и отправки.
- Вынести форматтер даты в одну общую константу.
- Добавить тесты для `TelegramBotUpdatesListener`.
- Настроить отдельные профили для local/dev/prod.
- Снизить уровень логирования на проде до `WARN`/`ERROR`.
