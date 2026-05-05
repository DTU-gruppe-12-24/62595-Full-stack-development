# 62595 Full stack development

Download: https://nodejs.org/en

## Setup database

An available MYSQL server is required (change the url, username and password in application.properties if different than example). 

Can for example be run through docker, compose example is available in `backend/docker-compose.yml`

## Setup environment

To run the project a few environment variables must be set. This can be done by copying the `.env.example` file in the `backend` folder to the same folder but being called `.env` and changing any values that should be different.

## Run project

**Backend:**
```bash
cd frontend && npm install && npm run build && cd ../backend && mvn spring-boot:run
```

**Frontend:**
- Build: `cd frontend && npm install && npm run build`
- Dev run: `cd frontend && npm install && npm run dev`
- Note: `npm run build` outputs to Spring Boot locally, while `BUILD_TARGET=server npm run build` creates `dist/` for Nginx in production.

