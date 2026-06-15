// ============================================
// 🔑 API Keys Screen - إدارة مفاتيح API
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../../../core/theme/app_theme.dart';

class ApiKeysScreen extends StatefulWidget {
  const ApiKeysScreen({super.key});

  @override
  State<ApiKeysScreen> createState() => _ApiKeysScreenState();
}

class _ApiKeysScreenState extends State<ApiKeysScreen> {
  bool _showKey = false;
  int _selectedKey = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'مفاتيح API',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.add, color: Colors.white),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Info Card
          _buildInfoCard(),
          
          const SizedBox(height: 20),
          
          // API Keys List
          ..._getApiKeys().asMap().entries.map((entry) {
            return _buildApiKeyCard(entry.value, entry.key);
          }),
          
          const SizedBox(height: 20),
          
          // Documentation
          _buildDocumentationCard(),
        ],
      ),
    );
  }

  Widget _buildInfoCard() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.primaryColor, AppTheme.primaryDark],
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Row(
            children: [
              Icon(Icons.info_outline, color: Colors.white),
              SizedBox(width: 8),
              Text(
                'تكامل API',
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
            'استخدم مفاتيح API للتكامل مع أنظمتك الخارجية. احتفظ بهذه المفاتيح سرية ولا تشاركها مع أي شخص.',
            style: TextStyle(
              color: Colors.white.withOpacity(0.9),
              fontSize: 13,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildApiKeyCard(ApiKeyModel key, int index) {
    final isSelected = _selectedKey == index;

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: isSelected
            ? Border.all(color: AppTheme.primaryColor, width: 2)
            : null,
      ),
      child: ExpansionTile(
        initiallyExpanded: isSelected,
        onExpansionChanged: (expanded) {
          if (expanded) {
            setState(() => _selectedKey = index);
          }
        },
        leading: Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: key.isLive
                ? Colors.green.withOpacity(0.2)
                : Colors.orange.withOpacity(0.2),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(
            Icons.vpn_key,
            color: key.isLive ? Colors.green : Colors.orange,
            size: 20,
          ),
        ),
        title: Text(
          key.name,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
        ),
        subtitle: Row(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: key.isLive
                    ? Colors.green.withOpacity(0.2)
                    : Colors.orange.withOpacity(0.2),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                key.isLive ? 'Live' : 'Test',
                style: TextStyle(
                  color: key.isLive ? Colors.green : Colors.orange,
                  fontSize: 11,
                ),
              ),
            ),
            const SizedBox(width: 8),
            Text(
              '•••• ${key.last4}',
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 12,
              ),
            ),
          ],
        ),
        trailing: Icon(
          isSelected ? Icons.expand_less : Icons.expand_more,
          color: AppTheme.textSecondary,
        ),
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // API Key Display
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AppTheme.backgroundColor,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: Text(
                          _showKey
                              ? 'ak_live_xxxxxxxxxxxxxxxx'
                              : '••••••••••••••••••••••',
                          style: TextStyle(
                            color: _showKey
                                ? Colors.white
                                : AppTheme.textSecondary,
                            fontFamily: 'monospace',
                            letterSpacing: 2,
                          ),
                        ),
                      ),
                      IconButton(
                        onPressed: () {
                          setState(() => _showKey = !_showKey);
                        },
                        icon: Icon(
                          _showKey ? Icons.visibility_off : Icons.visibility,
                          color: AppTheme.textSecondary,
                          size: 20,
                        ),
                      ),
                      IconButton(
                        onPressed: () {
                          Clipboard.setData(const ClipboardData(
                            text: 'ak_live_xxxxxxxxxxxxxxxx',
                          ));
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(
                              content: Text('تم نسخ المفتاح'),
                            ),
                          );
                        },
                        icon: Icon(
                          Icons.copy,
                          color: AppTheme.primaryColor,
                          size: 20,
                        ),
                      ),
                    ],
                  ),
                ),
                
                const SizedBox(height: 16),
                
                // Stats
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildStat('الطلبات', '${key.requests}'),
                    _buildStat('آخر استخدام', key.lastUsed),
                    _buildStat('الحالة', key.status),
                  ],
                ),
                
                const SizedBox(height: 16),
                
                // Permissions
                const Text(
                  'الصلاحيات',
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: key.permissions.map((perm) {
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
                        perm,
                        style: TextStyle(
                          color: AppTheme.primaryColor,
                          fontSize: 11,
                        ),
                      ),
                    );
                  }).toList(),
                ),
                
                const SizedBox(height: 16),
                
                // Actions
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: () {},
                        icon: const Icon(Icons.refresh, size: 18),
                        label: const Text('تجديد'),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: AppTheme.primaryColor,
                          side: BorderSide(color: AppTheme.primaryColor),
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
                          foregroundColor: Colors.blue,
                          side: const BorderSide(color: Colors.blue),
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
                          side: const BorderSide(color: Colors.red),
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

  Widget _buildDocumentationCard() {
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
            'التوثيق والتكامل',
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 16,
            ),
          ),
          const SizedBox(height: 16),
          _buildDocItem(
            '📚',
            'API Documentation',
            'توثيق شامل لجميع نقاط النهاية',
          ),
          _buildDocItem(
            '💻',
            'SDKs & Libraries',
            'مكتبات جاهزة للـ JavaScript, Python, PHP',
          ),
          _buildDocItem(
            '🎯',
            'Webhooks',
            'استقبال الأحداث في الوقت الفعلي',
          ),
          _buildDocItem(
            '🧪',
            'API Tester',
            'اختبار API مباشرة من المتصفح',
          ),
        ],
      ),
    );
  }

  Widget _buildDocItem(String icon, String title, String description) {
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: Text(icon, style: const TextStyle(fontSize: 24)),
      title: Text(
        title,
        style: const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
        ),
      ),
      subtitle: Text(
        description,
        style: TextStyle(
          color: AppTheme.textSecondary,
          fontSize: 12,
        ),
      ),
      trailing: Icon(
        Icons.arrow_forward_ios,
        color: AppTheme.primaryColor,
        size: 16,
      ),
      onTap: () {},
    );
  }

  List<ApiKeyModel> _getApiKeys() {
    return [
      ApiKeyModel(
        name: 'Production Key',
        last4: 'xK9m',
        isLive: true,
        requests: 12543,
        lastUsed: 'منذ ساعة',
        status: 'نشط',
        permissions: [
          'shipments.read',
          'shipments.write',
          'tracking.read',
        ],
      ),
      ApiKeyModel(
        name: 'Test Key',
        last4: 'pL2n',
        isLive: false,
        requests: 892,
        lastUsed: 'منذ 3 أيام',
        status: 'نشط',
        permissions: [
          'shipments.read',
          'tracking.read',
        ],
      ),
    ];
  }
}

class ApiKeyModel {
  final String name;
  final String last4;
  final bool isLive;
  final int requests;
  final String lastUsed;
  final String status;
  final List<String> permissions;

  ApiKeyModel({
    required this.name,
    required this.last4,
    required this.isLive,
    required this.requests,
    required this.lastUsed,
    required this.status,
    required this.permissions,
  });
}
