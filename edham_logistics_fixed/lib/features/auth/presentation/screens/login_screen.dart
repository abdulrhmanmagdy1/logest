// ============================================
// 🔐 Edham Logistics - Premium Glassmorphism Login Screen
// Enterprise Authentication with Cinematic UI
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/models/user_model.dart';
import '../../../../core/navigation/app_router.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';
import '../bloc/auth_bloc.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen>
    with TickerProviderStateMixin {
  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;
  
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;
  bool _rememberMe = false;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _startAnimations();
  }

  void _initializeAnimations() {
    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _slideController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _fadeController,
      curve: Curves.easeIn,
    ));

    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.3),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _slideController,
      curve: Curves.easeOutCubic,
    ));
  }

  void _startAnimations() {
    _fadeController.forward();
    Future.delayed(const Duration(milliseconds: 200), () {
      if (mounted) {
        _slideController.forward();
      }
    });
  }

  @override
  void dispose() {
    _fadeController.dispose();
    _slideController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _login() {
    if (_formKey.currentState?.validate() ?? false) {
      setState(() {
        _isLoading = true;
      });

      // Simulate API call
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) {
          setState(() {
            _isLoading = false;
          });
          context.read<AuthBloc>().add(
            LoginRequested(
              email: _emailController.text,
              password: _passwordController.text,
            ),
          );
        }
      });
    }
  }

  void _socialLogin(String provider) {
    setState(() {
      _isLoading = true;
    });

    // Simulate social login
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
        _demoLogin(UserRole.client);
      }
    });
  }

  void _demoLogin(UserRole role) {
    context.read<AuthBloc>().add(DemoLoginRequested(role: role));
  }

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<AuthBloc, AuthState>(
      listener: (context, state) {
        if (state is Authenticated) {
          switch (state.user.role) {
            case UserRole.client:
              AppRouter.goClientDashboard(context);
              break;
            case UserRole.driver:
              AppRouter.goDriverDashboard(context);
              break;
            default:
              AppRouter.goClientDashboard(context);
          }
        } else if (state is AuthError) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(state.message),
              backgroundColor: AppTheme.error,
            ),
          );
        }
      },
      builder: (context, state) {
        return Scaffold(
          backgroundColor: AppTheme.background,
          body: SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(20),
              child: FadeTransition(
                opacity: _fadeAnimation,
                child: SlideTransition(
                  position: _slideAnimation,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: 40),

                      // Logo and Title
                      Center(
                        child: Column(
                          children: [
                            Container(
                              width: 80,
                              height: 80,
                              decoration: BoxDecoration(
                                gradient: LinearGradient(
                                  colors: [
                                    AppTheme.primary,
                                    AppTheme.primaryDark,
                                  ],
                                  begin: Alignment.topLeft,
                                  end: Alignment.bottomRight,
                                ),
                                borderRadius: BorderRadius.circular(20),
                                boxShadow: [
                                  BoxShadow(
                                    color: AppTheme.primary.withOpacity(0.3),
                                    blurRadius: 20,
                                    offset: const Offset(0, 10),
                                  ),
                                ],
                              ),
                              child: const Center(
                                child: Icon(
                                  Icons.local_shipping,
                                  color: Colors.white,
                                  size: 40,
                                ),
                              ),
                            ),

                            const SizedBox(height: 24),

                            Text(
                              'مرحباً بعودتك',
                              style: GoogleFonts.inter(
                                fontSize: 28,
                                fontWeight: FontWeight.bold,
                                color: AppTheme.textPrimary,
                              ),
                            ),

                            const SizedBox(height: 8),

                            Text(
                              'سجل دخولك لحسابك في نظام إدهام اللوجستي',
                              style: GoogleFonts.inter(
                                fontSize: 16,
                                color: AppTheme.textSecondary,
                              ),
                            ),
                          ],
                        ),
                      ),

                      const SizedBox(height: 60),

                      // Premium Glassmorphism Login Form
                      GlassContainer(
                        padding: const EdgeInsets.all(32),
                        radius: 24,
                        boxShadow: AppShadows.glassmorphism,
                        child: Form(
                          key: _formKey,
                          child: Column(
                            children: [
                              // Premium Email Field
                              TextFormField(
                                controller: _emailController,
                                keyboardType: TextInputType.emailAddress,
                                style: GoogleFonts.cairo(
                                  color: AppTheme.textPrimary,
                                  fontSize: 16,
                                  fontWeight: FontWeight.w500,
                                ),
                                decoration: InputDecoration(
                                  labelText: 'البريد الإلكتروني',
                                  labelStyle: GoogleFonts.cairo(
                                    color: AppTheme.textSecondary,
                                    fontSize: 14,
                                  ),
                                  prefixIcon: const Icon(
                                    Icons.email_outlined,
                                    color: AppTheme.primary,
                                  ),
                                  filled: true,
                                  fillColor: Colors.white.withOpacity(0.05),
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(16),
                                    borderSide: BorderSide(
                                      color: AppTheme.primary.withOpacity(0.3),
                                      width: 1,
                                    ),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(16),
                                    borderSide: BorderSide(
                                      color: AppTheme.primary.withOpacity(0.2),
                                      width: 1,
                                    ),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(16),
                                    borderSide: BorderSide(
                                      color: AppTheme.primary,
                                      width: 2,
                                    ),
                                  ),
                                ),
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return 'البريد الإلكتروني مطلوب';
                                  }
                                  if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$')
                                      .hasMatch(value)) {
                                    return 'البريد الإلكتروني غير صالح';
                                  }
                                  return null;
                                },
                              ).animate()
                                  .fadeIn(delay: const Duration(milliseconds: 600))
                                  .slideX(begin: -0.2, end: 0),

                              const SizedBox(height: 20),

                              // Password Field
                              TextFormField(
                                controller: _passwordController,
                                obscureText: !_obscurePassword,
                                style: GoogleFonts.inter(
                                  color: AppTheme.textPrimary,
                                  fontSize: 16,
                                ),
                                decoration: InputDecoration(
                                  labelText: 'كلمة المرور',
                                  prefixIcon: const Icon(
                                    Icons.lock_outlined,
                                    color: AppTheme.textHint,
                                  ),
                                  suffixIcon: IconButton(
                                    onPressed: () {
                                      setState(() {
                                        _obscurePassword = !_obscurePassword;
                                      });
                                    },
                                    icon: Icon(
                                      _obscurePassword
                                          ? Icons.visibility_outlined
                                          : Icons.visibility_off_outlined,
                                      color: AppTheme.textHint,
                                    ),
                                  ),
                                  filled: true,
                                  fillColor: AppTheme.surfaceLight,
                                  border: const OutlineInputBorder(
                                    borderRadius: BorderRadius.all(Radius.circular(12)),
                                    borderSide: BorderSide.none,
                                  ),
                                  enabledBorder: const OutlineInputBorder(
                                    borderRadius: BorderRadius.all(Radius.circular(12)),
                                    borderSide: BorderSide.none,
                                  ),
                                  focusedBorder: const OutlineInputBorder(
                                    borderRadius: BorderRadius.all(Radius.circular(12)),
                                    borderSide: BorderSide(
                                      color: AppTheme.primary,
                                      width: 2,
                                    ),
                                  ),
                                ),
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return 'كلمة المرور مطلوبة';
                                  }
                                  if (value.length < 6) {
                                    return 'كلمة المرور يجب أن تكون 6 أحرف على الأقل';
                                  }
                                  return null;
                                },
                              ),

                              const SizedBox(height: 24),

                              // Remember Me & Forgot Password
                              Row(
                                children: [
                                  // Remember Me
                                  Expanded(
                                    child: Row(
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
                                          style: GoogleFonts.inter(
                                            color: AppTheme.textSecondary,
                                            fontSize: 14,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),

                                  // Forgot Password
                                  TextButton(
                                    onPressed: () {
                                      AppRouter.goResetPassword(context);
                                    },
                                    child: Text(
                                      'نسيت كلمة المرور؟',
                                      style: GoogleFonts.inter(
                                        color: AppTheme.primary,
                                        fontSize: 14,
                                        fontWeight: FontWeight.w600,
                                      ),
                                    ),
                                  ),
                                ],
                              ),

                              const SizedBox(height: 32),

                              // Login Button
                              SizedBox(
                                width: double.infinity,
                                height: 56,
                                child: ElevatedButton(
                                  onPressed: _isLoading ? null : _login,
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: AppTheme.primary,
                                    foregroundColor: Colors.white,
                                    elevation: 0,
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                  ),
                                  child: _isLoading
                                      ? const SizedBox(
                                          width: 20,
                                          height: 20,
                                          child: CircularProgressIndicator(
                                            strokeWidth: 2,
                                            valueColor: AlwaysStoppedAnimation<Color>(
                                              Colors.white,
                                            ),
                                          ),
                                        )
                                      : Text(
                                          'تسجيل الدخول',
                                          style: GoogleFonts.inter(
                                            fontSize: 16,
                                            fontWeight: FontWeight.w600,
                                          ),
                                        ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),

                      const SizedBox(height: 40),

                      // Social Login
                      Column(
                        children: [
                          // Divider
                          Row(
                            children: [
                              Expanded(
                                child: Container(
                                  height: 1,
                                  color: AppTheme.textHint.withOpacity(0.3),
                                ),
                              ),
                              Padding(
                                padding: const EdgeInsets.symmetric(horizontal: 16),
                                child: Text(
                                  'أو سجل الدخول باستخدام',
                                  style: GoogleFonts.inter(
                                    color: AppTheme.textHint,
                                    fontSize: 14,
                                  ),
                                ),
                              ),
                              Expanded(
                                child: Container(
                                  height: 1,
                                  color: AppTheme.textHint.withOpacity(0.3),
                                ),
                              ),
                            ],
                          ),

                          const SizedBox(height: 24),

                          // Social Buttons
                          Row(
                            children: [
                              Expanded(
                                child: _buildSocialButton(
                                  'Google',
                                  'G',
                                  AppTheme.surfaceLight,
                                  () => _socialLogin('google'),
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: _buildSocialButton(
                                  'Apple',
                                  '🍎',
                                  Colors.black,
                                  () => _socialLogin('apple'),
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),

                      const SizedBox(height: 40),

                      // Sign Up Link
                      Center(
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              'ليس لديك حساب؟',
                              style: GoogleFonts.inter(
                                color: AppTheme.textSecondary,
                                fontSize: 14,
                              ),
                            ),
                            TextButton(
                              onPressed: () {
                                // TODO: Navigate to sign up
                              },
                              child: Text(
                                'إنشاء حساب جديد',
                                style: GoogleFonts.inter(
                                  color: AppTheme.primary,
                                  fontSize: 14,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildSocialButton(
    String label,
    String icon,
    Color backgroundColor,
    VoidCallback onTap,
  ) {
    return GestureDetector(
      onTap: _isLoading ? null : onTap,
      child: Container(
        height: 56,
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: AppTheme.textHint.withOpacity(0.2),
          ),
        ),
        child: Center(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Icon
              Container(
                width: 24,
                height: 24,
                decoration: BoxDecoration(
                  color: label == 'Google' ? const Color(0xFF4285F4) : null,
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Center(
                  child: Text(
                    icon,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ),
              ),

              const SizedBox(width: 12),

              // Label
              Text(
                label,
                style: GoogleFonts.inter(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                  color: AppTheme.textPrimary,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
