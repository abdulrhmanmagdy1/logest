const mongoose = require('mongoose');

const AnalyticsSchema = new mongoose.Schema(
  {
    date: {
      type: Date,
      required: true,
    },
    totalShipments: { type: Number, default: 0 },
    revenue: { type: Number, default: 0 },
    onTimeRate: { type: Number, default: 0 },
    shipmentsChange: { type: Number, default: 0 },
    revenueChange: { type: Number, default: 0 },
    onTimeChange: { type: Number, default: 0 },
    dailyStats: [
      {
        date: Date,
        count: Number,
        revenue: Number,
        avgTime: Number,
      },
    ],
  },
  {
    timestamps: true,
  }
);

AnalyticsSchema.index({ date: -1 });

// Static method used by exportService.generateAnalyticsReport
AnalyticsSchema.statics.getReportData = async function (startDate, endDate) {
  const Shipment = require('./Shipment');

  const start = new Date(startDate);
  const end = new Date(endDate);

  const [shipments, prev] = await Promise.all([
    Shipment.find({ createdAt: { $gte: start, $lte: end } }).lean(),
    Shipment.find({
      createdAt: {
        $gte: new Date(start - (end - start)),
        $lt: start,
      },
    }).lean(),
  ]);

  const revenue = shipments.reduce((s, sh) => s + (sh.totalAmount || 0), 0);
  const prevRevenue = prev.reduce((s, sh) => s + (sh.totalAmount || 0), 0);
  const onTime = shipments.filter(
    (sh) => sh.status === 'delivered' && sh.deliveredAt && sh.estimatedDelivery && sh.deliveredAt <= sh.estimatedDelivery
  );
  const onTimeRate = shipments.length ? Math.round((onTime.length / shipments.length) * 100) : 0;

  // Group by day
  const byDay = {};
  shipments.forEach((sh) => {
    const day = sh.createdAt.toISOString().split('T')[0];
    if (!byDay[day]) byDay[day] = { date: day, count: 0, revenue: 0, totalTime: 0, delivered: 0 };
    byDay[day].count++;
    byDay[day].revenue += sh.totalAmount || 0;
    if (sh.deliveredAt && sh.createdAt) {
      byDay[day].totalTime += sh.deliveredAt - sh.createdAt;
      byDay[day].delivered++;
    }
  });

  const dailyStats = Object.values(byDay).map((d) => ({
    date: d.date,
    count: d.count,
    revenue: d.revenue,
    avgTime: d.delivered ? Math.round(d.totalTime / d.delivered / 3600000) : 0,
  }));

  return {
    totalShipments: shipments.length,
    revenue,
    onTimeRate,
    shipmentsChange: prev.length ? shipments.length - prev.length : 0,
    revenueChange: prevRevenue ? revenue - prevRevenue : 0,
    onTimeChange: 0,
    dailyStats,
  };
};

module.exports = mongoose.model('Analytics', AnalyticsSchema);
