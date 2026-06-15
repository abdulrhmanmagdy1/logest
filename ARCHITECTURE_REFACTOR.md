# Enterprise SaaS Refactor (Strangler Pattern)

This repository has been prepared for a non-breaking migration to a scalable SaaS architecture while keeping all existing features operational.

## What Was Refactored

1. **Module registry for API composition**
   - Added `modules/core/moduleRegistry.js`
   - Added `modules/legacy/legacyRouteModules.js`
   - `server.js` now registers API modules through a centralized registry instead of hard-coded scattered route mounts.

2. **SaaS request foundations**
   - Added `middleware/requestContext.js`
     - Injects/propagates `x-correlation-id` for traceability.
   - Added `middleware/tenantContext.js`
     - Resolves tenant/company from `x-tenant-id`, `x-company-id`, or query fallback.
     - Attaches tenant metadata to `req.tenant`.

3. **Production environment safety**
   - Updated `config/environment.js` to fail fast in production if required env variables are missing.

4. **Server stability fix**
   - Added missing `mongoose` import in `server.js` so existing health/shutdown logic can safely access DB connection state.

5. **Enterprise domain mapping (feature-preserving)**
   - Explicitly mapped the following business domains into module registration:
     1) Authentication
     2) Roles & Permissions
     3) Dashboard
     4) Shipment Flow
     5) Driver Tracking
     6) Notifications
     7) Billing
     8) Maintenance
   - Added optional module loading (`safeRequire`) so missing domain routes never break server startup.
   - Added `routes/dashboard.js` as a dashboard facade delegated to existing analytics controllers.
   - Added centralized capability map in `config/permissions.js` and `authorizeCapability` middleware.

## Backward Compatibility

- All existing route paths remain unchanged.
- No feature routes were removed or renamed.
- Middleware additions are passive and non-breaking by default.

## Target Enterprise End-State

The intended architecture is:

1. **API Gateway/BFF**
   - Auth, rate limits, request context, tenant context, API versioning.
2. **Domain Services**
   - Shipments, Fleet, Billing, Tracking, Identity, Notifications, Analytics.
3. **Shared Platform**
   - Event bus, cache, observability, audit, document storage.
4. **Frontend Apps**
   - Role-specific micro-frontends or domain-based modules behind a single shell.
5. **Data**
   - Tenant-aware data strategy with explicit ownership per service boundary.

## Next Refactor Phases

1. **Phase 1 (done in this change)**
   - Introduce cross-cutting SaaS middleware and modular route registry.

2. **Phase 2**
   - Introduce domain modules (`modules/<domain>`) with service/repository layering.
   - Move each legacy route/controller incrementally under a domain module while preserving external API contract.

3. **Phase 3**
   - Extract high-load domains (tracking, billing, notifications) behind internal service interfaces.
   - Add asynchronous event flow for non-critical write paths.

4. **Phase 4**
   - Add gateway-level policy enforcement: tenant isolation guards, quotas, per-plan limits, and API key scoping.

5. **Phase 5**
   - Split deployment units (if required) after observability and contract tests are fully in place.

