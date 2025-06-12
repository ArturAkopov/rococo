#!/bin/bash

# Проверяем, существует ли файл
COMPOSE_FILE="docker-compose.localenv.yml"
if [ ! -f "$COMPOSE_FILE" ]; then
  echo "Ошибка: файл $COMPOSE_FILE не найден!"
  exit 1
fi

echo "Полная остановка и очистка..."
docker-compose -f "$COMPOSE_FILE" down -v --remove-orphans

# Основной процесс
check_containers

echo "Запускаем новые контейнеры..."
docker-compose -f "$COMPOSE_FILE" up -d

echo "Проверяем статус контейнеров:"
docker-compose -f "$COMPOSE_FILE" ps

echo "Готово! Контейнеры перезапущены."