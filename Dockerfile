# ============================================
# Edham Logistics — Production Dockerfile
# Canonical target: /backend (Node.js 20 LTS)
# Azure App Service deployment
# ============================================

FROM node:20-alpine AS base
WORKDIR /app

# Install production dependencies only
FROM base AS deps
COPY backend/package*.json ./
RUN npm ci --only=production && npm cache clean --force

# Final production image
FROM base AS runner
ENV NODE_ENV=production

# Create non-root user for security
RUN addgroup -g 1001 -S nodejs && adduser -S nodejs -u 1001

# Copy installed deps
COPY --from=deps --chown=nodejs:nodejs /app/node_modules ./node_modules

# Copy canonical backend source
COPY --chown=nodejs:nodejs backend/ .

# Create required runtime directories
RUN mkdir -p logs && chown -R nodejs:nodejs /app

USER nodejs

EXPOSE 5000

HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD node -e "require('http').get('http://localhost:5000/api/v1/health', (r) => r.statusCode === 200 ? process.exit(0) : process.exit(1))"

CMD ["node", "server.js"]
