FROM node:21.5.0-bullseye as sources
WORKDIR /app
COPY . /app
RUN npm install && npm run build

FROM node:21.5.0-alpine3.18
WORKDIR /app
COPY --from=sources /app /app
RUN npm install -g serve
EXPOSE 3000
CMD ["serve", "-s", "build", "-l", "3000"]
