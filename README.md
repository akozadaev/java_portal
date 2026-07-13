# Портал обращений граждан

Краткое описание учебного backend-проекта на Java 17 и Spring Boot 3.1.1.

## Назначение

Проект демонстрирует типичный backend-сервис для приема и асинхронной обработки обращений граждан.

Пользователь создает заявку через REST API. Сервис сохраняет ее в PostgreSQL со статусом `NEW`, затем scheduler переводит заявку в `PROCESSING`, отправляет событие в Kafka, consumer завершает обработку и ставит статус `COMPLETED`. После завершения обработки заявителю отправляется email с текстом:

```text
Ваша заявка обработана
```

## Основной стек

- Java 17
- Spring Boot 3.1.1
- Spring Web
- Spring Validation
- Spring Data JPA
- Spring Security
- Spring Kafka
- Spring Mail
- Liquibase
- PostgreSQL 15
- Kafka + Zookeeper
- Mailpit для локальной проверки писем
- Docker Compose
- Testcontainers
- JUnit 5
- Awaitility
- Swagger/OpenAPI через springdoc

## Что умеет сервис

- Создать обращение.
- Провалидировать входные данные.
- Нормализовать телефон.
- Сохранить обращение в PostgreSQL.
- Получить обращение по `id`.
- Получить список обращений.
- Фильтровать обращения по статусу.
- Асинхронно обработать обращение через scheduler и Kafka.
- Перевести обращение в статус `COMPLETED`.
- Отправить email после завершения обработки.
- Показать Swagger UI.

## Жизненный цикл заявки

```text
POST /api/v1/applications
    -> status = NEW
    -> scheduler
    -> status = PROCESSING
    -> Kafka producer
    -> Kafka topic
    -> Kafka consumer
    -> status = COMPLETED
    -> email notification
```

## Состав заявки

Заявка содержит:

- `id`
- `fullName`
- `phone`
- `email`
- `text`
- `status`
- `createdAt`
- `updatedAt`
- `processedAt`

## Основные статусы

- `NEW` - заявка создана.
- `PROCESSING` - заявка взята в обработку.
- `COMPLETED` - заявка обработана.
- `ERROR` - ошибка обработки.

## Локальный запуск

Поднять инфраструктуру:

```bash
make infra-up
```

Запустить приложение:

```bash
make run-dev
```

Остановить инфраструктуру:

```bash
make infra-down
```

## REST API

Создать заявку:

```bash
curl -i -X POST http://localhost:8080/api/v1/applications \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "Иван Иванов",
    "phone": "8-999-000-11-22",
    "email": "ivan@example.com",
    "text": "Прошу рассмотреть мое обращение."
  }'
```

Получить заявку по `id`:

```bash
curl http://localhost:8080/api/v1/applications/<id>
```

Получить все заявки:

```bash
curl http://localhost:8080/api/v1/applications
```

Получить заявки по статусу:

```bash
curl 'http://localhost:8080/api/v1/applications?status=COMPLETED'
```

## Полезные адреса

- Приложение: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Healthcheck: `http://localhost:8080/actuator/health`
- Mailpit UI: `http://localhost:8025`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

## Проверка

Быстрые тесты:

```bash
make test-fast
```

Сборка jar без тестов и Checkstyle:

```bash
make package-fast
```

Обычная сборка без тестов:

```bash
./mvnw -DskipTests package
```

## Дополнительные материалы

- `legacy-module` - пример плохого legacy-кода.
- `modern-service` - пример рефакторинга legacy-кода.
