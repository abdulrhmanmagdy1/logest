// ============================================
// ➕ Create Shipment Screen - Wizard
// ============================================

import 'package:flutter/material.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/navigation/app_router.dart';

class CreateShipmentScreen extends StatefulWidget {
  const CreateShipmentScreen({super.key});

  @override
  State<CreateShipmentScreen> createState() => _CreateShipmentScreenState();
}

class _CreateShipmentScreenState extends State<CreateShipmentScreen> {
  int _currentStep = 0;
  final _formKey = GlobalKey<FormState>();

  // Form data
  String _cargoType = 'general';
  final _weightController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _pickupCityController = TextEditingController(text: 'الرياض');
  final _deliveryCityController = TextEditingController(text: 'جدة');
  final _contactNameController = TextEditingController();
  final _contactPhoneController = TextEditingController();

  @override
  void dispose() {
    _weightController.dispose();
    _descriptionController.dispose();
    _pickupCityController.dispose();
    _deliveryCityController.dispose();
    _contactNameController.dispose();
    _contactPhoneController.dispose();
    super.dispose();
  }

  void _nextStep() {
    if (_currentStep < 2) {
      setState(() {
        _currentStep++;
      });
    } else {
      _submit();
    }
  }

  void _previousStep() {
    if (_currentStep > 0) {
      setState(() {
        _currentStep--;
      });
    }
  }

  void _submit() {
    // Show success dialog
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: AppTheme.surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        content: Column(
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
            const Text(
              'تم إنشاء الشحنة!',
              style: TextStyle(
                color: AppTheme.textPrimary,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'رقم التتبع: #EDH-5678',
              style: TextStyle(
                color: AppTheme.primary,
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  Navigator.pop(context);
                  AppRouter.goClientDashboard(context);
                },
                child: const Text('حسناً'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: AppBar(
        title: const Text('شحنة جديدة'),
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.close),
          onPressed: () => AppRouter.goBack(context),
        ),
      ),
      body: Form(
        key: _formKey,
        child: Column(
          children: [
            // Step Indicator
            _buildStepIndicator(),

            // Content
            Expanded(
              child: _buildStepContent(),
            ),

            // Navigation Buttons
            _buildNavigationButtons(),
          ],
        ),
      ),
    );
  }

  Widget _buildStepIndicator() {
    return Container(
      padding: const EdgeInsets.all(20),
      color: AppTheme.surface,
      child: Row(
        children: [
          _buildStepCircle(0, 'الشحنة'),
          _buildStepLine(0),
          _buildStepCircle(1, 'المواقع'),
          _buildStepLine(1),
          _buildStepCircle(2, 'التأكيد'),
        ],
      ),
    );
  }

  Widget _buildStepCircle(int step, String label) {
    final isActive = step <= _currentStep;
    return Expanded(
      child: Column(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: isActive ? AppTheme.primary : AppTheme.surfaceLight,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Center(
              child: isActive && step < _currentStep
                  ? const Icon(Icons.check, color: Colors.white, size: 20)
                  : Text(
                      '${step + 1}',
                      style: TextStyle(
                        color: isActive ? Colors.white : AppTheme.textHint,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
            ),
          ),
          const SizedBox(height: 8),
          Text(
            label,
            style: TextStyle(
              color: isActive ? AppTheme.textPrimary : AppTheme.textHint,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStepLine(int step) {
    final isActive = step < _currentStep;
    return Expanded(
      child: Container(
        height: 2,
        margin: const EdgeInsets.only(bottom: 25),
        color: isActive ? AppTheme.primary : AppTheme.surfaceLight,
      ),
    );
  }

  Widget _buildStepContent() {
    switch (_currentStep) {
      case 0:
        return _buildCargoStep();
      case 1:
        return _buildLocationsStep();
      case 2:
        return _buildConfirmationStep();
      default:
        return const SizedBox.shrink();
    }
  }

  Widget _buildCargoStep() {
    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Text(
          'معلومات الشحنة',
          style: Theme.of(context).textTheme.titleLarge,
        ),
        const SizedBox(height: 24),

        // Cargo Type
        Text(
          'نوع البضاعة',
          style: Theme.of(context).textTheme.bodyMedium,
        ),
        const SizedBox(height: 12),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            _buildTypeChip('عام', 'general'),
            _buildTypeChip('مجمد', 'frozen'),
            _buildTypeChip('مبرد', 'chilled'),
            _buildTypeChip('أدوية', 'pharmaceutical'),
            _buildTypeChip('زهور', 'flowers'),
          ],
        ),

        const SizedBox(height: 24),

        // Weight
        TextFormField(
          controller: _weightController,
          keyboardType: TextInputType.number,
          decoration: const InputDecoration(
            hintText: 'الوزن (كجم)',
            prefixIcon: Icon(Icons.scale_outlined),
          ),
          validator: (value) {
            if (value?.isEmpty ?? true) return 'الوزن مطلوب';
            return null;
          },
        ),

        const SizedBox(height: 16),

        // Description
        TextFormField(
          controller: _descriptionController,
          maxLines: 3,
          decoration: const InputDecoration(
            hintText: 'وصف البضاعة (اختياري)',
            prefixIcon: Icon(Icons.description_outlined),
          ),
        ),
      ],
    );
  }

  Widget _buildTypeChip(String label, String value) {
    final isSelected = _cargoType == value;
    return ChoiceChip(
      label: Text(label),
      selected: isSelected,
      onSelected: (selected) {
        setState(() {
          _cargoType = value;
        });
      },
      selectedColor: AppTheme.primary,
      backgroundColor: AppTheme.surfaceLight,
      labelStyle: TextStyle(
        color: isSelected ? Colors.white : AppTheme.textSecondary,
      ),
    );
  }

  Widget _buildLocationsStep() {
    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Text(
          'مواقع الاستلام والتسليم',
          style: Theme.of(context).textTheme.titleLarge,
        ),
        const SizedBox(height: 24),

        // Pickup
        TextFormField(
          controller: _pickupCityController,
          decoration: const InputDecoration(
            hintText: 'مدينة الاستلام',
            prefixIcon: Icon(Icons.location_on_outlined),
          ),
        ),

        const SizedBox(height: 16),

        // Delivery
        TextFormField(
          controller: _deliveryCityController,
          decoration: const InputDecoration(
            hintText: 'مدينة التسليم',
            prefixIcon: Icon(Icons.location_on_outlined),
          ),
        ),

        const SizedBox(height: 24),

        // Contact Info
        Text(
          'معلومات التواصل',
          style: Theme.of(context).textTheme.bodyMedium,
        ),
        const SizedBox(height: 12),

        TextFormField(
          controller: _contactNameController,
          decoration: const InputDecoration(
            hintText: 'اسم المستلم',
            prefixIcon: Icon(Icons.person_outline),
          ),
        ),

        const SizedBox(height: 16),

        TextFormField(
          controller: _contactPhoneController,
          keyboardType: TextInputType.phone,
          decoration: const InputDecoration(
            hintText: 'رقم الجوال',
            prefixIcon: Icon(Icons.phone_outlined),
          ),
        ),
      ],
    );
  }

  Widget _buildConfirmationStep() {
    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        Text(
          'تأكيد الطلب',
          style: Theme.of(context).textTheme.titleLarge,
        ),
        const SizedBox(height: 24),

        // Summary Card
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildSummaryItem('نوع البضاعة', 'عام'),
                _buildSummaryItem('الوزن', '${_weightController.text} كجم'),
                _buildSummaryItem('من', _pickupCityController.text),
                _buildSummaryItem('إلى', _deliveryCityController.text),
                const Divider(height: 32),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'التكلفة التقديرية:',
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 16,
                      ),
                    ),
                    Text(
                      '350 ر.س',
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        color: AppTheme.primary,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),

        const SizedBox(height: 24),

        // Note
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppTheme.accent.withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Row(
            children: [
              Icon(Icons.info_outline, color: AppTheme.accent),
              const SizedBox(width: 12),
              Expanded(
                child: Text(
                  'سيتم إرسال تأكيد الطلب عبر البريد الإلكتروني والرسائل النصية',
                  style: TextStyle(
                    color: AppTheme.accent,
                    fontSize: 14,
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildSummaryItem(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
            ),
          ),
          Text(
            value,
            style: const TextStyle(
              color: AppTheme.textPrimary,
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNavigationButtons() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 20,
            offset: const Offset(0, -5),
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          children: [
            if (_currentStep > 0)
              Expanded(
                child: OutlinedButton(
                  onPressed: _previousStep,
                  child: const Text('رجوع'),
                ),
              ),
            if (_currentStep > 0) const SizedBox(width: 16),
            Expanded(
              flex: _currentStep > 0 ? 1 : 2,
              child: ElevatedButton(
                onPressed: _nextStep,
                child: Text(_currentStep == 2 ? 'تأكيد الطلب' : 'التالي'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
