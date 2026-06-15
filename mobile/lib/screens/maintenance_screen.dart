import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';

class MaintenanceScreen extends StatefulWidget {
  const MaintenanceScreen({super.key});

  @override
  State<MaintenanceScreen> createState() => _MaintenanceScreenState();
}

class _MaintenanceScreenState extends State<MaintenanceScreen> {
  List<dynamic> _records = [];
  List<dynamic> _alerts = [];
  bool _isLoading = true;
  int _selectedTab = 0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final authService = Provider.of<AuthService>(context, listen: false);
    ApiService.setToken(authService.token);

    try {
      final records = await ApiService.getMaintenanceRecords();
      setState(() {
        _records = records;
        _isLoading = false;
      });

      // Load alerts for supervisor/admin only
      if (authService.userRole == 'supervisor' || authService.userRole == 'admin') {
        _loadAlerts();
      }
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('خطأ في تحميل البيانات: $e')),
        );
      }
    }
  }

  Future<void> _loadAlerts() async {
    try {
      final response = await ApiService.getMaintenanceAlerts();
      setState(() {
        _alerts = response['alerts'] ?? [];
      });
    } catch (e) {
      print('Error loading alerts: $e');
    }
  }

  Color _getStatusColor(String? status) {
    switch (status) {
      case 'scheduled':
        return Colors.blue;
      case 'in_progress':
        return Colors.orange;
      case 'completed':
        return Colors.green;
      case 'overdue':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  String _getStatusLabel(String? status) {
    switch (status) {
      case 'scheduled':
        return 'مجدول';
      case 'in_progress':
        return 'قيد التنفيذ';
      case 'completed':
        return 'مكتمل';
      case 'overdue':
        return 'متأخر';
      default:
        return 'غير معروف';
    }
  }

  String _getTypeLabel(String? type) {
    switch (type) {
      case 'oil_change':
        return 'تغيير زيت';
      case 'tire_rotation':
        return 'تدوير إطارات';
      case 'brake_inspection':
        return 'فحص فرامل';
      case 'general_inspection':
        return 'فحص عام';
      case 'engine_service':
        return 'صيانة محرك';
      case 'transmission':
        return 'صيانة ناقل الحركة';
      default:
        return type ?? 'غير محدد';
    }
  }

  IconData _getTypeIcon(String? type) {
    switch (type) {
      case 'oil_change':
        return Icons.oil_barrel;
      case 'tire_rotation':
        return Icons.tire_repair;
      case 'brake_inspection':
        return Icons.car_crash;
      case 'general_inspection':
        return Icons.fact_check;
      case 'engine_service':
        return Icons.settings;
      case 'transmission':
        return Icons.cable;
      default:
        return Icons.build;
    }
  }

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final canManage = authService.userRole == 'supervisor' || authService.userRole == 'admin';

    return Scaffold(
      appBar: AppBar(
        title: const Text('الصيانة'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              setState(() => _isLoading = true);
              _loadData();
            },
          ),
        ],
        bottom: TabBar(
          onTap: (index) => setState(() => _selectedTab = index),
          tabs: [
            const Tab(icon: Icon(Icons.build), text: 'السجلات'),
            if (canManage)
              Tab(
                icon: Badge(
                  isLabelVisible: _alerts.isNotEmpty,
                  label: Text(_alerts.length.toString()),
                  child: const Icon(Icons.notifications_active),
                ),
                text: 'التنبيهات',
              ),
          ],
        ),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _selectedTab == 0
              ? _buildRecordsList()
              : _buildAlertsList(),
      floatingActionButton: canManage
          ? FloatingActionButton(
              onPressed: () => _showCreateMaintenanceDialog(),
              child: const Icon(Icons.add),
            )
          : null,
    );
  }

  Widget _buildRecordsList() {
    if (_records.isEmpty) {
      return const Center(
        child: Text(
          'لا توجد سجلات صيانة',
          style: TextStyle(color: Colors.white70),
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadData,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _records.length,
        itemBuilder: (context, index) {
          final record = _records[index];
          return _buildMaintenanceCard(record);
        },
      ),
    );
  }

  Widget _buildAlertsList() {
    if (_alerts.isEmpty) {
      return const Center(
        child: Text(
          'لا توجد تنبيهات',
          style: TextStyle(color: Colors.white70),
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _alerts.length,
      itemBuilder: (context, index) {
        final alert = _alerts[index];
        return _buildAlertCard(alert);
      },
    );
  }

  Widget _buildMaintenanceCard(dynamic record) {
    final status = record['status'] as String?;
    final statusColor = _getStatusColor(status);
    final statusLabel = _getStatusLabel(status);
    final type = record['maintenanceType'] as String?;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        onTap: () => _showMaintenanceDetails(record),
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    width: 48,
                    height: 48,
                    decoration: BoxDecoration(
                      color: const Color(0xFFDC2626).withOpacity(0.2),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Icon(
                      _getTypeIcon(type),
                      color: const Color(0xFFDC2626),
                      size: 24,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          _getTypeLabel(type),
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          'شاحنة: ${record['truck']?['plateNumber'] ?? 'غير محدد'}',
                          style: const TextStyle(
                            color: Colors.white70,
                            fontSize: 13,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: statusColor.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      statusLabel,
                      style: TextStyle(
                        color: statusColor,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              const Divider(color: Colors.white12),
              const SizedBox(height: 12),
              Row(
                children: [
                  const Icon(Icons.calendar_today, size: 16, color: Colors.white54),
                  const SizedBox(width: 4),
                  Text(
                    'تاريخ الصيانة: ${_formatDate(record['scheduledDate'])}',
                    style: const TextStyle(color: Colors.white70, fontSize: 13),
                  ),
                ],
              ),
              if (record['cost'] != null) ...[
                const SizedBox(height: 4),
                Row(
                  children: [
                    const Icon(Icons.attach_money, size: 16, color: Colors.white54),
                    const SizedBox(width: 4),
                    Text(
                      'التكلفة: ${record['cost']} ريال',
                      style: const TextStyle(color: Colors.white70, fontSize: 13),
                    ),
                  ],
                ),
              ],
              if (record['odometer'] != null) ...[
                const SizedBox(height: 4),
                Row(
                  children: [
                    const Icon(Icons.speed, size: 16, color: Colors.white54),
                    const SizedBox(width: 4),
                    Text(
                      'عداد المسافات: ${record['odometer']} كم',
                      style: const TextStyle(color: Colors.white70, fontSize: 13),
                    ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAlertCard(dynamic alert) {
    final severity = alert['severity'] as String? ?? 'medium';
    final severityColor = severity == 'high'
        ? Colors.red
        : severity == 'medium'
            ? Colors.orange
            : Colors.yellow;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      color: const Color(0xFF3D3D3D),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.warning_amber,
                  color: severityColor,
                  size: 24,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    alert['title'] ?? 'تنبيه صيانة',
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: severityColor.withOpacity(0.2),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: Text(
                    severity == 'high' ? 'عالي' : severity == 'medium' ? 'متوسط' : 'منخفض',
                    style: TextStyle(
                      color: severityColor,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              alert['message'] ?? '',
              style: const TextStyle(color: Colors.white70),
            ),
            const SizedBox(height: 8),
            Text(
              'الشاحنة: ${alert['truck']?['plateNumber'] ?? 'غير محدد'}',
              style: const TextStyle(color: Colors.white54, fontSize: 13),
            ),
          ],
        ),
      ),
    );
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

  void _showMaintenanceDetails(dynamic record) {
    final authService = Provider.of<AuthService>(context, listen: false);
    final canManage = authService.userRole == 'supervisor' || authService.userRole == 'admin';

    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF2D2D2D),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => Container(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'تفاصيل الصيانة',
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
            const SizedBox(height: 16),
            _buildDetailRow('نوع الصيانة', _getTypeLabel(record['maintenanceType'])),
            _buildDetailRow('الحالة', _getStatusLabel(record['status'])),
            _buildDetailRow('الشاحنة', record['truck']?['plateNumber'] ?? 'غير محدد'),
            _buildDetailRow('تاريخ الجدولة', _formatDate(record['scheduledDate'])),
            _buildDetailRow('تاريخ الإنجاز', _formatDate(record['completedDate'])),
            _buildDetailRow('التكلفة', '${record['cost'] ?? 0} ريال'),
            _buildDetailRow('عداد المسافات', '${record['odometer'] ?? 0} كم'),
            if (record['description'] != null)
              _buildDetailRow('الوصف', record['description']),
            if (record['parts'] != null && (record['parts'] as List).isNotEmpty)
              _buildDetailRow('قطع الغيار', (record['parts'] as List).join(', ')),
            const SizedBox(height: 20),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => Navigator.pop(context),
                    child: const Text('إغلاق'),
                  ),
                ),
                if (canManage && record['status'] != 'completed') ...[
                  const SizedBox(width: 12),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.pop(context);
                        _completeMaintenance(record['_id']);
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.green,
                      ),
                      child: const Text('إكمال'),
                    ),
                  ),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '$label: ',
            style: const TextStyle(
              color: Colors.white70,
              fontWeight: FontWeight.bold,
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(color: Colors.white),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _completeMaintenance(String id) async {
    final authService = Provider.of<AuthService>(context, listen: false);
    ApiService.setToken(authService.token);

    try {
      await ApiService.completeMaintenance(id);
      _loadData();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('تم إكمال الصيانة بنجاح')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('خطأ: $e')),
        );
      }
    }
  }

  void _showCreateMaintenanceDialog() {
    final formKey = GlobalKey<FormState>();
    final truckController = TextEditingController();
    final typeController = TextEditingController();
    final costController = TextEditingController();
    final descriptionController = TextEditingController();

    final maintenanceTypes = [
      'oil_change',
      'tire_rotation',
      'brake_inspection',
      'general_inspection',
      'engine_service',
      'transmission',
    ];

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF2D2D2D),
        title: const Text('إنشاء صيانة جديدة', style: TextStyle(color: Colors.white)),
        content: Form(
          key: formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: truckController,
                  decoration: const InputDecoration(labelText: 'رقم اللوحة'),
                  style: const TextStyle(color: Colors.white),
                  validator: (value) => value?.isEmpty ?? true ? 'مطلوب' : null,
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  decoration: const InputDecoration(labelText: 'نوع الصيانة'),
                  dropdownColor: const Color(0xFF2D2D2D),
                  style: const TextStyle(color: Colors.white),
                  items: maintenanceTypes.map((type) {
                    return DropdownMenuItem(
                      value: type,
                      child: Text(_getTypeLabel(type)),
                    );
                  }).toList(),
                  onChanged: (value) => typeController.text = value ?? '',
                  validator: (value) => value == null ? 'مطلوب' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: costController,
                  decoration: const InputDecoration(labelText: 'التكلفة (ريال)'),
                  style: const TextStyle(color: Colors.white),
                  keyboardType: TextInputType.number,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: descriptionController,
                  decoration: const InputDecoration(labelText: 'الوصف'),
                  style: const TextStyle(color: Colors.white),
                  maxLines: 2,
                ),
              ],
            ),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء', style: TextStyle(color: Colors.white70)),
          ),
          ElevatedButton(
            onPressed: () async {
              if (formKey.currentState?.validate() ?? false) {
                final authService = Provider.of<AuthService>(context, listen: false);
                ApiService.setToken(authService.token);

                try {
                  await ApiService.createMaintenanceRecord({
                    'truckPlateNumber': truckController.text,
                    'maintenanceType': typeController.text,
                    'cost': double.tryParse(costController.text),
                    'description': descriptionController.text,
                    'scheduledDate': DateTime.now().toIso8601String(),
                  });
                  if (mounted) {
                    Navigator.pop(context);
                    _loadData();
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('تم إنشاء الصيانة بنجاح')),
                    );
                  }
                } catch (e) {
                  if (mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('خطأ: $e')),
                    );
                  }
                }
              }
            },
            child: const Text('إنشاء'),
          ),
        ],
      ),
    );
  }
}
