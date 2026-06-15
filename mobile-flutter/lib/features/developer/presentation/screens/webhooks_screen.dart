// ============================================
// 🪝 Webhooks Screen - إدارة Webhooks
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../../../core/theme/app_theme.dart';

class WebhooksScreen extends StatefulWidget {
  const WebhooksScreen({super.key});

  @override
  State<WebhooksScreen> createState() => _WebhooksScreenState();
}

class _WebhooksScreenState extends State<WebhooksScreen> {
  bool _showSecrets = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'Webhooks',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: () => _showAddWebhookDialog(),
            icon: const Icon(Icons.add, color: Colors.white),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildInfoCard(),
          const SizedBox(height: 20),
          _buildEndpointCard(),
          const SizedBox(height: 20),
          ..._getWebhooks().map((webhook) => _buildWebhookCard(webhook)),
          const SizedBox(height: 20),
          _buildEventsReference(),
        ],
      ),
    );
  }

  Widget _buildInfoCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Colors.purple.shade700, Colors.purple.shade900],
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Row(
            children: [
              Icon(Icons.webhook, color: Colors.white),
              SizedBox(width: 8),
              Text(
                'Webhooks',
                style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            'استقبال الأحداث في الوقت الفعلي عند حدوث تغييرات في الشحنات والفواتير والمزيد.',
            style: TextStyle(
              color: Colors.white.withOpacity(0.9),
              fontSize: 13,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEndpointCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'عنوان النقطة الطرفية',
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'https://api.edham-logistics.com/api/v1/webhooks',
            style: TextStyle(
              color: AppTheme.primaryColor,
              fontFamily: 'monospace',
              fontSize: 12,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.green.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text(
                  'HTTPS فقط',
                  style: TextStyle(
                    color: Colors.green,
                    fontSize: 11,
                  ),
                ),
              ),
              const SizedBox(width: 8),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.blue.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text(
                  'POST فقط',
                  style: TextStyle(
                    color: Colors.blue,
                    fontSize: 11,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildWebhookCard(WebhookModel webhook) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: ExpansionTile(
        leading: Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: webhook.isActive
                ? Colors.green.withOpacity(0.2)
                : Colors.red.withOpacity(0.2),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            webhook.isActive ? Icons.check_circle : Icons.error,
            color: webhook.isActive ? Colors.green : Colors.red,
            size: 20,
          ),
        ),
        title: Text(
          webhook.name,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              webhook.url,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              'نجاح: ${webhook.successRate}% | آخر تسليم: ${webhook.lastDelivery}',
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 11,
              ),
            ),
          ],
        ),
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Secret Key
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AppTheme.backgroundColor,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'مفتاح السر (Secret)',
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                          fontSize: 12,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          Expanded(
                            child: Text(
                              _showSecrets
                                  ? webhook.secret
                                  : '••••••••••••••••••••••••••••••••',
                              style: TextStyle(
                                color: _showSecrets
                                    ? Colors.white
                                    : AppTheme.textSecondary,
                                fontFamily: 'monospace',
                                fontSize: 12,
                              ),
                            ),
                          ),
                          IconButton(
                            onPressed: () {
                              setState(() => _showSecrets = !_showSecrets);
                            },
                            icon: Icon(
                              _showSecrets ? Icons.visibility_off : Icons.visibility,
                              color: AppTheme.textSecondary,
                              size: 18,
                            ),
                          ),
                          IconButton(
                            onPressed: () {
                              Clipboard.setData(ClipboardData(text: webhook.secret));
                            },
                            icon: Icon(
                              Icons.copy,
                              color: AppTheme.primaryColor,
                              size: 18,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                
                const SizedBox(height: 16),
                
                // Events
                const Text(
                  'الأحداث المشترك بها',
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                  ),
                ),
                const SizedBox(height: 8),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: webhook.events.map((event) {
                    return Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: AppTheme.primaryColor.withOpacity(0.2),
                        borderRadius: BorderRadius.circular(6),
                      ),
                      child: Text(
                        event,
                        style: TextStyle(
                          color: AppTheme.primaryColor,
                          fontSize: 11,
                          fontFamily: 'monospace',
                        ),
                      ),
                    );
                  }).toList(),
                ),
                
                const SizedBox(height: 16),
                
                // Stats
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildStat('الطلبات', '${webhook.totalRequests}'),
                    _buildStat('النجاح', '${webhook.successful}'),
                    _buildStat('الفشل', '${webhook.failed}'),
                  ],
                ),
                
                const SizedBox(height: 16),
                
                // Actions
                Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () {},
                        icon: const Icon(Icons.play_arrow, size: 18),
                        label: const Text('اختبار'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.green,
                          padding: const EdgeInsets.symmetric(vertical: 12),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: () {},
                        icon: const Icon(Icons.edit, size: 18),
                        label: const Text('تعديل'),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: AppTheme.primaryColor,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: () {},
                        icon: const Icon(Icons.delete, size: 18),
                        label: const Text('حذف'),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.red,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStat(String label, String value) {
    return Column(
      children: [
        Text(
          value,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
        ),
      ],
    );
  }

  Widget _buildEventsReference() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'الأحداث المتاحة',
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 16,
            ),
          ),
          const SizedBox(height: 16),
          _buildEventItem('shipment.created', 'عند إنشاء شحنة جديدة'),
          _buildEventItem('shipment.updated', 'عند تحديث بيانات الشحنة'),
          _buildEventItem('shipment.delivered', 'عند تسليم الشحنة'),
          _buildEventItem('shipment.cancelled', 'عند إلغاء الشحنة'),
          _buildEventItem('invoice.paid', 'عند سداد الفاتورة'),
          _buildEventItem('invoice.overdue', 'عند تأخر الفاتورة'),
          _buildEventItem('driver.assigned', 'عند تعيين سائق للشحنة'),
          _buildEventItem('temperature.alert', 'عند تجاوز درجة الحرارة'),
        ],
      ),
    );
  }

  Widget _buildEventItem(String event, String description) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
            decoration: BoxDecoration(
              color: AppTheme.primaryColor.withOpacity(0.2),
              borderRadius: BorderRadius.circular(4),
            ),
            child: Text(
              event,
              style: TextStyle(
                color: AppTheme.primaryColor,
                fontSize: 11,
                fontFamily: 'monospace',
              ),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Text(
              description,
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _showAddWebhookDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: AppTheme.cardBackground,
        title: const Text(
          'إضافة Webhook جديد',
          style: TextStyle(color: Colors.white),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              style: const TextStyle(color: Colors.white),
              decoration: InputDecoration(
                labelText: 'الاسم',
                labelStyle: TextStyle(color: AppTheme.textSecondary),
                border: const OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              style: const TextStyle(color: Colors.white),
              decoration: InputDecoration(
                labelText: 'الرابط (URL)',
                labelStyle: TextStyle(color: AppTheme.textSecondary),
                border: const OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إضافة'),
          ),
        ],
      ),
    );
  }

  List<WebhookModel> _getWebhooks() {
    return [
      WebhookModel(
        name: 'Production Webhook',
        url: 'https://api.mycompany.com/webhooks/edham',
        secret: 'whsec_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
        events: [
          'shipment.created',
          'shipment.updated',
          'shipment.delivered',
          'invoice.paid',
        ],
        isActive: true,
        successRate: 98,
        lastDelivery: 'منذ 5 دقائق',
        totalRequests: 1234,
        successful: 1209,
        failed: 25,
      ),
      WebhookModel(
        name: 'Testing Webhook',
        url: 'https://staging.mycompany.com/webhooks',
        secret: 'whsec_yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy',
        events: [
          'shipment.created',
          'shipment.cancelled',
        ],
        isActive: false,
        successRate: 85,
        lastDelivery: 'منذ 3 أيام',
        totalRequests: 56,
        successful: 48,
        failed: 8,
      ),
    ];
  }
}

class WebhookModel {
  final String name;
  final String url;
  final String secret;
  final List<String> events;
  final bool isActive;
  final int successRate;
  final String lastDelivery;
  final int totalRequests;
  final int successful;
  final int failed;

  WebhookModel({
    required this.name,
    required this.url,
    required this.secret,
    required this.events,
    required this.isActive,
    required this.successRate,
    required this.lastDelivery,
    required this.totalRequests,
    required this.successful,
    required this.failed,
  });
}
