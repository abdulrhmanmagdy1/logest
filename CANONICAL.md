# Edham Logistics — Canonical Project Structure

## Production Targets (Azure Deployment)

| Layer | Directory | Framework | Deployment Target |
|-------|-----------|-----------|-------------------|
| **Backend API** | `/backend` | Node.js 20 + Express | Azure App Service (B3) |
| **Frontend** | `/frontend` | Next.js 14 (TypeScript) | Azure Static Web Apps |
| **Database** | — | MongoDB (Mongoose) | Azure Cosmos DB for MongoDB |
| **Cache** | — | Redis | Azure Cache for Redis |
| **File Storage** | — | Azure Blob SDK | Azure Blob Storage |

## Deprecated / Legacy Directories

The following directories are **not** deployed to Azure. They are kept for reference only:

- `/client` — Legacy Create React App prototype (superseded by `/frontend`)
- `/server.js` (root) — Legacy monolith entry point (superseded by `/backend/server.js`)
- `/controllers`, `/routes`, `/models`, `/middleware`, `/services`, `/sockets` (root level) — Legacy monolith modules (superseded by `/backend/*`)
- `/mock-backend`, `/mock-server`, `/node-mock-backend` — Local development mocks

## Active Docker Configuration

- `docker-compose.azure.yml` — **Production** (Azure-ready, no bind mounts, no weak credentials)
- `docker-compose.backend.yml` — Local development with MongoDB + Redis

## Environment Variables

Copy `.env.azure.example` and populate with real Azure resource values before deploying.
