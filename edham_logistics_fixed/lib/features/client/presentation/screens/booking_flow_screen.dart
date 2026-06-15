// ============================================
// 📦 Edham Logistics - Booking Flow Screen
// Professional Flutter implementation matching web design
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';

class BookingFlowScreen extends StatefulWidget {
  const BookingFlowScreen({super.key});

  @override
  State<BookingFlowScreen> createState() => _BookingFlowScreenState();
}

class _BookingFlowScreenState extends State<BookingFlowScreen>
    with TickerProviderStateMixin {
  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;
  
  int _currentStep = 0;
  final _formKey = GlobalKey<FormState>();
  
  // Form data
  final _pickupController = TextEditingController(text: '278 Ash Dr. San Jose, South Dakota 83475');
  final _dropoffController = TextEditingController(text: '4140 Parker Rd. Allentown, New Mexico 3034');
  final _descriptionController = TextEditingController();
  final _weightController = TextEditingController();
  String _selectedVehicle = '';
  bool _isLoading = false;

  final List<VehicleOption> _vehicles = [
    VehicleOption(
      id: 'motorcycle',
      name: 'Motorcycle',
      description: 'For small packages and documents',
      capacity: 'Up to 20kg',
      dimensions: '60x40x30 cm',
      price: 15,
      icon: '🏍️',
    ),
    VehicleOption(
      id: 'van',
      name: 'Van',
      description: 'For medium-sized deliveries',
      capacity: 'Up to 500kg',
      dimensions: '300x200x200 cm',
      price: 25,
      icon: '🚐',
    ),
    VehicleOption(
      id: 'truck',
      name: 'Truck',
      description: 'For large cargo and furniture',
      capacity: 'Up to 2000kg',
      dimensions: '600x250x300 cm',
      price: 45,
      icon: '🚚',
    ),
  ];

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
    _pickupController.dispose();
    _dropoffController.dispose();
    _descriptionController.dispose();
    _weightController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep < 2) {
      setState(() {
        _currentStep++;
      });
    } else {
      _submitBooking();
    }
  }

  void _previousStep() {
    if (_currentStep > 0) {
      setState(() {
        _currentStep--;
      });
    }
  }

  void _selectVehicle(String vehicleId) {
    setState(() {
      _selectedVehicle = vehicleId;
    });
  }

  void _submitBooking() {
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
          _showSuccessDialog();
        }
      });
    }
  }

  void _showSuccessDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => Dialog(
        backgroundColor: AppTheme.surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 80,
                height: 80,
                decoration: BoxDecoration(
                  color: AppTheme.success.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(40),
                ),
                child: const Icon(
                  Icons.check_circle,
                  color: AppTheme.success,
                  size: 50,
                ),
              ),
              const SizedBox(height: 24),
              Text(
                'تم إنشاء الحجز!',
                style: GoogleFonts.inter(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: AppTheme.textPrimary,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'رقم التتبع: #EDH-5678',
                style: GoogleFonts.inter(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: AppTheme.primary,
                ),
              ),
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                    AppRouter.goClientDashboard(context);
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppTheme.primary,
                    foregroundColor: Colors.white,
                    elevation: 0,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: Text(
                    'حسناً',
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
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: SlideTransition(
            position: _slideAnimation,
            child: Column(
              children: [
                // Header
                _buildHeader(),

                // Progress Bar
                _buildProgressBar(),

                // Content
                Expanded(
                  child: _buildStepContent(),
                ),

                // Bottom Actions
                _buildBottomActions(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.background,
        border: Border(
          bottom: BorderSide(
            color: AppTheme.surface,
            width: 1,
          ),
        ),
      ),
      child: Row(
        children: [
          // Back Button
          IconButton(
            onPressed: () => AppRouter.goBack(context),
            icon: const Icon(Icons.arrow_back),
            color: AppTheme.textPrimary,
          ),

          // Title
          Expanded(
            child: Text(
              'طلب جديد',
              textAlign: TextAlign.center,
              style: GoogleFonts.inter(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppTheme.textPrimary,
              ),
            ),
          ),

          // Menu Button
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.more_vert),
            color: AppTheme.textPrimary,
          ),
        ],
      ),
    );
  }

  Widget _buildProgressBar() {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 24),
      child: Row(
        children: List.generate(
          3,
          (index) => Expanded(
            child: Container(
              margin: const EdgeInsets.symmetric(horizontal: 4),
              height: 4,
              decoration: BoxDecoration(
                color: index <= _currentStep
                    ? AppTheme.primary
                    : AppTheme.textHint.withOpacity(0.3),
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildStepContent() {
    switch (_currentStep) {
      case 0:
        return _buildLocationStep();
      case 1:
        return _buildVehicleStep();
      case 2:
        return _buildCargoStep();
      default:
        return Container();
    }
  }

  Widget _buildLocationStep() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        children: [
          // Location Cards
          Row(
            children: [
              Expanded(
                child: _buildLocationCard(
                  title: 'نقطع الاستلام',
                  address: _pickupController.text,
                  icon: Icons.location_on,
                  color: AppTheme.primary,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildLocationCard(
                  title: 'نقطع التوصيل',
                  address: _dropoffController.text,
                  icon: Icons.flag,
                  color: AppTheme.accent,
                ),
              ),
            ],
          ),

          const SizedBox(height: 24),

          // Change Location Button
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: () {},
              icon: const Icon(Icons.edit_location_alt),
              label: Text(
                'تغيير العناوين',
                style: GoogleFonts.inter(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                  color: AppTheme.primary,
                ),
              ),
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: AppTheme.primary),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLocationCard({
    required String title,
    required String address,
    required IconData icon,
    required Color color,
  }) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppTheme.surfaceLight,
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: color, size: 20),
              const SizedBox(width: 12),
              Text(
                title,
                style: GoogleFonts.inter(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: AppTheme.textPrimary,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            address,
            style: GoogleFonts.inter(
              fontSize: 14,
              color: AppTheme.textSecondary,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }

  Widget _buildVehicleStep() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'اختر المركبة',
            style: GoogleFonts.inter(
              fontSize: 20,
              fontWeight: FontWeight.w700,
              color: AppTheme.textPrimary,
            ),
          ),

          const SizedBox(height: 24),

          // Vehicle Options
          ..._vehicles.map((vehicle) => Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: _buildVehicleCard(vehicle),
          )),

          const SizedBox(height: 24),

          // Price Display
          if (_selectedVehicle.isNotEmpty)
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppTheme.primary.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: AppTheme.primary.withOpacity(0.3),
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.attach_money, color: AppTheme.primary),
                  const SizedBox(width: 8),
                  Text(
                    'السعر: \$${_vehicles.firstWhere((v) => v.id == _selectedVehicle).price}',
                    style: GoogleFonts.inter(
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                      color: AppTheme.primary,
                    ),
                  ),
                ],
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildVehicleCard(VehicleOption vehicle) {
    final isSelected = _selectedVehicle == vehicle.id;
    
    return GestureDetector(
      onTap: () => _selectVehicle(vehicle.id),
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: isSelected ? AppTheme.primary.withOpacity(0.05) : AppTheme.surface,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: isSelected ? AppTheme.primary : AppTheme.surfaceLight,
            width: isSelected ? 2 : 1,
          ),
        ),
        child: Row(
          children: [
            // Vehicle Icon
            Container(
              width: 80,
              height: 60,
              decoration: BoxDecoration(
                color: AppTheme.surfaceLight,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Stack(
                children: [
                  Center(
                    child: Text(
                      vehicle.icon,
                      style: const TextStyle(fontSize: 30),
                    ),
                  ),
                  if (isSelected)
                    Positioned(
                      top: -8,
                      right: -8,
                      child: Container(
                        width: 24,
                        height: 24,
                        decoration: BoxDecoration(
                          color: AppTheme.primary,
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(
                            color: AppTheme.background,
                            width: 2,
                          ),
                        ),
                        child: const Icon(
                          Icons.check,
                          color: Colors.white,
                          size: 14,
                        ),
                      ),
                    ),
                ],
              ),
            ),

            const SizedBox(width: 16),

            // Vehicle Info
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    vehicle.name,
                    style: GoogleFonts.inter(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: AppTheme.textPrimary,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    vehicle.description,
                    style: GoogleFonts.inter(
                      fontSize: 14,
                      color: AppTheme.textSecondary,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      _buildSpec(Icons.speed, vehicle.capacity),
                      const SizedBox(width: 16),
                      _buildSpec(Icons.straighten, vehicle.dimensions),
                    ],
                  ),
                ],
              ),
            ),

            // Price
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppTheme.primary.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '\$${vehicle.price}',
                style: GoogleFonts.inter(
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                  color: AppTheme.primary,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSpec(IconData icon, String text) {
    return Row(
      children: [
        Icon(icon, color: AppTheme.primary, size: 16),
        const SizedBox(width: 6),
        Text(
          text,
          style: GoogleFonts.inter(
            fontSize: 12,
            color: AppTheme.textSecondary,
          ),
        ),
      ],
    );
  }

  Widget _buildCargoStep() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'تفاصيل الحمولة',
              style: GoogleFonts.inter(
                fontSize: 20,
                fontWeight: FontWeight.w700,
                color: AppTheme.textPrimary,
              ),
            ),

            const SizedBox(height: 24),

            // Description Field
            TextFormField(
              controller: _descriptionController,
              maxLines: 3,
              decoration: const InputDecoration(
                labelText: 'وصف الحمولة',
                filled: true,
                fillColor: AppTheme.surfaceLight,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                  borderSide: BorderSide.none,
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                  borderSide: BorderSide.none,
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                  borderSide: BorderSide(
                    color: AppTheme.primary,
                    width: 2,
                  ),
                ),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'وصف الحمولة مطلوب';
                }
                return null;
              },
            ),
            const SizedBox(height: 20),

            // Weight Field
            TextFormField(
              controller: _weightController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                labelText: 'الوزن (كجم)',
                filled: true,
                fillColor: AppTheme.surfaceLight,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                  borderSide: BorderSide.none,
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                  borderSide: BorderSide.none,
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                  borderSide: BorderSide(
                    color: AppTheme.primary,
                    width: 2,
                  ),
                ),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'الوزن مطلوب';
                }
                return null;
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBottomActions() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.background,
        border: Border(
          top: BorderSide(
            color: AppTheme.surface,
            width: 1,
          ),
        ),
      ),
      child: SafeArea(
        child: Row(
          children: [
            // Previous Button
            if (_currentStep > 0)
              Expanded(
                child: OutlinedButton(
                  onPressed: _previousStep,
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: AppTheme.textHint),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: Text(
                    'السابق',
                    style: GoogleFonts.inter(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: AppTheme.textPrimary,
                    ),
                  ),
                ),
              ),

            if (_currentStep > 0) const SizedBox(width: 16),

            // Next/Submit Button
            Expanded(
              child: ElevatedButton(
                onPressed: (_currentStep == 2 && _selectedVehicle.isEmpty) ? null : _nextStep,
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
                        _currentStep == 2 ? 'إرسال' : 'التالي',
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
    );
  }
}

class VehicleOption {
  final String id;
  final String name;
  final String description;
  final String capacity;
  final String dimensions;
  final int price;
  final String icon;

  VehicleOption({
    required this.id,
    required this.name,
    required this.description,
    required this.capacity,
    required this.dimensions,
    required this.price,
    required this.icon,
  });
}
