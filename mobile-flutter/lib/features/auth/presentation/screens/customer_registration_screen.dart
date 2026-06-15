// ============================================
// 🎨 Premium Customer Registration Flow - World-Class UX
// ============================================

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:intl_phone_field/intl_phone_field.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/design_system/shadows.dart';
import '../../../../core/utils/validators.dart';

class CustomerRegistrationScreen extends StatefulWidget {
  const CustomerRegistrationScreen({super.key});

  @override
  State<CustomerRegistrationScreen> createState() => _CustomerRegistrationScreenState();
}

class _CustomerRegistrationScreenState extends State<CustomerRegistrationScreen> {
  final PageController _pageController = PageController();
  final _formKey = GlobalKey<FormState>();
  int _currentStep = 0;
  bool _isLoading = false;

  // Form controllers
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();
  final _companyController = TextEditingController();
  final _addressController = TextEditingController();
  final _cityController = TextEditingController();
  final _postalCodeController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();

  // Form state
  String _selectedCountry = 'United States';
  String _selectedBusinessType = 'Individual';
  bool _agreeToTerms = false;
  bool _agreeToPrivacy = false;
  bool _marketingConsent = false;

  final List<RegistrationStep> _steps = [
    RegistrationStep(
      title: 'Welcome to Edham Logistics',
      subtitle: 'Create your account in just a few steps',
      icon: Icons.waving_hand_rounded,
    ),
    RegistrationStep(
      title: 'Personal Information',
      subtitle: 'Tell us about yourself',
      icon: Icons.person_rounded,
    ),
    RegistrationStep(
      title: 'Contact Details',
      subtitle: 'How can we reach you?',
      icon: Icons.contact_phone_rounded,
    ),
    RegistrationStep(
      title: 'Business Information',
      subtitle: 'Help us understand your needs',
      icon: Icons.business_rounded,
    ),
    RegistrationStep(
      title: 'Security Setup',
      subtitle: 'Create your secure password',
      icon: Icons.lock_rounded,
    ),
    RegistrationStep(
      title: 'Terms & Conditions',
      subtitle: 'Review and accept our policies',
      icon: Icons.description_rounded,
    ),
  ];

  @override
  void dispose() {
    _pageController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    _companyController.dispose();
    _addressController.dispose();
    _cityController.dispose();
    _postalCodeController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep < _steps.length - 1) {
      if (_validateCurrentStep()) {
        _pageController.nextPage(
          duration: const Duration(milliseconds: 500),
          curve: Curves.easeInOutCubic,
        );
      }
    } else {
      _submitRegistration();
    }
  }

  void _previousStep() {
    if (_currentStep > 0) {
      _pageController.previousPage(
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOutCubic,
      );
    }
  }

  bool _validateCurrentStep() {
    switch (_currentStep) {
      case 1: // Personal Information
        return _firstNameController.text.isNotEmpty && 
               _lastNameController.text.isNotEmpty;
      case 2: // Contact Details
        return _emailController.text.isNotEmpty && 
               _phoneController.text.isNotEmpty &&
               Validators.isValidEmail(_emailController.text);
      case 3: // Business Information
        return _companyController.text.isNotEmpty && 
               _addressController.text.isNotEmpty &&
               _cityController.text.isNotEmpty;
      case 4: // Security Setup
        return _passwordController.text.isNotEmpty && 
               _confirmPasswordController.text.isNotEmpty &&
               _passwordController.text == _confirmPasswordController.text &&
               _passwordController.text.length >= 8;
      case 5: // Terms & Conditions
        return _agreeToTerms && _agreeToPrivacy;
      default:
        return true;
    }
  }

  Future<void> _submitRegistration() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
    });

    // Simulate API call
    await Future.delayed(const Duration(seconds: 2));

    setState(() {
      _isLoading = false;
    });

    // Navigate to success screen or login
    context.go('/login');
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
                  AppTheme.surface.withOpacity(0.3),
                  AppTheme.primary.withOpacity(0.1),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          
          // Skip button (top-right)
          Positioned(
            top: 60,
            right: 20,
            child: TextButton(
              onPressed: () => context.go('/login'),
              child: Text(
                'Skip for now',
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ).animate()
              .fadeIn(delay: const Duration(milliseconds: 500)),
          ),
          
          // Main content
          SafeArea(
            child: Column(
              children: [
                const SizedBox(height: 120),
                
                // Progress indicator
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: List.generate(
                    _steps.length,
                    (index) => AnimatedContainer(
                      duration: const Duration(milliseconds: 300),
                      margin: const EdgeInsets.symmetric(horizontal: 4),
                      height: 4,
                      width: _currentStep == index ? 32 : 8,
                      decoration: BoxDecoration(
                        color: _currentStep >= index
                            ? AppTheme.primary
                            : AppTheme.textHint.withOpacity(0.3),
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ),
                  ),
                ).animate()
                  .fadeIn(delay: const Duration(milliseconds: 600)),
                
                // Step title
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
                  child: Column(
                    children: [
                      Icon(
                        _steps[_currentStep].icon,
                        size: 48,
                        color: AppTheme.primary,
                      ).animate()
                        .fadeIn(delay: const Duration(milliseconds: 800))
                        .scaleXY(begin: 0.8, end: 1.0),
                      
                      const SizedBox(height: 16),
                      
                      Text(
                        _steps[_currentStep].title,
                        textAlign: TextAlign.center,
                        style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                          fontSize: 28,
                          fontWeight: FontWeight.w900,
                          color: AppTheme.textPrimary,
                          height: 1.2,
                        ),
                      ).animate()
                        .fadeIn(delay: const Duration(milliseconds: 900))
                        .slideY(begin: 0.3, end: 0),
                      
                      const SizedBox(height: 8),
                      
                      Text(
                        _steps[_currentStep].subtitle,
                        textAlign: TextAlign.center,
                        style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          fontSize: 16,
                          color: AppTheme.textSecondary,
                          height: 1.4,
                        ),
                      ).animate()
                        .fadeIn(delay: const Duration(milliseconds: 1000))
                        .slideY(begin: 0.3, end: 0),
                    ],
                  ),
                ),
                
                // Form content
                Expanded(
                  child: PageView.builder(
                    controller: _pageController,
                    onPageChanged: (index) {
                      setState(() {
                        _currentStep = index;
                      });
                    },
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: _steps.length,
                    itemBuilder: (context, index) {
                      return _buildStepContent(index);
                    },
                  ),
                ),
                
                // Navigation buttons
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                  child: Row(
                    children: [
                      // Previous button
                      if (_currentStep > 0)
                        Expanded(
                          child: GlassContainer(
                            height: 56,
                            radius: 28,
                            backgroundColor: AppTheme.surface.withOpacity(0.2),
                            borderColor: AppTheme.primary.withOpacity(0.3),
                            child: Center(
                              child: Text(
                                'Previous',
                                style: TextStyle(
                                  color: AppTheme.textPrimary,
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ).animate()
                            .fadeIn()
                            .scaleXY(begin: 0.8, end: 1.0),
                        ),
                      
                      if (_currentStep > 0) const SizedBox(width: 16),
                      
                      // Next/Submit button
                      Expanded(
                        child: GlassContainer(
                          height: 56,
                          radius: 28,
                          backgroundColor: AppTheme.primary.withOpacity(0.2),
                          borderColor: AppTheme.primary.withOpacity(0.6),
                          boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.3),
                          child: Center(
                            child: _isLoading
                                ? SizedBox(
                                    width: 20,
                                    height: 20,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                      valueColor: AlwaysStoppedAnimation<Color>(AppTheme.primary),
                                    ),
                                  )
                                : Text(
                                    _currentStep == _steps.length - 1 ? 'Create Account' : 'Next',
                                    style: TextStyle(
                                      color: AppTheme.primary,
                                      fontSize: 16,
                                      fontWeight: FontWeight.w700,
                                    ),
                                  ),
                          ),
                        ).animate()
                          .fadeIn()
                          .scaleXY(begin: 0.8, end: 1.0)
                          .then()
                          .shimmer(),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStepContent(int step) {
    switch (step) {
      case 0:
        return _buildWelcomeStep();
      case 1:
        return _buildPersonalInfoStep();
      case 2:
        return _buildContactStep();
      case 3:
        return _buildBusinessStep();
      case 4:
        return _buildSecurityStep();
      case 5:
        return _buildTermsStep();
      default:
        return const SizedBox.shrink();
    }
  }

  Widget _buildWelcomeStep() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Hero illustration placeholder
          Container(
            width: 200,
            height: 200,
            decoration: BoxDecoration(
              gradient: AppTheme.primaryGradient,
              borderRadius: BorderRadius.circular(100),
              boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.4),
            ),
            child: Icon(
              Icons.local_shipping_rounded,
              size: 100,
              color: Colors.white.withOpacity(0.9),
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 1200))
            .scaleXY(begin: 0.6, end: 1.0, curve: Curves.elasticOut)
            .then()
            .rotate(begin: 0, end: 0.05, duration: const Duration(milliseconds: 2000))
            .then()
            .rotate(begin: 0.05, end: -0.05, duration: const Duration(milliseconds: 2000))
            .repeat(),
          
          const SizedBox(height: 40),
          
          Text(
            'Join thousands of businesses\ntrust Edham Logistics',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              fontSize: 18,
              color: AppTheme.textSecondary,
              height: 1.5,
            ),
          ).animate()
            .fadeIn(delay: const Duration(milliseconds: 1400))
            .slideY(begin: 0.3, end: 0),
          
          const SizedBox(height: 24),
          
          _buildFeatureItem(
            '🚀',
            'Real-time tracking worldwide',
            delay: const Duration(milliseconds: 1600),
          ),
          
          const SizedBox(height: 16),
          
          _buildFeatureItem(
            '📊',
            'Advanced analytics & insights',
            delay: const Duration(milliseconds: 1700),
          ),
          
          const SizedBox(height: 16),
          
          _buildFeatureItem(
            '🔒',
            'Enterprise-grade security',
            delay: const Duration(milliseconds: 1800),
          ),
        ],
      ),
    );
  }

  Widget _buildFeatureItem(String emoji, String text, {required Duration delay}) {
    return Row(
      children: [
        Text(
          emoji,
          style: const TextStyle(fontSize: 24),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Text(
            text,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              fontSize: 16,
              color: AppTheme.textPrimary,
              height: 1.4,
            ),
          ),
        ),
      ],
    ).animate()
      .fadeIn(delay: delay)
      .slideX(begin: -0.3, end: 0);
  }

  Widget _buildPersonalInfoStep() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            _buildTextField(
              controller: _firstNameController,
              label: 'First Name',
              hint: 'Enter your first name',
              prefixIcon: Icons.person_outline_rounded,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter your first name';
                }
                return null;
              },
            ),
            
            const SizedBox(height: 20),
            
            _buildTextField(
              controller: _lastNameController,
              label: 'Last Name',
              hint: 'Enter your last name',
              prefixIcon: Icons.person_outline_rounded,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter your last name';
                }
                return null;
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContactStep() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        children: [
          _buildTextField(
            controller: _emailController,
            label: 'Email Address',
            hint: 'Enter your email address',
            prefixIcon: Icons.email_outlined,
            keyboardType: TextInputType.emailAddress,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter your email address';
              }
              if (!Validators.isValidEmail(value)) {
                return 'Please enter a valid email address';
              }
              return null;
            },
          ),
          
          const SizedBox(height: 20),
          
          GlassContainer(
            radius: 16,
            backgroundColor: AppTheme.surface.withOpacity(0.2),
            borderColor: AppTheme.primary.withOpacity(0.3),
            child: IntlPhoneField(
              controller: _phoneController,
              decoration: InputDecoration(
                labelText: 'Phone Number',
                hintText: 'Enter your phone number',
                hintStyle: TextStyle(color: AppTheme.textHint),
                labelStyle: TextStyle(color: AppTheme.textSecondary),
                border: InputBorder.none,
                contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
              ),
              initialCountryCode: 'US',
              onChanged: (phone) {
                // Handle phone number change
              },
              validator: (value) {
                if (value == null || value.number.isEmpty) {
                  return 'Please enter your phone number';
                }
                return null;
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBusinessStep() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        children: [
          _buildDropdownField(
            label: 'Business Type',
            value: _selectedBusinessType,
            items: ['Individual', 'Small Business', 'Medium Business', 'Enterprise'],
            onChanged: (value) {
              setState(() {
                _selectedBusinessType = value!;
              });
            },
          ),
          
          const SizedBox(height: 20),
          
          _buildTextField(
            controller: _companyController,
            label: 'Company Name',
            hint: 'Enter your company name',
            prefixIcon: Icons.business_outlined,
          ),
          
          const SizedBox(height: 20),
          
          _buildTextField(
            controller: _addressController,
            label: 'Address',
            hint: 'Enter your street address',
            prefixIcon: Icons.location_on_outlined,
          ),
          
          const SizedBox(height: 20),
          
          Row(
            children: [
              Expanded(
                child: _buildTextField(
                  controller: _cityController,
                  label: 'City',
                  hint: 'City',
                  prefixIcon: Icons.location_city_outlined,
                ),
              ),
              
              const SizedBox(width: 16),
              
              Expanded(
                child: _buildTextField(
                  controller: _postalCodeController,
                  label: 'Postal Code',
                  hint: 'Postal code',
                  prefixIcon: Icons.markunread_mailbox_outlined,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildSecurityStep() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        children: [
          _buildTextField(
            controller: _passwordController,
            label: 'Password',
            hint: 'Create a strong password',
            prefixIcon: Icons.lock_outline,
            obscureText: true,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter a password';
              }
              if (value.length < 8) {
                return 'Password must be at least 8 characters';
              }
              return null;
            },
          ),
          
          const SizedBox(height: 20),
          
          _buildTextField(
            controller: _confirmPasswordController,
            label: 'Confirm Password',
            hint: 'Re-enter your password',
            prefixIcon: Icons.lock_outline,
            obscureText: true,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please confirm your password';
              }
              if (value != _passwordController.text) {
                return 'Passwords do not match';
              }
              return null;
            },
          ),
          
          const SizedBox(height: 24),
          
          _buildPasswordRequirements(),
        ],
      ),
    );
  }

  Widget _buildPasswordRequirements() {
    return GlassContainer(
      radius: 12,
      backgroundColor: AppTheme.surface.withOpacity(0.2),
      borderColor: AppTheme.primary.withOpacity(0.3),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Password Requirements:',
              style: TextStyle(
                color: AppTheme.textPrimary,
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            _buildRequirementItem('At least 8 characters', _passwordController.text.length >= 8),
            _buildRequirementItem('Contains uppercase letter', _passwordController.text.contains(RegExp(r'[A-Z]'))),
            _buildRequirementItem('Contains lowercase letter', _passwordController.text.contains(RegExp(r'[a-z]'))),
            _buildRequirementItem('Contains number', _passwordController.text.contains(RegExp(r'[0-9]'))),
          ],
        ),
      ),
    );
  }

  Widget _buildRequirementItem(String text, bool satisfied) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        children: [
          Icon(
            satisfied ? Icons.check_circle_rounded : Icons.radio_button_unchecked_rounded,
            size: 16,
            color: satisfied ? AppTheme.success : AppTheme.textHint,
          ),
          const SizedBox(width: 8),
          Text(
            text,
            style: TextStyle(
              color: satisfied ? AppTheme.success : AppTheme.textSecondary,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTermsStep() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Column(
        children: [
          Expanded(
            child: GlassContainer(
              radius: 16,
              backgroundColor: AppTheme.surface.withOpacity(0.2),
              borderColor: AppTheme.primary.withOpacity(0.3),
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Terms of Service',
                      style: TextStyle(
                        color: AppTheme.textPrimary,
                        fontSize: 18,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'By using Edham Logistics, you agree to our terms of service and privacy policy. We are committed to protecting your data and providing the best logistics experience possible.',
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 14,
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'Privacy Policy',
                      style: TextStyle(
                        color: AppTheme.textPrimary,
                        fontSize: 18,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'We respect your privacy and are committed to protecting your personal information. Your data is encrypted and never shared with third parties without your consent.',
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 14,
                        height: 1.5,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          
          const SizedBox(height: 24),
          
          _buildCheckboxItem(
            title: 'I agree to the Terms of Service',
            subtitle: 'Required for account creation',
            value: _agreeToTerms,
            onChanged: (value) {
              setState(() {
                _agreeToTerms = value!;
              });
            },
          ),
          
          const SizedBox(height: 16),
          
          _buildCheckboxItem(
            title: 'I agree to the Privacy Policy',
            subtitle: 'Required for account creation',
            value: _agreeToPrivacy,
            onChanged: (value) {
              setState(() {
                _agreeToPrivacy = value!;
              });
            },
          ),
          
          const SizedBox(height: 16),
          
          _buildCheckboxItem(
            title: 'Send me marketing communications',
            subtitle: 'Optional - receive updates and offers',
            value: _marketingConsent,
            onChanged: (value) {
              setState(() {
                _marketingConsent = value!;
              });
            },
          ),
        ],
      ),
    );
  }

  Widget _buildTextField({
    required TextEditingController controller,
    required String label,
    required String hint,
    required IconData prefixIcon,
    bool obscureText = false,
    TextInputType? keyboardType,
    String? Function(String?)? validator,
  }) {
    return GlassContainer(
      radius: 16,
      backgroundColor: AppTheme.surface.withOpacity(0.2),
      borderColor: AppTheme.primary.withOpacity(0.3),
      child: TextFormField(
        controller: controller,
        obscureText: obscureText,
        keyboardType: keyboardType,
        validator: validator,
        decoration: InputDecoration(
          labelText: label,
          hintText: hint,
          hintStyle: TextStyle(color: AppTheme.textHint),
          labelStyle: TextStyle(color: AppTheme.textSecondary),
          prefixIcon: Icon(prefixIcon, color: AppTheme.primary),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        ),
        style: TextStyle(
          color: AppTheme.textPrimary,
          fontSize: 16,
        ),
      ),
    );
  }

  Widget _buildDropdownField({
    required String label,
    required String value,
    required List<String> items,
    required Function(String?) onChanged,
  }) {
    return GlassContainer(
      radius: 16,
      backgroundColor: AppTheme.surface.withOpacity(0.2),
      borderColor: AppTheme.primary.withOpacity(0.3),
      child: DropdownButtonFormField<String>(
        value: value,
        decoration: InputDecoration(
          labelText: label,
          hintStyle: TextStyle(color: AppTheme.textHint),
          labelStyle: TextStyle(color: AppTheme.textSecondary),
          prefixIcon: Icon(Icons.business_outlined, color: AppTheme.primary),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        ),
        dropdownColor: AppTheme.surface,
        borderRadius: BorderRadius.circular(12),
        items: items.map((String item) {
          return DropdownMenuItem<String>(
            value: item,
            child: Text(
              item,
              style: TextStyle(
                color: AppTheme.textPrimary,
                fontSize: 16,
              ),
            ),
          );
        }).toList(),
        onChanged: onChanged,
      ),
    );
  }

  Widget _buildCheckboxItem({
    required String title,
    required String subtitle,
    required bool value,
    required Function(bool?) onChanged,
  }) {
    return Row(
      children: [
        Checkbox(
          value: value,
          onChanged: onChanged,
          activeColor: AppTheme.primary,
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: TextStyle(
                  color: AppTheme.textPrimary,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
              Text(
                subtitle,
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 12,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class RegistrationStep {
  final String title;
  final String subtitle;
  final IconData icon;

  RegistrationStep({
    required this.title,
    required this.subtitle,
    required this.icon,
  });
}
