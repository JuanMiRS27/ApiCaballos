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

La aplicacion detecta automaticamente estos escenarios:

- Si defines `SPRING_DATASOURCE_URL`, usa esa URL.
- Si defines `INSTANCE_CONNECTION_NAME`, arma la conexion a Cloud SQL para Cloud Run.
- Si no defines nada, usa MySQL local en `localhost:3306`.

Para desplegar en Google Cloud:

1. Crea MySQL en Cloud SQL.
2. Crea Artifact Registry.
3. Reemplaza los valores placeholder de `cloudbuild.yaml`.
4. Usa `cloudrun.env.example` como referencia para tus variables reales.
5. Asegurate de que Cloud Run tenga adjunta la instancia Cloud SQL.
6. Ejecuta `gcloud builds submit --config cloudbuild.yaml`.

Configuracion preparada:

- servicio Cloud Run: `apicaballos`
- region: `europe-west1`
- puerto del contenedor: `8080`

No uses "Deploy from source" desde la consola de Cloud Run para este proyecto. Usa `cloudbuild.yaml` o `gcloud run deploy`, porque necesitas adjuntar Cloud SQL y pasar variables de entorno.

Ejemplo de despliegue manual:

```bash
gcloud run deploy apicaballos \
  --image=europe-west1-docker.pkg.dev/TU_PROJECT_ID/cloud-run-source-deploy/apicaballos/apicaballos:latest \
  --region=europe-west1 \
  --platform=managed \
  --allow-unauthenticated \
  --port=8080 \
  --add-cloudsql-instances=TU_PROJECT_ID:europe-west1:apicaballos-db \
  --set-env-vars=DB_NAME=carreras_db,INSTANCE_CONNECTION_NAME=TU_PROJECT_ID:europe-west1:apicaballos-db,SPRING_DATASOURCE_USERNAME=root,SPRING_DATASOURCE_PASSWORD=TU_PASSWORD,APP_JWT_SECRET=TU_SECRET,APP_ADMIN_USERNAME=admin,APP_ADMIN_PASSWORD=TU_ADMIN_PASSWORD,APP_ADMIN_EMAIL=admin@tu-dominio.com
```

Variables minimas en Cloud Run:

- `INSTANCE_CONNECTION_NAME`
- `DB_NAME`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_ADMIN_PASSWORD`
