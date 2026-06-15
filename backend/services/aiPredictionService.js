//
/**
 * ============================================
 * 🤖 AI Prediction Service - خدمة التنبؤ الذكي
 * ============================================
 */

const logger = require('../utils/logger');

class AIPredictionService {
  constructor() {
    this.models = {};
    this.cache = new Map();
  }

  /**
   * Predict delivery time based on historical data
   */
  async predictDeliveryTime(shipmentData) {
    try {
      const { origin, destination, cargoType, pickupTime, weather = 'clear' } = shipmentData;
      
      // Calculate base time from historical averages
      const baseTime = await this.getHistoricalAverageTime(origin, destination);
      
      // Apply multipliers
      const factors = {
        cargoType: this.getCargoTypeMultiplier(cargoType),
        timeOfDay: this.getTimeOfDayMultiplier(pickupTime),
        weather: this.getWeatherMultiplier(weather),
        traffic: await this.getTrafficPrediction(origin, destination, pickupTime)
      };
      
      // Calculate adjusted time
      let adjustedTime = baseTime;
      Object.values(factors).forEach(factor => {
        adjustedTime *= factor;
      });
      
      // Add confidence interval
      const confidenceInterval = baseTime * 0.15; // ±15%
      
      return {
        estimatedTime: Math.round(adjustedTime),
        minTime: Math.round(adjustedTime - confidenceInterval),
        maxTime: Math.round(adjustedTime + confidenceInterval),
        confidence: this.calculateConfidence(factors),
        factors,
        timestamp: new Date()
      };
    } catch (error) {
      logger.error('Delivery time prediction error:', error);
      throw error;
    }
  }

  /**
   * Predict demand for route optimization
   */
  async predictDemand(city, days = 7) {
    try {
      const predictions = [];
      
      for (let i = 0; i < days; i++) {
        const date = new Date();
        date.setDate(date.getDate() + i);
        
        const prediction = await this.predictSingleDayDemand(city, date);
        predictions.push(prediction);
      }
      
      return {
        city,
        predictions,
        summary: {
          totalExpected: predictions.reduce((sum, p) => sum + p.expectedShipments, 0),
          peakDay: predictions.reduce((max, p) => p.expectedShipments > max.expectedShipments ? p : max),
          average: predictions.reduce((sum, p) => sum + p.expectedShipments, 0) / predictions.length
        }
      };
    } catch (error) {
      logger.error('Demand prediction error:', error);
      throw error;
    }
  }

  /**
   * Predict vehicle maintenance needs
   */
  async predictMaintenance(truckId) {
    try {
      const Truck = require('../models/Truck');
      const truck = await Truck.findById(truckId);
      
      if (!truck) {
        throw new Error('Truck not found');
      }
      
      // Calculate maintenance score based on factors
      const factors = {
        mileage: truck.mileage || 0,
        age: this.calculateVehicleAge(truck.year),
        usageIntensity: await this.getUsageIntensity(truckId),
        lastMaintenance: truck.lastMaintenance,
        failureHistory: await this.getFailureHistory(truckId)
      };
      
      const maintenanceScore = this.calculateMaintenanceScore(factors);
      
      return {
        truckId,
        maintenanceScore,
        riskLevel: this.getRiskLevel(maintenanceScore),
        predictedIssues: this.predictIssues(factors),
        recommendedActions: this.getRecommendedActions(maintenanceScore, factors),
        nextServiceDate: this.predictNextService(factors),
        confidence: 0.75
      };
    } catch (error) {
      logger.error('Maintenance prediction error:', error);
      throw error;
    }
  }

  /**
   * Predict temperature anomalies
   */
  async predictTemperatureAnomalies(sensorId, readings) {
    try {
      // Calculate moving average and standard deviation
      const temps = readings.map(r => r.temperature.value);
      const avg = temps.reduce((a, b) => a + b, 0) / temps.length;
      const std = Math.sqrt(
        temps.reduce((sq, n) => sq + Math.pow(n - avg, 2), 0) / temps.length
      );
      
      // Detect anomalies
      const anomalies = readings.filter(r => {
        const deviation = Math.abs(r.temperature.value - avg);
        return deviation > (2 * std); // 2-sigma rule
      });
      
      // Predict future anomalies
      const trend = this.calculateTrend(temps);
      const prediction = {
        currentAvg: Math.round(avg * 100) / 100,
        currentStd: Math.round(std * 100) / 100,
        trend,
        detectedAnomalies: anomalies.length,
        riskOfFailure: trend > 0 ? 'increasing' : 'stable',
        recommendedAction: anomalies.length > 2 ? 'immediate_check' : 'monitor',
        predictedNextAnomaly: trend > 0 ? 'within_2_hours' : 'none_expected'
      };
      
      return prediction;
    } catch (error) {
      logger.error('Temperature anomaly prediction error:', error);
      throw error;
    }
  }

  /**
   * Optimize pricing dynamically
   */
  async optimizePricing(route, cargoType, demand = 'normal') {
    try {
      // Get base price
      const basePrice = await this.getBasePrice(route, cargoType);
      
      // Apply dynamic factors
      const factors = {
        demand: this.getDemandMultiplier(demand),
        fuelPrice: await this.getCurrentFuelPrice(),
        seasonality: this.getSeasonalityFactor(),
        competition: await this.getCompetitionFactor(route),
        capacity: await this.getCapacityFactor()
      };
      
      // Calculate optimized price
      let optimizedPrice = basePrice;
      Object.values(factors).forEach(factor => {
        optimizedPrice *= factor;
      });
      
      // Ensure price is within acceptable range
      optimizedPrice = Math.max(basePrice * 0.8, Math.min(basePrice * 1.5, optimizedPrice));
      
      return {
        basePrice,
        optimizedPrice: Math.round(optimizedPrice * 100) / 100,
        factors,
        confidence: this.calculatePricingConfidence(factors),
        priceRange: {
          min: Math.round(basePrice * 0.8 * 100) / 100,
          max: Math.round(basePrice * 1.5 * 100) / 100
        }
      };
    } catch (error) {
      logger.error('Price optimization error:', error);
      throw error;
    }
  }

  /**
   * Predict driver performance
   */
  async predictDriverPerformance(driverId) {
    try {
      const User = require('../models/User');
      const driver = await User.findById(driverId);
      
      // Historical performance metrics
      const metrics = await this.getDriverMetrics(driverId);
      
      // Calculate performance score
      const score = this.calculateDriverScore(metrics);
      
      // Predict future performance
      const trend = this.analyzePerformanceTrend(metrics.history);
      
      return {
        driverId,
        currentScore: score,
        trend,
        predictedNextMonthScore: Math.min(5, score + (trend === 'improving' ? 0.2 : -0.1)),
        strengths: this.identifyStrengths(metrics),
        weaknesses: this.identifyWeaknesses(metrics),
        recommendations: this.generateDriverRecommendations(metrics, trend),
        riskOfLeaving: this.predictTurnoverRisk(metrics)
      };
    } catch (error) {
      logger.error('Driver performance prediction error:', error);
      throw error;
    }
  }

  // Helper methods
  async getHistoricalAverageTime(origin, destination) {
    const Shipment = require('../models/Shipment');
    
    const shipments = await Shipment.find({
      'pickup.address.city': origin,
      'delivery.address.city': destination,
      status: 'delivered',
      'delivery.actualDuration': { $exists: true }
    }).limit(100);
    
    if (shipments.length === 0) return 180; // Default 3 hours
    
    const avg = shipments.reduce((sum, s) => sum + s.delivery.actualDuration, 0) / shipments.length;
    return avg;
  }

  getCargoTypeMultiplier(type) {
    const multipliers = {
      'frozen': 1.2,
      'chilled': 1.1,
      'pharmaceutical': 1.15,
      'general': 1.0
    };
    return multipliers[type] || 1.0;
  }

  getTimeOfDayMultiplier(time) {
    const hour = new Date(time).getHours();
    if (hour >= 7 && hour <= 9) return 1.3; // Rush hour
    if (hour >= 17 && hour <= 19) return 1.3; // Rush hour
    if (hour >= 22 || hour <= 5) return 1.1; // Night
    return 1.0;
  }

  getWeatherMultiplier(weather) {
    const multipliers = {
      'clear': 1.0,
      'cloudy': 1.05,
      'rain': 1.2,
      'snow': 1.5,
      'fog': 1.3,
      'sandstorm': 1.4
    };
    return multipliers[weather] || 1.0;
  }

  calculateConfidence(factors) {
    // Calculate confidence based on data availability and factor consistency
    const factorValues = Object.values(factors);
    const variance = this.calculateVariance(factorValues);
    return Math.max(0.5, Math.min(0.95, 1 - variance));
  }

  calculateVariance(values) {
    const avg = values.reduce((a, b) => a + b, 0) / values.length;
    return values.reduce((sq, n) => sq + Math.pow(n - avg, 2), 0) / values.length;
  }

  async predictSingleDayDemand(city, date) {
    const dayOfWeek = date.getDay();
    const isWeekend = dayOfWeek === 5 || dayOfWeek === 6; // Friday, Saturday in KSA
    
    // Historical average for this day of week
    const baseDemand = isWeekend ? 40 : 80;
    
    // Apply seasonal factors
    const month = date.getMonth();
    const seasonalFactor = month === 11 || month === 0 ? 1.3 : 1.0; // Holiday season
    
    return {
      date: date.toISOString().split('T')[0],
      dayOfWeek,
      expectedShipments: Math.round(baseDemand * seasonalFactor),
      confidence: 0.7
    };
  }

  calculateVehicleAge(year) {
    return new Date().getFullYear() - year;
  }

  calculateMaintenanceScore(factors) {
    let score = 100;
    
    // Deduct for mileage
    score -= (factors.mileage / 10000) * 5;
    
    // Deduct for age
    score -= factors.age * 2;
    
    // Deduct for usage intensity
    score -= factors.usageIntensity * 10;
    
    // Deduct for failure history
    score -= factors.failureHistory.length * 5;
    
    return Math.max(0, Math.min(100, score));
  }

  getRiskLevel(score) {
    if (score >= 80) return 'low';
    if (score >= 60) return 'medium';
    if (score >= 40) return 'high';
    return 'critical';
  }

  calculateTrend(values) {
    if (values.length < 2) return 0;
    const firstHalf = values.slice(0, Math.floor(values.length / 2));
    const secondHalf = values.slice(Math.floor(values.length / 2));
    
    const firstAvg = firstHalf.reduce((a, b) => a + b, 0) / firstHalf.length;
    const secondAvg = secondHalf.reduce((a, b) => a + b, 0) / secondHalf.length;
    
    return secondAvg - firstAvg;
  }
}

module.exports = new AIPredictionService();
