# API sencilla de inscripcion de caballos de carreras

API monolitica con Spring Boot, JWT y MySQL. No usa microservicios.

## Funcionalidad

- Registro y login con JWT.
- Roles `ADMIN` y `USER`.
- CRUD basico de caballos: listar, ver detalle y crear.
- Inscripcion de caballos a carreras.
- Docker para desarrollo local.
- Despliegue listo para Google Cloud Run con Cloud SQL.

## Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/horses`
- `GET /api/horses/{id}`
- `POST /api/horses`
- `POST /api/registrations`
- `GET /api/registrations/mine`
- `GET /api/registrations`
- `GET /api/health`

## Ejecutar con Docker

```bash
docker compose up --build
```

Credenciales iniciales del administrador:

- usuario: `admin`
- clave: `Admin123*`

## Google Cloud

1. Crea MySQL en Cloud SQL.
2. Crea Artifact Registry.
3. Ajusta `cloudbuild.yaml`.
4. Ejecuta `gcloud builds submit --config cloudbuild.yaml`.
