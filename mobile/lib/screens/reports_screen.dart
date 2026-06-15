import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';

class ReportsScreen extends StatefulWidget {
  const ReportsScreen({super.key});

  @override
  State<ReportsScreen> createState() => _ReportsScreenState();
}

class _ReportsScreenState extends State<ReportsScreen> {
  Map<String, dynamic> _analytics = {};
  bool _isLoading = true;
  int _selectedReport = 0;

  final List<Map<String, dynamic>> _reportTypes = [
    {'index': 0, 'label': 'نظرة عامة', 'icon': Icons.dashboard},
    {'index': 1, 'label': 'الشحنات', 'icon': Icons.local_shipping},
    {'index': 2, 'label': 'المالية', 'icon': Icons.attach_money},
    {'index': 3, 'label': 'الصيانة', 'icon': Icons.build},
  ];

  @override
  void initState() {
    super.initState();
    _loadAnalytics();
  }

  Future<void> _loadAnalytics() async {
    final authService = Provider.of<AuthService>(context, listen: false);
    ApiService.setToken(authService.token);

    try {
      final analytics = await ApiService.getAnalytics();
      setState(() {
        _analytics = analytics;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('خطأ في تحميل التحليلات: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final hasAccess = authService.userRole == 'supervisor' ||
        authService.userRole == 'admin' ||
        authService.userRole == 'accountant';

    if (!hasAccess) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('التقارير'),
        ),
        body: const Center(
          child: Text(
            'لا تملك صلاحية الوصول للتقارير',
            style: TextStyle(color: Colors.white70),
          ),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('التقارير'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              setState(() => _isLoading = true);
              _loadAnalytics();
            },
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                // Report Type Selector
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      children: _reportTypes.map((type) {
                        final isSelected = _selectedReport == type['index'];
                        return Padding(
                          padding: const EdgeInsets.only(left: 8),
                          child: ChoiceChip(
                            avatar: Icon(
                              type['icon'],
                              size: 18,
                              color: isSelected ? Colors.white : Colors.white70,
                            ),
                            label: Text(type['label']),
                            selected: isSelected,
                            onSelected: (selected) {
                              setState(() => _selectedReport = type['index']);
                            },
                            selectedColor: const Color(0xFFDC2626),
                            backgroundColor: const Color(0xFF2D2D2D),
                            labelStyle: TextStyle(
                              color: isSelected ? Colors.white : Colors.white70,
                            ),
                          ),
                        );
                      }).toList(),
                    ),
                  ),
                ),

                // Report Content
                Expanded(
                  child: RefreshIndicator(
                    onRefresh: _loadAnalytics,
                    child: SingleChildScrollView(
                      padding: const EdgeInsets.all(16),
                      child: _buildReportContent(),
                    ),
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildReportContent() {
    switch (_selectedReport) {
      case 0:
        return _buildOverviewReport();
      case 1:
        return _buildShipmentsReport();
      case 2:
        return _buildFinancialReport();
      case 3:
        return _buildMaintenanceReport();
      default:
        return _buildOverviewReport();
    }
  }

  Widget _buildOverviewReport() {
    final stats = _analytics['overview'] ?? {};

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Summary Cards
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          children: [
            _buildStatCard(
              'إجمالي الشحنات',
              stats['totalShipments']?.toString() ?? '0',
              Icons.local_shipping,
              Colors.blue,
            ),
            _buildStatCard(
              'الشحنات المكتملة',
              stats['completedShipments']?.toString() ?? '0',
              Icons.check_circle,
              Colors.green,
            ),
            _buildStatCard(
              'الإيرادات',
              '${stats['totalRevenue']?.toString() ?? '0'} ريال',
              Icons.attach_money,
              Colors.orange,
            ),
            _buildStatCard(
              'العملاء',
              stats['totalClients']?.toString() ?? '0',
              Icons.people,
              Colors.purple,
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Recent Activity
        const Text(
          'النشاط الأخير',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        const SizedBox(height: 12),
        _buildActivityList(),
      ],
    );
  }

  Widget _buildShipmentsReport() {
    final shipmentStats = _analytics['shipments'] ?? {};
    final statusData = shipmentStats['byStatus'] ?? {};

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Status Cards
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          children: [
            _buildStatCard(
              'معلقة',
              statusData['pending']?.toString() ?? '0',
              Icons.pending,
              Colors.orange,
            ),
            _buildStatCard(
              'قيد النقل',
              statusData['in_transit']?.toString() ?? '0',
              Icons.local_shipping,
              Colors.blue,
            ),
            _buildStatCard(
              'تم التسليم',
              statusData['delivered']?.toString() ?? '0',
              Icons.check_circle,
              Colors.green,
            ),
            _buildStatCard(
              'ملغاة',
              statusData['cancelled']?.toString() ?? '0',
              Icons.cancel,
              Colors.red,
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Performance Metrics
        const Text(
          'مؤشرات الأداء',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        const SizedBox(height: 12),
        _buildMetricCard(
          'متوسط وقت التسليم',
          '${shipmentStats['avgDeliveryTime']?.toString() ?? '0'} ساعة',
          Icons.timer,
          Colors.cyan,
        ),
        const SizedBox(height: 8),
        _buildMetricCard(
          'معدل رضا العملاء',
          '${shipmentStats['satisfactionRate']?.toString() ?? '0'}%',
          Icons.thumb_up,
          Colors.green,
        ),
      ],
    );
  }

  Widget _buildFinancialReport() {
    final financial = _analytics['financial'] ?? {};
    final invoices = financial['invoices'] ?? {};
    final payments = financial['payments'] ?? {};

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Revenue Cards
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          children: [
            _buildStatCard(
              'إجمالي الإيرادات',
              '${financial['totalRevenue']?.toString() ?? '0'} ريال',
              Icons.trending_up,
              Colors.green,
            ),
            _buildStatCard(
              'المدفوعات',
              '${payments['total']?.toString() ?? '0'} ريال',
              Icons.payments,
              Colors.blue,
            ),
            _buildStatCard(
              'الفواتير المدفوعة',
              invoices['paid']?.toString() ?? '0',
              Icons.receipt,
              Colors.orange,
            ),
            _buildStatCard(
              'الفواتير المعلقة',
              invoices['pending']?.toString() ?? '0',
              Icons.pending_actions,
              Colors.red,
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Financial Summary
        const Text(
          'ملخص مالي',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        const SizedBox(height: 12),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                _buildSummaryRow('المبالغ المستحقة', '${financial['outstanding']?.toString() ?? '0'} ريال'),
                const Divider(color: Colors.white12),
                _buildSummaryRow('المبالغ المتأخرة', '${financial['overdue']?.toString() ?? '0'} ريال'),
                const Divider(color: Colors.white12),
                _buildSummaryRow('متوسط قيمة الفاتورة', '${financial['avgInvoice']?.toString() ?? '0'} ريال'),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildMaintenanceReport() {
    final maintenance = _analytics['maintenance'] ?? {};
    final records = maintenance['records'] ?? {};
    final costs = maintenance['costs'] ?? {};

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Maintenance Cards
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          children: [
            _buildStatCard(
              'إجمالي الصيانات',
              records['total']?.toString() ?? '0',
              Icons.build,
              Colors.blue,
            ),
            _buildStatCard(
              'المكتملة',
              records['completed']?.toString() ?? '0',
              Icons.check_circle,
              Colors.green,
            ),
            _buildStatCard(
              'تكلفة الصيانة',
              '${costs['total']?.toString() ?? '0'} ريال',
              Icons.attach_money,
              Colors.orange,
            ),
            _buildStatCard(
              'الصيانات المتأخرة',
              records['overdue']?.toString() ?? '0',
              Icons.warning,
              Colors.red,
            ),
          ],
        ),
        const SizedBox(height: 24),

        // Cost Breakdown
        const Text(
          'تفصيل التكاليف',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        const SizedBox(height: 12),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                _buildSummaryRow('قطع الغيار', '${costs['parts']?.toString() ?? '0'} ريال'),
                const Divider(color: Colors.white12),
                _buildSummaryRow('العمالة', '${costs['labor']?.toString() ?? '0'} ريال'),
                const Divider(color: Colors.white12),
                _buildSummaryRow('أخرى', '${costs['other']?.toString() ?? '0'} ريال'),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: color.withOpacity(0.2),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Icon(icon, color: color, size: 20),
            ),
            const Spacer(),
            Text(
              value,
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              title,
              style: const TextStyle(fontSize: 12, color: Colors.white70),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetricCard(String title, String value, IconData icon, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Container(
              width: 48,
              height: 48,
              decoration: BoxDecoration(
                color: color.withOpacity(0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Icon(icon, color: color, size: 24),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 14,
                      color: Colors.white70,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    value,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSummaryRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: const TextStyle(color: Colors.white70),
          ),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActivityList() {
    final activities = _analytics['recentActivity'] ?? [];

    if (activities.isEmpty) {
      return const Card(
        child: Padding(
          padding: EdgeInsets.all(20),
          child: Center(
            child: Text(
              'لا يوجد نشاط حديث',
              style: TextStyle(color: Colors.white70),
            ),
          ),
        ),
      );
    }

    return Column(
      children: (activities as List).take(5).map((activity) {
        return Card(
          margin: const EdgeInsets.only(bottom: 8),
          child: ListTile(
            leading: Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: _getActivityColor(activity['type']).withOpacity(0.2),
                shape: BoxShape.circle,
              ),
              child: Icon(
                _getActivityIcon(activity['type']),
                color: _getActivityColor(activity['type']),
                size: 20,
              ),
            ),
            title: Text(
              activity['description'] ?? 'نشاط',
              style: const TextStyle(color: Colors.white, fontSize: 14),
            ),
            subtitle: Text(
              _formatDate(activity['timestamp']),
              style: const TextStyle(color: Colors.white54, fontSize: 12),
            ),
          ),
        );
      }).toList(),
    );
  }

  IconData _getActivityIcon(String? type) {
    switch (type) {
      case 'shipment':
        return Icons.local_shipping;
      case 'payment':
        return Icons.payment;
      case 'maintenance':
        return Icons.build;
      case 'user':
        return Icons.person;
      default:
        return Icons.info;
    }
  }

  Color _getActivityColor(String? type) {
    switch (type) {
      case 'shipment':
        return Colors.blue;
      case 'payment':
        return Colors.green;
      case 'maintenance':
        return Colors.orange;
      case 'user':
        return Colors.purple;
      default:
        return Colors.grey;
    }
  }

  String _formatDate(String? dateString) {
    if (dateString == null) return 'غير محدد';
    try {
      final date = DateTime.parse(dateString);
      return '${date.day}/${date.month}/${date.year}';
    } catch (e) {
      return 'غير محدد';
    }
  }
}
