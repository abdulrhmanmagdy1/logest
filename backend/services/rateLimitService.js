/**
 * ============================================
 * Rate Limit Service — Redis v4 (Azure Cache for Redis compatible)
 * ============================================
 */

const { createClient } = require('redis');
const logger = require('../utils/logger');

class RateLimitService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.defaultLimits = {
      anonymous:     { windowMs: 15 * 60 * 1000, maxRequests: 100 },
      authenticated: { windowMs: 15 * 60 * 1000, maxRequests: 1000 },
      api:           { windowMs: 60 * 1000,       maxRequests: 100 },
      premium:       { windowMs: 15 * 60 * 1000, maxRequests: 10000 },
    };
  }

  async init() {
    const redisUrl = process.env.REDIS_URL;
    if (!redisUrl) {
      logger.warn('REDIS_URL not set — rate limit service running in memory-only mode');
      return;
    }

    try {
      this.client = createClient({
        url: redisUrl,
        socket: {
          tls: redisUrl.startsWith('rediss://'),
          reconnectStrategy: (retries) => Math.min(retries * 100, 3000),
        },
      });

      this.client.on('error', (err) => logger.error('Redis client error:', err.message));
      this.client.on('ready', () => {
        this.connected = true;
        logger.info('Rate limit service connected to Redis');
      });
      this.client.on('end', () => {
        this.connected = false;
        logger.warn('Redis connection closed');
      });

      await this.client.connect();
    } catch (error) {
      logger.error('Failed to connect to Redis — degraded mode active:', error.message);
      this.client = null;
    }
  }

  async checkLimit(key, tier = 'authenticated') {
    try {
      if (!this.client || !this.connected) return { allowed: true };

      const limit = this.defaultLimits[tier] || this.defaultLimits.authenticated;
      const now = Date.now();
      const windowStart = now - limit.windowMs;

      await this.client.zRemRangeByScore(key, 0, windowStart);
      const count = await this.client.zCard(key);

      if (count >= limit.maxRequests) {
        const oldest = await this.client.zRangeWithScores(key, 0, 0);
        const resetTime = oldest.length ? oldest[0].score + limit.windowMs : now + limit.windowMs;
        return {
          allowed: false,
          limit: limit.maxRequests,
          remaining: 0,
          resetTime,
          retryAfter: Math.ceil((resetTime - now) / 1000),
        };
      }

      const member = `${now}-${Math.random().toString(36).slice(2)}`;
      await this.client.zAdd(key, { score: now, value: member });
      await this.client.expire(key, Math.ceil(limit.windowMs / 1000));

      return {
        allowed: true,
        limit: limit.maxRequests,
        remaining: limit.maxRequests - count - 1,
        resetTime: now + limit.windowMs,
      };
    } catch (error) {
      logger.error('Rate limit check error:', error.message);
      return { allowed: true };
    }
  }

  middleware(options = {}) {
    return async (req, res, next) => {
      try {
        const tier = req.user?.apiTier || (req.user ? 'authenticated' : 'anonymous');
        const key = options.keyGenerator ? options.keyGenerator(req) : `ratelimit:${tier}:${req.ip}`;

        if (options.whitelist?.includes(req.ip)) return next();

        const result = await this.checkLimit(key, tier);

        if (result.limit) {
          res.setHeader('X-RateLimit-Limit', result.limit);
          res.setHeader('X-RateLimit-Remaining', Math.max(0, result.remaining ?? 0));
          res.setHeader('X-RateLimit-Reset', Math.ceil((result.resetTime ?? Date.now()) / 1000));
        }

        if (!result.allowed) {
          res.setHeader('Retry-After', result.retryAfter);
          return res.status(429).json({
            success: false,
            message: 'Too many requests, please try again later',
            retryAfter: result.retryAfter,
          });
        }

        next();
      } catch (error) {
        logger.error('Rate limit middleware error:', error.message);
        next();
      }
    };
  }

  async resetLimit(key) {
    try {
      if (this.client && this.connected) await this.client.del(key);
    } catch (error) {
      logger.error('Reset limit error:', error.message);
    }
  }

  async blockIP(ip, duration = 3600) {
    try {
      if (this.client && this.connected) {
        await this.client.set(`blocked:ip:${ip}`, 'blocked', { EX: duration });
        logger.info(`IP blocked: ${ip} for ${duration}s`);
      }
    } catch (error) {
      logger.error('Block IP error:', error.message);
    }
  }

  async isBlocked(ip) {
    try {
      if (!this.client || !this.connected) return false;
      return !!(await this.client.get(`blocked:ip:${ip}`));
    } catch {
      return false;
    }
  }

  async close() {
    if (this.client && this.connected) await this.client.quit();
  }
}

module.exports = new RateLimitService();
