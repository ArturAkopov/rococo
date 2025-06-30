# Этап сборки приложения
FROM node:22.6.0-alpine AS build

# Аргументы сборки
ARG NPM_COMMAND=build
ARG VERSION=latest

WORKDIR /app

# Копируем зависимости и устанавливаем их
COPY package.json ./
COPY package-lock.json ./
RUN npm install

# Копируем исходный код
COPY . ./

# Запускаем сборку с указанной командой
RUN npm run ${NPM_COMMAND}

FROM nginx:1.27.1-alpine

# Аргументы (можно переопределить при сборке)
ARG VERSION
ENV VERSION=${VERSION}
ENV TZ=Europe/Moscow

# Копируем конфигурацию nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Копируем собранное приложение из этапа build
COPY --from=build /app/build /usr/share/nginx/html/

# Экспонируем порт и запускаем nginx
CMD ["nginx", "-g", "daemon off;"]