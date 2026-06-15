import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../../shared/presentation/widgets/glass_container.dart';
import '../../../shared/presentation/theme/app_theme.dart';
import '../../../shared/presentation/theme/app_shadows.dart';

class EmployeeLoginScreen extends StatefulWidget {
  const EmployeeLoginScreen({super.key});

  @override
  State<EmployeeLoginScreen> createState() => _EmployeeLoginScreenState();
}

class _EmployeeLoginScreenState extends State<EmployeeLoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;
  bool _obscurePassword = true;
  String? _detectedRole;
  bool _isAutoDetecting = true;

  // Employee roles with their permissions
  final Map<String, Map<String, dynamic>> _employeeRoles = {
    'driver': {
      'title': 'سائق',
      'icon': Icons.drive_eta,
      'color': AppTheme.primaryBlue,
      'permissions': ['tracking', 'deliveries', 'communications'],
      'dashboard': '/driver_dashboard',
    },
    'supervisor': {
      'title': 'مشرف',
      'icon': Icons.supervisor_account,
      'color': AppTheme.primaryPurple,
      'permissions': ['fleet_management', 'driver_assignment', 'analytics'],
      'dashboard': '/supervisor_dashboard',
    },
    'accountant': {
      'title': 'محاسب',
      'icon': Icons.account_balance,
      'color': AppTheme.primaryGreen,
      'permissions': ['financial_reports', 'invoices', 'payments'],
      'dashboard': '/accountant_dashboard',
    },
    'workshop': {
      'title': 'ورشة',
      'icon': Icons.build,
      'color': AppTheme.primaryOrange,
      'permissions': ['maintenance', 'vehicle_health', 'parts_inventory'],
      'dashboard': '/workshop_dashboard',
    },
    'admin': {
      'title': 'مسؤول',
      'icon': Icons.admin_panel_settings,
      'color': AppTheme.primaryRed,
      'permissions': ['user_management', 'system_settings', 'full_access'],
      'dashboard': '/admin_dashboard',
    },
  };

  @override
  void initState() {
    super.initState();
    _simulateAutoRoleDetection();
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _simulateAutoRoleDetection() {
    // Simulate automatic role detection based on email domain
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _isAutoDetecting = false;
          // Auto-detect role based on email pattern (simulation)
          final email = _emailController.text.toLowerCase();
          if (email.contains('driver') || email.contains('سائق')) {
            _detectedRole = 'driver';
          } else if (email.contains('supervisor') || email.contains('مشرف')) {
            _detectedRole = 'supervisor';
          } else if (email.contains('accountant') || email.contains('محاسب')) {
            _detectedRole = 'accountant';
          } else if (email.contains('workshop') || email.contains('ورشة')) {
            _detectedRole = 'workshop';
          } else if (email.contains('admin') || email.contains('مسؤول')) {
            _detectedRole = 'admin';
          }
        });
      }
    });
  }

  void _onEmailChanged(String email) {
    if (_isAutoDetecting) return;
    
    setState(() {
      // Auto-detect role based on email pattern
      final emailLower = email.toLowerCase();
      if (emailLower.contains('driver') || emailLower.contains('سائق')) {
        _detectedRole = 'driver';
      } else if (emailLower.contains('supervisor') || emailLower.contains('مشرف')) {
        _detectedRole = 'supervisor';
      } else if (emailLower.contains('accountant') || emailLower.contains('محاسب')) {
        _detectedRole = 'accountant';
      } else if (emailLower.contains('workshop') || emailLower.contains('ورشة')) {
        _detectedRole = 'workshop';
      } else if (emailLower.contains('admin') || emailLower.contains('مسؤول')) {
        _detectedRole = 'admin';
      } else {
        _detectedRole = null;
      }
    });
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    // Simulate API call
    await Future.delayed(const Duration(seconds: 2));

    if (mounted) {
      setState(() => _isLoading = false);
      
      // Navigate to appropriate dashboard based on detected role
      final role = _detectedRole ?? 'admin'; // Default to admin if no role detected
      final dashboardRoute = _employeeRoles[role]?['dashboard'] ?? '/admin_dashboard';
      
      context.pushReplacement(dashboardRoute);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // Animated gradient background
          Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [
                  Color(0xFF0F172A),
                  Color(0xFF1E293B),
                  Color(0xFF334155),
                ],
              ),
            ),
          ),
          
          // Floating particles animation
          ...List.generate(20, (index) {
            return Positioned(
              left: (index * 137.0) % MediaQuery.of(context).size.width,
              top: (index * 97.0) % MediaQuery.of(context).size.height,
              child: Container(
                width: 4,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
              )
                .animate(onPlay: (controller) => controller.repeat())
                .moveY(
                  begin: -100,
                  end: MediaQuery.of(context).size.height + 100,
                  duration: Duration(seconds: 10 + index % 5),
                  curve: Curves.linear,
                )
                .fadeIn(delay: Duration(milliseconds: index * 100)),
            );
          }),

          // Main content
          SafeArea(
            child: Center(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(24.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    // Logo and title
                    Column(
                      children: [
                        Container(
                          width: 80,
                          height: 80,
                          decoration: BoxDecoration(
                            gradient: LinearGradient(
                              colors: [
                                AppTheme.primaryBlue,
                                AppTheme.primaryPurple,
                              ],
                            ),
                            borderRadius: BorderRadius.circular(20),
                            boxShadow: [AppShadows.glowShadow],
                          ),
                          child: const Icon(
                            Icons.local_shipping,
                            size: 40,
                            color: Colors.white,
                          ),
                        )
                            .animate()
                            .scale(duration: 600.ms, curve: Curves.elasticOut)
                            .shimmer(duration: 1500.ms),
                        
                        const SizedBox(height: 24),
                        
                        Text(
                          'تسجيل دخول الموظفين',
                          style: GoogleFonts.cairo(
                            fontSize: 28,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        )
                            .animate()
                            .fadeIn(delay: 200.ms, duration: 600.ms)
                            .slideY(begin: -20, end: 0),
                        
                        const SizedBox(height: 8),
                        
                        Text(
                          'اكتشف دورك تلقائياً وسجل الدخول',
                          style: GoogleFonts.cairo(
                            fontSize: 16,
                            color: Colors.white.withOpacity(0.8),
                          ),
                        )
                            .animate()
                            .fadeIn(delay: 300.ms, duration: 600.ms)
                            .slideY(begin: -20, end: 0),
                      ],
                    ),

                    const SizedBox(height: 48),

                    // Role detection indicator
                    if (_isAutoDetecting)
                      GlassContainer(
                        child: Row(
                          children: [
                            SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                valueColor: AlwaysStoppedAnimation<Color>(
                                  AppTheme.primaryBlue,
                                ),
                              ),
                            ),
                            const SizedBox(width: 12),
                            Text(
                              'جاري اكتشاف دورك...',
                              style: GoogleFonts.cairo(
                                color: Colors.white,
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ),
                      )
                          .animate()
                          .fadeIn(delay: 400.ms, duration: 600.ms),

                    if (_detectedRole != null && !_isAutoDetecting) ...[
                      const SizedBox(height: 16),
                      GlassContainer(
                        child: Row(
                          children: [
                            Icon(
                              _employeeRoles[_detectedRole]?['icon'],
                              color: _employeeRoles[_detectedRole]?['color'],
                              size: 24,
                            ),
                            const SizedBox(width: 12),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'دورك المكتشف:',
                                  style: GoogleFonts.cairo(
                                    color: Colors.white.withOpacity(0.7),
                                    fontSize: 12,
                                  ),
                                ),
                                Text(
                                  _employeeRoles[_detectedRole]?['title'] ?? '',
                                  style: GoogleFonts.cairo(
                                    color: _employeeRoles[_detectedRole]?['color'],
                                    fontSize: 16,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      )
                          .animate()
                          .fadeIn(delay: 400.ms, duration: 600.ms)
                          .slideX(begin: -20, end: 0),
                    ],

                    const SizedBox(height: 32),

                    // Login form
                    GlassContainer(
                      child: Form(
                        key: _formKey,
                        child: Column(
                          children: [
                            // Email field
                            TextFormField(
                              controller: _emailController,
                              onChanged: _onEmailChanged,
                              keyboardType: TextInputType.emailAddress,
                              style: GoogleFonts.cairo(color: Colors.white),
                              decoration: InputDecoration(
                                labelText: 'البريد الإلكتروني',
                                labelStyle: GoogleFonts.cairo(
                                  color: Colors.white.withOpacity(0.7),
                                ),
                                prefixIcon: Icon(
                                  Icons.email,
                                  color: Colors.white.withOpacity(0.7),
                                ),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(
                                    color: Colors.white.withOpacity(0.3),
                                  ),
                                ),
                                enabledBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(
                                    color: Colors.white.withOpacity(0.3),
                                  ),
                                ),
                                focusedBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(
                                    color: AppTheme.primaryBlue,
                                  ),
                                ),
                              ),
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'الرجاء إدخال البريد الإلكتروني';
                                }
                                if (!value.contains('@')) {
                                  return 'الرجاء إدخال بريد إلكتروني صحيح';
                                }
                                return null;
                              },
                            )
                                .animate()
                                .fadeIn(delay: 500.ms, duration: 600.ms)
                                .slideY(begin: 20, end: 0),

                            const SizedBox(height: 20),

                            // Password field
                            TextFormField(
                              controller: _passwordController,
                              obscureText: _obscurePassword,
                              style: GoogleFonts.cairo(color: Colors.white),
                              decoration: InputDecoration(
                                labelText: 'كلمة المرور',
                                labelStyle: GoogleFonts.cairo(
                                  color: Colors.white.withOpacity(0.7),
                                ),
                                prefixIcon: Icon(
                                  Icons.lock,
                                  color: Colors.white.withOpacity(0.7),
                                ),
                                suffixIcon: IconButton(
                                  icon: Icon(
                                    _obscurePassword
                                        ? Icons.visibility
                                        : Icons.visibility_off,
                                    color: Colors.white.withOpacity(0.7),
                                  ),
                                  onPressed: () {
                                    setState(() => _obscurePassword = !_obscurePassword);
                                  },
                                ),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(
                                    color: Colors.white.withOpacity(0.3),
                                  ),
                                ),
                                enabledBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(
                                    color: Colors.white.withOpacity(0.3),
                                  ),
                                ),
                                focusedBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(
                                    color: AppTheme.primaryBlue,
                                  ),
                                ),
                              ),
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'الرجاء إدخال كلمة المرور';
                                }
                                if (value.length < 6) {
                                  return 'كلمة المرور يجب أن تكون 6 أحرف على الأقل';
                                }
                                return null;
                              },
                            )
                                .animate()
                                .fadeIn(delay: 600.ms, duration: 600.ms)
                                .slideY(begin: 20, end: 0),

                            const SizedBox(height: 32),

                            // Login button
                            SizedBox(
                              width: double.infinity,
                              height: 50,
                              child: ElevatedButton(
                                onPressed: _isLoading ? null : _handleLogin,
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: AppTheme.primaryBlue,
                                  foregroundColor: Colors.white,
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(12),
                                  ),
                                  elevation: 0,
                                ),
                                child: _isLoading
                                    ? const Center(
                                        child: SizedBox(
                                          width: 20,
                                          height: 20,
                                          child: CircularProgressIndicator(
                                            strokeWidth: 2,
                                            valueColor: AlwaysStoppedAnimation<Color>(
                                              Colors.white,
                                            ),
                                          ),
                                        ),
                                      )
                                    : Text(
                                        'تسجيل الدخول',
                                        style: GoogleFonts.cairo(
                                          fontSize: 16,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                              )
                            )
                                .animate()
                                .fadeIn(delay: 700.ms, duration: 600.ms)
                                .slideY(begin: 20, end: 0),
                          ],
                        ),
                      ),
                    )
                        .animate()
                        .fadeIn(delay: 400.ms, duration: 600.ms)
                        .scale(begin: const Offset(0.9, 0.9), end: const Offset(1, 1)),

                    const SizedBox(height: 24),

                    // Back to login option
                    TextButton(
                      onPressed: () {
                        context.pushReplacement('/login');
                      },
                      child: Text(
                        'العودة إلى تسجيل الدخول العام',
                        style: GoogleFonts.cairo(
                          color: Colors.white.withOpacity(0.8),
                          decoration: TextDecoration.underline,
                        ),
                      ),
                    )
                        .animate()
                        .fadeIn(delay: 800.ms, duration: 600.ms),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
