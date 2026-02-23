# 62595 Full stack development

Download: https://nodejs.org/en

## Setup database

An available MYSQL server is required (change the url, username and password in application.properties if different than example). 

Can for example be run through docker, compose example is available in `backend/docker-compose.yml`

## Run project

**Backend:**
```bash
cd frontend && npm install && npm run build && cd ../backend && mvn spring-boot:run
```

**Frontend:**
- Build: `cd frontend && npm install && npm run build`
- Dev run: `cd frontend && npm install && npm run dev`
