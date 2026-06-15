/**
 * ============================================
 * ⚡ Performance Optimization Service
 * Memory, API, and rendering optimization
 * ============================================
 */

class PerformanceOptimizationService {
  /**
   * Request batching for multiple API calls
   */
  static createBatchProcessor(batchSize = 10, delayMs = 100) {
    let batch = [];
    let timer = null;

    const processBatch = async (requests) => {
      if (requests.length === 0) return [];

      try {
        const results = await Promise.all(requests.map(req => req.executor()));
        return results;
      } catch (error) {
        console.error('Batch processing error:', error);
        throw error;
      }
    };

    return {
      add: (request) => {
        batch.push(request);

        if (batch.length >= batchSize) {
          const currentBatch = [...batch];
          batch = [];
          clearTimeout(timer);
          return processBatch(currentBatch);
        } else {
          clearTimeout(timer);
          timer = setTimeout(() => {
            if (batch.length > 0) {
              const currentBatch = [...batch];
              batch = [];
              processBatch(currentBatch);
            }
          }, delayMs);
        }
      },
      flush: () => {
        if (batch.length > 0) {
          const currentBatch = [...batch];
          batch = [];
          clearTimeout(timer);
          return processBatch(currentBatch);
        }
      }
    };
  }

  /**
   * Intelligent caching with TTL
   */
  static createMemoryCache(ttlSeconds = 300) {
    const cache = new Map();
    const timers = new Map();

    return {
      get: (key) => {
        return cache.get(key);
      },
      set: (key, value) => {
        // Clear old timer
        if (timers.has(key)) {
          clearTimeout(timers.get(key));
        }

        cache.set(key, value);

        // Set auto-expiry
        const timer = setTimeout(() => {
          cache.delete(key);
          timers.delete(key);
        }, ttlSeconds * 1000);

        timers.set(key, timer);
      },
      has: (key) => cache.has(key),
      clear: () => {
        timers.forEach(timer => clearTimeout(timer));
        cache.clear();
        timers.clear();
      },
      getStats: () => ({
        size: cache.size,
        items: Array.from(cache.keys())
      })
    };
  }

  /**
   * Debounce function for API calls
   */
  static debounce(func, wait = 300) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  }

  /**
   * Throttle function for scroll events
   */
  static throttle(func, limit = 300) {
    let inThrottle;
    return function(...args) {
      if (!inThrottle) {
        func.apply(this, args);
        inThrottle = true;
        setTimeout(() => inThrottle = false, limit);
      }
    };
  }

  /**
   * Virtual scrolling helper
   */
  static createVirtualScroller(items, itemHeight, containerHeight) {
    const visibleCount = Math.ceil(containerHeight / itemHeight) + 2;
    const startIndex = Math.max(0, Math.floor(0 / itemHeight) - 1);
    const endIndex = Math.min(items.length, startIndex + visibleCount);

    return {
      visibleItems: items.slice(startIndex, endIndex),
      offsetY: startIndex * itemHeight,
      startIndex,
      endIndex,
      update: (scrollTop) => {
        const newStartIndex = Math.max(0, Math.floor(scrollTop / itemHeight) - 1);
        const newEndIndex = Math.min(items.length, newStartIndex + visibleCount);
        return {
          visibleItems: items.slice(newStartIndex, newEndIndex),
          offsetY: newStartIndex * itemHeight,
          startIndex: newStartIndex,
          endIndex: newEndIndex
        };
      }
    };
  }

  /**
   * API response compression
   */
  static compressData(data) {
    try {
      const json = JSON.stringify(data);
      const compressed = LZ4.encode(json);
      return compressed;
    } catch (error) {
      console.error('Compression error:', error);
      return data;
    }
  }

  /**
   * Lazy load images
   */
  static lazyLoadImage(img) {
    if ('IntersectionObserver' in window) {
      const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const img = entry.target;
            img.src = img.dataset.src;
            img.classList.add('loaded');
            observer.unobserve(img);
          }
        });
      });

      imageObserver.observe(img);
    }
  }

  /**
   * Web worker management
   */
  static createWorker(scriptUrl) {
    if (typeof Worker === 'undefined') {
      console.warn('Web Workers not supported');
      return null;
    }

    const worker = new Worker(scriptUrl);
    const messagePromises = new Map();
    let messageId = 0;

    worker.onmessage = (event) => {
      const { id, result, error } = event.data;
      const promise = messagePromises.get(id);
      if (promise) {
        if (error) {
          promise.reject(new Error(error));
        } else {
          promise.resolve(result);
        }
        messagePromises.delete(id);
      }
    };

    return {
      execute: (command, data) => {
        return new Promise((resolve, reject) => {
          const id = ++messageId;
          messagePromises.set(id, { resolve, reject });
          worker.postMessage({ id, command, data });
        });
      },
      terminate: () => worker.terminate()
    };
  }

  /**
   * Memory-efficient list rendering
   */
  static createPaginatedList(items, pageSize = 20) {
    const totalPages = Math.ceil(items.length / pageSize);
    let currentPage = 1;

    return {
      getCurrentPage: () => currentPage,
      getTotalPages: () => totalPages,
      getPageItems: (page = currentPage) => {
        const startIndex = (page - 1) * pageSize;
        return items.slice(startIndex, startIndex + pageSize);
      },
      nextPage: () => {
        if (currentPage < totalPages) {
          currentPage++;
          return this.getPageItems();
        }
        return [];
      },
      prevPage: () => {
        if (currentPage > 1) {
          currentPage--;
          return this.getPageItems();
        }
        return [];
      },
      goToPage: (page) => {
        if (page >= 1 && page <= totalPages) {
          currentPage = page;
          return this.getPageItems();
        }
        return [];
      }
    };
  }

  /**
   * Performance monitoring
   */
  static createPerformanceMonitor() {
    const metrics = {};

    return {
      startMeasure: (label) => {
        performance.mark(`${label}-start`);
      },
      endMeasure: (label) => {
        performance.mark(`${label}-end`);
        try {
          performance.measure(label, `${label}-start`, `${label}-end`);
          const measure = performance.getEntriesByName(label)[0];
          metrics[label] = measure.duration;
        } catch (error) {
          console.error('Performance measurement error:', error);
        }
      },
      getMetrics: () => metrics,
      reportMetrics: () => {
        const report = {
          timestamp: new Date().toISOString(),
          metrics,
          navigation: window.performance?.getEntriesByType?.('navigation')?.[0],
          memory: performance.memory
        };
        console.table(report);
        return report;
      }
    };
  }

  /**
   * Connection awareness
   */
  static isSlowConnection() {
    if ('connection' in navigator) {
      const conn = navigator.connection;
      return (
        conn.effectiveType === '2g' ||
        conn.effectiveType === '3g' ||
        conn.saveData === true
      );
    }
    return false;
  }

  /**
   * Adaptive loading strategy
   */
  static getAdaptiveStrategy() {
    const isSlowConnection = this.isSlowConnection();
    const isLowEndDevice = navigator.deviceMemory < 4;

    return {
      imageQuality: isSlowConnection ? 'low' : 'high',
      animationLevel: isLowEndDevice ? 'minimal' : 'full',
      preloadContent: !isSlowConnection,
      chunkSize: isSlowConnection ? 5 : 20,
      ttl: isSlowConnection ? 600 : 300
    };
  }

  /**
   * Resource preloading
   */
  static preloadResources(urls) {
    urls.forEach(url => {
      if (url.endsWith('.js')) {
        const link = document.createElement('link');
        link.rel = 'modulepreload';
        link.href = url;
        document.head.appendChild(link);
      } else if (url.endsWith('.css')) {
        const link = document.createElement('link');
        link.rel = 'preload';
        link.as = 'style';
        link.href = url;
        document.head.appendChild(link);
      } else if (url.match(/\.(jpg|png|webp)$/)) {
        const link = document.createElement('link');
        link.rel = 'preload';
        link.as = 'image';
        link.href = url;
        document.head.appendChild(link);
      }
    });
  }

  /**
   * Bundle size analysis
   */
  static analyzeBundleSize() {
    const scripts = document.querySelectorAll('script[src]');
    let totalSize = 0;

    const bundleSizes = Array.from(scripts).map(script => {
      const src = script.src;
      const resource = performance.getEntriesByName(src)?.[0];
      const size = resource?.transferSize || resource?.encodedBodySize || 0;
      totalSize += size;
      return { src, size };
    });

    return {
      totalSize,
      bundles: bundleSizes,
      recommendation: totalSize > 500000 ? 'Consider code splitting' : 'Bundle size is optimal'
    };
  }
}

export default PerformanceOptimizationService;
