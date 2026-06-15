import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';

class ShipmentsScreen extends StatefulWidget {
  const ShipmentsScreen({super.key});

  @override
  State<ShipmentsScreen> createState() => _ShipmentsScreenState();
}

class _ShipmentsScreenState extends State<ShipmentsScreen> {
  List<dynamic> _shipments = [];
  bool _isLoading = true;
  String _selectedFilter = 'all';

  final List<Map<String, dynamic>> _filters = [
    {'value': 'all', 'label': 'الكل', 'color': Colors.grey},
    {'value': 'pending', 'label': 'معلق', 'color': Colors.orange},
    {'value': 'in_transit', 'label': 'قيد النقل', 'color': Colors.blue},
    {'value': 'delivered', 'label': 'تم التسليم', 'color': Colors.green},
    {'value': 'cancelled', 'label': 'ملغي', 'color': Colors.red},
  ];

  @override
  void initState() {
    super.initState();
    _loadShipments();
  }

  Future<void> _loadShipments() async {
    final authService = Provider.of<AuthService>(context, listen: false);
    ApiService.setToken(authService.token);

    try {
      final shipments = await ApiService.getShipments();
      setState(() {
        _shipments = shipments;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('خطأ في تحميل الشحنات: $e')),
        );
      }
    }
  }

  List<dynamic> get _filteredShipments {
    if (_selectedFilter == 'all') return _shipments;
    return _shipments.where((s) => s['status'] == _selectedFilter).toList();
  }

  Color _getStatusColor(String? status) {
    switch (status) {
      case 'pending':
        return Colors.orange;
      case 'in_transit':
        return Colors.blue;
      case 'delivered':
        return Colors.green;
      case 'cancelled':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  String _getStatusLabel(String? status) {
    switch (status) {
      case 'pending':
        return 'معلق';
      case 'in_transit':
        return 'قيد النقل';
      case 'delivered':
        return 'تم التسليم';
      case 'cancelled':
        return 'ملغي';
      default:
        return 'غير معروف';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('الشحنات'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              setState(() => _isLoading = true);
              _loadShipments();
            },
          ),
        ],
      ),
      body: Column(
        children: [
          // Filter Chips
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: _filters.map((filter) {
                  final isSelected = _selectedFilter == filter['value'];
                  return Padding(
                    padding: const EdgeInsets.only(left: 8),
                    child: ChoiceChip(
                      label: Text(filter['label']),
                      selected: isSelected,
                      onSelected: (selected) {
                        setState(() => _selectedFilter = filter['value']);
                      },
                      selectedColor: filter['color'],
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

          // Statistics Cards
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              children: [
                Expanded(
                  child: _buildStatCard(
                    'الكل',
                    _shipments.length.toString(),
                    Colors.grey,
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: _buildStatCard(
                    'معلق',
                    _shipments.where((s) => s['status'] == 'pending').length.toString(),
                    Colors.orange,
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: _buildStatCard(
                    'قيد النقل',
                    _shipments.where((s) => s['status'] == 'in_transit').length.toString(),
                    Colors.blue,
                  ),
                ),
              ],
            ),
          ),

          const SizedBox(height: 16),

          // Shipments List
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _filteredShipments.isEmpty
                    ? const Center(
                        child: Text(
                          'لا توجد شحنات',
                          style: TextStyle(color: Colors.white70),
                        ),
                      )
                    : RefreshIndicator(
                        onRefresh: _loadShipments,
                        child: ListView.builder(
                          padding: const EdgeInsets.symmetric(horizontal: 16),
                          itemCount: _filteredShipments.length,
                          itemBuilder: (context, index) {
                            final shipment = _filteredShipments[index];
                            return _buildShipmentCard(shipment);
                          },
                        ),
                      ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showCreateShipmentDialog(),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildStatCard(String title, String value, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          children: [
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

  Widget _buildShipmentCard(dynamic shipment) {
    final status = shipment['status'] as String?;
    final statusColor = _getStatusColor(status);
    final statusLabel = _getStatusLabel(status);

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        onTap: () => _showShipmentDetails(shipment),
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
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
                  const Spacer(),
                  Text(
                    '#${shipment['_id']?.toString().substring(0, 8) ?? 'N/A'}',
                    style: const TextStyle(
                      color: Colors.white54,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Text(
                shipment['description'] ?? 'شحنة بدون وصف',
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.location_on, size: 16, color: Colors.white54),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      '${shipment['pickupLocation']?['city'] ?? 'غير محدد'} → ${shipment['deliveryLocation']?['city'] ?? 'غير محدد'}',
                      style: const TextStyle(color: Colors.white70, fontSize: 13),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.scale, size: 16, color: Colors.white54),
                  const SizedBox(width: 4),
                  Text(
                    '${shipment['weight'] ?? 0} كجم',
                    style: const TextStyle(color: Colors.white70, fontSize: 13),
                  ),
                  const Spacer(),
                  const Icon(Icons.access_time, size: 16, color: Colors.white54),
                  const SizedBox(width: 4),
                  Text(
                    _formatDate(shipment['createdAt']),
                    style: const TextStyle(color: Colors.white70, fontSize: 13),
                  ),
                ],
              ),
            ],
          ),
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

  void _showShipmentDetails(dynamic shipment) {
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
              'تفاصيل الشحنة #${shipment['_id']?.toString().substring(0, 8)}',
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
            const SizedBox(height: 16),
            _buildDetailRow('الوصف', shipment['description'] ?? 'بدون وصف'),
            _buildDetailRow('الحالة', _getStatusLabel(shipment['status'])),
            _buildDetailRow('الوزن', '${shipment['weight'] ?? 0} كجم'),
            _buildDetailRow('الحجم', '${shipment['volume'] ?? 0} م³'),
            _buildDetailRow('نقطة الاستلام', shipment['pickupLocation']?['address'] ?? 'غير محدد'),
            _buildDetailRow('نقطة التسليم', shipment['deliveryLocation']?['address'] ?? 'غير محدد'),
            _buildDetailRow('السعر', '${shipment['price'] ?? 0} ريال'),
            const SizedBox(height: 20),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => Navigator.pop(context),
                    child: const Text('إغلاق'),
                  ),
                ),
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

  void _showCreateShipmentDialog() {
    final formKey = GlobalKey<FormState>();
    final descriptionController = TextEditingController();
    final weightController = TextEditingController();
    final pickupController = TextEditingController();
    final deliveryController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF2D2D2D),
        title: const Text('إنشاء شحنة جديدة', style: TextStyle(color: Colors.white)),
        content: Form(
          key: formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: descriptionController,
                  decoration: const InputDecoration(labelText: 'وصف الشحنة'),
                  style: const TextStyle(color: Colors.white),
                  validator: (value) => value?.isEmpty ?? true ? 'مطلوب' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: weightController,
                  decoration: const InputDecoration(labelText: 'الوزن (كجم)'),
                  style: const TextStyle(color: Colors.white),
                  keyboardType: TextInputType.number,
                  validator: (value) => value?.isEmpty ?? true ? 'مطلوب' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: pickupController,
                  decoration: const InputDecoration(labelText: 'نقطة الاستلام'),
                  style: const TextStyle(color: Colors.white),
                  validator: (value) => value?.isEmpty ?? true ? 'مطلوب' : null,
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: deliveryController,
                  decoration: const InputDecoration(labelText: 'نقطة التسليم'),
                  style: const TextStyle(color: Colors.white),
                  validator: (value) => value?.isEmpty ?? true ? 'مطلوب' : null,
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
                  await ApiService.createShipment({
                    'description': descriptionController.text,
                    'weight': double.parse(weightController.text),
                    'pickupLocation': {'address': pickupController.text, 'city': pickupController.text},
                    'deliveryLocation': {'address': deliveryController.text, 'city': deliveryController.text},
                  });
                  if (mounted) {
                    Navigator.pop(context);
                    _loadShipments();
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('تم إنشاء الشحنة بنجاح')),
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
