// ============================================
// 👨‍💼 Supervisor Login Screen - Premium Admin Access
// Enterprise Supervisor Authentication with Security
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';

class SupervisorLoginScreen extends StatefulWidget {
  const SupervisorLoginScreen({super.key});

  @override
  State<SupervisorLoginScreen> createState() => _SupervisorLoginScreenState();
}

class _SupervisorLoginScreenState extends State<SupervisorLoginScreen>
    with TickerProviderStateMixin {
  late AnimationController _logoController;
  late AnimationController _formController;
  
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _adminCodeController = TextEditingController();
  
  bool _obscurePassword = true;
  bool _obscureAdminCode = true;
  bool _rememberMe = false;
  bool _isLoading = false;
  bool _useBiometric = false;

  @override
  void initState() {
    super.initState();
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    _formController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _logoController.forward();
    _formController.forward();
  }

  @override
  void dispose() {
    _logoController.dispose();
    _formController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _adminCodeController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: Stack(
        children: [
          // Premium animated background
          Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  AppTheme.background,
                  AppTheme.surface.withOpacity(0.2),
                  AppTheme.primary.withOpacity(0.05),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          
          // Security pattern overlay
          Positioned.fill(
            child: CustomPaint(
              painter: SecurityPatternPainter(),
            ),
          ),
          
          // Main content
          SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  const SizedBox(height: 40),
                  
                  // Premium Logo with Security Badge
                  _buildLogoWithBadge(),
                  
                  const SizedBox(height: 40),
                  
                  // Login Form
                  _buildLoginForm(),
                  
                  const SizedBox(height: 30),
                  
                  // Security Options
                  _buildSecurityOptions(),
                  
                  const SizedBox(height: 40),
                  
                  // Login Button
                  _buildLoginButton(),
                  
                  const SizedBox(height: 20),
                  
                  // Help & Support
                  _buildHelpSection(),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLogoWithBadge() {
    return Column(
      children: [
        // Main Logo
        Hero(
          tag: 'supervisor-logo',
          child: GlassContainer(
            width: 120,
            height: 120,
            radius: 30,
            backgroundColor: AppTheme.primary.withOpacity(0.1),
            borderColor: AppTheme.primary.withOpacity(0.3),
            boxShadow: AppShadows.glowing(AppTheme.primary),
            child: const Icon(
              Icons.admin_panel_settings,
              size: 60,
              color: AppTheme.primary,
            ),
          ).animate(controller: _logoController)
            .fadeIn(duration: const Duration(milliseconds: 800))
            .scale(begin: const Offset(0.5, 0.5), end: const Offset(1, 1))
            .then()
            .shimmer(duration: const Duration(milliseconds: 2000)),
        ),
        
        const SizedBox(height: 20),
        
        // Security Badge
        GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          radius: 20,
          backgroundColor: AppTheme.success.withOpacity(0.1),
          borderColor: AppTheme.success.withOpacity(0.3),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.verified_user,
                color: AppTheme.success,
                size: 20,
              ),
              const SizedBox(width: 8),
              Text(
                'دخول المشرفين',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.success,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 1000)),
        
        const SizedBox(height: 20),
        
        Text(
          'نظام إدارة إدهام',
          style: Theme.of(context).textTheme.displayMedium?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.bold,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 1200)),
        
        const SizedBox(height: 8),
        
        Text(
          'وصول آمن للمشرفين المعتمدين',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 1400)),
      ],
    );
  }

  Widget _buildLoginForm() {
    return GlassContainer(
      padding: const EdgeInsets.all(32),
      radius: 24,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      boxShadow: AppShadows.glassmorphism,
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            // Email Field
            TextFormField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              style: const TextStyle(color: AppTheme.textPrimary),
              decoration: InputDecoration(
                labelText: 'البريد الإلكتروني للمشرف',
                labelStyle: const TextStyle(color: AppTheme.textSecondary),
                prefixIcon: const Icon(Icons.email, color: AppTheme.primary),
                filled: true,
                fillColor: Colors.white.withOpacity(0.05),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.3)),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.2)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: const BorderSide(color: AppTheme.primary, width: 2),
                ),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'البريد الإلكتروني مطلوب';
                }
                if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
                  return 'البريد الإلكتروني غير صالح';
                }
                return null;
              },
            ).animate().fadeIn(delay: const Duration(milliseconds: 600)),
            
            const SizedBox(height: 20),
            
            // Password Field
            TextFormField(
              controller: _passwordController,
              obscureText: _obscurePassword,
              style: const TextStyle(color: AppTheme.textPrimary),
              decoration: InputDecoration(
                labelText: 'كلمة المرور',
                labelStyle: const TextStyle(color: AppTheme.textSecondary),
                prefixIcon: const Icon(Icons.lock, color: AppTheme.primary),
                suffixIcon: IconButton(
                  onPressed: () {
                    setState(() {
                      _obscurePassword = !_obscurePassword;
                    });
                  },
                  icon: Icon(
                    _obscurePassword ? Icons.visibility : Icons.visibility_off,
                    color: AppTheme.textHint,
                  ),
                ),
                filled: true,
                fillColor: Colors.white.withOpacity(0.05),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.3)),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.2)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: const BorderSide(color: AppTheme.primary, width: 2),
                ),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'كلمة المرور مطلوبة';
                }
                if (value.length < 8) {
                  return 'كلمة المرور يجب أن تكون 8 أحرف على الأقل';
                }
                return null;
              },
            ).animate().fadeIn(delay: const Duration(milliseconds: 800)),
            
            const SizedBox(height: 20),
            
            // Admin Code Field
            TextFormField(
              controller: _adminCodeController,
              obscureText: _obscureAdminCode,
              style: const TextStyle(color: AppTheme.textPrimary),
              decoration: InputDecoration(
                labelText: 'رمز المشرف',
                labelStyle: const TextStyle(color: AppTheme.textSecondary),
                prefixIcon: const Icon(Icons.vpn_key, color: AppTheme.primary),
                suffixIcon: IconButton(
                  onPressed: () {
                    setState(() {
                      _obscureAdminCode = !_obscureAdminCode;
                    });
                  },
                  icon: Icon(
                    _obscureAdminCode ? Icons.visibility : Icons.visibility_off,
                    color: AppTheme.textHint,
                  ),
                ),
                filled: true,
                fillColor: Colors.white.withOpacity(0.05),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.3)),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: BorderSide(color: AppTheme.primary.withOpacity(0.2)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                  borderSide: const BorderSide(color: AppTheme.primary, width: 2),
                ),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'رمز المشرف مطلوب';
                }
                if (value.length < 6) {
                  return 'رمز المشرف يجب أن يكون 6 أحرف على الأقل';
                }
                return null;
              },
            ).animate().fadeIn(delay: const Duration(milliseconds: 1000)),
            
            const SizedBox(height: 24),
            
            // Remember Me
            Row(
              children: [
                Checkbox(
                  value: _rememberMe,
                  onChanged: (value) {
                    setState(() {
                      _rememberMe = value ?? false;
                    });
                  },
                  activeColor: AppTheme.primary,
                ),
                Text(
                  'تذكرني',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
              ],
            ).animate().fadeIn(delay: const Duration(milliseconds: 1200)),
          ],
        ),
      ),
    ).animate(controller: _formController)
      .fadeIn(delay: const Duration(milliseconds: 400))
      .slideY(begin: 0.3, end: 0);
  }

  Widget _buildSecurityOptions() {
    return Column(
      children: [
        // Biometric Login
        GlassContainer(
          padding: const EdgeInsets.all(16),
          radius: 16,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.textHint.withOpacity(0.2),
          child: InkWell(
            onTap: () {
              setState(() {
                _useBiometric = !_useBiometric;
              });
            },
            child: Row(
              children: [
                Icon(
                  Icons.fingerprint,
                  color: _useBiometric ? AppTheme.primary : AppTheme.textHint,
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Text(
                    'استخدام المصادقة البيومترية',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: _useBiometric ? AppTheme.primary : AppTheme.textSecondary,
                    ),
                  ),
                ),
                Switch(
                  value: _useBiometric,
                  onChanged: (value) {
                    setState(() {
                      _useBiometric = value;
                    });
                  },
                  activeColor: AppTheme.primary,
                ),
              ],
            ),
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 1400)),
        
        const SizedBox(height: 16),
        
        // Security Notice
        GlassContainer(
          padding: const EdgeInsets.all(16),
          radius: 16,
          backgroundColor: AppTheme.success.withOpacity(0.1),
          borderColor: AppTheme.success.withOpacity(0.3),
          child: Row(
            children: [
              Icon(
                Icons.security,
                color: AppTheme.success,
                size: 24,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'اتصال آمن',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.success,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    Text(
                      'جميع البيانات مشفرة end-to-end',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 1600)),
      ],
    );
  }

  Widget _buildLoginButton() {
    return GlowingButton(
      text: _isLoading ? 'جاري تسجيل الدخول...' : 'دخول المشرف',
      onPressed: _isLoading ? null : _login,
      color: AppTheme.primary,
      icon: Icons.login,
      height: 56,
    ).animate().fadeIn(delay: const Duration(milliseconds: 1800));
  }

  Widget _buildHelpSection() {
    return Column(
      children: [
        TextButton(
          onPressed: () {
            _showHelpDialog();
          },
          child: Text(
            'هل نسيت كلمة المرور؟',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.primary,
              fontWeight: FontWeight.w600,
            ),
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 2000)),
        
        const SizedBox(height: 12),
        
        TextButton(
          onPressed: () {
            _contactSupport();
          },
          child: Text(
            'تواصل مع فريق الدعم',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ).animate().fadeIn(delay: const Duration(milliseconds: 2200)),
      ],
    );
  }

  void _login() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      // Simulate authentication
      await Future.delayed(const Duration(seconds: 2));

      setState(() {
        _isLoading = false;
      });

      // Show success message
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          backgroundColor: AppTheme.success,
          content: Text('تم تسجيل الدخول بنجاح!'),
        ),
      );

      // Navigate to supervisor dashboard
      Navigator.pushReplacementNamed(context, '/supervisor/dashboard');
    }
  }

  void _showHelpDialog() {
    showDialog(
      context: context,
      builder: (context) => Dialog(
        backgroundColor: Colors.transparent,
        child: GlassContainer(
          padding: const EdgeInsets.all(24),
          radius: 20,
          backgroundColor: AppTheme.background.withOpacity(0.9),
          borderColor: AppTheme.primary.withOpacity(0.3),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.help_outline,
                color: AppTheme.primary,
                size: 64,
              ),
              const SizedBox(height: 16),
              Text(
                'مساعدة في تسجيل الدخول',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              Text(
                'لإعادة تعيين كلمة المرور، يرجى التواصل مع مدير النظام أو فريق الدعم الفني.',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              GlowingButton(
                text: 'تواصل مع الدعم',
                onPressed: () {
                  Navigator.pop(context);
                  _contactSupport();
                },
                color: AppTheme.primary,
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _contactSupport() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        backgroundColor: AppTheme.primary,
        content: Text('سيتم التواصل معك خلال 24 ساعة'),
      ),
    );
  }
}

// Custom painter for security pattern background
class SecurityPatternPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = AppTheme.primary.withOpacity(0.03)
      ..style = PaintingStyle.fill;

    // Draw security pattern
    for (int i = 0; i < 20; i++) {
      for (int j = 0; j < 20; j++) {
        if ((i + j) % 2 == 0) {
          final x = i * size.width / 20;
          final y = j * size.height / 20;
          canvas.drawCircle(Offset(x, y), 2, paint);
        }
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
