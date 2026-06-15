//
/**
 * ============================================
 * 📦 Warehouse Routes - إدارة المستودعات والمخزون
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const Warehouse = require('../models/Warehouse');
const { InventoryItem, StockLevel } = require('../models/Warehouse');
const logger = require('../utils/logger');

// @route   GET /api/v1/warehouses
// @desc    Get all warehouses
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, type, city } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;
    if (city) query['location.address.city'] = city;

    const warehouses = await Warehouse.find(query)
      .populate('company', 'name')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: warehouses.length,
      data: warehouses
    });

  } catch (error) {
    logger.error('Get warehouses error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/warehouses
// @desc    Create warehouse
// @access  Private (Admin, Supervisor)
router.post('/', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const warehouse = await Warehouse.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: warehouse
    });

  } catch (error) {
    logger.error('Create warehouse error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/warehouses/:id
// @desc    Get single warehouse
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const warehouse = await Warehouse.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!warehouse) {
      return res.status(404).json({ success: false, message: 'Warehouse not found' });
    }

    // Get stock levels
    const stockLevels = await StockLevel.find({ warehouse: warehouse._id })
      .populate('item', 'name sku')
      .sort({ 'quantity.onHand': -1 })
      .limit(100);

    res.json({
      success: true,
      data: {
        ...warehouse.toObject(),
        utilization: warehouse.getUtilization(),
        isOpen: warehouse.isOpen(),
        stockLevels
      }
    });

  } catch (error) {
    logger.error('Get warehouse error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/warehouses/:id
// @desc    Update warehouse
// @access  Private (Admin, Supervisor)
router.put('/:id', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const warehouse = await Warehouse.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      req.body,
      { new: true, runValidators: true }
    );

    if (!warehouse) {
      return res.status(404).json({ success: false, message: 'Warehouse not found' });
    }

    res.json({
      success: true,
      data: warehouse
    });

  } catch (error) {
    logger.error('Update warehouse error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/warehouses/:id/inventory
// @desc    Get warehouse inventory
// @access  Private
router.get('/:id/inventory', protect, async (req, res) => {
  try {
    const { page = 1, limit = 50, category, status } = req.query;

    let query = { warehouse: req.params.id };
    if (status) query.status = status;

    const stockLevels = await StockLevel.find(query)
      .populate({
        path: 'item',
        match: category ? { category } : {},
        select: 'name sku category description'
      })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const total = await StockLevel.countDocuments(query);

    res.json({
      success: true,
      count: stockLevels.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / limit),
      data: stockLevels.filter(sl => sl.item)
    });

  } catch (error) {
    logger.error('Get warehouse inventory error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/warehouses/:id/stock-in
// @desc    Stock in (receive goods)
// @access  Private (Warehouse Staff)
router.post('/:id/stock-in', protect, authorize(['supervisor', 'warehouse_staff']), async (req, res) => {
  try {
    const { items, reference, notes } = req.body;
    const warehouseId = req.params.id;

    const results = [];

    for (const item of items) {
      let stockLevel = await StockLevel.findOne({
        warehouse: warehouseId,
        item: item.itemId,
        zone: item.zone,
        bin: item.bin,
        lotNumber: item.lotNumber
      });

      if (stockLevel) {
        stockLevel.quantity.onHand += item.quantity;
        stockLevel.quantity.available = stockLevel.quantity.onHand - stockLevel.quantity.reserved;
        stockLevel.lastMovement = new Date();
        await stockLevel.save();
      } else {
        stockLevel = await StockLevel.create({
          item: item.itemId,
          warehouse: warehouseId,
          zone: item.zone,
          bin: item.bin,
          lotNumber: item.lotNumber,
          expiryDate: item.expiryDate,
          quantity: {
            onHand: item.quantity,
            available: item.quantity,
            reserved: 0
          },
          receivedDate: new Date(),
          lastMovement: new Date()
        });
      }

      results.push(stockLevel);

      // Update zone capacity
      await Warehouse.updateOne(
        { _id: warehouseId, 'zones.name': item.zone },
        { $inc: { 'zones.$.capacity.currentPallets': 1 } }
      );
    }

    res.json({
      success: true,
      message: `Stocked in ${items.length} items`,
      data: results
    });

  } catch (error) {
    logger.error('Stock in error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/warehouses/:id/stock-out
// @desc    Stock out (issue goods)
// @access  Private (Warehouse Staff)
router.post('/:id/stock-out', protect, authorize(['supervisor', 'warehouse_staff']), async (req, res) => {
  try {
    const { items, reference, notes } = req.body;
    const warehouseId = req.params.id;

    const results = [];

    for (const item of items) {
      const stockLevel = await StockLevel.findOne({
        warehouse: warehouseId,
        item: item.itemId,
        zone: item.zone,
        bin: item.bin
      });

      if (!stockLevel) {
        throw new Error(`Stock not found for item ${item.itemId}`);
      }

      if (stockLevel.quantity.available < item.quantity) {
        throw new Error(`Insufficient stock for item ${item.itemId}`);
      }

      stockLevel.quantity.onHand -= item.quantity;
      stockLevel.quantity.available = stockLevel.quantity.onHand - stockLevel.quantity.reserved;
      stockLevel.lastMovement = new Date();
      await stockLevel.save();

      results.push(stockLevel);
    }

    res.json({
      success: true,
      message: `Stocked out ${items.length} items`,
      data: results
    });

  } catch (error) {
    logger.error('Stock out error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   POST /api/v1/warehouses/:id/transfer
// @desc    Transfer stock between zones/bins
// @access  Private (Warehouse Staff)
router.post('/:id/transfer', protect, authorize(['supervisor', 'warehouse_staff']), async (req, res) => {
  try {
    const { transfers } = req.body;
    const warehouseId = req.params.id;

    const results = [];

    for (const transfer of transfers) {
      // Deduct from source
      const sourceStock = await StockLevel.findOneAndUpdate(
        {
          warehouse: warehouseId,
          item: transfer.itemId,
          zone: transfer.fromZone,
          bin: transfer.fromBin
        },
        {
          $inc: { 'quantity.onHand': -transfer.quantity }
        },
        { new: true }
      );

      if (!sourceStock) {
        throw new Error(`Source stock not found for item ${transfer.itemId}`);
      }

      // Add to destination
      let destStock = await StockLevel.findOne({
        warehouse: warehouseId,
        item: transfer.itemId,
        zone: transfer.toZone,
        bin: transfer.toBin
      });

      if (destStock) {
        destStock.quantity.onHand += transfer.quantity;
        destStock.quantity.available = destStock.quantity.onHand - destStock.quantity.reserved;
        destStock.lastMovement = new Date();
        await destStock.save();
      } else {
        destStock = await StockLevel.create({
          item: transfer.itemId,
          warehouse: warehouseId,
          zone: transfer.toZone,
          bin: transfer.toBin,
          lotNumber: sourceStock.lotNumber,
          expiryDate: sourceStock.expiryDate,
          quantity: {
            onHand: transfer.quantity,
            available: transfer.quantity,
            reserved: 0
          },
          receivedDate: sourceStock.receivedDate,
          lastMovement: new Date()
        });
      }

      results.push({ source: sourceStock, destination: destStock });
    }

    res.json({
      success: true,
      message: `Transferred ${transfers.length} items`,
      data: results
    });

  } catch (error) {
    logger.error('Transfer error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   GET /api/v1/inventory/items
// @desc    Get all inventory items
// @access  Private
router.get('/inventory/items', protect, async (req, res) => {
  try {
    const { category, page = 1, limit = 50 } = req.query;
    
    let query = { company: req.user.company };
    if (category) query.category = category;

    const items = await InventoryItem.find(query)
      .limit(limit * 1)
      .skip((page - 1) * limit)
      .sort({ createdAt: -1 });

    const total = await InventoryItem.countDocuments(query);

    res.json({
      success: true,
      count: items.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / limit),
      data: items
    });

  } catch (error) {
    logger.error('Get inventory items error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/inventory/items
// @desc    Create inventory item
// @access  Private (Admin, Supervisor)
router.post('/inventory/items', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const item = await InventoryItem.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: item
    });

  } catch (error) {
    logger.error('Create inventory item error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/inventory/low-stock
// @desc    Get low stock alerts
// @access  Private (Admin, Supervisor)
router.get('/inventory/low-stock', protect, authorize(['admin', 'supervisor', 'warehouse_staff']), async (req, res) => {
  try {
    const { threshold = 10 } = req.query;

    const lowStock = await StockLevel.find({
      'quantity.available': { $lte: parseInt(threshold) },
      status: 'good'
    })
      .populate('item', 'name sku')
      .populate('warehouse', 'name code');

    res.json({
      success: true,
      count: lowStock.length,
      data: lowStock
    });

  } catch (error) {
    logger.error('Get low stock error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
