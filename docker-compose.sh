#!/bin/bash
set -e
set -x

source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)
export FRONT_VERSION="1.0.0"

echo "### Проверка базовых образов ###"
docker-compose pull rococo-all-db zookeeper kafka

echo "### Остановка и очистка предыдущих контейнеров ###"
docker-compose down -v --remove-orphans

echo "### Удаление старых контейнеров ###"
docker ps -a --filter "name=rococo" --format "{{.ID}}" | xargs -r docker rm -f

echo "### Удаление старых образов ###"
docker images --format "{{.Repository}}:{{.Tag}}" | grep "rococo" | xargs -r docker rmi

echo '### Java version ###'
java --version

echo "### Сборка проектов ###"
bash ./gradlew clean

if [ "$1" = "push" ] || [ "$2" = "push" ]; then
  echo "### Сборка и публикация образов ###"
  bash ./gradlew jib
  docker compose push frontend.rococo.dc
else
  echo "### Локальная сборка образов ###"
  bash ./gradlew jibDockerBuild
fi

echo "### Запуск инфраструктуры (БД, Kafka) ###"
docker-compose up -d rococo-all-db zookeeper kafka

echo "### Ожидание готовности БД (10 секунд) ###"
sleep 10

echo "### Проверка образов ###"
for service in auth artist museum painting userdata gateway; do
  if ! docker inspect "${PREFIX}/rococo-${service}-${PROFILE}:latest" >/dev/null 2>&1; then
    echo "Ошибка: образ ${PREFIX}/rococo-${service}-${PROFILE}:latest не найден!"
    exit 1
  fi
done

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