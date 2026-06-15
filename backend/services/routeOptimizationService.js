//
/**
 * ============================================
 * 🗺️ Route Optimization Service
 * AI-powered route optimization for logistics
 * ============================================
 */

const logger = require('../utils/logger');

class RouteOptimizationService {
  constructor() {
    this.cache = new Map();
    this.cacheTTL = 30 * 60 * 1000; // 30 minutes
  }

  /**
   * Optimize delivery routes using multiple factors
   * @param {Array} shipments - Array of shipments to optimize
   * @param {Object} constraints - Optimization constraints
   * @returns {Object} Optimized routes
   */
  async optimizeRoutes(shipments, constraints = {}) {
    try {
      const {
        maxRouteTime = 480, // 8 hours in minutes
        maxDistance = 500, // km
        vehicleCapacity = 1000, // kg
        requireRefrigeration = false,
        priorityDeliveries = [],
        avoidTolls = false,
        avoidHighways = false,
        timeWindows = true
      } = constraints;

      // Sort by priority and time windows
      const sortedShipments = this.sortShipmentsByPriority(shipments, priorityDeliveries);

      // Cluster nearby deliveries
      const clusters = this.clusterByProximity(sortedShipments);

      // Optimize each cluster
      const optimizedRoutes = clusters.map((cluster, index) => {
        return this.optimizeCluster(cluster, {
          maxRouteTime,
          maxDistance,
          vehicleCapacity,
          requireRefrigeration,
          avoidTolls,
          avoidHighways,
          routeId: `ROUTE-${Date.now()}-${index}`
        });
      });

      // Calculate total metrics
      const totalMetrics = this.calculateTotalMetrics(optimizedRoutes);

      return {
        success: true,
        routes: optimizedRoutes,
        metrics: totalMetrics,
        savings: this.calculateSavings(optimizedRoutes),
        generatedAt: new Date()
      };
    } catch (error) {
      logger.error('Route optimization error:', error);
      throw error;
    }
  }

  /**
   * Sort shipments by priority and constraints
   */
  sortShipmentsByPriority(shipments, priorityIds) {
    return shipments.sort((a, b) => {
      // Priority shipments first
      const aPriority = priorityIds.includes(a._id.toString()) ? 2 : 0;
      const bPriority = priorityIds.includes(b._id.toString()) ? 2 : 0;

      if (aPriority !== bPriority) return bPriority - aPriority;

      // Then by delivery time window
      const aTime = a.delivery?.timeWindow?.start || '23:59';
      const bTime = b.delivery?.timeWindow?.start || '23:59';

      return aTime.localeCompare(bTime);
    });
  }

  /**
   * Cluster shipments by geographic proximity
   */
  clusterByProximity(shipments) {
    const clusters = [];
    const visited = new Set();
    const maxClusterDistance = 50; // km

    for (const shipment of shipments) {
      if (visited.has(shipment._id.toString())) continue;

      const cluster = [shipment];
      visited.add(shipment._id.toString());

      // Find nearby shipments
      for (const other of shipments) {
        if (visited.has(other._id.toString())) continue;

        const distance = this.calculateDistance(
          shipment.delivery.address.coordinates,
          other.delivery.address.coordinates
        );

        if (distance <= maxClusterDistance) {
          cluster.push(other);
          visited.add(other._id.toString());
        }
      }

      clusters.push(cluster);
    }

    return clusters;
  }

  /**
   * Optimize a single cluster using TSP (Traveling Salesman Problem) approximation
   */
  optimizeCluster(cluster, constraints) {
    const { routeId } = constraints;

    // Use nearest neighbor algorithm for TSP
    const optimized = this.nearestNeighborTSP(cluster);

    // Calculate route metrics
    let totalDistance = 0;
    let totalTime = 0;
    let totalWeight = 0;

    const stops = optimized.map((shipment, index) => {
      const prevStop = index > 0 ? optimized[index - 1] : null;
      
      if (prevStop) {
        const distance = this.calculateDistance(
          prevStop.delivery.address.coordinates,
          shipment.delivery.address.coordinates
        );
        const time = this.estimateTravelTime(distance);
        
        totalDistance += distance;
        totalTime += time;
      }

      totalWeight += shipment.cargo?.weight?.value || 0;

      return {
        sequence: index + 1,
        shipmentId: shipment._id,
        trackingNumber: shipment.trackingNumber,
        address: shipment.delivery.address,
        coordinates: shipment.delivery.address.coordinates,
        timeWindow: shipment.delivery.timeWindow,
        cargo: shipment.cargo,
        estimatedArrival: this.calculateETA(totalTime),
        distanceFromPrevious: index > 0 ? totalDistance : 0
      };
    });

    return {
      routeId,
      stops,
      totalStops: stops.length,
      totalDistance: Math.round(totalDistance * 100) / 100,
      estimatedDuration: Math.round(totalTime),
      totalWeight: Math.round(totalWeight * 100) / 100,
      requiresRefrigeration: constraints.requireRefrigeration,
      feasibility: this.assessFeasibility(totalTime, constraints.maxRouteTime)
    };
  }

  /**
   * Nearest Neighbor TSP approximation
   */
  nearestNeighborTSP(shipments) {
    if (shipments.length <= 1) return shipments;

    const unvisited = [...shipments];
    const route = [];
    
    // Start with first shipment
    let current = unvisited.shift();
    route.push(current);

    while (unvisited.length > 0) {
      let nearest = null;
      let minDistance = Infinity;
      let nearestIndex = -1;

      for (let i = 0; i < unvisited.length; i++) {
        const distance = this.calculateDistance(
          current.delivery.address.coordinates,
          unvisited[i].delivery.address.coordinates
        );

        if (distance < minDistance) {
          minDistance = distance;
          nearest = unvisited[i];
          nearestIndex = i;
        }
      }

      route.push(nearest);
      unvisited.splice(nearestIndex, 1);
      current = nearest;
    }

    return route;
  }

  /**
   * Calculate distance between two coordinates using Haversine formula
   */
  calculateDistance(coord1, coord2) {
    if (!coord1 || !coord2) return 0;

    const R = 6371; // Earth's radius in km
    const dLat = this.toRadians(coord2.lat - coord1.lat);
    const dLon = this.toRadians(coord2.lng - coord1.lng);

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(this.toRadians(coord1.lat)) * 
              Math.cos(this.toRadians(coord2.lat)) *
              Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }

  toRadians(degrees) {
    return degrees * (Math.PI / 180);
  }

  /**
   * Estimate travel time based on distance and traffic
   */
  estimateTravelTime(distanceKm) {
    // Assume average speed of 60 km/h with traffic consideration
    const baseSpeed = 60;
    const trafficFactor = 1.2; // 20% buffer for traffic
    const minutes = (distanceKm / baseSpeed) * 60 * trafficFactor;
    
    // Add stop time (15 minutes per delivery)
    const stopTime = 15;
    
    return Math.round(minutes + stopTime);
  }

  /**
   * Calculate estimated time of arrival
   */
  calculateETA(minutesFromNow) {
    const eta = new Date();
    eta.setMinutes(eta.getMinutes() + minutesFromNow);
    return eta;
  }

  /**
   * Assess route feasibility
   */
  assessFeasibility(estimatedTime, maxTime) {
    const ratio = estimatedTime / maxTime;
    
    if (ratio <= 0.7) return { status: 'optimal', score: 10 };
    if (ratio <= 0.85) return { status: 'good', score: 8 };
    if (ratio <= 1.0) return { status: 'acceptable', score: 6 };
    return { status: 'overloaded', score: 4 };
  }

  /**
   * Calculate total metrics for all routes
   */
  calculateTotalMetrics(routes) {
    let totalDistance = 0;
    let totalDuration = 0;
    let totalStops = 0;

    routes.forEach(route => {
      totalDistance += route.totalDistance;
      totalDuration += route.estimatedDuration;
      totalStops += route.totalStops;
    });

    return {
      totalRoutes: routes.length,
      totalDistance: Math.round(totalDistance * 100) / 100,
      totalDuration,
      totalStops,
      averageRouteDistance: Math.round((totalDistance / routes.length) * 100) / 100,
      averageRouteDuration: Math.round(totalDuration / routes.length),
      averageStopsPerRoute: Math.round((totalStops / routes.length) * 10) / 10
    };
  }

  /**
   * Calculate cost and time savings
   */
  calculateSavings(optimizedRoutes) {
    // Estimate baseline (unoptimized) metrics
    const baselineMultiplier = 1.35; // 35% inefficiency without optimization
    
    const totalOptimizedDistance = optimizedRoutes.reduce(
      (sum, route) => sum + route.totalDistance, 0
    );

    const baselineDistance = totalOptimizedDistance * baselineMultiplier;
    const distanceSaved = baselineDistance - totalOptimizedDistance;
    const fuelSaved = distanceSaved * 0.35; // 0.35L per km average
    const costSaved = distanceSaved * 2.5; // $2.5 per km average cost

    return {
      distanceSaved: Math.round(distanceSaved * 100) / 100,
      fuelSavedLiters: Math.round(fuelSaved * 100) / 100,
      costSaved: Math.round(costSaved * 100) / 100,
      efficiency: Math.round((distanceSaved / baselineDistance) * 100)
    };
  }

  /**
   * Real-time route adjustment based on traffic/conditions
   */
  async adjustRouteForConditions(routeId, conditions) {
    const { trafficDelay, weatherDelay, roadClosure } = conditions;
    
    // Implementation for real-time adjustments
    // This would integrate with external APIs (Google Maps, etc.)
    
    return {
      routeId,
      adjustments: {
        timeAdded: (trafficDelay || 0) + (weatherDelay || 0),
        alternativeRoute: roadClosure ? true : false
      },
      updatedAt: new Date()
    };
  }

  /**
   * Predict optimal dispatch time based on historical data
   */
  async predictOptimalDispatch(origin, destination, cargoType) {
    // This would use ML model in production
    // Simplified implementation
    
    const predictions = {
      morning: { score: 8.5, avgTime: 120 },
      afternoon: { score: 7.2, avgTime: 145 },
      evening: { score: 6.8, avgTime: 160 }
    };

    return {
      origin,
      destination,
      cargoType,
      recommendations: predictions,
      bestTime: 'morning',
      confidence: 0.85
    };
  }
}

module.exports = new RouteOptimizationService();
