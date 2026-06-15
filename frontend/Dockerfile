# ================================================================
# Edham Logistics — Next.js 14 Production Dockerfile
#
# Uses Next.js "standalone" output (set in next.config.mjs) to
# produce a self-contained server with only the files it needs.
# Final image is ~200 MB instead of ~1 GB.
#
# NEXT_PUBLIC_* env vars are baked into the JS bundle at build
# time — pass them as build ARGs via docker-compose.prod.yml.
# ================================================================

# ── Stage 1: Dependency installer ────────────────────────────
FROM node:20-alpine AS deps
WORKDIR /app

# Required for some native npm packages on Alpine
RUN apk add --no-cache libc6-compat

COPY package.json package-lock.json* ./
# --frozen-lockfile ensures the lock file is never auto-updated
RUN npm ci --frozen-lockfile

# ── Stage 2: Builder ─────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app

# Accept build-time public variables
ARG NEXT_PUBLIC_API_BASE_URL
ARG NEXT_PUBLIC_SOCKET_URL
ARG NEXT_PUBLIC_GOOGLE_MAPS_KEY

ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}
ENV NEXT_PUBLIC_SOCKET_URL=${NEXT_PUBLIC_SOCKET_URL}
ENV NEXT_PUBLIC_GOOGLE_MAPS_KEY=${NEXT_PUBLIC_GOOGLE_MAPS_KEY}
ENV NEXT_TELEMETRY_DISABLED=1
ENV NODE_ENV=production

COPY --from=deps /app/node_modules ./node_modules
COPY . .

RUN npm run build

# ── Stage 3: Minimal production runner ───────────────────────
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Non-root user for security
RUN addgroup -g 1001 -S nodejs && adduser -S nextjs -u 1001

# standalone/ contains server.js + node_modules only what's needed
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
# static assets (CSS, JS chunks, images) must be copied separately
COPY --from=builder --chown=nextjs:nodejs /app/.next/static     ./.next/static
# public/ (favicon, og-images, etc.)
COPY --from=builder --chown=nextjs:nodejs /app/public           ./public

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD node -e "require('http').get('http://localhost:3000',r=>r.statusCode<500?process.exit(0):process.exit(1)).on('error',()=>process.exit(1))"

# standalone output produces its own server.js at the root
CMD ["node", "server.js"]
