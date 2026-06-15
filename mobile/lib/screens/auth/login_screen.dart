import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../dashboard/dashboard_screen.dart';
import '../../widgets/custom_text_field.dart';
import '../../widgets/loading_button.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({Key? key}) : super(key: key);

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> with TickerProviderStateMixin {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _employeeIdController = TextEditingController();
  
  bool _isLoading = false;
  bool _obscurePassword = true;
  bool _isEmployeeLogin = false;
  String? _selectedRole;
  
  late AnimationController _backgroundController;
  late AnimationController _formController;
  late Animation<double> _backgroundAnimation;
  late Animation<double> _formAnimation;

  final List<String> _roles = [
    'Driver',
    'Supervisor',
    'Warehouse Manager',
    'Accountant',
    'Admin',
  ];

  @override
  void initState() {
    super.initState();
    
    _backgroundController = AnimationController(
      duration: const Duration(seconds: 20),
      vsync: this,
    )..repeat();
    
    _formController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );

    _backgroundAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(_backgroundController);

    _formAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _formController,
      curve: Curves.easeOutBack,
    ));

    _formController.forward();
  }

  @override
  void dispose() {
    _backgroundController.dispose();
    _formController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _employeeIdController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              Color(0xFF0A0E1A),
              Color(0xFF1A1F2E),
              Color(0xFF2D3748),
            ],
          ),
        ),
        child: Stack(
          children: [
            // Animated Background
            _buildAnimatedBackground(),
            
            // Main Content
            SafeArea(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(24.0),
                child: Column(
                  children: [
                    const SizedBox(height: 40),
                    
                    // Logo and Title
                    _buildHeader(),
                    
                    const SizedBox(height: 40),
                    
                    // Login Form
                    _buildLoginForm(),
                    
                    const SizedBox(height: 32),
                    
                    // Login Type Toggle
                    _buildLoginTypeToggle(),
                    
                    const SizedBox(height: 40),
                    
                    // Login Button
                    _buildLoginButton(),
                    
                    const SizedBox(height: 24),
                    
                    // Forgot Password
                    _buildForgotPassword(),
                    
                    const SizedBox(height: 40),
                    
                    // Register Link
                    _buildRegisterLink(),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAnimatedBackground() {
    return AnimatedBuilder(
      animation: _backgroundAnimation,
      builder: (context, child) {
        return Positioned.fill(
          child: CustomPaint(
            painter: BackgroundPainter(_backgroundAnimation.value),
          ),
        );
      },
    );
  }

  Widget _buildHeader() {
    return Column(
      children: [
        // Logo
        Container(
          width: 80,
          height: 80,
          decoration: BoxDecoration(
            gradient: const LinearGradient(
              colors: [Color(0xFFF97316), Color(0xFFEA580C)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.circular(20),
            boxShadow: [
              BoxShadow(
                color: const Color(0xFFF97316).withOpacity(0.3),
                blurRadius: 20,
                offset: const Offset(0, 10),
              ),
            ],
          ),
          child: const Icon(
            Icons.local_shipping,
            color: Colors.white,
            size: 40,
          ),
        )
            .animate()
            .scale(duration: 600.ms, curve: Curves.elasticOut)
            .rotate(duration: 800.ms, curve: Curves.easeInOut),

        const SizedBox(height: 24),

        // Title
        Text(
          'إدهام للخدمات اللوجستية',
          style: Theme.of(context).textTheme.headlineMedium?.copyWith(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: 24,
          ),
          textAlign: TextAlign.center,
        )
            .animate()
            .fadeIn(duration: 600.ms, delay: 200.ms),

        const SizedBox(height: 8),

        // Subtitle
        Text(
          'تسجيل الدخول إلى حسابك',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: Colors.white.withOpacity(0.7),
            fontSize: 16,
          ),
          textAlign: TextAlign.center,
        )
            .animate()
            .fadeIn(duration: 600.ms, delay: 400.ms),
      ],
    );
  }

  Widget _buildLoginForm() {
    return AnimatedBuilder(
      animation: _formAnimation,
      builder: (context, child) {
        return Transform.translate(
          offset: Offset(0, (1 - _formAnimation.value) * 30),
          child: Opacity(
            opacity: _formAnimation.value,
            child: Container(
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.05),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                  color: Colors.white.withOpacity(0.1),
                  width: 1,
                ),
                backdropFilter: null,
              ),
              child: Form(
                key: _formKey,
                child: Column(
                  children: [
                    // Email Field
                    CustomTextField(
                      controller: _emailController,
                      label: 'البريد الإلكتروني',
                      hint: 'أدخل بريدك الإلكتروني',
                      prefixIcon: Icons.email_outlined,
                      keyboardType: TextInputType.emailAddress,
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'الرجاء إدخال البريد الإلكتروني';
                        }
                        if (!value.contains('@')) {
                          return 'الرجاء إدخال بريد إلكتروني صحيح';
                        }
                        return null;
                      },
                    ),

                    const SizedBox(height: 20),

                    // Password Field
                    CustomTextField(
                      controller: _passwordController,
                      label: 'كلمة المرور',
                      hint: 'أدخل كلمة المرور',
                      prefixIcon: Icons.lock_outline,
                      obscureText: _obscurePassword,
                      suffixIcon: IconButton(
                        icon: Icon(
                          _obscurePassword ? Icons.visibility_off : Icons.visibility,
                          color: Colors.white.withOpacity(0.7),
                        ),
                        onPressed: () {
                          setState(() {
                            _obscurePassword = !_obscurePassword;
                          });
                        },
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
                    ),

                    const SizedBox(height: 20),

                    // Employee ID Field (only for employee login)
                    if (_isEmployeeLogin) ...[
                      CustomTextField(
                        controller: _employeeIdController,
                        label: 'رقم الموظف',
                        hint: 'أدخل رقم الموظف',
                        prefixIcon: Icons.badge_outlined,
                        validator: (value) {
                          if (_isEmployeeLogin && (value == null || value.isEmpty)) {
                            return 'الرجاء إدخال رقم الموظف';
                          }
                          return null;
                        },
                      ),

                      const SizedBox(height: 20),

                      // Role Selection
                      DropdownButtonFormField<String>(
                        value: _selectedRole,
                        decoration: InputDecoration(
                          labelText: 'الوظيفة',
                          labelStyle: TextStyle(
                            color: Colors.white.withOpacity(0.7),
                          ),
                          hintText: 'اختر وظيفتك',
                          hintStyle: TextStyle(
                            color: Colors.white.withOpacity(0.5),
                          ),
                          prefixIcon: Icon(
                            Icons.work_outline,
                            color: Colors.white.withOpacity(0.7),
                          ),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: BorderSide(
                              color: Colors.white.withOpacity(0.2),
                            ),
                          ),
                          enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: BorderSide(
                              color: Colors.white.withOpacity(0.2),
                            ),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: const BorderSide(
                              color: Color(0xFFF97316),
                              width: 2,
                            ),
                          ),
                        ),
                        dropdownColor: const Color(0xFF2D3748),
                        style: const TextStyle(
                          color: Colors.white,
                        ),
                        items: _roles.map((role) {
                          return DropdownMenuItem<String>(
                            value: role,
                            child: Text(role),
                          );
                        }).toList(),
                        validator: (value) {
                          if (_isEmployeeLogin && value == null) {
                            return 'الرجاء اختيار الوظيفة';
                          }
                          return null;
                        },
                        onChanged: (value) {
                          setState(() {
                            _selectedRole = value;
                          });
                        },
                      ),
                    ],
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildLoginTypeToggle() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          _isEmployeeLogin ? 'تسجيل دخول كـ' : 'تسجيل دخول كـ',
          style: TextStyle(
            color: Colors.white.withOpacity(0.7),
            fontSize: 16,
          ),
        ),
        TextButton(
          onPressed: () {
            setState(() {
              _isEmployeeLogin = !_isEmployeeLogin;
              _selectedRole = null;
              _employeeIdController.clear();
            });
            HapticFeedback.lightImpact();
          },
          child: Text(
            _isEmployeeLogin ? 'عميل' : 'موظف',
            style: const TextStyle(
              color: Color(0xFFF97316),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildLoginButton() {
    return LoadingButton(
      isLoading: _isLoading,
      onPressed: _handleLogin,
      text: 'تسجيل الدخول',
      backgroundColor: const Color(0xFFF97316),
      textColor: Colors.white,
      padding: const EdgeInsets.symmetric(vertical: 16),
      borderRadius: 12,
    );
  }

  Widget _buildForgotPassword() {
    return TextButton(
      onPressed: () {
        HapticFeedback.lightImpact();
        // TODO: Navigate to forgot password screen
      },
      child: Text(
        'نسيت كلمة المرور؟',
        style: TextStyle(
          color: Colors.white.withOpacity(0.7),
          fontSize: 16,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }

  Widget _buildRegisterLink() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          'ليس لديك حساب؟',
          style: TextStyle(
            color: Colors.white.withOpacity(0.7),
            fontSize: 16,
          ),
        ),
        TextButton(
          onPressed: () {
            HapticFeedback.lightImpact();
            // TODO: Navigate to registration screen
          },
          child: const Text(
            'إنشاء حساب جديد',
            style: TextStyle(
              color: Color(0xFFF97316),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    );
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      HapticFeedback.mediumImpact();
      
      // Simulate API call
      await Future.delayed(const Duration(seconds: 2));
      
      // TODO: Implement actual authentication logic
      // For demo, navigate to dashboard
      Navigator.pushReplacement(
        context,
        PageRouteBuilder(
          pageBuilder: (context, animation, secondaryAnimation) {
            return DashboardScreen();
          },
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return SlideTransition(
              position: animation.drive(
                Tween(begin: const Offset(1.0, 0.0), end: Offset.zero)
                    .chain(CurveTween(curve: Curves.easeInOut)),
              ),
              child: child,
            );
          },
          transitionDuration: const Duration(milliseconds: 500),
        ),
      );
      
    } catch (e) {
      // TODO: Show error message
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('خطأ في تسجيل الدخول: $e'),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }
}

class BackgroundPainter extends CustomPainter {
  final double animation;
  
  BackgroundPainter(this.animation);

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = const Color(0xFFF97316).withOpacity(0.03)
      ..style = PaintingStyle.fill;

    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width * 0.3 + (animation * 100);

    canvas.drawCircle(center, radius, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
