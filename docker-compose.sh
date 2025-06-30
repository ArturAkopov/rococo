#!/bin/bash
source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)
export FRONT_VERSION="1.0.0"

docker compose down

docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $docker_images
fi

echo '### Java version ###'
java --version

pull_if_not_exists() {
  local image=$1
  if ! docker image inspect "$image" >/dev/null 2>&1; then
    echo "Pulling image: $image"
    docker pull "$image"
  else
    echo "Image already exists: $image"
  fi
}

echo "### Checking base images ###"
pull_if_not_exists "mysql:8.3"
pull_if_not_exists "confluentinc/cp-zookeeper:7.3.2"
pull_if_not_exists "confluentinc/cp-kafka:7.3.2"

bash ./gradlew clean
if [ "$1" = "push" ]; then
  echo "### Build & push images ###"
  bash ./gradlew jib
  docker-compose push frontend.rococo.dc
else
  echo "### Build images ###"
  bash ./gradlew jibDockerBuild
fi

docker-compose -f docker-compose.yml up -d --build
docker images
docker ps -a

sleep 10

echo "### Problem containers logs ###"
docker-compose ps --filter "status=exited" --format "{{.Names}}" | while read -r container; do
  echo "=== Logs for $container ==="
  docker logs "$container"
done