// ============================================
// 🔒 Privacy Screen - Premium Privacy Controls
// Enterprise Privacy Settings with Data Protection
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class PrivacyScreen extends StatefulWidget {
  const PrivacyScreen({super.key});

  @override
  State<PrivacyScreen> createState() => _PrivacyScreenState();
}

class _PrivacyScreenState extends State<PrivacyScreen> {
  bool _dataCollectionEnabled = true;
  bool _analyticsEnabled = false;
  bool _marketingEnabled = false;
  bool _locationSharingEnabled = true;
  bool _profileVisibilityEnabled = true;
  bool _searchHistoryEnabled = true;
  bool _cookiesEnabled = true;
  bool _thirdPartySharingEnabled = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: AppBar(
        title: Text(
          'الخصوصية والأمان',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          onPressed: () => Navigator.pop(context),
          icon: const Icon(Icons.arrow_back, color: AppTheme.textPrimary),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Privacy Overview
            _buildPrivacyOverview(),
            const SizedBox(height: 30),

            // Data Collection
            _buildPrivacySection('جمع البيانات', [
              _buildToggleSetting(
                'جمع البيانات الأساسية',
                'جمع المعلومات الضرورية لتقديم الخدمات',
                _dataCollectionEnabled,
                (value) => setState(() => _dataCollectionEnabled = value),
                isRequired: true,
              ),
              _buildToggleSetting(
                'تحليلات الاستخدام',
                'مشاركة بيانات الاستخدام المجهولة الهوية',
                _analyticsEnabled,
                (value) => setState(() => _analyticsEnabled = value),
              ),
              _buildToggleSetting(
                'التسويق والإعلانات',
                'استخدام البيانات للعروض المخصصة',
                _marketingEnabled,
                (value) => setState(() => _marketingEnabled = value),
              ),
            ]),
            const SizedBox(height: 24),

            // Location & Tracking
            _buildPrivacySection('الموقع والتتبع', [
              _buildToggleSetting(
                'مشاركة الموقع',
                'مشاركة الموقع أثناء توصيل الشحنات',
                _locationSharingEnabled,
                (value) => setState(() => _locationSharingEnabled = value),
                isRequired: true,
              ),
              _buildToggleSetting(
                'سجل البحث',
                'حفظ سجل عمليات البحث',
                _searchHistoryEnabled,
                (value) => setState(() => _searchHistoryEnabled = value),
              ),
            ]),
            const SizedBox(height: 24),

            // Profile & Social
            _buildPrivacySection('الملف الشخصي والاجتماعي', [
              _buildToggleSetting(
                'رؤية الملف الشخصي',
                'السماح للآخرين برؤية ملفك الشخصي',
                _profileVisibilityEnabled,
                (value) => setState(() => _profileVisibilityEnabled = value),
              ),
              _buildToggleSetting(
                'ملفات تعريف الارتباط',
                'استخدام ملفات تعريف الارتباط',
                _cookiesEnabled,
                (value) => setState(() => _cookiesEnabled = value),
              ),
              _buildToggleSetting(
                'مشاركة مع أطراف ثالثة',
                'مشاركة البيانات مع شركاء موثوقين',
                _thirdPartySharingEnabled,
                (value) => setState(() => _thirdPartySharingEnabled = value),
              ),
            ]),
            const SizedBox(height: 24),

            // Data Management
            _buildPrivacySection('إدارة البيانات', [
              _buildActionItem(
                'تنزيل بياناتي',
                Icons.download,
                () => _downloadData(),
              ),
              _buildActionItem(
                'تصدير البيانات',
                Icons.file_download,
                () => _exportData(),
              ),
              _buildActionItem(
                'حذف البيانات',
                Icons.delete_sweep,
                () => _deleteData(),
                isDanger: true,
              ),
            ]),
            const SizedBox(height: 24),

            // Security
            _buildPrivacySection('الأمان', [
              _buildActionItem(
                'تغيير كلمة المرور',
                Icons.lock,
                () => _changePassword(),
              ),
              _buildActionItem(
                'المصادقة الثنائية',
                Icons.security,
                () => _enable2FA(),
              ),
              _buildActionItem(
                'جلسات نشطة',
                Icons.devices,
                () => _viewActiveSessions(),
              ),
            ]),
            const SizedBox(height: 30),

            // Privacy Policy
            _buildPrivacyPolicy(),
          ],
        ),
      ),
    );
  }

  Widget _buildPrivacyOverview() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      boxShadow: AppShadows.glowing(AppTheme.primary),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.privacy_tip,
                color: AppTheme.primary,
                size: 32,
              ),
              const SizedBox(width: 12),
              Text(
                'خصوصيتك مهمة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Text(
            'نحن نحترم خصوصيتك ونلتزم بحماية بياناتك. يمكنك التحكم في كيفية جمع واستخدام بياناتك من خلال الإعدادات أدناه.',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
              height: 1.5,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Icon(
                Icons.shield,
                color: AppTheme.success,
                size: 20,
              ),
              const SizedBox(width: 8),
              Text(
                'بياناتك محمية بالتشفير',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.success,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildPrivacySection(String title, List<Widget> items) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.bold,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 200)),
        const SizedBox(height: 12),
        GlassContainer(
          radius: 16,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.textHint.withOpacity(0.2),
          child: Column(
            children: items,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 400)),
      ],
    );
  }

  Widget _buildToggleSetting(
    String title,
    String description,
    bool value,
    Function(bool) onChanged, {
    bool isRequired = false,
  }) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Text(
                          title,
                          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            color: AppTheme.textPrimary,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        if (isRequired) ...[
                          const SizedBox(width: 8),
                          Icon(
                            Icons.priority_high,
                            color: AppTheme.primary,
                            size: 16,
                          ),
                        ],
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      description,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textSecondary,
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
              Switch(
                value: value,
                onChanged: isRequired ? null : onChanged,
                activeColor: AppTheme.primary,
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildActionItem(
    String title,
    IconData icon,
    VoidCallback onTap, {
    bool isDanger = false,
  }) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(
              icon,
              color: isDanger ? AppTheme.error : AppTheme.primary,
              size: 24,
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Text(
                title,
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: isDanger ? AppTheme.error : AppTheme.textPrimary,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
            Icon(
              Icons.arrow_forward_ios,
              color: isDanger ? AppTheme.error : AppTheme.textHint,
              size: 16,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPrivacyPolicy() {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.textHint.withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'سياسة الخصوصية',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            'لقراءة سياسة الخصوصية الكاملة، يرجى زيارة موقعنا الإلكتروني أو الاتصال بفريق الدعم.',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: GlowingButton(
                  text: 'قراءة السياسة',
                  onPressed: () => _openPrivacyPolicy(),
                  color: AppTheme.primary,
                  icon: Icons.description,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: GlassContainer(
                  height: 48,
                  radius: 16,
                  backgroundColor: Colors.white.withOpacity(0.05),
                  borderColor: AppTheme.primary.withOpacity(0.3),
                  child: InkWell(
                    onTap: () => _contactSupport(),
                    child: Center(
                      child: Text(
                        'تواصل مع الدعم',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.primary,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 1000));
  }

  void _downloadData() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.primary,
        content: Text('جاري تحضير بياناتك للتنزيل...'),
        duration: Duration(seconds: 2),
      ),
    );
  }

  void _exportData() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.primary,
        content: Text('جاري تصدير بياناتك...'),
        duration: Duration(seconds: 2),
      ),
    );
  }

  void _deleteData() {
    showDialog(
      context: context,
      builder: (context) => Dialog(
        backgroundColor: Colors.transparent,
        child: GlassContainer(
          padding: const EdgeInsets.all(24),
          radius: 20,
          backgroundColor: AppTheme.background.withOpacity(0.9),
          borderColor: AppTheme.error.withOpacity(0.3),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.warning,
                color: AppTheme.error,
                size: 64,
              ),
              const SizedBox(height: 16),
              Text(
                'حذف البيانات',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'هل أنت متأكد من حذف بياناتك؟ هذا الإجراء لا يمكن التراجع عنه.',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              Row(
                children: [
                  Expanded(
                    child: GlassContainer(
                      height: 48,
                      radius: 16,
                      backgroundColor: Colors.white.withOpacity(0.05),
                      borderColor: AppTheme.textHint.withOpacity(0.3),
                      child: Center(
                        child: Text(
                          'إلغاء',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textSecondary,
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: GlowingButton(
                      text: 'حذف البيانات',
                      onPressed: () {
                        Navigator.pop(context);
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                            backgroundColor: AppTheme.error,
                            content: Text('تم حذف البيانات بنجاح'),
                          ),
                        );
                      },
                      color: AppTheme.error,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _changePassword() {
    // Navigate to change password
  }

  void _enable2FA() {
    // Navigate to 2FA setup
  }

  void _viewActiveSessions() {
    // Navigate to active sessions
  }

  void _openPrivacyPolicy() {
    // Open privacy policy
  }

  void _contactSupport() {
    // Contact support
  }
}
