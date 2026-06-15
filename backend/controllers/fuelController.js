/**
 * ============================================
 * ⛽ Fuel Controller - نظام إدارة الوقود
 * Fuel management, tracking, and analytics
 * ============================================
 */

const FuelRecord = require('../models/FuelRecord');
const Truck = require('../models/Truck');
const User = require('../models/User');
const logger = require('../utils/logger');

// ========================
// FUEL RECORD MANAGEMENT
// ========================

/**
 * @desc    Create new fuel record
 * @route   POST /api/v1/fuel/records
 * @access  Private
 */
exports.createFuelRecord = async (req, res) => {
  try {
    const {
      truck,
      fuelingDate,
      station,
      fuelType,
      quantity,
      pricePerUnit,
      odometerBefore,
      odometerAfter,
      driver,
      relatedTrip,
      payment,
      expenseCategory,
      notes
    } = req.body;

    // Validate truck exists
    const truckDoc = await Truck.findById(truck);
    if (!truckDoc) {
      return res.status(404).json({
        success: false,
        message: 'المركبة غير موجودة'
      });
    }

    const fuelRecord = await FuelRecord.create({
      truck,
      fuelingDate: new Date(fuelingDate),
      station,
      fuelType,
      quantity: {
        value: quantity.value,
        unit: quantity.unit || 'liters'
      },
      pricePerUnit,
      odometerBefore,
      odometerAfter,
      driver: driver || req.user.id,
      relatedTrip,
      payment: {
        method: payment?.method || 'fleet_card',
        receiptNumber: payment?.receiptNumber,
        receiptImage: payment?.receiptImage,
        status: payment?.status || 'approved'
      },
      expenseCategory: expenseCategory || 'regular',
      notes,
      recordedBy: req.user.id
    });

    // Populate references
    await fuelRecord.populate('truck', 'plateNumber type');
    await fuelRecord.populate('driver', 'firstName lastName phone');

    logger.info(`Fuel record created: ${fuelRecord._id}`);

    res.status(201).json({
      success: true,
      message: 'تم تسجيل الوقود بنجاح',
      data: fuelRecord
    });
  } catch (error) {
    logger.error('Create fuel record error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في تسجيل الوقود',
      error: error.message
    });
  }
};

/**
 * @desc    Get fuel records with filters
 * @route   GET /api/v1/fuel/records
 * @access  Private
 */
exports.getFuelRecords = async (req, res) => {
  try {
    const {
      truck,
      startDate,
      endDate,
      fuelType,
      page = 1,
      limit = 20,
      sort = '-fuelingDate'
    } = req.query;

    // Build filter
    const filter = {};
    
    if (truck) filter.truck = truck;
    if (fuelType) filter.fuelType = fuelType;
    
    if (startDate || endDate) {
      filter.fuelingDate = {};
      if (startDate) filter.fuelingDate.$gte = new Date(startDate);
      if (endDate) filter.fuelingDate.$lte = new Date(endDate);
    }

    // Pagination
    const skip = (parseInt(page) - 1) * parseInt(limit);

    // Execute query
    const records = await FuelRecord.find(filter)
      .populate('truck', 'plateNumber type')
      .populate('driver', 'firstName lastName phone')
      .sort(sort)
      .skip(skip)
      .limit(parseInt(limit));

    // Get total count
    const total = await FuelRecord.countDocuments(filter);

    res.json({
      success: true,
      data: records,
      pagination: {
        total,
        page: parseInt(page),
        pages: Math.ceil(total / parseInt(limit))
      }
    });
  } catch (error) {
    logger.error('Get fuel records error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب سجلات الوقود'
    });
  }
};

/**
 * @desc    Get fuel record by ID
 * @route   GET /api/v1/fuel/records/:id
 * @access  Private
 */
exports.getFuelRecordById = async (req, res) => {
  try {
    const record = await FuelRecord.findById(req.params.id)
      .populate('truck')
      .populate('driver')
      .populate('relatedTrip');

    if (!record) {
      return res.status(404).json({
        success: false,
        message: 'سجل الوقود غير موجود'
      });
    }

    res.json({
      success: true,
      data: record
    });
  } catch (error) {
    logger.error('Get fuel record error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب سجل الوقود'
    });
  }
};

/**
 * @desc    Update fuel record
 * @route   PUT /api/v1/fuel/records/:id
 * @access  Private
 */
exports.updateFuelRecord = async (req, res) => {
  try {
    let record = await FuelRecord.findById(req.params.id);

    if (!record) {
      return res.status(404).json({
        success: false,
        message: 'سجل الوقود غير موجود'
      });
    }

    // Update fields
    Object.assign(record, req.body);
    record.updatedAt = Date.now();

    await record.save();
    await record.populate('truck driver');

    res.json({
      success: true,
      message: 'تم تحديث سجل الوقود بنجاح',
      data: record
    });
  } catch (error) {
    logger.error('Update fuel record error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في تحديث سجل الوقود'
    });
  }
};

/**
 * @desc    Delete fuel record
 * @route   DELETE /api/v1/fuel/records/:id
 * @access  Private
 */
exports.deleteFuelRecord = async (req, res) => {
  try {
    const record = await FuelRecord.findByIdAndDelete(req.params.id);

    if (!record) {
      return res.status(404).json({
        success: false,
        message: 'سجل الوقود غير موجود'
      });
    }

    logger.info(`Fuel record deleted: ${record._id}`);

    res.json({
      success: true,
      message: 'تم حذف سجل الوقود بنجاح'
    });
  } catch (error) {
    logger.error('Delete fuel record error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في حذف سجل الوقود'
    });
  }
};

// ========================
// FUEL ANALYTICS
// ========================

/**
 * @desc    Get fuel statistics for a truck
 * @route   GET /api/v1/fuel/analytics/truck/:truckId
 * @access  Private
 */
exports.getTruckFuelStats = async (req, res) => {
  try {
    const { startDate, endDate } = req.query;
    const { truckId } = req.params;

    let dateFilter = {};
    if (startDate || endDate) {
      dateFilter.fuelingDate = {};
      if (startDate) dateFilter.fuelingDate.$gte = new Date(startDate);
      if (endDate) dateFilter.fuelingDate.$lte = new Date(endDate);
    }

    const stats = await FuelRecord.aggregate([
      {
        $match: {
          truck: mongoose.Types.ObjectId(truckId),
          ...dateFilter
        }
      },
      {
        $group: {
          _id: null,
          totalFuel: { $sum: '$quantity.value' },
          totalCost: { $sum: '$totalCost' },
          totalDistance: { $sum: '$distanceSinceLast' },
          fuelingCount: { $sum: 1 },
          avgPricePerUnit: { $avg: '$pricePerUnit' },
          avgEfficiency: { $avg: '$efficiency.kmPerLiter' },
          minEfficiency: { $min: '$efficiency.kmPerLiter' },
          maxEfficiency: { $max: '$efficiency.kmPerLiter' }
        }
      }
    ]);

    const data = stats[0] || {
      totalFuel: 0,
      totalCost: 0,
      totalDistance: 0,
      fuelingCount: 0,
      avgPricePerUnit: 0,
      avgEfficiency: 0,
      minEfficiency: 0,
      maxEfficiency: 0
    };

    res.json({
      success: true,
      data
    });
  } catch (error) {
    logger.error('Get truck fuel stats error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب إحصائيات الوقود'
    });
  }
};

/**
 * @desc    Get fleet fuel summary
 * @route   GET /api/v1/fuel/analytics/fleet
 * @access  Private
 */
exports.getFleetFuelSummary = async (req, res) => {
  try {
    const { startDate, endDate } = req.query;

    let dateFilter = {};
    if (startDate || endDate) {
      dateFilter.fuelingDate = {};
      if (startDate) dateFilter.fuelingDate.$gte = new Date(startDate);
      if (endDate) dateFilter.fuelingDate.$lte = new Date(endDate);
    }

    // Get fleet summary
    const summary = await FuelRecord.aggregate([
      {
        $match: dateFilter
      },
      {
        $group: {
          _id: '$fuelType',
          totalFuel: { $sum: '$quantity.value' },
          totalCost: { $sum: '$totalCost' },
          recordCount: { $sum: 1 }
        }
      }
    ]);

    // Get top trucks
    const topTrucks = await FuelRecord.aggregate([
      {
        $match: dateFilter
      },
      {
        $group: {
          _id: '$truck',
          totalCost: { $sum: '$totalCost' },
          totalFuel: { $sum: '$quantity.value' },
          recordCount: { $sum: 1 }
        }
      },
      { $sort: { totalCost: -1 } },
      { $limit: 10 }
    ]);

    res.json({
      success: true,
      data: {
        byFuelType: summary,
        topTrucks: topTrucks
      }
    });
  } catch (error) {
    logger.error('Get fleet fuel summary error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب ملخص الوقود'
    });
  }
};

/**
 * @desc    Get fuel consumption trends
 * @route   GET /api/v1/fuel/analytics/trends
 * @access  Private
 */
exports.getFuelConsumptionTrends = async (req, res) => {
  try {
    const { truck, days = 30 } = req.query;

    const startDate = new Date();
    startDate.setDate(startDate.getDate() - parseInt(days));

    let matchStage = {
      fuelingDate: { $gte: startDate }
    };

    if (truck) {
      matchStage.truck = mongoose.Types.ObjectId(truck);
    }

    const trends = await FuelRecord.aggregate([
      { $match: matchStage },
      {
        $group: {
          _id: {
            $dateToString: { format: '%Y-%m-%d', date: '$fuelingDate' }
          },
          totalFuel: { $sum: '$quantity.value' },
          totalCost: { $sum: '$totalCost' },
          avgEfficiency: { $avg: '$efficiency.kmPerLiter' },
          recordCount: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({
      success: true,
      data: trends
    });
  } catch (error) {
    logger.error('Get fuel consumption trends error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب اتجاهات الاستهلاك'
    });
  }
};

/**
 * @desc    Get fuel expense reports
 * @route   GET /api/v1/fuel/analytics/expense-report
 * @access  Private
 */
exports.getFuelExpenseReport = async (req, res) => {
  try {
    const { startDate, endDate, truck, expenseCategory } = req.query;

    let filter = {};
    
    if (startDate || endDate) {
      filter.fuelingDate = {};
      if (startDate) filter.fuelingDate.$gte = new Date(startDate);
      if (endDate) filter.fuelingDate.$lte = new Date(endDate);
    }
    
    if (truck) filter.truck = truck;
    if (expenseCategory) filter.expenseCategory = expenseCategory;

    const records = await FuelRecord.find(filter)
      .populate('truck', 'plateNumber type')
      .sort('-fuelingDate');

    // Calculate summary
    const summary = {
      totalRecords: records.length,
      totalExpense: records.reduce((sum, r) => sum + r.totalCost, 0),
      totalFuel: records.reduce((sum, r) => sum + r.quantity.value, 0),
      averagePrice: records.length > 0 ? records.reduce((sum, r) => sum + r.pricePerUnit, 0) / records.length : 0,
      avgEfficiency: records.length > 0 ? records.reduce((sum, r) => sum + (r.efficiency?.kmPerLiter || 0), 0) / records.length : 0
    };

    // Group by category
    const byCategory = {};
    records.forEach(r => {
      const cat = r.expenseCategory || 'regular';
      if (!byCategory[cat]) {
        byCategory[cat] = { count: 0, total: 0 };
      }
      byCategory[cat].count++;
      byCategory[cat].total += r.totalCost;
    });

    res.json({
      success: true,
      data: {
        summary,
        byCategory,
        records: records.slice(0, 100) // Limit to 100 records
      }
    });
  } catch (error) {
    logger.error('Get fuel expense report error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب تقرير المصروفات'
    });
  }
};

/**
 * @desc    Get fuel optimization recommendations
 * @route   GET /api/v1/fuel/analytics/recommendations
 * @access  Private
 */
exports.getFuelOptimizationRecommendations = async (req, res) => {
  try {
    const { truck, days = 30 } = req.query;

    const startDate = new Date();
    startDate.setDate(startDate.getDate() - parseInt(days));

    let filter = {
      fuelingDate: { $gte: startDate }
    };

    if (truck) {
      filter.truck = truck;
    }

    const records = await FuelRecord.find(filter);

    const recommendations = [];

    if (records.length > 0) {
      // Calculate average efficiency
      const avgEfficiency = records.reduce((sum, r) => sum + (r.efficiency?.kmPerLiter || 0), 0) / records.length;
      const efficiencies = records.map(r => r.efficiency?.kmPerLiter || 0).sort((a, b) => a - b);
      const maxEfficiency = efficiencies[efficiencies.length - 1];

      // High-cost days
      const costByDay = {};
      records.forEach(r => {
        const day = new Date(r.fuelingDate).toDateString();
        costByDay[day] = (costByDay[day] || 0) + r.totalCost;
      });

      const avgDailyCost = Object.values(costByDay).reduce((a, b) => a + b, 0) / Object.keys(costByDay).length;
      const highCostDays = Object.entries(costByDay).filter(([_, cost]) => cost > avgDailyCost * 1.5);

      if (highCostDays.length > 0) {
        recommendations.push({
          type: 'high_cost_periods',
          severity: 'warning',
          message: `عدد أيام ذات تكاليف وقود مرتفعة: ${highCostDays.length}`,
          details: `التكلفة المتوسطة: ${avgDailyCost.toFixed(2)} ريال`
        });
      }

      // Efficiency improvement
      if (avgEfficiency < 7) {
        recommendations.push({
          type: 'low_efficiency',
          severity: 'high',
          message: 'كفاءة الوقود منخفضة',
          details: `كفاءة الوقود الحالية: ${avgEfficiency.toFixed(2)} كم/لتر - يمكن تحسينها إلى ${maxEfficiency.toFixed(2)}`
        });
      }

      // Price optimization
      const avgPrice = records.reduce((sum, r) => sum + r.pricePerUnit, 0) / records.length;
      const maxPrice = Math.max(...records.map(r => r.pricePerUnit));
      const minPrice = Math.min(...records.map(r => r.pricePerUnit));

      if ((maxPrice - minPrice) / minPrice > 0.15) {
        recommendations.push({
          type: 'price_variation',
          severity: 'medium',
          message: 'تباين كبير في أسعار الوقود',
          details: `السعر الأدنى: ${minPrice.toFixed(2)} - السعر الأعلى: ${maxPrice.toFixed(2)}`
        });
      }
    }

    res.json({
      success: true,
      data: recommendations
    });
  } catch (error) {
    logger.error('Get fuel optimization recommendations error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب التوصيات'
    });
  }
};
