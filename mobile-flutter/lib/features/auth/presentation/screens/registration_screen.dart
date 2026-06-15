// ============================================
// 📝 Registration Screen - Premium User Onboarding
// Enterprise Registration with Wallet Creation
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';
import '../../../../core/design_system/spacing.dart';

class RegistrationScreen extends StatefulWidget {
  const RegistrationScreen({super.key});

  @override
  State<RegistrationScreen> createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends State<RegistrationScreen>
    with TickerProviderStateMixin {
  late AnimationController _formController;
  late AnimationController _walletController;
  
  final _formKey = GlobalKey<FormState>();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _companyController = TextEditingController();
  
  String _selectedRole = 'client';
  bool _agreeToTerms = false;
  bool _isLoading = false;
  int _currentStep = 0;

  @override
  void initState() {
    super.initState();
    _formController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    _walletController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _formController.forward();
  }

  @override
  void dispose() {
    _formController.dispose();
    _walletController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _companyController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Stack(
        children: [
          // Background gradient
          Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  AppTheme.background,
                  AppTheme.surface.withOpacity(0.2),
                  AppTheme.primary.withOpacity(0.05),
                ],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
              ),
            ),
          ),
          
          // Main content
          SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  // Header
                  _buildHeader(),
                  const SizedBox(height: 30),
                  
                  // Progress Steps
                  _buildProgressSteps(),
                  const SizedBox(height: 30),
                  
                  // Form Content
                  _buildFormContent(),
                  const SizedBox(height: 30),
                  
                  // Action Buttons
                  _buildActionButtons(),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return Column(
      children: [
        Hero(
          tag: 'logo',
          child: GlassContainer(
            width: 80,
            height: 80,
            radius: 20,
            backgroundColor: AppTheme.primary.withOpacity(0.1),
            borderColor: AppTheme.primary.withOpacity(0.3),
            boxShadow: AppShadows.glowing(AppTheme.primary),
            child: const Icon(
              Icons.person_add,
              size: 40,
              color: AppTheme.primary,
            ),
          ),
        ).animate().fadeIn().scale(begin: const Offset(0.5, 0.5), end: const Offset(1, 1)),
        
        const SizedBox(height: 20),
        
        Text(
          'إنشاء حساب جديد',
          style: Theme.of(context).textTheme.displayMedium?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.bold,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 200)),
        
        const SizedBox(height: 8),
        
        Text(
          'انضم إلى منصة إدهام اللوجستية',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 400)),
      ],
    );
  }

  Widget _buildProgressSteps() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        _buildStepIndicator(0, 'المعلومات الشخصية'),
        _buildStepConnector(),
        _buildStepIndicator(1, 'المحفظة'),
        _buildStepConnector(),
        _buildStepIndicator(2, 'التأكيد'),
      ],
    ).animate().fadeIn(delay: const Duration(milliseconds: 600));
  }

  Widget _buildStepIndicator(int step, String title) {
    final isActive = _currentStep == step;
    final isCompleted = _currentStep > step;
    
    return Column(
      children: [
        GlassContainer(
          width: 40,
          height: 40,
          radius: 20,
          backgroundColor: isActive 
              ? AppTheme.primary.withOpacity(0.2)
              : isCompleted 
                  ? AppTheme.success.withOpacity(0.2)
                  : Colors.white.withOpacity(0.05),
          borderColor: isActive 
              ? AppTheme.primary
              : isCompleted 
                  ? AppTheme.success
                  : AppTheme.textHint.withOpacity(0.3),
          child: Center(
            child: isCompleted
                ? Icon(Icons.check, color: AppTheme.success, size: 20)
                : Text(
                    '${step + 1}',
                    style: TextStyle(
                      color: isActive ? AppTheme.primary : AppTheme.textHint,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
          ),
        ),
        const SizedBox(height: 8),
        Text(
          title,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: isActive ? AppTheme.primary : AppTheme.textHint,
            fontWeight: isActive ? FontWeight.w600 : FontWeight.normal,
          ),
        ),
      ],
    );
  }

  Widget _buildStepConnector() {
    return Container(
      width: 40,
      height: 2,
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: _currentStep > 0 
              ? [AppTheme.success, AppTheme.success]
              : [AppTheme.textHint.withOpacity(0.3), AppTheme.textHint.withOpacity(0.3)],
        ),
      ),
    );
  }

  Widget _buildFormContent() {
    switch (_currentStep) {
      case 0:
        return _buildPersonalInfoForm();
      case 1:
        return _buildWalletForm();
      case 2:
        return _buildConfirmationForm();
      default:
        return _buildPersonalInfoForm();
    }
  }

  Widget _buildPersonalInfoForm() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      boxShadow: AppShadows.glassmorphism,
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            // Role Selection
            Text(
              'نوع الحساب',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            
            Row(
              children: [
                Expanded(
                  child: _buildRoleOption('client', 'عميل', Icons.person),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildRoleOption('supervisor', 'مشرف', Icons.admin_panel_settings),
                ),
              ],
            ),
            
            const SizedBox(height: 24),
            
            // Name Fields
            Row(
              children: [
                Expanded(
                  child: _buildTextField(
                    'الاسم الأول',
                    _firstNameController,
                    Icons.person,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'الاسم الأول مطلوب';
                      }
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildTextField(
                    'الاسم الأخير',
                    _lastNameController,
                    Icons.person_outline,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'الاسم الأخير مطلوب';
                      }
                      return null;
                    },
                  ),
                ),
              ],
            ),
            
            const SizedBox(height: 16),
            
            _buildTextField(
              'البريد الإلكتروني',
              _emailController,
              Icons.email,
              keyboardType: TextInputType.emailAddress,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'البريد الإلكتروني مطلوب';
                }
                if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
                  return 'البريد الإلكتروني غير صالح';
                }
                return null;
              },
            ),
            
            const SizedBox(height: 16),
            
            _buildTextField(
              'رقم الهاتف',
              _phoneController,
              Icons.phone,
              keyboardType: TextInputType.phone,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'رقم الهاتف مطلوب';
                }
                if (value.length < 10) {
                  return 'رقم الهاتف يجب أن يكون 10 أرقام على الأقل';
                }
                return null;
              },
            ),
            
            if (_selectedRole == 'supervisor') ...[
              const SizedBox(height: 16),
              _buildTextField(
                'اسم الشركة',
                _companyController,
                Icons.business,
                validator: (value) {
                  if (_selectedRole == 'supervisor' && (value == null || value.isEmpty)) {
                    return 'اسم الشركة مطلوب للمشرفين';
                  }
                  return null;
                },
              ),
            ],
            
            const SizedBox(height: 16),
            
            _buildTextField(
              'كلمة المرور',
              _passwordController,
              Icons.lock,
              obscureText: true,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'كلمة المرور مطلوبة';
                }
                if (value.length < 8) {
                  return 'كلمة المرور يجب أن تكون 8 أحرف على الأقل';
                }
                return null;
              },
            ),
            
            const SizedBox(height: 16),
            
            _buildTextField(
              'تأكيد كلمة المرور',
              _confirmPasswordController,
              Icons.lock_outline,
              obscureText: true,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'تأكيد كلمة المرور مطلوب';
                }
                if (value != _passwordController.text) {
                  return 'كلمات المرور غير متطابقة';
                }
                return null;
              },
            ),
            
            const SizedBox(height: 20),
            
            // Terms and Conditions
            Row(
              children: [
                Checkbox(
                  value: _agreeToTerms,
                  onChanged: (value) {
                    setState(() {
                      _agreeToTerms = value ?? false;
                    });
                  },
                  activeColor: AppTheme.primary,
                ),
                Expanded(
                  child: Text(
                    'أوافق على الشروط والأحكام',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 800)).slideY(begin: 0.3, end: 0);
  }

  Widget _buildWalletForm() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      boxShadow: AppShadows.glassmorphism,
      child: Column(
        children: [
          Icon(
            Icons.account_balance_wallet,
            color: AppTheme.primary,
            size: 64,
          ).animate().scale(begin: const Offset(0, 0), end: const Offset(1, 1)),
          
          const SizedBox(height: 20),
          
          Text(
            'محفظتك الإلكترونية',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 8),
          
          Text(
            'سيتم إنشاء محفظة إلكترونية لك تلقائياً',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
          
          const SizedBox(height: 30),
          
          // Wallet Features
          _buildWalletFeature(
            'رصيد ابتدائي',
            '100 ريال',
            Icons.card_giftcard,
            AppTheme.success,
          ),
          
          const SizedBox(height: 16),
          
          _buildWalletFeature(
            'عملة المكافآت',
            'نقاط ولاء',
            Icons.stars,
            AppTheme.accent,
          ),
          
          const SizedBox(height: 16),
          
          _buildWalletFeature(
            'طرق الدفع',
            'متعددة',
            Icons.payment,
            AppTheme.primary,
          ),
          
          const SizedBox(height: 30),
          
          GlowingButton(
            text: 'تفعيل المحفظة',
            onPressed: () {
              _walletController.forward();
              Future.delayed(const Duration(milliseconds: 500), () {
                setState(() {
                  _currentStep = 2;
                });
              });
            },
            color: AppTheme.primary,
            icon: Icons.account_balance_wallet,
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 800)).slideY(begin: 0.3, end: 0);
  }

  Widget _buildConfirmationForm() {
    return GlassContainer(
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      boxShadow: AppShadows.glassmorphism,
      child: Column(
        children: [
          Icon(
            Icons.check_circle_outline,
            color: AppTheme.success,
            size: 64,
          ).animate().scale(begin: const Offset(0, 0), end: const Offset(1, 1)),
          
          const SizedBox(height: 20),
          
          Text(
            'تأكيد المعلومات',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 20),
          
          // Summary Information
          _buildSummaryRow('الاسم', '${_firstNameController.text} ${_lastNameController.text}'),
          _buildSummaryRow('البريد', _emailController.text),
          _buildSummaryRow('الهاتف', _phoneController.text),
          _buildSummaryRow('نوع الحساب', _selectedRole == 'client' ? 'عميل' : 'مشرف'),
          
          if (_selectedRole == 'supervisor')
            _buildSummaryRow('الشركة', _companyController.text),
          
          const SizedBox(height: 20),
          
          Text(
            'سيتم إنشاء محفظة إلكترونية برصيد 100 ريال',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.primary,
              fontWeight: FontWeight.w600,
            ),
          ),
          
          const SizedBox(height: 30),
          
          GlowingButton(
            text: _isLoading ? 'جاري الإنشاء...' : 'إنشاء الحساب',
            onPressed: _isLoading ? null : _createAccount,
            color: AppTheme.success,
            icon: Icons.check_circle,
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 800)).slideY(begin: 0.3, end: 0);
  }

  Widget _buildRoleOption(String value, String label, IconData icon) {
    final isSelected = _selectedRole == value;
    
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 12,
      backgroundColor: isSelected 
          ? AppTheme.primary.withOpacity(0.2)
          : Colors.white.withOpacity(0.05),
      borderColor: isSelected 
          ? AppTheme.primary
          : AppTheme.textHint.withOpacity(0.3),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedRole = value;
          });
        },
        child: Column(
          children: [
            Icon(icon, color: isSelected ? AppTheme.primary : AppTheme.textHint),
            const SizedBox(height: 8),
            Text(
              label,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: isSelected ? AppTheme.primary : AppTheme.textHint,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTextField(
    String label,
    TextEditingController controller,
    IconData icon, {
    bool obscureText = false,
    TextInputType? keyboardType,
    String? Function(String?)? validator,
  }) {
    return TextFormField(
      controller: controller,
      obscureText: obscureText,
      keyboardType: keyboardType,
      style: const TextStyle(color: AppTheme.textPrimary),
      decoration: InputDecoration(
        labelText: label,
        labelStyle: const TextStyle(color: AppTheme.textSecondary),
        prefixIcon: Icon(icon, color: AppTheme.primary),
        filled: true,
        fillColor: Colors.white.withOpacity(0.05),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.3)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.2)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppTheme.primary, width: 2),
        ),
      ),
      validator: validator,
    );
  }

  Widget _buildWalletFeature(String title, String value, IconData icon, Color color) {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 12,
      backgroundColor: color.withOpacity(0.1),
      borderColor: color.withOpacity(0.3),
      child: Row(
        children: [
          Icon(icon, color: color),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  value,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: color,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSummaryRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Text(
            '$label:',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
          const Spacer(),
          Text(
            value,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActionButtons() {
    return Row(
      children: [
        if (_currentStep > 0)
          Expanded(
            child: GlassContainer(
              height: 48,
              radius: 16,
              backgroundColor: Colors.white.withOpacity(0.05),
              borderColor: AppTheme.textHint.withOpacity(0.3),
              child: InkWell(
                onTap: () {
                  setState(() {
                    _currentStep--;
                  });
                },
                child: Center(
                  child: Text(
                    'السابق',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.textSecondary,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ),
            ),
          ),
        
        if (_currentStep > 0) const SizedBox(width: 16),
        
        Expanded(
          flex: _currentStep == 0 ? 2 : 1,
          child: GlowingButton(
            text: _currentStep == 0 ? 'التالي' : 'إنشاء الحساب',
            onPressed: _currentStep == 0 ? _nextStep : _createAccount,
            color: AppTheme.primary,
            icon: _currentStep == 0 ? Icons.arrow_forward : Icons.check_circle,
          ),
        ),
      ],
    ).animate().fadeIn(delay: const Duration(milliseconds: 1000));
  }

  void _nextStep() {
    if (_formKey.currentState!.validate() && _agreeToTerms) {
      setState(() {
        _currentStep = 1;
      });
    } else if (!_agreeToTerms) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          backgroundColor: AppTheme.error,
          content: Text('يجب الموافقة على الشروط والأحكام'),
        ),
      );
    }
  }

  void _createAccount() async {
    setState(() {
      _isLoading = true;
    });

    // Simulate account creation
    await Future.delayed(const Duration(seconds: 2));

    setState(() {
      _isLoading = false;
    });

    // Show success message
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.success,
        content: Text('تم إنشاء الحساب بنجاح!'),
      ),
    );

    // Navigate to login
    Navigator.pushReplacementNamed(context, '/login');
  }
}
