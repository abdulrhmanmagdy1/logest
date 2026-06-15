// ============================================
// 💎 Subscription Screen - شاشة الاشتراكات والأسعار
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class SubscriptionScreen extends StatefulWidget {
  const SubscriptionScreen({super.key});

  @override
  State<SubscriptionScreen> createState() => _SubscriptionScreenState();
}

class _SubscriptionScreenState extends State<SubscriptionScreen> {
  bool _isYearly = false;
  int _selectedPlan = 1;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'الاشتراكات',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
      ),
      body: Column(
        children: [
          // Billing Toggle
          _buildBillingToggle(),
          
          const SizedBox(height: 16),
          
          // Plans
          Expanded(
            child: ListView(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              children: [
                _buildPlanCard(
                  'Basic',
                  'الأساسي',
                  _isYearly ? 199 : 249,
                  [
                    'حتى 100 شحنة/شهر',
                    '5 سائقين',
                    'دعم عبر البريد',
                    'تقارير أساسية',
                  ],
                  0,
                  color: Colors.grey,
                ),
                const SizedBox(height: 12),
                _buildPlanCard(
                  'Standard',
                  'القياسي',
                  _isYearly ? 399 : 499,
                  [
                    'حتى 500 شحنة/شهر',
                    '20 سائق',
                    'دعم فني م Priority',
                    'تقارير متقدمة',
                    'تتبع GPS',
                    'تنبيهات درجة الحرارة',
                  ],
                  1,
                  color: AppTheme.primaryColor,
                  isPopular: true,
                ),
                const SizedBox(height: 12),
                _buildPlanCard(
                  'Premium',
                  'المميز',
                  _isYearly ? 799 : 999,
                  [
                    'شحنات غير محدودة',
                    'سائقين غير محدودين',
                    'دعم فني 24/7',
                    'تحليلات AI',
                    'تكامل API',
                    'تخصيص كامل',
                  ],
                  2,
                  color: Colors.purple,
                ),
                const SizedBox(height: 12),
                _buildEnterpriseCard(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBillingToggle() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Expanded(
            child: GestureDetector(
              onTap: () => setState(() => _isYearly = false),
              child: Container(
                padding: const EdgeInsets.symmetric(vertical: 12),
                decoration: BoxDecoration(
                  color: !_isYearly ? AppTheme.primaryColor : Colors.transparent,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  'شهري',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: !_isYearly ? FontWeight.bold : FontWeight.normal,
                  ),
                ),
              ),
            ),
          ),
          Expanded(
            child: GestureDetector(
              onTap: () => setState(() => _isYearly = true),
              child: Container(
                padding: const EdgeInsets.symmetric(vertical: 12),
                decoration: BoxDecoration(
                  color: _isYearly ? AppTheme.primaryColor : Colors.transparent,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  children: [
                    Text(
                      'سنوي',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: _isYearly ? FontWeight.bold : FontWeight.normal,
                      ),
                    ),
                    if (_isYearly)
                      Container(
                        margin: const EdgeInsets.only(top: 2),
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: Colors.green,
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: const Text(
                          'وفر 20%',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 10,
                          ),
                        ),
                      ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPlanCard(
    String code,
    String name,
    double price,
    List<String> features,
    int index, {
    required Color color,
    bool isPopular = false,
  }) {
    final isSelected = _selectedPlan == index;

    return Container(
      decoration: BoxDecoration(
        gradient: isSelected
            ? LinearGradient(
                colors: [color.withOpacity(0.3), color.withOpacity(0.1)],
              )
            : null,
        color: isSelected ? null : AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isSelected ? color : Colors.transparent,
          width: 2,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (isPopular)
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 8),
              decoration: BoxDecoration(
                color: color,
                borderRadius: const BorderRadius.vertical(
                  top: Radius.circular(14),
                ),
              ),
              child: const Text(
                'الأكثر شعبية',
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 12,
                ),
              ),
            ),
          
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Header
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          name,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Text(
                          code,
                          style: TextStyle(
                            color: AppTheme.textSecondary,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                    
                    // Radio Button
                    Container(
                      width: 24,
                      height: 24,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        border: Border.all(
                          color: isSelected ? color : AppTheme.textSecondary,
                          width: 2,
                        ),
                        color: isSelected ? color : Colors.transparent,
                      ),
                      child: isSelected
                          ? const Icon(
                              Icons.check,
                              color: Colors.white,
                              size: 16,
                            )
                          : null,
                    ),
                  ],
                ),
                
                const SizedBox(height: 16),
                
                // Price
                Row(
                  crossAxisAlignment: CrossAxisAlignment.baseline,
                  textBaseline: TextBaseline.alphabetic,
                  children: [
                    Text(
                      price.toStringAsFixed(0),
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 36,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      'ر.س',
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 14,
                      ),
                    ),
                    Text(
                      _isYearly ? '/سنة' : '/شهر',
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
                
                const SizedBox(height: 16),
                
                // Features
                ...features.map((feature) => _buildFeatureItem(feature, color)),
                
                const SizedBox(height: 16),
                
                // Select Button
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () {
                      setState(() => _selectedPlan = index);
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: isSelected ? color : AppTheme.primaryColor,
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    child: Text(
                      isSelected ? 'مختار' : 'اختيار',
                      style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFeatureItem(String feature, Color color) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        children: [
          Icon(
            Icons.check_circle,
            color: color,
            size: 20,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              feature,
              style: TextStyle(
                color: AppTheme.textSecondary,
                fontSize: 13,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEnterpriseCard() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Colors.orange.shade700, Colors.orange.shade900],
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Icon(
                  Icons.business,
                  color: Colors.white,
                ),
              ),
              const SizedBox(width: 12),
              const Expanded(
                child: Text(
                  'خطة مخصصة للشركات',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            'حلول مخصصة للشركات الكبيرة مع متطلبات خاصة',
            style: TextStyle(
              color: Colors.white.withOpacity(0.8),
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: () {},
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.white,
              foregroundColor: Colors.orange.shade900,
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
            ),
            child: const Text(
              'تواصل مع المبيعات',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }
}
