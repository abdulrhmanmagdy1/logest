/**
 * ============================================
 * ⛽ Fuel Analytics Service
 * Advanced fuel analysis and optimization
 * ============================================
 */

const FuelRecord = require('../models/FuelRecord');
const Truck = require('../models/Truck');
const mongoose = require('mongoose');
const logger = require('../utils/logger');

class FuelAnalyticsService {
  /**
   * Calculate fuel efficiency metrics
   */
  static async calculateEfficiencyMetrics(truckId, startDate, endDate) {
    try {
      const records = await FuelRecord.find({
        truck: truckId,
        fuelingDate: { $gte: startDate, $lte: endDate }
      }).sort('fuelingDate');

      if (records.length === 0) {
        return null;
      }

      // Calculate metrics
      const totalDistance = records.reduce((sum, r) => sum + (r.distanceSinceLast || 0), 0);
      const totalFuel = records.reduce((sum, r) => sum + r.quantity.value, 0);
      const totalCost = records.reduce((sum, r) => sum + r.totalCost, 0);

      const avgEfficiency = totalDistance > 0 ? totalDistance / totalFuel : 0;
      const costPerKm = totalDistance > 0 ? totalCost / totalDistance : 0;
      const costPerLiter = totalFuel > 0 ? totalCost / totalFuel : 0;

      // Efficiency trend
      const efficiencies = records.map(r => r.efficiency?.kmPerLiter || 0);
      const minEfficiency = Math.min(...efficiencies);
      const maxEfficiency = Math.max(...efficiencies);

      return {
        totalRecords: records.length,
        totalDistance,
        totalFuel,
        totalCost,
        avgEfficiency: parseFloat(avgEfficiency.toFixed(2)),
        costPerKm: parseFloat(costPerKm.toFixed(2)),
        costPerLiter: parseFloat(costPerLiter.toFixed(2)),
        minEfficiency: parseFloat(minEfficiency.toFixed(2)),
        maxEfficiency: parseFloat(maxEfficiency.toFixed(2)),
        fuelTypeDistribution: this.calculateFuelDistribution(records),
        dateRange: { startDate, endDate }
      };
    } catch (error) {
      logger.error('Calculate efficiency metrics error:', error);
      throw error;
    }
  }

  /**
   * Calculate fuel distribution by type
   */
  static calculateFuelDistribution(records) {
    const distribution = {};
    records.forEach(r => {
      const fuelType = r.fuelType || 'unknown';
      if (!distribution[fuelType]) {
        distribution[fuelType] = { count: 0, total: 0 };
      }
      distribution[fuelType].count++;
      distribution[fuelType].total += r.quantity.value;
    });
    return distribution;
  }

  /**
   * Generate cost optimization analysis
   */
  static async generateCostOptimizationAnalysis(truckId, days = 30) {
    try {
      const endDate = new Date();
      const startDate = new Date();
      startDate.setDate(startDate.getDate() - days);

      const records = await FuelRecord.find({
        truck: truckId,
        fuelingDate: { $gte: startDate, $lte: endDate }
      }).sort('fuelingDate');

      if (records.length === 0) {
        return null;
      }

      // Current metrics
      const totalCost = records.reduce((sum, r) => sum + r.totalCost, 0);
      const avgPrice = records.reduce((sum, r) => sum + r.pricePerUnit, 0) / records.length;
      const minPrice = Math.min(...records.map(r => r.pricePerUnit));
      const maxPrice = Math.max(...records.map(r => r.pricePerUnit));

      // Potential savings
      const potentialSavingsPricing = records.reduce((sum, r) => {
        return sum + (r.pricePerUnit - minPrice) * r.quantity.value;
      }, 0);

      // Efficiency improvement potential
      const efficiencies = records.map(r => r.efficiency?.kmPerLiter || 0);
      const avgEfficiency = efficiencies.reduce((a, b) => a + b, 0) / efficiencies.length;
      const bestEfficiency = Math.max(...efficiencies);
      const totalDistance = records.reduce((sum, r) => sum + (r.distanceSinceLast || 0), 0);

      // Calculate potential savings from efficiency improvement
      const currentFuelUsage = records.reduce((sum, r) => sum + r.quantity.value, 0);
      const potentialFuelUsageImproved = totalDistance / bestEfficiency;
      const potentialSavingsFuel = (currentFuelUsage - potentialFuelUsageImproved) * avgPrice;

      return {
        currentMetrics: {
          totalCost: parseFloat(totalCost.toFixed(2)),
          avgPrice: parseFloat(avgPrice.toFixed(2)),
          minPrice: parseFloat(minPrice.toFixed(2)),
          maxPrice: parseFloat(maxPrice.toFixed(2)),
          avgEfficiency: parseFloat(avgEfficiency.toFixed(2)),
          bestEfficiency: parseFloat(bestEfficiency.toFixed(2))
        },
        potentialSavings: {
          fromPricing: parseFloat(potentialSavingsPricing.toFixed(2)),
          fromEfficiency: parseFloat(potentialSavingsFuel.toFixed(2)),
          total: parseFloat((potentialSavingsPricing + potentialSavingsFuel).toFixed(2))
        },
        recommendations: this.generateCostRecommendations(
          avgPrice,
          minPrice,
          avgEfficiency,
          bestEfficiency
        ),
        period: { startDate, endDate, days }
      };
    } catch (error) {
      logger.error('Generate cost optimization analysis error:', error);
      throw error;
    }
  }

  /**
   * Generate cost recommendations
   */
  static generateCostRecommendations(avgPrice, minPrice, avgEfficiency, bestEfficiency) {
    const recommendations = [];

    // Price optimization
    const priceVariation = ((avgPrice - minPrice) / minPrice * 100).toFixed(1);
    if (priceVariation > 10) {
      recommendations.push({
        type: 'price_optimization',
        priority: 'high',
        message: 'تباين أسعار الوقود',
        detail: `تباين ${priceVariation}% - استخدم محطات الوقود بأسعار أقل`,
        potentialSavings: ((avgPrice - minPrice) * 1000).toFixed(2) // Estimate for 1000L
      });
    }

    // Efficiency improvement
    const efficiencyGap = ((bestEfficiency - avgEfficiency) / avgEfficiency * 100).toFixed(1);
    if (efficiencyGap > 15) {
      recommendations.push({
        type: 'efficiency_improvement',
        priority: 'high',
        message: 'تحسين كفاءة الوقود',
        detail: `إمكانية تحسين الكفاءة بنسبة ${efficiencyGap}% - تحقق من صيانة المحرك`,
        potentialSavings: (avgPrice * 500 * efficiencyGap / 100).toFixed(2) // Estimate
      });
    }

    // Maintenance reminder
    if (efficiencyGap > 20) {
      recommendations.push({
        type: 'maintenance',
        priority: 'critical',
        message: 'صيانة ضرورية',
        detail: 'كفاءة الوقود منخفضة جداً - يلزم فحص شامل للمركبة'
      });
    }

    return recommendations;
  }

  /**
   * Calculate fleet-wide fuel statistics
   */
  static async calculateFleetStatistics(startDate, endDate) {
    try {
      const records = await FuelRecord.find({
        fuelingDate: { $gte: startDate, $lte: endDate }
      }).populate('truck', 'plateNumber type');

      if (records.length === 0) {
        return null;
      }

      // By truck
      const byTruck = {};
      records.forEach(r => {
        const truckId = r.truck._id.toString();
        if (!byTruck[truckId]) {
          byTruck[truckId] = {
            plateNumber: r.truck.plateNumber,
            type: r.truck.type,
            records: 0,
            totalCost: 0,
            totalFuel: 0,
            totalDistance: 0
          };
        }
        byTruck[truckId].records++;
        byTruck[truckId].totalCost += r.totalCost;
        byTruck[truckId].totalFuel += r.quantity.value;
        byTruck[truckId].totalDistance += r.distanceSinceLast || 0;
      });

      // Calculate efficiencies
      Object.keys(byTruck).forEach(truckId => {
        const truck = byTruck[truckId];
        truck.avgEfficiency = truck.totalDistance > 0 ? 
          parseFloat((truck.totalDistance / truck.totalFuel).toFixed(2)) : 0;
        truck.costPerKm = truck.totalDistance > 0 ? 
          parseFloat((truck.totalCost / truck.totalDistance).toFixed(2)) : 0;
      });

      // Summary
      const summary = {
        totalRecords: records.length,
        totalCost: records.reduce((sum, r) => sum + r.totalCost, 0),
        totalFuel: records.reduce((sum, r) => sum + r.quantity.value, 0),
        totalDistance: records.reduce((sum, r) => sum + (r.distanceSinceLast || 0), 0),
        fleetCount: Object.keys(byTruck).length,
        avgCostPerKm: 0,
        avgEfficiency: 0
      };

      summary.avgCostPerKm = parseFloat((summary.totalCost / summary.totalDistance).toFixed(2));
      summary.avgEfficiency = parseFloat((summary.totalDistance / summary.totalFuel).toFixed(2));

      // Top consumers
      const topConsumers = Object.values(byTruck)
        .sort((a, b) => b.totalCost - a.totalCost)
        .slice(0, 10);

      // Top performers
      const topPerformers = Object.values(byTruck)
        .sort((a, b) => b.avgEfficiency - a.avgEfficiency)
        .slice(0, 10);

      return {
        summary,
        byTruck,
        topConsumers,
        topPerformers,
        dateRange: { startDate, endDate }
      };
    } catch (error) {
      logger.error('Calculate fleet statistics error:', error);
      throw error;
    }
  }

  /**
   * Generate expense report by category
   */
  static async generateExpenseReportByCategory(startDate, endDate) {
    try {
      const records = await FuelRecord.find({
        fuelingDate: { $gte: startDate, $lte: endDate }
      });

      const byCategory = {};
      const categories = ['regular', 'maintenance', 'emergency', 'tank_full'];

      categories.forEach(cat => {
        byCategory[cat] = { count: 0, total: 0, details: [] };
      });

      records.forEach(r => {
        const category = r.expenseCategory || 'regular';
        if (!byCategory[category]) {
          byCategory[category] = { count: 0, total: 0, details: [] };
        }
        byCategory[category].count++;
        byCategory[category].total += r.totalCost;
      });

      // Calculate percentages
      const grandTotal = Object.values(byCategory).reduce((sum, c) => sum + c.total, 0);
      Object.keys(byCategory).forEach(cat => {
        byCategory[cat].percentage = grandTotal > 0 ? 
          parseFloat(((byCategory[cat].total / grandTotal) * 100).toFixed(2)) : 0;
      });

      return {
        byCategory,
        grandTotal: parseFloat(grandTotal.toFixed(2)),
        dateRange: { startDate, endDate }
      };
    } catch (error) {
      logger.error('Generate expense report by category error:', error);
      throw error;
    }
  }

  /**
   * Identify efficiency anomalies
   */
  static async identifyEfficiencyAnomalies(truckId, threshold = 0.85) {
    try {
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

      const records = await FuelRecord.find({
        truck: truckId,
        fuelingDate: { $gte: thirtyDaysAgo }
      }).sort('fuelingDate');

      if (records.length < 2) {
        return [];
      }

      // Calculate rolling average efficiency
      const efficiencies = records.map(r => r.efficiency?.kmPerLiter || 0);
      const avgEfficiency = efficiencies.reduce((a, b) => a + b, 0) / efficiencies.length;

      // Identify anomalies
      const anomalies = [];
      records.forEach((record, index) => {
        const efficiency = record.efficiency?.kmPerLiter || 0;
        const ratio = efficiency / avgEfficiency;

        if (ratio < threshold) {
          anomalies.push({
            date: record.fuelingDate,
            efficiency,
            avgEfficiency,
            ratio: parseFloat(ratio.toFixed(2)),
            severity: ratio < 0.7 ? 'critical' : 'warning',
            message: `كفاءة منخفضة: ${efficiency.toFixed(2)} كم/لتر (المتوسط: ${avgEfficiency.toFixed(2)})`
          });
        }
      });

      return anomalies;
    } catch (error) {
      logger.error('Identify efficiency anomalies error:', error);
      throw error;
    }
  }
}

module.exports = FuelAnalyticsService;
