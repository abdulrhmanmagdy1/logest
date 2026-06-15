import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:syncfusion_flutter_gauges/gauges.dart';
import '../glass_container.dart';
import '../smooth_animation_system.dart';
import '../../theme/app_theme.dart';

/// Interactive Chart System for Premium Dashboards
/// Provides advanced interactive charts with animations, tooltips, and premium styling
class InteractiveChartSystem {
  // Chart color schemes
  static const List<Color> primaryGradient = [
    AppTheme.primary,
    AppTheme.primaryDark,
  ];
  
  static const List<Color> successGradient = [
    AppTheme.success,
    Color(0xFF16A34A),
  ];
  
  static const List<Color> warningGradient = [
    AppTheme.warning,
    Color(0xFFD97706),
  ];
  
  static const List<Color> infoGradient = [
    AppTheme.info,
    Color(0xFF2563EB),
  ];
  
  static const List<Color> errorGradient = [
    AppTheme.error,
    Color(0xFFDC2626),
  ];

  // Chart animations
  static AnimationEffect get chartEntrance => SmoothAnimationSystem.cardEntrance;
  static AnimationEffect get dataPointAnimation => SmoothAnimationSystem.scaleIn;
  static AnimationEffect get tooltipAnimation => SmoothAnimationSystem.fadeIn;
}

/// Premium Interactive Line Chart
class PremiumLineChart extends StatefulWidget {
  final List<ChartData> data;
  final String title;
  final String? subtitle;
  final Color primaryColor;
  final bool showGrid;
  final bool showTooltip;
  final Duration animationDuration;
  final bool isAnimated;
  final Function(ChartData)? onPointTapped;

  const PremiumLineChart({
    Key? key,
    required this.data,
    required this.title,
    this.subtitle,
    this.primaryColor = AppTheme.primary,
    this.showGrid = true,
    this.showTooltip = true,
    this.animationDuration = const Duration(milliseconds: 800),
    this.isAnimated = true,
    this.onPointTapped,
  }) : super(key: key);

  @override
  State<PremiumLineChart> createState() => _PremiumLineChartState();
}

class _PremiumLineChartState extends State<PremiumLineChart> {
  late TooltipBehavior _tooltipBehavior;
  late ZoomPanBehavior _zoomPanBehavior;
  
  @override
  void initState() {
    super.initState();
    _initializeBehaviors();
  }

  void _initializeBehaviors() {
    _tooltipBehavior = TooltipBehavior(
      enable: widget.showTooltip,
      color: AppTheme.surface.withOpacity(0.9),
      textStyle: const TextStyle(
        color: AppTheme.textPrimary,
        fontSize: 12,
        fontWeight: FontWeight.w500,
      ),
      border: Border.all(color: widget.primaryColor.withOpacity(0.3)),
      borderRadius: BorderRadius.circular(8),
      format: 'point.x: point.y',
    );

    _zoomPanBehavior = ZoomPanBehavior(
      enablePinching: true,
      enableDoubleTapZooming: true,
      enablePanning: true,
      enableSelectionZooming: true,
      selectionRectBorderWidth: 1,
      selectionRectColor: widget.primaryColor.withOpacity(0.1),
      selectionRectBorderColor: widget.primaryColor,
    );
  }

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      margin: const EdgeInsets.all(8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          const SizedBox(height: 16),
          _buildChart(),
        ],
      ),
    ).animate().fadeIn(duration: widget.animationDuration);
  }

  Widget _buildHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.title,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (widget.subtitle != null) ...[
          const SizedBox(height: 4),
          Text(
            widget.subtitle!,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildChart() {
    return SizedBox(
      height: 250,
      child: SfCartesianChart(
        plotAreaBorder: const BorderSide(color: Colors.transparent),
        primaryXAxis: CategoryAxis(
          majorGridLines: widget.showGrid
              ? const MajorGridLines(color: AppTheme.surfaceLight, width: 0.5)
              : const MajorGridLines(width: 0),
          axisLine: const AxisLine(color: AppTheme.surfaceLight),
          labelStyle: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
          edgeLabelPlacement: EdgeLabelPlacement.shift,
        ),
        primaryYAxis: NumericAxis(
          majorGridLines: widget.showGrid
              ? const MajorGridLines(color: AppTheme.surfaceLight, width: 0.5)
              : const MajorGridLines(width: 0),
          axisLine: const AxisLine(color: AppTheme.surfaceLight),
          labelStyle: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
          numberFormat: NumberFormat.compact(),
        ),
        tooltipBehavior: _tooltipBehavior,
        zoomPanBehavior: _zoomPanBehavior,
        series: <LineSeries<ChartData, String>>[
          LineSeries<ChartData, String>(
            dataSource: widget.data,
            xValueMapper: (ChartData data, _) => data.x,
            yValueMapper: (ChartData data, _) => data.y,
            gradient: LinearGradient(
              colors: [widget.primaryColor, widget.primaryColor.withOpacity(0.3)],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
            width: 3,
            markerSettings: const MarkerSettings(
              isVisible: true,
              width: 6,
              height: 6,
              shape: DataMarkerType.circle,
              borderWidth: 2,
              borderColor: AppTheme.background,
            ),
            dataLabelSettings: const DataLabelSettings(
              isVisible: false,
            ),
            animationDuration: widget.isAnimated ? widget.animationDuration.inMilliseconds : 0,
            onPointTap: widget.onPointTapped != null
                ? (ChartPointDetails details) {
                    widget.onPointTapped!(details.dataPoints![0].data! as ChartData);
                  }
                : null,
            enableTooltip: widget.showTooltip,
          ),
        ],
      ),
    ).animate().slideUp(duration: widget.animationDuration);
  }
}

/// Premium Interactive Bar Chart
class PremiumBarChart extends StatefulWidget {
  final List<ChartData> data;
  final String title;
  final String? subtitle;
  final Color primaryColor;
  final bool showGrid;
  final bool showTooltip;
  final Duration animationDuration;
  final bool isAnimated;
  final Function(ChartData)? onBarTapped;
  final bool isHorizontal;

  const PremiumBarChart({
    Key? key,
    required this.data,
    required this.title,
    this.subtitle,
    this.primaryColor = AppTheme.primary,
    this.showGrid = true,
    this.showTooltip = true,
    this.animationDuration = const Duration(milliseconds: 800),
    this.isAnimated = true,
    this.onBarTapped,
    this.isHorizontal = false,
  }) : super(key: key);

  @override
  State<PremiumBarChart> createState() => _PremiumBarChartState();
}

class _PremiumBarChartState extends State<PremiumBarChart> {
  late TooltipBehavior _tooltipBehavior;
  
  @override
  void initState() {
    super.initState();
    _initializeBehaviors();
  }

  void _initializeBehaviors() {
    _tooltipBehavior = TooltipBehavior(
      enable: widget.showTooltip,
      color: AppTheme.surface.withOpacity(0.9),
      textStyle: const TextStyle(
        color: AppTheme.textPrimary,
        fontSize: 12,
        fontWeight: FontWeight.w500,
      ),
      border: Border.all(color: widget.primaryColor.withOpacity(0.3)),
      borderRadius: BorderRadius.circular(8),
      format: 'point.x: point.y',
    );
  }

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      margin: const EdgeInsets.all(8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          const SizedBox(height: 16),
          _buildChart(),
        ],
      ),
    ).animate().fadeIn(duration: widget.animationDuration);
  }

  Widget _buildHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.title,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (widget.subtitle != null) ...[
          const SizedBox(height: 4),
          Text(
            widget.subtitle!,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildChart() {
    return SizedBox(
      height: 250,
      child: SfCartesianChart(
        plotAreaBorder: const BorderSide(color: Colors.transparent),
        isTransposed: widget.isHorizontal,
        primaryXAxis: CategoryAxis(
          majorGridLines: widget.showGrid
              ? const MajorGridLines(color: AppTheme.surfaceLight, width: 0.5)
              : const MajorGridLines(width: 0),
          axisLine: const AxisLine(color: AppTheme.surfaceLight),
          labelStyle: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
          edgeLabelPlacement: EdgeLabelPlacement.shift,
        ),
        primaryYAxis: NumericAxis(
          majorGridLines: widget.showGrid
              ? const MajorGridLines(color: AppTheme.surfaceLight, width: 0.5)
              : const MajorGridLines(width: 0),
          axisLine: const AxisLine(color: AppTheme.surfaceLight),
          labelStyle: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
          numberFormat: NumberFormat.compact(),
        ),
        tooltipBehavior: _tooltipBehavior,
        series: <BarSeries<ChartData, String>>[
          BarSeries<ChartData, String>(
            dataSource: widget.data,
            xValueMapper: (ChartData data, _) => data.x,
            yValueMapper: (ChartData data, _) => data.y,
            gradient: LinearGradient(
              colors: [widget.primaryColor, widget.primaryColor.withOpacity(0.7)],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(8),
              topRight: Radius.circular(8),
            ),
            width: 0.7,
            dataLabelSettings: const DataLabelSettings(
              isVisible: false,
            ),
            animationDuration: widget.isAnimated ? widget.animationDuration.inMilliseconds : 0,
            onPointTap: widget.onBarTapped != null
                ? (ChartPointDetails details) {
                    widget.onBarTapped!(details.dataPoints![0].data! as ChartData);
                  }
                : null,
            enableTooltip: widget.showTooltip,
          ),
        ],
      ),
    ).animate().slideUp(duration: widget.animationDuration);
  }
}

/// Premium Interactive Pie Chart
class PremiumPieChart extends StatefulWidget {
  final List<ChartData> data;
  final String title;
  final String? subtitle;
  final List<Color> colors;
  final Duration animationDuration;
  final bool isAnimated;
  final Function(ChartData)? onSliceTapped;
  final bool showLabels;
  final bool showLegend;

  const PremiumPieChart({
    Key? key,
    required this.data,
    required this.title,
    this.subtitle,
    this.colors = InteractiveChartSystem.primaryGradient,
    this.animationDuration = const Duration(milliseconds: 800),
    this.isAnimated = true,
    this.onSliceTapped,
    this.showLabels = true,
    this.showLegend = true,
  }) : super(key: key);

  @override
  State<PremiumPieChart> createState() => _PremiumPieChartState();
}

class _PremiumPieChartState extends State<PremiumPieChart> {
  late TooltipBehavior _tooltipBehavior;
  
  @override
  void initState() {
    super.initState();
    _initializeBehaviors();
  }

  void _initializeBehaviors() {
    _tooltipBehavior = TooltipBehavior(
      enable: true,
      color: AppTheme.surface.withOpacity(0.9),
      textStyle: const TextStyle(
        color: AppTheme.textPrimary,
        fontSize: 12,
        fontWeight: FontWeight.w500,
      ),
      border: Border.all(color: AppTheme.surface.withOpacity(0.3)),
      borderRadius: BorderRadius.circular(8),
      format: 'point.x: point.y%',
    );
  }

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      margin: const EdgeInsets.all(8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          const SizedBox(height: 16),
          _buildChart(),
        ],
      ),
    ).animate().fadeIn(duration: widget.animationDuration);
  }

  Widget _buildHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            widget.title,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          if (widget.subtitle != null) ...[
            const SizedBox(height: 4),
            Text(
              widget.subtitle!,
              style: const TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 14,
              ),
            ),
          ],
        ],
    );
  }

  Widget _buildChart() {
    return SizedBox(
      height: 250,
      child: SfCircularChart(
        tooltipBehavior: _tooltipBehavior,
        legend: widget.showLegend
            ? Legend(
                isVisible: true,
                position: LegendPosition.bottom,
                textStyle: const TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 11,
                ),
                overflowMode: LegendItemOverflowMode.wrap,
              )
            : null,
        series: <PieSeries<ChartData, String>>[
          PieSeries<ChartData, String>(
            dataSource: widget.data,
            xValueMapper: (ChartData data, _) => data.x,
            yValueMapper: (ChartData data, _) => data.y,
            pointColorMapper: (ChartData data, _) => widget.colors[data.hashCode % widget.colors.length],
            radius: '80%',
            innerRadius: '40%',
            dataLabelSettings: widget.showLabels
                ? const DataLabelSettings(
                    isVisible: true,
                    labelPosition: ChartDataLabelPosition.outside,
                    textStyle: TextStyle(
                      color: AppTheme.textPrimary,
                      fontSize: 10,
                      fontWeight: FontWeight.w500,
                    ),
                    connectorLineSettings: ConnectorLineSettings(
                      type: ConnectorType.line,
                      length: '15%',
                      width: 1,
                      color: AppTheme.surfaceLight,
                    ),
                  )
                : const DataLabelSettings(isVisible: false),
            animationDuration: widget.isAnimated ? widget.animationDuration.inMilliseconds : 0,
            onPointTap: widget.onSliceTapped != null
                ? (ChartPointDetails details) {
                    widget.onSliceTapped!(details.dataPoints![0].data! as ChartData);
                  }
                : null,
            enableTooltip: true,
            strokeColor: AppTheme.background,
            strokeWidth: 2,
          ),
        ],
      ),
    ).animate().scale(duration: widget.animationDuration);
  }
}

/// Premium Gauge Chart
class PremiumGaugeChart extends StatefulWidget {
  final double value;
  final double maxValue;
  final String title;
  final String? subtitle;
  final String unit;
  final Color primaryColor;
  final List<Color> rangeColors;
  final Duration animationDuration;
  final bool isAnimated;

  const PremiumGaugeChart({
    Key? key,
    required this.value,
    required this.maxValue,
    required this.title,
    this.subtitle,
    this.unit = '',
    this.primaryColor = AppTheme.primary,
    this.rangeColors = const [AppTheme.success, AppTheme.warning, AppTheme.error],
    this.animationDuration = const Duration(milliseconds: 1000),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<PremiumGaugeChart> createState() => _PremiumGaugeChartState();
}

class _PremiumGaugeChartState extends State<PremiumGaugeChart> {
  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      margin: const EdgeInsets.all(8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          _buildHeader(),
          const SizedBox(height: 16),
          _buildGauge(),
        ],
      ),
    ).animate().fadeIn(duration: widget.animationDuration);
  }

  Widget _buildHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Text(
          widget.title,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (widget.subtitle != null) ...[
          const SizedBox(height: 4),
          Text(
            widget.subtitle!,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildGauge() {
    return SizedBox(
      height: 200,
      child: SfRadialGauge(
        axes: <RadialAxis>[
          RadialAxis(
            minimum: 0,
            maximum: widget.maxValue,
            startAngle: 150,
            endAngle: 30,
            showLabels: false,
            showTicks: false,
            axisLineStyle: const AxisLineStyle(
              thickness: 10,
              cornerStyle: CornerStyle.bothFlat,
              color: AppTheme.surfaceLight,
            ),
            pointers: <GaugePointer>[
              RangePointer(
                value: widget.value,
                width: 10,
                cornerStyle: CornerStyle.bothFlat,
                gradient: LinearGradient(
                  colors: widget.rangeColors,
                  stops: const [0.0, 0.5, 1.0],
                ),
              ),
              MarkerPointer(
                value: widget.value,
                markerType: MarkerType.circle,
                markerHeight: 15,
                markerWidth: 15,
                color: widget.primaryColor,
                border: Border.all(color: AppTheme.background, width: 2),
              ),
            ],
            annotations: <GaugeAnnotation>[
              GaugeAnnotation(
                widget: Column(
                  children: [
                    Text(
                      '${widget.value.toStringAsFixed(1)}${widget.unit}',
                      style: const TextStyle(
                        color: AppTheme.textPrimary,
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text(
                      'of ${widget.maxValue.toStringAsFixed(0)}',
                      style: const TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
                angle: 90,
                positionFactor: 0.8,
              ),
            ],
          ),
        ],
      ),
    ).animate().scale(duration: widget.animationDuration);
  }
}

/// Premium Area Chart
class PremiumAreaChart extends StatefulWidget {
  final List<ChartData> data;
  final String title;
  final String? subtitle;
  final Color primaryColor;
  final bool showGrid;
  final bool showTooltip;
  final Duration animationDuration;
  final bool isAnimated;
  final Function(ChartData)? onPointTapped;

  const PremiumAreaChart({
    Key? key,
    required this.data,
    required this.title,
    this.subtitle,
    this.primaryColor = AppTheme.primary,
    this.showGrid = true,
    this.showTooltip = true,
    this.animationDuration = const Duration(milliseconds: 800),
    this.isAnimated = true,
    this.onPointTapped,
  }) : super(key: key);

  @override
  State<PremiumAreaChart> createState() => _PremiumAreaChartState();
}

class _PremiumAreaChartState extends State<PremiumAreaChart> {
  late TooltipBehavior _tooltipBehavior;
  
  @override
  void initState() {
    super.initState();
    _initializeBehaviors();
  }

  void _initializeBehaviors() {
    _tooltipBehavior = TooltipBehavior(
      enable: widget.showTooltip,
      color: AppTheme.surface.withOpacity(0.9),
      textStyle: const TextStyle(
        color: AppTheme.textPrimary,
        fontSize: 12,
        fontWeight: FontWeight.w500,
      ),
      border: Border.all(color: widget.primaryColor.withOpacity(0.3)),
      borderRadius: BorderRadius.circular(8),
      format: 'point.x: point.y',
    );
  }

  @override
  Widget build(BuildContext context) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      margin: const EdgeInsets.all(8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          const SizedBox(height: 16),
          _buildChart(),
        ],
      ),
    ).animate().fadeIn(duration: widget.animationDuration);
  }

  Widget _buildHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.title,
          style: const TextStyle(
            color: AppTheme.textPrimary,
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (widget.subtitle != null) ...[
          const SizedBox(height: 4),
          Text(
            widget.subtitle!,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildChart() {
    return SizedBox(
      height: 250,
      child: SfCartesianChart(
        plotAreaBorder: const BorderSide(color: Colors.transparent),
        primaryXAxis: CategoryAxis(
          majorGridLines: widget.showGrid
              ? const MajorGridLines(color: AppTheme.surfaceLight, width: 0.5)
              : const MajorGridLines(width: 0),
          axisLine: const AxisLine(color: AppTheme.surfaceLight),
          labelStyle: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
          edgeLabelPlacement: EdgeLabelPlacement.shift,
        ),
        primaryYAxis: NumericAxis(
          majorGridLines: widget.showGrid
              ? const MajorGridLines(color: AppTheme.surfaceLight, width: 0.5)
              : const MajorGridLines(width: 0),
          axisLine: const AxisLine(color: AppTheme.surfaceLight),
          labelStyle: const TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
          numberFormat: NumberFormat.compact(),
        ),
        tooltipBehavior: _tooltipBehavior,
        series: <AreaSeries<ChartData, String>>[
          AreaSeries<ChartData, String>(
            dataSource: widget.data,
            xValueMapper: (ChartData data, _) => data.x,
            yValueMapper: (ChartData data, _) => data.y,
            gradient: LinearGradient(
              colors: [
                widget.primaryColor.withOpacity(0.8),
                widget.primaryColor.withOpacity(0.1),
              ],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
            borderColor: widget.primaryColor,
            borderWidth: 2,
            dataLabelSettings: const DataLabelSettings(
              isVisible: false,
            ),
            animationDuration: widget.isAnimated ? widget.animationDuration.inMilliseconds : 0,
            onPointTap: widget.onPointTapped != null
                ? (ChartPointDetails details) {
                    widget.onPointTapped!(details.dataPoints![0].data! as ChartData);
                  }
                : null,
            enableTooltip: widget.showTooltip,
          ),
        ],
      ),
    ).animate().slideUp(duration: widget.animationDuration);
  }
}

/// Chart Data Model
class ChartData {
  final String x;
  final double y;
  final dynamic extraData;

  ChartData(this.x, this.y, {this.extraData});

  @override
  String toString() => 'ChartData(x: $x, y: $y)';
}

/// Chart Type Enum
enum ChartType {
  line,
  bar,
  pie,
  area,
  gauge,
}

/// Chart Factory for Easy Chart Creation
class ChartFactory {
  static Widget createChart({
    required ChartType type,
    required List<ChartData> data,
    required String title,
    String? subtitle,
    Color primaryColor = AppTheme.primary,
    List<Color>? colors,
    bool showGrid = true,
    bool showTooltip = true,
    Duration animationDuration = const Duration(milliseconds: 800),
    bool isAnimated = true,
    Function(ChartData)? onInteraction,
    bool showLabels = true,
    bool showLegend = true,
    bool isHorizontal = false,
    String unit = '',
    double maxValue = 100,
  }) {
    switch (type) {
      case ChartType.line:
        return PremiumLineChart(
          data: data,
          title: title,
          subtitle: subtitle,
          primaryColor: primaryColor,
          showGrid: showGrid,
          showTooltip: showTooltip,
          animationDuration: animationDuration,
          isAnimated: isAnimated,
          onPointTapped: onInteraction,
        );
      case ChartType.bar:
        return PremiumBarChart(
          data: data,
          title: title,
          subtitle: subtitle,
          primaryColor: primaryColor,
          showGrid: showGrid,
          showTooltip: showTooltip,
          animationDuration: animationDuration,
          isAnimated: isAnimated,
          onBarTapped: onInteraction,
          isHorizontal: isHorizontal,
        );
      case ChartType.pie:
        return PremiumPieChart(
          data: data,
          title: title,
          subtitle: subtitle,
          colors: colors ?? InteractiveChartSystem.primaryGradient,
          animationDuration: animationDuration,
          isAnimated: isAnimated,
          onSliceTapped: onInteraction,
          showLabels: showLabels,
          showLegend: showLegend,
        );
      case ChartType.area:
        return PremiumAreaChart(
          data: data,
          title: title,
          subtitle: subtitle,
          primaryColor: primaryColor,
          showGrid: showGrid,
          showTooltip: showTooltip,
          animationDuration: animationDuration,
          isAnimated: isAnimated,
          onPointTapped: onInteraction,
        );
      case ChartType.gauge:
        return PremiumGaugeChart(
          value: data.isNotEmpty ? data.first.y : 0,
          maxValue: maxValue,
          title: title,
          subtitle: subtitle,
          unit: unit,
          primaryColor: primaryColor,
          animationDuration: animationDuration,
          isAnimated: isAnimated,
        );
    }
  }
}

/// Chart Analytics Helper
class ChartAnalytics {
  static double calculateAverage(List<ChartData> data) {
    if (data.isEmpty) return 0;
    return data.map((d) => d.y).reduce((a, b) => a + b) / data.length;
  }

  static double findMax(List<ChartData> data) {
    if (data.isEmpty) return 0;
    return data.map((d) => d.y).reduce((a, b) => a > b ? a : b);
  }

  static double findMin(List<ChartData> data) {
    if (data.isEmpty) return 0;
    return data.map((d) => d.y).reduce((a, b) => a < b ? a : b);
  }

  static double calculateTrend(List<ChartData> data) {
    if (data.length < 2) return 0;
    
    double firstValue = data.first.y;
    double lastValue = data.last.y;
    
    if (firstValue == 0) return 0;
    
    return ((lastValue - firstValue) / firstValue) * 100;
  }

  static List<ChartData> generateRandomData({
    required int count,
    required double minValue,
    required double maxValue,
    required String prefix,
  }) {
    return List.generate(count, (index) {
      final value = minValue + (maxValue - minValue) * (index / count);
      final randomOffset = (maxValue - minValue) * 0.1 * (index % 3 - 1);
      return ChartData('$prefix${index + 1}', value + randomOffset);
    });
  }
}

/// Chart Export Utility
class ChartExport {
  static Future<void> exportToImage({
    required GlobalKey chartKey,
    required String fileName,
  }) async {
    // Implementation for exporting chart as image
    // This would use flutter/rendering to capture the chart widget
  }

  static Future<void> exportToCSV({
    required List<ChartData> data,
    required String fileName,
  }) async {
    // Implementation for exporting chart data as CSV
    // This would generate a CSV file from the chart data
  }
}
