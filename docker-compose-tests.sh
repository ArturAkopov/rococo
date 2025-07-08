#!/bin/bash
set -e
set -x

source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export FRONT_VERSION="0.0.1-SNAPSHOT"
export COMPOSE_PROFILES=test

echo "### Проверка базовых образов ###"
docker-compose pull rococo-all-db zookeeper kafka selenoid selenoid-ui allure allure-ui

echo "### Полная очистка окружения ###"
docker-compose down -v --remove-orphans --rmi local

echo "### Удаление образов Rococo ###"
docker images --filter "reference=*rococo*" --format "{{.Repository}}:{{.Tag}}" | xargs -r docker rmi || true

echo '### Java version ###'
java --version

echo "### Сборка проектов ###"
bash ./gradlew clean

echo "### Локальная сборка образов Rococo ###"
for image in "mysql:8.3" "confluentinc/cp-zookeeper:7.3.2" "confluentinc/cp-kafka:7.3.2" "${PREFIX}/rococo-auth-${PROFILE}:latest" "${PREFIX}/rococo-artist-${PROFILE}:latest" "${PREFIX}/rococo-museum-${PROFILE}:latest" "${PREFIX}/rococo-painting-${PROFILE}:latest" "${PREFIX}/rococo-userdata-${PROFILE}:latest" "${PREFIX}/rococo-gateway-${PROFILE}:latest" "${PREFIX}/rococo-client-${PROFILE}:latest" "aerokube/selenoid:1.11.3" "aerokube/selenoid-ui:1.10.11" "${PREFIX}/rococo-e-2-e-tests:latest" "frankescobar/allure-docker-service:2.27.0" "frankescobar/allure-docker-service-ui:7.0.3"; do

  if [[ "$(docker images -q "$image" 2> /dev/null)" == "" ]]; then
    bash ./gradlew clean
    bash ./gradlew jibDockerBuild -x :rococo-e-2-e-tests:test
    echo "### Собраны образы Rococo ###"
    break 2
  fi
done

echo "### Запуск инфраструктуры (БД, Kafka) ###"
docker-compose up -d rococo-all-db zookeeper kafka

echo "### Ожидание готовности БД (10 секунд) ###"
sleep 10

echo "### Проверка образов ###"
for service in auth artist museum painting userdata gateway client; do
  if ! docker inspect "${PREFIX}/rococo-${service}-${PROFILE}:latest" >/dev/null 2>&1; then
    echo "Ошибка: образ ${PREFIX}/rococo-${service}-${PROFILE}:latest не найден!"
    exit 1
  fi
done

echo "### Скачивание образа Chrome для Selenoid ###"
docker pull selenoid/vnc_chrome:127.0

echo "### Запуск всех сервисов ###"
docker-compose up -d || {
  echo "### Ошибка при запуске контейнеров ###"
  docker-compose logs
  exit 1
}

echo "### Статус контейнеров ###"
docker-compose ps

echo "### Логи проблемных контейнеров ###"
docker-compose ps --filter "status=exited" --format "{{.Names}}" | while read -r container; do
  echo "=== Логи $container ==="
  docker logs "$container" || echo "Не удалось получить логи для $container"
done

echo "### Готово! ###"