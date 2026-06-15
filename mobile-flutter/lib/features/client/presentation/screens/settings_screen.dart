// ============================================
// ⚙️ Settings Screen - Premium User Settings
// Enterprise Settings with Privacy Controls
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _notificationsEnabled = true;
  bool _locationEnabled = true;
  bool _biometricEnabled = false;
  bool _darkModeEnabled = true;
  bool _autoBackupEnabled = true;
  String _selectedLanguage = 'ar';
  String _selectedCurrency = 'SAR';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: AppBar(
        title: Text(
          'الإعدادات',
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
          children: [
            // Profile Section
            _buildProfileSection(),
            const SizedBox(height: 24),

            // Account Settings
            _buildSection('حسابي', [
              _buildSettingItem(
                'معلوماتي الشخصية',
                Icons.person,
                () => _navigateToProfile(),
              ),
              _buildSettingItem(
                'الأمان',
                Icons.security,
                () => _navigateToSecurity(),
              ),
              _buildSettingItem(
                'المحفظة والمدفوعات',
                Icons.account_balance_wallet,
                () => _navigateToWallet(),
              ),
            ]),
            const SizedBox(height: 24),

            // App Settings
            _buildSection('إعدادات التطبيق', [
              _buildToggleSetting(
                'الإشعارات',
                Icons.notifications,
                _notificationsEnabled,
                (value) => setState(() => _notificationsEnabled = value),
              ),
              _buildToggleSetting(
                'الموقع',
                Icons.location_on,
                _locationEnabled,
                (value) => setState(() => _locationEnabled = value),
              ),
              _buildToggleSetting(
                'الوضع الليلي',
                Icons.dark_mode,
                _darkModeEnabled,
                (value) => setState(() => _darkModeEnabled = value),
              ),
              _buildToggleSetting(
                'المصادقة البيومترية',
                Icons.fingerprint,
                _biometricEnabled,
                (value) => setState(() => _biometricEnabled = value),
              ),
              _buildToggleSetting(
                'النسخ الاحتياطي التلقائي',
                Icons.backup,
                _autoBackupEnabled,
                (value) => setState(() => _autoBackupEnabled = value),
              ),
            ]),
            const SizedBox(height: 24),

            // Preferences
            _buildSection('التفضيلات', [
              _buildLanguageSetting(),
              _buildCurrencySetting(),
            ]),
            const SizedBox(height: 24),

            // Privacy & Support
            _buildSection('الخصوصية والدعم', [
              _buildSettingItem(
                'الخصوصية',
                Icons.privacy_tip,
                () => _navigateToPrivacy(),
              ),
              _buildSettingItem(
                'الشروط والأحكام',
                Icons.description,
                () => _navigateToTerms(),
              ),
              _buildSettingItem(
                'مركز المساعدة',
                Icons.help,
                () => _navigateToHelp(),
              ),
              _buildSettingItem(
                'تواصل معنا',
                Icons.contact_support,
                () => _navigateToContact(),
              ),
            ]),
            const SizedBox(height: 24),

            // Danger Zone
            _buildSection('خيارات خطرة', [
              _buildSettingItem(
                'حذف الحساب',
                Icons.delete_forever,
                () => _showDeleteAccountDialog(),
                isDanger: true,
              ),
            ]),
            const SizedBox(height: 40),

            // App Version
            _buildAppVersion(),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileSection() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      boxShadow: AppShadows.glowing(AppTheme.primary),
      child: Row(
        children: [
          // Profile Avatar
          GlassContainer(
            width: 80,
            height: 80,
            radius: 40,
            backgroundColor: AppTheme.primary.withOpacity(0.2),
            borderColor: AppTheme.primary.withOpacity(0.4),
            child: const Icon(
              Icons.person,
              size: 40,
              color: AppTheme.primary,
            ),
          ),
          const SizedBox(width: 20),

          // Profile Info
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'أحمد محمد',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  'ahmed.mohammed@example.com',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Icon(
                      Icons.account_balance_wallet,
                      color: AppTheme.primary,
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'المحفظة: 2,500 ريال',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.primary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          // Edit Button
          IconButton(
            onPressed: () => _navigateToProfile(),
            icon: const Icon(Icons.edit, color: AppTheme.primary),
          ),
        ],
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildSection(String title, List<Widget> items) {
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

  Widget _buildSettingItem(
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

  Widget _buildToggleSetting(
    String title,
    IconData icon,
    bool value,
    Function(bool) onChanged,
  ) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          Icon(
            icon,
            color: AppTheme.primary,
            size: 24,
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Text(
              title,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Switch(
            value: value,
            onChanged: onChanged,
            activeColor: AppTheme.primary,
          ),
        ],
      ),
    );
  }

  Widget _buildLanguageSetting() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          const Icon(Icons.language, color: AppTheme.primary, size: 24),
          const SizedBox(width: 16),
          Expanded(
            child: Text(
              'اللغة',
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          DropdownButton<String>(
            value: _selectedLanguage,
            onChanged: (value) {
              setState(() {
                _selectedLanguage = value!;
              });
            },
            items: const [
              DropdownMenuItem(value: 'ar', child: Text('العربية')),
              DropdownMenuItem(value: 'en', child: Text('English')),
            ],
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
            ),
            dropdownColor: AppTheme.surface,
          ),
        ],
      ),
    );
  }

  Widget _buildCurrencySetting() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          const Icon(Icons.attach_money, color: AppTheme.primary, size: 24),
          const SizedBox(width: 16),
          Expanded(
            child: Text(
              'العملة',
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          DropdownButton<String>(
            value: _selectedCurrency,
            onChanged: (value) {
              setState(() {
                _selectedCurrency = value!;
              });
            },
            items: const [
              DropdownMenuItem(value: 'SAR', child: Text('ريال سعودي')),
              DropdownMenuItem(value: 'AED', child: Text('درهم إماراتي')),
              DropdownMenuItem(value: 'USD', child: Text('دولار أمريكي')),
            ],
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
            ),
            dropdownColor: AppTheme.surface,
          ),
        ],
      ),
    );
  }

  Widget _buildAppVersion() {
    return Column(
      children: [
        Text(
          'إصدار التطبيق',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          'v2.0.1',
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textHint,
          ),
        ),
        const SizedBox(height: 16),
        Text(
          '© 2024 إدهام للخدمات اللوجستية',
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textHint,
          ),
        ),
      ],
    ).animate().fadeIn(delay: const Duration(milliseconds: 1200));
  }

  void _navigateToProfile() {
    // Navigate to profile screen
  }

  void _navigateToSecurity() {
    // Navigate to security settings
  }

  void _navigateToWallet() {
    // Navigate to wallet settings
  }

  void _navigateToPrivacy() {
    // Navigate to privacy settings
  }

  void _navigateToTerms() {
    // Navigate to terms and conditions
  }

  void _navigateToHelp() {
    // Navigate to help center
  }

  void _navigateToContact() {
    // Navigate to contact support
  }

  void _showDeleteAccountDialog() {
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
                'حذف الحساب',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'هل أنت متأكد من حذف حسابك؟ هذا الإجراء لا يمكن التراجع عنه.',
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
                      text: 'حذف الحساب',
                      onPressed: () {
                        Navigator.pop(context);
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                            backgroundColor: AppTheme.error,
                            content: Text('تم حذف الحساب بنجاح'),
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
}
