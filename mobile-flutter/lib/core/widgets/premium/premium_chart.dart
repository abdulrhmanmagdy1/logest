// ============================================
// 📈 Premium Chart - Animated Glassmorphism Charts
// ============================================

import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../theme/app_theme.dart';
import '../glass_container.dart';

class PremiumLineChart extends StatefulWidget {
  final List<ChartData> data;
  final Color lineColor;
  final Color gradientColor;
  final String title;
  final bool isAnimated;
  final Duration? animationDelay;

  const PremiumLineChart({
    super.key,
    required this.data,
    required this.lineColor,
    required this.gradientColor,
    required this.title,
    this.isAnimated = true,
    this.animationDelay,
  });

  @override
  State<PremiumLineChart> createState() => _PremiumLineChartState();
}

class _PremiumLineChartState extends State<PremiumLineChart>
    with SingleTickerProviderStateMixin {
  late AnimationController _chartController;
  late Animation<double> _chartAnimation;

  @override
  void initState() {
    super.initState();
    _chartController = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    );
    _chartAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _chartController,
      curve: Curves.easeInOutCubic,
    ));

    if (widget.isAnimated) {
      Future.delayed(widget.animationDelay ?? Duration.zero, () {
        if (mounted) {
          _chartController.forward();
        }
      });
    } else {
      _chartController.value = 1.0;
    }
  }

  @override
  void dispose() {
    _chartController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Title
          Text(
            widget.title,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ).animate()
              .fadeIn(delay: widget.animationDelay ?? Duration.zero)
              .slideX(begin: -0.2, end: 0),

          const SizedBox(height: 20),

          // Chart
          SizedBox(
            height: 200,
            child: AnimatedBuilder(
              animation: _chartAnimation,
              builder: (context, child) {
                return LineChart(
                  LineChartData(
                    gridData: FlGridData(
                      show: true,
                      drawVerticalLine: true,
                      drawHorizontalLine: true,
                      horizontalInterval: 1000,
                      verticalInterval: 1,
                      getDrawingHorizontalLine: (value) {
                        return FlLine(
                          color: AppTheme.surfaceLight.withOpacity(0.3),
                          strokeWidth: 1,
                        );
                      },
                      getDrawingVerticalLine: (value) {
                        return FlLine(
                          color: AppTheme.surfaceLight.withOpacity(0.3),
                          strokeWidth: 1,
                        );
                      },
                    ),
                    titlesData: const FlTitlesData(
                      show: false,
                    ),
                    borderData: FlBorderData(
                      show: false,
                    ),
                    minX: 0,
                    maxX: (widget.data.length - 1).toDouble(),
                    minY: 0,
                    maxY: _getMaxY() * 1.2,
                    lineBarsData: [
                      LineChartBarData(
                        spots: widget.data.asMap().entries.map((entry) {
                          return FlSpot(
                            entry.key.toDouble(),
                            entry.value.value * _chartAnimation.value,
                          );
                        }).toList(),
                        isCurved: true,
                        gradient: LinearGradient(
                          colors: [
                            widget.gradientColor.withOpacity(0.8),
                            widget.gradientColor.withOpacity(0.1),
                          ],
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter,
                        ),
                        barWidth: 4,
                        isStrokeCapRound: true,
                        dotData: FlDotData(
                          show: true,
                          getDotPainter: (spot, percent, barData, index) {
                            return FlDotCirclePainter(
                              radius: 6,
                              color: widget.lineColor,
                              strokeWidth: 2,
                              strokeColor: AppTheme.background,
                            );
                          },
                        ),
                        belowBarData: BarAreaData(
                          show: true,
                          gradient: LinearGradient(
                            colors: [
                              widget.gradientColor.withOpacity(0.3),
                              widget.gradientColor.withOpacity(0.0),
                            ],
                            begin: Alignment.topCenter,
                            end: Alignment.bottomCenter,
                          ),
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    ).animate()
        .fadeIn(delay: widget.animationDelay ?? Duration.zero)
        .slideY(begin: 0.2, end: 0);
  }

  double _getMaxY() {
    return widget.data
        .map((e) => e.value)
        .reduce((a, b) => a > b ? a : b)
        .toDouble();
  }
}

class ChartData {
  final double value;
  final String? label;

  const ChartData({
    required this.value,
    this.label,
  });
}

// Premium Revenue Chart with glow effect
class PremiumRevenueChart extends StatelessWidget {
  final List<double> revenueData;
  final List<String> timeLabels;
  final String title;
  final String currency;

  const PremiumRevenueChart({
    super.key,
    required this.revenueData,
    required this.timeLabels,
    required this.title,
    this.currency = 'ر.س',
  });

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Row(
            children: [
              Text(
                title,
                style: const TextStyle(
                  color: AppTheme.textPrimary,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Spacer(),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  gradient: AppTheme.successGradient,
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Text(
                  '+$currency${revenueData.last.toInt()}',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),

          const SizedBox(height: 20),

          // Chart
          SizedBox(
            height: 250,
            child: LineChart(
              LineChartData(
                gridData: FlGridData(
                  show: true,
                  drawVerticalLine: true,
                  drawHorizontalLine: true,
                  horizontalInterval: _getInterval(),
                  getDrawingHorizontalLine: (value) {
                    return FlLine(
                      color: AppTheme.surfaceLight.withOpacity(0.2),
                      strokeWidth: 1,
                    );
                  },
                  getDrawingVerticalLine: (value) {
                    return FlLine(
                      color: AppTheme.surfaceLight.withOpacity(0.2),
                      strokeWidth: 1,
                    );
                  },
                ),
                titlesData: FlTitlesData(
                  leftTitles: AxisTitles(
                    sideTitles: SideTitles(
                      showTitles: true,
                      reservedSize: 40,
                      interval: _getInterval(),
                      getTitlesWidget: (value, meta) {
                        return Text(
                          '$currency${value.toInt()}',
                          style: const TextStyle(
                            color: AppTheme.textSecondary,
                            fontSize: 10,
                          ),
                        );
                      },
                    ),
                  ),
                  bottomTitles: AxisTitles(
                    sideTitles: SideTitles(
                      showTitles: true,
                      reservedSize: 30,
                      interval: 1,
                      getTitlesWidget: (value, meta) {
                        if (value.toInt() < timeLabels.length) {
                          return Text(
                            timeLabels[value.toInt()],
                            style: const TextStyle(
                              color: AppTheme.textSecondary,
                              fontSize: 10,
                            ),
                          );
                        }
                        return const Text('');
                      },
                    ),
                  ),
                  topTitles: const AxisTitles(sideTitles: SideTitles(showTitles: false)),
                  rightTitles: const AxisTitles(sideTitles: SideTitles(showTitles: false)),
                ),
                borderData: FlBorderData(
                  show: false,
                ),
                minX: 0,
                maxX: (revenueData.length - 1).toDouble(),
                minY: 0,
                maxY: _getMaxY() * 1.2,
                lineBarsData: [
                  LineChartBarData(
                    spots: revenueData.asMap().entries.map((entry) {
                      return FlSpot(entry.key.toDouble(), entry.value);
                    }).toList(),
                    isCurved: true,
                    gradient: AppTheme.primaryGradient,
                    barWidth: 4,
                    isStrokeCapRound: true,
                    dotData: FlDotData(
                      show: true,
                      getDotPainter: (spot, percent, barData, index) {
                        return FlDotCirclePainter(
                          radius: 6,
                          color: AppTheme.primary,
                          strokeWidth: 3,
                          strokeColor: AppTheme.background,
                        );
                      },
                    ),
                    belowBarData: BarAreaData(
                      show: true,
                      gradient: LinearGradient(
                        colors: [
                          AppTheme.primary.withOpacity(0.3),
                          AppTheme.primary.withOpacity(0.0),
                        ],
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  double _getMaxY() {
    return revenueData.reduce((a, b) => a > b ? a : b).toDouble();
  }

  double _getInterval() {
    final max = _getMaxY();
    if (max < 1000) return 200;
    if (max < 5000) return 1000;
    if (max < 10000) return 2000;
    return 5000;
  }
}
