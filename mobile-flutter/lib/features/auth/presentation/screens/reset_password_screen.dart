import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';

class ResetPasswordScreen extends StatefulWidget {
  const ResetPasswordScreen({super.key});

  @override
  State<ResetPasswordScreen> createState() => _ResetPasswordScreenState();
}

class _ResetPasswordScreenState extends State<ResetPasswordScreen>
    with TickerProviderStateMixin {
  late AnimationController _formController;
  late AnimationController _logoController;
  
  final _emailController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;
  bool _emailSent = false;

  @override
  void initState() {
    super.initState();
    
    _formController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _logoController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );

    _startAnimations();
  }

  void _startAnimations() async {
    await Future.delayed(const Duration(milliseconds: 200));
    _logoController.forward();
    
    await Future.delayed(const Duration(milliseconds: 400));
    _formController.forward();
  }

  @override
  void dispose() {
    _formController.dispose();
    _logoController.dispose();
    _emailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              const SizedBox(height: 40),
              
              // Logo Section
              _buildLogoSection(),
              
              const SizedBox(height: 40),
              
              // Form Section
              _buildFormSection(),
              
              const SizedBox(height: 30),
              
              // Back to Login
              _buildBackToLogin(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLogoSection() {
    return AnimatedBuilder(
      animation: _logoController,
      builder: (context, child) {
        return Column(
          children: [
            // Logo Container
            Container(
              width: 100,
              height: 100,
              decoration: BoxDecoration(
                color: AppTheme.primary.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                  color: AppTheme.primary.withOpacity(0.2),
                  width: 1,
                ),
              ),
              child: Center(
                child: Icon(
                  Icons.local_shipping,
                  size: 50,
                  color: AppTheme.primary,
                ),
              ),
            ).animate(controller: _logoController)
             .scale(duration: 600.ms, curve: Curves.elasticOut)
             .fadeIn(duration: 400.ms),
            
            const SizedBox(height: 20),
            
            // Title
            Text(
              'إعادة تعيين كلمة المرور',
              style: GoogleFonts.inter(
                fontSize: 28,
                fontWeight: FontWeight.bold,
                color: AppTheme.textPrimary,
              ),
              textAlign: TextAlign.center,
            ).animate(controller: _logoController)
             .fadeIn(duration: 600.ms, delay: 200.ms)
             .slideY(begin: 20, end: 0, duration: 600.ms, delay: 200.ms),
            
            const SizedBox(height: 12),
            
            // Subtitle
            Text(
              _emailSent 
                ? 'تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني'
                : 'أدخل بريدك الإلكتروني لإعادة تعيين كلمة المرور',
              style: GoogleFonts.inter(
                fontSize: 16,
                color: AppTheme.textSecondary,
              ),
              textAlign: TextAlign.center,
            ).animate(controller: _logoController)
             .fadeIn(duration: 600.ms, delay: 400.ms)
             .slideY(begin: 15, end: 0, duration: 600.ms, delay: 400.ms),
          ],
        );
      },
    );
  }

  Widget _buildFormSection() {
    if (_emailSent) {
      return _buildSuccessMessage();
    }

    return Form(
      key: _formKey,
      child: Column(
        children: [
          // Email Field
          Container(
            decoration: BoxDecoration(
              color: AppTheme.surface,
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: AppTheme.surfaceLight,
                width: 1,
              ),
            ),
            child: TextFormField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              style: GoogleFonts.inter(
                color: AppTheme.textPrimary,
                fontSize: 16,
              ),
              decoration: InputDecoration(
                labelText: 'البريد الإلكتروني',
                labelStyle: GoogleFonts.inter(
                  color: AppTheme.textSecondary,
                  fontSize: 14,
                ),
                prefixIcon: Icon(
                  Icons.email_outlined,
                  color: AppTheme.textHint,
                ),
                border: InputBorder.none,
                enabledBorder: InputBorder.none,
                focusedBorder: InputBorder.none,
                contentPadding: const EdgeInsets.all(16),
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
            ),
          ).animate(controller: _formController)
           .fadeIn(duration: 600.ms, delay: 200.ms)
           .slideY(begin: 20, end: 0, duration: 600.ms, delay: 200.ms),
          
          const SizedBox(height: 24),
          
          // Submit Button
          SizedBox(
            width: double.infinity,
            height: 56,
            child: ElevatedButton(
              onPressed: _isLoading ? null : _handleSubmit,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.primary,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                elevation: 0,
              ),
              child: _isLoading
                ? Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Text(
                        'جاري الإرسال...',
                        style: GoogleFonts.inter(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  )
                : Text(
                    'إرسال رابط إعادة التعيين',
                    style: GoogleFonts.inter(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
            ),
          ).animate(controller: _formController)
           .fadeIn(duration: 600.ms, delay: 400.ms)
           .slideY(begin: 20, end: 0, duration: 600.ms, delay: 400.ms),
        ],
      ),
    );
  }

  Widget _buildSuccessMessage() {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: Colors.green.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: Colors.green.withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Column(
        children: [
          Icon(
            Icons.check_circle,
            color: Colors.green,
            size: 60,
          ).animate(controller: _formController)
           .scale(duration: 400.ms, curve: Curves.elasticOut)
           .fadeIn(duration: 300.ms),
          
          const SizedBox(height: 16),
          
          Text(
            'تم الإرسال بنجاح!',
            style: GoogleFonts.inter(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.green,
            ),
            textAlign: TextAlign.center,
          ).animate(controller: _formController)
           .fadeIn(duration: 400.ms, delay: 200.ms),
          
          const SizedBox(height: 8),
          
          Text(
            'تحقق من بريدك الإلكتروني للحصول على رابط إعادة تعيين كلمة المرور',
            style: GoogleFonts.inter(
              fontSize: 14,
              color: AppTheme.textSecondary,
            ),
            textAlign: TextAlign.center,
          ).animate(controller: _formController)
           .fadeIn(duration: 400.ms, delay: 400.ms),
        ],
      ),
    );
  }

  Widget _buildBackToLogin() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          'تذكرت كلمة المرور؟',
          style: GoogleFonts.inter(
            color: AppTheme.textSecondary,
            fontSize: 14,
          ),
        ),
        TextButton(
          onPressed: () {
            AppRouter.goBack(context);
          },
          child: Text(
            'تسجيل الدخول',
            style: GoogleFonts.inter(
              color: AppTheme.primary,
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    ).animate(controller: _formController)
     .fadeIn(duration: 400.ms, delay: 600.ms);
  }

  void _handleSubmit() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      // Simulate API call
      await Future.delayed(const Duration(seconds: 2));

      setState(() {
        _isLoading = false;
        _emailSent = true;
      });
    }
  }
}
