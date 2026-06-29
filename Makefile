SHELL := /bin/bash
MVNW := ./mvnw
COMPOSE := docker compose

.DEFAULT_GOAL := help

.PHONY: help clean compile test-compile test package run run-dev infra-up infra-down infra-logs

help:
	@printf "Доступные цели:\n"
	@printf "  make clean         Удалить артефакты сборки Maven\n"
	@printf "  make compile       Компилировать исходный код основного приложения\n"
	@printf "  make test-compile  Компилировать исходный код основного приложения и тестов\n"
	@printf "  make test          Запустить тесты, включая тесты Testcontainers\n"
	@printf "  make package       Собрать jar-файл приложения\n"
	@printf "  make run           Запустить приложение с профилем dev\n"
	@printf "  make run-dev       Запустить приложение с профилем dev\n"
	@printf "  make infra-up      Поднять PostgreSQL, Zookeeper и Kafka\n"
	@printf "  make infra-down    Остановить локальную инфраструктуру\n"
	@printf "  make infra-logs    Показать логи локальной инфраструктуры\n"

clean:
	$(MVNW) clean

compile:
	$(MVNW) -DskipTests compile

test-compile:
	$(MVNW) -DskipTests test-compile

test:
	$(MVNW) test

package:
	$(MVNW) -DskipTests package

run:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=dev

run-dev: run

infra-up:
	$(COMPOSE) up -d

infra-down:
	$(COMPOSE) down

infra-logs:
	$(COMPOSE) logs -f
