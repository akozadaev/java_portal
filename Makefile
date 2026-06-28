SHELL := /bin/bash
MVNW := ./mvnw

.DEFAULT_GOAL := help

.PHONY: help clean compile test-compile test package run

help:
	@printf "Доступные цели:\n"
	@printf " make clean Удалить артефакты сборки Maven\n"
	@printf " make compile Компилировать исходный код основного приложения\n"
	@printf " make test-compile Компилировать исходный код основного приложения и тестов\n"
	@printf " make test Запустить тесты, включая тесты Testcontainers\n"
	@printf " make package Собрать jar-файл приложения\n"
	@printf " make run Запустить приложение с профилем разработчика\n"

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
