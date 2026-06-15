// ============================================
// 🛒 Shipment Cart Screen - Premium Shopping Experience
// Enterprise Shipment Request with Cart Functionality
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/design_system/shadows.dart';
import '../../../../core/design_system/spacing.dart';

class ShipmentCartScreen extends StatefulWidget {
  const ShipmentCartScreen({super.key});

  @override
  State<ShipmentCartScreen> createState() => _ShipmentCartScreenState();
}

class _ShipmentCartScreenState extends State<ShipmentCartScreen> {
  List<CartItem> cartItems = [
    CartItem(
      id: '1',
      title: 'شحن سريع داخل الرياض',
      description: 'توصيل خلال 2-4 ساعات',
      price: 150.0,
      quantity: 1,
      icon: Icons.flash_on,
      color: AppTheme.primary,
    ),
    CartItem(
      id: '2',
      title: 'شحن طبي مبرد',
      description: 'نقل أدوية ومستلزمات طبية',
      price: 300.0,
      quantity: 2,
      icon: Icons.medical_services,
      color: AppTheme.success,
    ),
  ];

  double get subtotal => cartItems.fold(0, (sum, item) => sum + (item.price * item.quantity));
  double get tax => subtotal * 0.15; // 15% tax
  double get total => subtotal + tax;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: AppBar(
        title: Text(
          'سلة الشحنات',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          onPressed: () => Navigator.pop(context),
          icon: const Icon(Icons.arrow_back, color: AppTheme.textPrimary),
        ),
      ),
      body: Column(
        children: [
          // Cart Items
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(20),
              itemCount: cartItems.length,
              itemBuilder: (context, index) {
                return _buildCartItem(cartItems[index], index);
              },
            ),
          ),

          // Order Summary
          _buildOrderSummary(),
        ],
      ),
    );
  }

  Widget _buildCartItem(CartItem item, int index) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: item.color.withOpacity(0.3),
      boxShadow: AppShadows.glassmorphism,
      child: Row(
        children: [
          // Service Icon
          GlassContainer(
            width: 60,
            height: 60,
            radius: 16,
            backgroundColor: item.color.withOpacity(0.1),
            borderColor: item.color.withOpacity(0.3),
            child: Icon(item.icon, color: item.color, size: 28),
          ),
          const SizedBox(width: 16),

          // Service Details
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.title,
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  item.description,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  '${item.price.toStringAsFixed(2)} ريال',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: item.color,
                    fontWeight: FontWeight.w900,
                  ),
                ),
              ],
            ),
          ),

          // Quantity Controls
          Column(
            children: [
              Row(
                children: [
                  GlassContainer(
                    width: 32,
                    height: 32,
                    radius: 8,
                    backgroundColor: Colors.white.withOpacity(0.1),
                    child: IconButton(
                      padding: EdgeInsets.zero,
                      onPressed: () {
                        setState(() {
                          if (item.quantity > 1) item.quantity--;
                        });
                      },
                      icon: const Icon(Icons.remove, size: 16, color: AppTheme.textPrimary),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Text(
                    '${item.quantity}',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      color: AppTheme.textPrimary,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(width: 12),
                  GlassContainer(
                    width: 32,
                    height: 32,
                    radius: 8,
                    backgroundColor: item.color.withOpacity(0.2),
                    child: IconButton(
                      padding: EdgeInsets.zero,
                      onPressed: () {
                        setState(() {
                          item.quantity++;
                        });
                      },
                      icon: Icon(Icons.add, size: 16, color: item.color),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              IconButton(
                onPressed: () {
                  setState(() {
                    cartItems.remove(item);
                  });
                },
                icon: const Icon(Icons.delete_outline, color: AppTheme.error, size: 20),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn(delay: Duration(milliseconds: index * 100)).slideX(begin: -0.2, end: 0);
  }

  Widget _buildOrderSummary() {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(24),
      radius: 20,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      boxShadow: AppShadows.glowing(AppTheme.primary, intensity: 0.6),
      child: Column(
        children: [
          // Summary Title
          Row(
            children: [
              Icon(Icons.receipt_long, color: AppTheme.primary, size: 24),
              const SizedBox(width: 12),
              Text(
                'ملخص الطلب',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),

          // Price Breakdown
          _buildPriceRow('المجموع الفرعي', subtotal),
          const SizedBox(height: 8),
          _buildPriceRow('ضريبة القيمة المضافة (15%)', tax),
          const Divider(color: AppTheme.textHint, height: 20),
          _buildPriceRow('الإجمالي', total, isTotal: true),
          const SizedBox(height: 20),

          // Action Buttons
          Row(
            children: [
              Expanded(
                child: GlassContainer(
                  height: 48,
                  radius: 16,
                  backgroundColor: Colors.white.withOpacity(0.05),
                  borderColor: AppTheme.primary.withOpacity(0.3),
                  child: Center(
                    child: Text(
                      'متابعة التسوق',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.primary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                flex: 2,
                child: GlowingButton(
                  text: 'إكمال الطلب',
                  onPressed: () {
                    _showCheckoutDialog();
                  },
                  color: AppTheme.primary,
                  icon: Icons.payment,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 400)).slideY(begin: 0.3, end: 0);
  }

  Widget _buildPriceRow(String label, double amount, {bool isTotal = false}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: isTotal ? AppTheme.textPrimary : AppTheme.textSecondary,
            fontWeight: isTotal ? FontWeight.bold : FontWeight.normal,
          ),
        ),
        Text(
          '${amount.toStringAsFixed(2)} ريال',
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: isTotal ? AppTheme.primary : AppTheme.textPrimary,
            fontWeight: isTotal ? FontWeight.w900 : FontWeight.w600,
            fontSize: isTotal ? 18 : 16,
          ),
        ),
      ],
    );
  }

  void _showCheckoutDialog() {
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
                Icons.check_circle_outline,
                color: AppTheme.success,
                size: 64,
              ),
              const SizedBox(height: 16),
              Text(
                'تأكيد الطلب',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'إجمالي الطلب: ${total.toStringAsFixed(2)} ريال',
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: AppTheme.primary,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 24),
              Row(
                children: [
                  Expanded(
                    child: GlassContainer(
                      height: 48,
                      radius: 16,
                      backgroundColor: Colors.white.withOpacity(0.05),
                      borderColor: AppTheme.textHint.withOpacity(0.3),
                      child: Center(
                        child: Text(
                          'إلغاء',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppTheme.textSecondary,
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: GlowingButton(
                      text: 'تأكيد الدفع',
                      onPressed: () {
                        Navigator.pop(context);
                        _showSuccessMessage();
                      },
                      color: AppTheme.success,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showSuccessMessage() {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        backgroundColor: AppTheme.success,
        content: Row(
          children: [
            const Icon(Icons.check_circle, color: Colors.white),
            const SizedBox(width: 8),
            Text(
              'تم تأكيد طلبك بنجاح!',
              style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600),
            ),
          ],
        ),
      ),
    );
  }
}

class CartItem {
  final String id;
  final String title;
  final String description;
  final double price;
  int quantity;
  final IconData icon;
  final Color color;

  CartItem({
    required this.id,
    required this.title,
    required this.description,
    required this.price,
    required this.quantity,
    required this.icon,
    required this.color,
  });
}
