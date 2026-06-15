import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  bool _notificationsEnabled = true;
  bool _darkModeEnabled = true;
  String _selectedLanguage = 'العربية';

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final user = authService.user;

    return Scaffold(
      appBar: AppBar(
        title: const Text('الملف الشخصي'),
        centerTitle: true,
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Profile Header Card
          Card(
            child: Container(
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF0099D8), Color(0xFF003D5C)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                children: [
                  const CircleAvatar(
                    radius: 50,
                    backgroundColor: Colors.white,
                    child: Icon(Icons.person, size: 50, color: Color(0xFF0099D8)),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    user?['name'] ?? 'غير معروف',
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    user?['email'] ?? '',
                    style: const TextStyle(
                      fontSize: 16,
                      color: Colors.white70,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(
                      color: const Color(0xFFD4AF37),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      user?['role'] ?? 'موظف',
                      style: const TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),

          // Account Section
          _buildSectionTitle('الحساب'),
          Card(
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.edit, color: Color(0xFF0099D8)),
                  title: const Text('تعديل الملف الشخصي'),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                  onTap: () {
                    _showEditProfileDialog(context, user);
                  },
                ),
                const Divider(height: 1),
                ListTile(
                  leading: const Icon(Icons.lock, color: Color(0xFF0099D8)),
                  title: const Text('تغيير كلمة المرور'),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                  onTap: () {
                    _showChangePasswordDialog(context);
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Preferences Section
          _buildSectionTitle('التفضيلات'),
          Card(
            child: Column(
              children: [
                SwitchListTile(
                  secondary: const Icon(Icons.notifications, color: Color(0xFF0099D8)),
                  title: const Text('الإشعارات'),
                  subtitle: const Text('تفعيل إشعارات التطبيق'),
                  value: _notificationsEnabled,
                  activeColor: const Color(0xFF0099D8),
                  onChanged: (value) {
                    setState(() => _notificationsEnabled = value);
                  },
                ),
                const Divider(height: 1),
                SwitchListTile(
                  secondary: const Icon(Icons.dark_mode, color: Color(0xFF0099D8)),
                  title: const Text('الوضع الداكن'),
                  subtitle: const Text('تفعيل المظهر الداكن'),
                  value: _darkModeEnabled,
                  activeColor: const Color(0xFF0099D8),
                  onChanged: (value) {
                    setState(() => _darkModeEnabled = value);
                  },
                ),
                const Divider(height: 1),
                ListTile(
                  leading: const Icon(Icons.language, color: Color(0xFF0099D8)),
                  title: const Text('اللغة'),
                  subtitle: Text(_selectedLanguage),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                  onTap: () {
                    _showLanguageDialog(context);
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Support Section
          _buildSectionTitle('الدعم'),
          Card(
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.help, color: Color(0xFF0099D8)),
                  title: const Text('مركز المساعدة'),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                  onTap: () {},
                ),
                const Divider(height: 1),
                ListTile(
                  leading: const Icon(Icons.privacy_tip, color: Color(0xFF0099D8)),
                  title: const Text('سياسة الخصوصية'),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                  onTap: () {},
                ),
                const Divider(height: 1),
                ListTile(
                  leading: const Icon(Icons.info, color: Color(0xFF0099D8)),
                  title: const Text('عن التطبيق'),
                  trailing: const Text('v1.0.0', style: TextStyle(color: Colors.grey)),
                  onTap: () {
                    _showAboutDialog(context);
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Logout Button
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: () {
                authService.logout();
                Navigator.pushReplacementNamed(context, '/login');
              },
              icon: const Icon(Icons.logout),
              label: const Text('تسجيل الخروج'),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFEF4444),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
            ),
          ),
          const SizedBox(height: 32),
        ],
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.only(right: 8, bottom: 8),
      child: Text(
        title,
        style: const TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.bold,
          color: Color(0xFF0099D8),
        ),
      ),
    );
  }

  void _showEditProfileDialog(BuildContext context, Map<String, dynamic>? user) {
    final nameController = TextEditingController(text: user?['name'] ?? '');
    final phoneController = TextEditingController(text: user?['phone'] ?? '');

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تعديل الملف الشخصي'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: nameController,
              decoration: const InputDecoration(
                labelText: 'الاسم',
                prefixIcon: Icon(Icons.person),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: phoneController,
              decoration: const InputDecoration(
                labelText: 'رقم الهاتف',
                prefixIcon: Icon(Icons.phone),
              ),
              keyboardType: TextInputType.phone,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('تم حفظ التغييرات')),
              );
            },
            child: const Text('حفظ'),
          ),
        ],
      ),
    );
  }

  void _showChangePasswordDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تغيير كلمة المرور'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              obscureText: true,
              decoration: const InputDecoration(
                labelText: 'كلمة المرور الحالية',
                prefixIcon: Icon(Icons.lock),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              obscureText: true,
              decoration: const InputDecoration(
                labelText: 'كلمة المرور الجديدة',
                prefixIcon: Icon(Icons.lock_outline),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              obscureText: true,
              decoration: const InputDecoration(
                labelText: 'تأكيد كلمة المرور',
                prefixIcon: Icon(Icons.lock_outline),
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
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('تم تغيير كلمة المرور')),
              );
            },
            child: const Text('تغيير'),
          ),
        ],
      ),
    );
  }

  void _showLanguageDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('اختيار اللغة'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            RadioListTile(
              title: const Text('العربية'),
              value: 'العربية',
              groupValue: _selectedLanguage,
              activeColor: const Color(0xFF0099D8),
              onChanged: (value) {
                setState(() => _selectedLanguage = value.toString());
                Navigator.pop(context);
              },
            ),
            RadioListTile(
              title: const Text('English'),
              value: 'English',
              groupValue: _selectedLanguage,
              activeColor: const Color(0xFF0099D8),
              onChanged: (value) {
                setState(() => _selectedLanguage = value.toString());
                Navigator.pop(context);
              },
            ),
          ],
        ),
      ),
    );
  }

  void _showAboutDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Column(
          children: [
            Icon(
              Icons.local_shipping,
              size: 48,
              color: Color(0xFF0099D8),
            ),
            SizedBox(height: 16),
            Text('إدهام للنقل المبرد'),
          ],
        ),
        content: const Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('الإصدار 1.0.0'),
            SizedBox(height: 16),
            Text(
              'نظام متكامل لإدارة النقل المبرد والشحنات',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey),
            ),
            SizedBox(height: 16),
            Text(
              '© 2025 إدهام للنقل المبرد',
              style: TextStyle(fontSize: 12, color: Colors.grey),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('موافق'),
          ),
        ],
      ),
    );
  }
}
