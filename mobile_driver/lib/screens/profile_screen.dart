import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  bool _isEditing = false;
  final _nameController = TextEditingController();
  final _phoneController = TextEditingController();
  final _emailController = TextEditingController();
  final _vehicleController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  void _loadUserData() {
    final authService = Provider.of<AuthService>(context, listen: false);
    final user = authService.user;

    if (user != null) {
      _nameController.text = user['name'] ?? '';
      _phoneController.text = user['phone'] ?? '';
      _emailController.text = user['email'] ?? '';
      _vehicleController.text = user['vehicle'] ?? '';
    }
  }

  Future<void> _saveProfile() async {
    final authService = Provider.of<AuthService>(context, listen: false);

    final success = await authService.updateProfile({
      'name': _nameController.text,
      'phone': _phoneController.text,
      'vehicle': _vehicleController.text,
    });

    if (success) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('تم حفظ التغييرات بنجاح'),
          backgroundColor: Colors.green,
        ),
      );
      setState(() => _isEditing = false);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('فشل حفظ التغييرات'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  Future<void> _logout() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تسجيل الخروج'),
        content: const Text('هل أنت متأكد من تسجيل الخروج؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
            ),
            child: const Text('تأكيد'),
          ),
        ],
      ),
    );

    if (confirm == true) {
      final authService = Provider.of<AuthService>(context, listen: false);
      await authService.logout();
      Navigator.pushReplacementNamed(context, '/login');
    }
  }

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final user = authService.user;

    return Scaffold(
      appBar: AppBar(
        title: const Text('الملف الشخصي'),
        actions: [
          IconButton(
            icon: Icon(_isEditing ? Icons.check : Icons.edit),
            onPressed: () {
              if (_isEditing) {
                _saveProfile();
              } else {
                setState(() => _isEditing = true);
              }
            },
          ),
        ],
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              // Profile Header
              Container(
                padding: const EdgeInsets.all(24),
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [Color(0xFF003D5C), Color(0xFF005A7F)],
                  ),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  children: [
                    Stack(
                      children: [
                        Container(
                          width: 100,
                          height: 100,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: const Color(0xFFD4AF37),
                            border: Border.all(
                              color: Colors.white,
                              width: 3,
                            ),
                            boxShadow: [
                              BoxShadow(
                                color: Colors.black.withOpacity(0.3),
                                blurRadius: 10,
                              ),
                            ],
                          ),
                          child: const Icon(
                            Icons.person,
                            size: 50,
                            color: Colors.white,
                          ),
                        ),
                        Positioned(
                          bottom: 0,
                          right: 0,
                          child: Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                              color: const Color(0xFF27AE60),
                              borderRadius: BorderRadius.circular(20),
                              border: Border.all(
                                color: Colors.white,
                                width: 2,
                              ),
                            ),
                            child: const Icon(
                              Icons.check,
                              color: Colors.white,
                              size: 16,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Text(
                      user?['name'] ?? 'اسم السائق',
                      style: const TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 6,
                      ),
                      decoration: BoxDecoration(
                        color: const Color(0xFFD4AF37).withOpacity(0.2),
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(
                          color: const Color(0xFFD4AF37),
                          width: 1,
                        ),
                      ),
                      child: const Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(
                            Icons.star,
                            color: Color(0xFFD4AF37),
                            size: 16,
                          ),
                          SizedBox(width: 4),
                          Text(
                            '4.8 - سائق مميز',
                            style: TextStyle(
                              color: Color(0xFFD4AF37),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // Stats
              Row(
                children: [
                  Expanded(
                    child: _buildStatCard(
                      icon: Icons.local_shipping,
                      value: '156',
                      label: 'رحلة منجزة',
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _buildStatCard(
                      icon: Icons.access_time,
                      value: '1,250',
                      label: 'ساعة عمل',
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _buildStatCard(
                      icon: Icons.account_balance_wallet,
                      value: '45K',
                      label: 'إجمالي الأرباح',
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),

              // Personal Info
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: const Color(0xFF1B2E4D),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'المعلومات الشخصية',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        ),
                        if (_isEditing)
                          TextButton(
                            onPressed: () => setState(() => _isEditing = false),
                            child: const Text('إلغاء'),
                          ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    _buildTextField(
                      controller: _nameController,
                      label: 'الاسم الكامل',
                      icon: Icons.person,
                      enabled: _isEditing,
                    ),
                    const SizedBox(height: 16),
                    _buildTextField(
                      controller: _phoneController,
                      label: 'رقم الهاتف',
                      icon: Icons.phone,
                      enabled: _isEditing,
                      keyboardType: TextInputType.phone,
                    ),
                    const SizedBox(height: 16),
                    _buildTextField(
                      controller: _emailController,
                      label: 'البريد الإلكتروني',
                      icon: Icons.email,
                      enabled: false,
                    ),
                    const SizedBox(height: 16),
                    _buildTextField(
                      controller: _vehicleController,
                      label: 'رقم المركبة',
                      icon: Icons.directions_car,
                      enabled: _isEditing,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // Settings
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: const Color(0xFF1B2E4D),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'الإعدادات',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 16),
                    _buildSettingsTile(
                      icon: Icons.notifications,
                      title: 'الإشعارات',
                      subtitle: 'تفعيل إشعارات الرحلات',
                      onTap: () {},
                    ),
                    const Divider(color: Colors.white24),
                    _buildSettingsTile(
                      icon: Icons.language,
                      title: 'اللغة',
                      subtitle: 'العربية',
                      onTap: () {},
                    ),
                    const Divider(color: Colors.white24),
                    _buildSettingsTile(
                      icon: Icons.help,
                      title: 'المساعدة والدعم',
                      subtitle: 'تواصل مع فريق الدعم',
                      onTap: () {},
                    ),
                    const Divider(color: Colors.white24),
                    _buildSettingsTile(
                      icon: Icons.privacy_tip,
                      title: 'سياسة الخصوصية',
                      subtitle: 'شروط الاستخدام',
                      onTap: () {},
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // Logout Button
              SizedBox(
                width: double.infinity,
                child: ElevatedButton.icon(
                  onPressed: _logout,
                  icon: const Icon(Icons.logout),
                  label: const Text('تسجيل الخروج'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                ),
              ),
              const SizedBox(height: 32),

              // Version
              Center(
                child: Text(
                  'الإصدار 1.0.0',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.white.withOpacity(0.5),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatCard({
    required IconData icon,
    required String value,
    required String label,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF1B2E4D),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: const Color(0xFF0099D8).withOpacity(0.3),
        width: 1,
        ),
      ),
      child: Column(
        children: [
          Icon(icon, color: const Color(0xFF0099D8), size: 24),
          const SizedBox(height: 8),
          Text(
            value,
            style: const TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: TextStyle(
              fontSize: 11,
              color: Colors.white.withOpacity(0.7),
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildTextField({
    required TextEditingController controller,
    required String label,
    required IconData icon,
    bool enabled = true,
    TextInputType keyboardType = TextInputType.text,
  }) {
    return TextField(
      controller: controller,
      enabled: enabled,
      keyboardType: keyboardType,
      style: TextStyle(
        color: enabled ? Colors.white : Colors.white.withOpacity(0.6),
      ),
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon, color: const Color(0xFF0099D8)),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: enabled ? const Color(0xFF0099D8) : Colors.white.withOpacity(0.3),
          ),
        ),
        disabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: Colors.white.withOpacity(0.1),
          ),
        ),
        filled: !enabled,
        fillColor: enabled ? null : Colors.white.withOpacity(0.05),
      ),
    );
  }

  Widget _buildSettingsTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
  }) {
    return ListTile(
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFF0099D8).withOpacity(0.2),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(icon, color: const Color(0xFF0099D8), size: 20),
      ),
      title: Text(
        title,
        style: const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.w600,
        ),
      ),
      subtitle: Text(
        subtitle,
        style: TextStyle(
          color: Colors.white.withOpacity(0.6),
          fontSize: 12,
        ),
      ),
      trailing: const Icon(
        Icons.arrow_forward_ios,
        color: Colors.white54,
        size: 16,
      ),
      onTap: onTap,
    );
  }
}
