// ============================================
// 🛍️ One-Click Ordering System
// Premium E-commerce Store with Instant Ordering
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class OneClickOrdering extends StatefulWidget {
  const OneClickOrdering({super.key});

  @override
  State<OneClickOrdering> createState() => _OneClickOrderingState();
}

class _OneClickOrderingState extends State<OneClickOrdering>
    with TickerProviderStateMixin {
  late AnimationController _categoriesController;
  late AnimationController _productsController;
  late AnimationController _cartController;
  
  // State
  String _selectedCategory = 'all';
  String _selectedTemperature = 'all';
  List<OneClickProduct> _products = [];
  List<CartItem> _cartItems = [];
  bool _isCartVisible = false;
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadProducts();
  }

  void _initializeAnimations() {
    _categoriesController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _productsController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _cartController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _categoriesController.forward();
    _productsController.forward();
  }

  void _loadProducts() {
    _products = [
      OneClickProduct(
        id: 'PROD-001',
        name: 'شحنة سريعة - الرياض إلى جدة',
        category: 'fast',
        temperature: TemperatureType.ambient,
        price: 250.0,
        weight: 1000.0,
        deliveryTime: '4 ساعات',
        description: 'شحنة سريعة مع توصيل خلال 4 ساعات',
        icon: Icons.flash_on,
        color: AppTheme.primary,
        isPopular: true,
      ),
      OneClickProduct(
        id: 'PROD-002',
        name: 'شحنة اقتصادية - جدة إلى الدمام',
        category: 'economy',
        temperature: TemperatureType.ambient,
        price: 180.0,
        weight: 1500.0,
        deliveryTime: '24 ساعة',
        description: 'شحنة اقتصادية مع توصيل خلال 24 ساعة',
        icon: Icons.savings,
        color: AppTheme.success,
        isPopular: false,
      ),
      OneClickProduct(
        id: 'PROD-003',
        name: 'شحنة دولية - الرياض إلى دبي',
        category: 'international',
        temperature: TemperatureType.ambient,
        price: 850.0,
        weight: 2000.0,
        deliveryTime: '48 ساعة',
        description: 'شحنة دولية مع توصيل خلال 48 ساعة',
        icon: Icons.public,
        color: AppTheme.accent,
        isPopular: false,
      ),
      OneClickProduct(
        id: 'PROD-004',
        name: 'شحنة مبردة - مكة إلى المدينة',
        category: 'cold',
        temperature: TemperatureType.chilled,
        price: 320.0,
        weight: 800.0,
        deliveryTime: '6 ساعات',
        description: 'شحنة مبردة (4°م) مع توصيل خلال 6 ساعات',
        icon: Icons.ac_unit,
        color: AppTheme.primary,
        isPopular: true,
      ),
      OneClickProduct(
        id: 'PROD-005',
        name: 'شحنة مجمدة - جدة إلى الرياض',
        category: 'frozen',
        temperature: TemperatureType.frozen,
        price: 450.0,
        weight: 1200.0,
        deliveryTime: '8 ساعات',
        description: 'شحنة مجمدة (-18°م) مع توصيل خلال 8 ساعات',
        icon: Icons.kitchen,
        color: AppTheme.accent,
        isPopular: false,
      ),
      OneClickProduct(
        id: 'PROD-006',
        name: 'شحنة طبية - الرياض إلى القصيم',
        category: 'medical',
        temperature: TemperatureType.cold,
        price: 680.0,
        weight: 500.0,
        deliveryTime: '3 ساعات',
        description: 'شحنة طبية مع توصيل خلال 3 ساعات',
        icon: Icons.local_hospital,
        color: AppTheme.error,
        isPopular: true,
      ),
      OneClickProduct(
        id: 'PROD-007',
        name: 'شحنة ضخمة - الدمام إلى تبوك',
        category: 'heavy',
        temperature: TemperatureType.ambient,
        price: 1200.0,
        weight: 5000.0,
        deliveryTime: '12 ساعة',
        description: 'شحنة ضخمة مع توصيل خلال 12 ساعة',
        icon: Icons.local_shipping,
        color: AppTheme.success,
        isPopular: false,
      ),
      OneClickProduct(
        id: 'PROD-008',
        name: 'شحنة مستعجلة - الطائف إلى جدة',
        category: 'urgent',
        temperature: TemperatureType.ambient,
        price: 550.0,
        weight: 600.0,
        deliveryTime: '2 ساعة',
        description: 'شحنة مستعجلة مع توصيل خلال ساعتين',
        icon: Icons.priority_high,
        color: AppTheme.error,
        isPopular: true,
      ),
    ];
  }

  @override
  void dispose() {
    _categoriesController.dispose();
    _productsController.dispose();
    _cartController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: Stack(
        children: [
          // Main content
          CustomScrollView(
            slivers: [
              // Hero Section
              SliverToBoxAdapter(
                child: _buildHeroSection(),
              ),
              
              const SliverPadding(padding: EdgeInsets.only(bottom: 20)),
              
              // Categories
              SliverToBoxAdapter(
                child: _buildCategoriesSection(),
              ),
              
              const SliverPadding(padding: EdgeInsets.only(bottom: 20)),
              
              // Products Grid
              SliverToBoxAdapter(
                child: _buildProductsSection(),
              ),
              
              const SliverPadding(padding: EdgeInsets.only(bottom: 100)),
            ],
          ),
          
          // Cart Overlay
          if (_isCartVisible) _buildCartOverlay(),
        ],
      ),
      floatingActionButton: _buildFloatingCartButton(),
    );
  }

  PreferredSizeWidget _buildPremiumAppBar() {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      title: Text(
        'متجر إدهام',
        style: Theme.of(context).textTheme.titleLarge?.copyWith(
          color: AppTheme.textPrimary,
          fontWeight: FontWeight.bold,
        ),
      ),
      actions: [
        GlassContainer(
          width: 50,
          height: 50,
          radius: 25,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.primary.withOpacity(0.2),
          child: Stack(
            children: [
              Center(
                child: Icon(
                  Icons.search,
                  color: AppTheme.textPrimary,
                  size: 24,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(width: 8),
        GlassContainer(
          width: 50,
          height: 50,
          radius: 25,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.primary.withOpacity(0.2),
          child: Stack(
            children: [
              Center(
                child: Icon(
                  Icons.notifications,
                  color: AppTheme.textPrimary,
                  size: 24,
                ),
              ),
              if (_cartItems.isNotEmpty)
                Positioned(
                  top: 8,
                  right: 8,
                  child: Container(
                    width: 12,
                    height: 12,
                    decoration: BoxDecoration(
                      color: AppTheme.error,
                      borderRadius: BorderRadius.circular(6),
                    ),
                  ),
                ),
            ],
          ),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildHeroSection() {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(32),
      radius: 24,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      boxShadow: AppShadows.glowing(AppTheme.primary),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.shopping_cart,
                color: AppTheme.primary,
                size: 32,
              ),
              const SizedBox(width: 12),
              Text(
                'طلب حمولة في لمسة واحدة',
                style: Theme.of(context).textTheme.displayMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          Text(
            'اختر من بين أفضل خدمات الشحن المتاحة واحصل على توصيل سريع وموثوق',
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              color: AppTheme.textSecondary,
              height: 1.5,
            ),
          ),
          
          const SizedBox(height: 24),
          
          // Quick Stats
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'خدمات متاحة',
                  value: '${_products.length}',
                  change: 'جديدة',
                  icon: Icons.category,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 200),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'توصيل سريع',
                  value: '2-48س',
                  change: 'مضمون',
                  icon: Icons.timer,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 400),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate().fadeIn().slideY(begin: 0.2, end: 0);
  }

  Widget _buildCategoriesSection() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'فئات الشحن',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate(controller: _categoriesController)
            .fadeIn(delay: const Duration(milliseconds: 200)),
          
          const SizedBox(height: 16),
          
          // Category Pills
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: [
                _buildCategoryChip('all', 'الكل', Icons.apps),
                _buildCategoryChip('fast', 'سريع', Icons.flash_on),
                _buildCategoryChip('economy', 'اقتصادي', Icons.savings),
                _buildCategoryChip('international', 'دولي', Icons.public),
                _buildCategoryChip('cold', 'مبرد', Icons.ac_unit),
                _buildCategoryChip('frozen', 'مجمد', Icons.kitchen),
                _buildCategoryChip('medical', 'طبي', Icons.local_hospital),
                _buildCategoryChip('heavy', 'ضخم', Icons.truck),
                _buildCategoryChip('urgent', 'مستعجل', Icons.priority_high),
              ],
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Temperature Filter
          Row(
            children: [
              Text(
                'درجة الحرارة:',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(width: 12),
              _buildTemperatureChip('all', 'الكل'),
              _buildTemperatureChip('ambient', 'غرفة'),
              _buildTemperatureChip('chilled', 'مبرد'),
              _buildTemperatureChip('frozen', 'مجمد'),
              _buildTemperatureChip('cold', 'بارد'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCategoryChip(String category, String label, IconData icon) {
    final isSelected = _selectedCategory == category;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedCategory = category;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          radius: 20,
          backgroundColor: isSelected 
            ? AppTheme.primary.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? AppTheme.primary.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                icon,
                color: isSelected ? AppTheme.primary : AppTheme.textHint,
                size: 16,
              ),
              const SizedBox(width: 6),
              Text(
                label,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: isSelected ? AppTheme.primary : AppTheme.textSecondary,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
            ],
          ),
        ).animate(controller: _categoriesController)
          .fadeIn(delay: Duration(milliseconds: 300)),
      ),
    );
  }

  Widget _buildTemperatureChip(String temperature, String label) {
    final isSelected = _selectedTemperature == temperature;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedTemperature = temperature;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          radius: 16,
          backgroundColor: isSelected 
            ? AppTheme.accent.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? AppTheme.accent.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: isSelected ? AppTheme.accent : AppTheme.textSecondary,
              fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildProductsSection() {
    final filteredProducts = _products.where((product) {
      final matchesCategory = _selectedCategory == 'all' || product.category == _selectedCategory;
      final matchesTemperature = _selectedTemperature == 'all' || product.temperature.name == _selectedTemperature;
      return matchesCategory && matchesTemperature;
    }).toList();

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'الخدمات المتاحة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                '${filteredProducts.length} خدمة',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          GridView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              crossAxisSpacing: 16,
              mainAxisSpacing: 16,
              childAspectRatio: 0.75,
            ),
            itemCount: filteredProducts.length,
            itemBuilder: (context, index) {
              final product = filteredProducts[index];
              return _buildProductCard(product, index);
            },
          ),
        ],
      ),
    );
  }

  Widget _buildProductCard(OneClickProduct product, int index) {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: product.color.withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Popular badge
          if (product.isPopular)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: AppTheme.error.withOpacity(0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                'الأكثر طلباً',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.error,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          
          // Icon
          Container(
            width: 50,
            height: 50,
            decoration: BoxDecoration(
              color: product.color.withOpacity(0.1),
              borderRadius: BorderRadius.circular(25),
            ),
            child: Icon(
              product.icon,
              color: product.color,
              size: 28,
            ),
          ),
          
          const SizedBox(height: 12),
          
          // Product name
          Text(
            product.name,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.w600,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          
          const SizedBox(height: 8),
          
          // Delivery time
          Row(
            children: [
              Icon(
                Icons.timer,
                color: AppTheme.textSecondary,
                size: 14,
              ),
              const SizedBox(width: 4),
              Text(
                product.deliveryTime,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const Spacer(),
          
          // Price and action
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                '${product.price.toStringAsFixed(0)} ريال',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: product.color,
                  fontWeight: FontWeight.bold,
                ),
              ),
              GlowingButton(
                text: 'طلب',
                onPressed: () => _addToCart(product),
                color: product.color,
                height: 32,
                width: 60,
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _productsController)
      .fadeIn(delay: Duration(milliseconds: index * 100))
      .scale(begin: const Offset(0.9, 0.9), end: const Offset(1, 1));
  }

  Widget _buildFloatingCartButton() {
    return Container(
      margin: const EdgeInsets.all(20),
      child: GlassContainer(
        width: 60,
        height: 60,
        radius: 30,
        backgroundColor: AppTheme.primary.withOpacity(0.2),
        borderColor: AppTheme.primary.withOpacity(0.4),
        boxShadow: AppShadows.glowing(AppTheme.primary),
        child: Stack(
          children: [
            Center(
              child: IconButton(
                onPressed: _toggleCart,
                icon: const Icon(
                  Icons.shopping_cart,
                  color: AppTheme.primary,
                  size: 28,
                ),
              ),
            ),
            if (_cartItems.isNotEmpty)
              Positioned(
                top: 8,
                right: 8,
                child: Container(
                  width: 20,
                  height: 20,
                  decoration: BoxDecoration(
                    color: AppTheme.error,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Center(
                    child: Text(
                      '${_cartItems.length}',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ),
          ],
        ),
      ).animate(controller: _cartController)
        .scale(begin: const Offset(1, 1), end: const Offset(1.1, 1.1))
        .then()
        .scale(begin: const Offset(1.1, 1.1), end: const Offset(1, 1)),
    );
  }

  Widget _buildCartOverlay() {
    return Positioned(
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      child: GestureDetector(
        onTap: _toggleCart,
        child: Container(
          color: Colors.black.withOpacity(0.5),
          child: Center(
            child: GlassContainer(
              margin: const EdgeInsets.all(20),
              padding: const EdgeInsets.all(24),
              radius: 24,
              backgroundColor: AppTheme.background.withOpacity(0.95),
              borderColor: AppTheme.primary.withOpacity(0.3),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  // Header
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        'سلة الطلبات',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: AppTheme.textPrimary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      IconButton(
                        onPressed: _toggleCart,
                        icon: const Icon(
                          Icons.close,
                          color: AppTheme.textHint,
                        ),
                      ),
                    ],
                  ),
                  
                  const SizedBox(height: 20),
                  
                  // Cart items
                  if (_cartItems.isEmpty)
                    Column(
                      children: [
                        Icon(
                          Icons.shopping_cart_outlined,
                          color: AppTheme.textHint,
                          size: 64,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'السلة فارغة',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            color: AppTheme.textHint,
                          ),
                        ),
                      ],
                    )
                  else
                    Column(
                      children: [
                        // Cart items list
                        ..._cartItems.asMap().entries.map((entry) {
                          final index = entry.key;
                          final item = entry.value;
                          return _buildCartItem(item, index);
                        }),
                        
                        const SizedBox(height: 20),
                        
                        // Total
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              'الإجمالي:',
                              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                color: AppTheme.textPrimary,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            Text(
                              '${_getCartTotal().toStringAsFixed(0)} ريال',
                              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                color: AppTheme.primary,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                        
                        const SizedBox(height: 20),
                        
                        // Checkout button
                        GlowingButton(
                          text: 'إتمام الطلب (${_cartItems.length})',
                          onPressed: _checkout,
                          color: AppTheme.primary,
                          icon: Icons.check_circle,
                          height: 56,
                        ),
                      ],
                    ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildCartItem(CartItem item, int index) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      radius: 12,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.textHint.withOpacity(0.2),
      child: Row(
        children: [
          // Product info
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.product.name,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  '${item.product.price.toStringAsFixed(0)} ريال',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
              ],
            ),
          ),
          
          // Quantity controls
          Row(
            children: [
              GlassContainer(
                width: 30,
                height: 30,
                radius: 15,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: IconButton(
                  onPressed: () => _updateQuantity(item, -1),
                  icon: const Icon(
                    Icons.remove,
                    color: AppTheme.textHint,
                    size: 16,
                  ),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                '${item.quantity}',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(width: 8),
              GlassContainer(
                width: 30,
                height: 30,
                radius: 15,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: IconButton(
                  onPressed: () => _updateQuantity(item, 1),
                  icon: const Icon(
                    Icons.add,
                    color: AppTheme.textHint,
                    size: 16,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  // Methods
  void _toggleCart() {
    setState(() {
      _isCartVisible = !_isCartVisible;
    });
    if (_isCartVisible) {
      _cartController.forward();
    } else {
      _cartController.reverse();
    }
  }

  void _addToCart(OneClickProduct product) {
    setState(() {
      final existingItem = _cartItems.firstWhere(
        (item) => item.product.id == product.id,
        orElse: () => CartItem(product: product, quantity: 0),
      );
      
      if (existingItem.quantity == 0) {
        _cartItems.add(CartItem(product: product, quantity: 1));
      } else {
        existingItem.quantity++;
      }
    });
    
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        backgroundColor: AppTheme.success,
        content: Text('تمت إضافة ${product.name} إلى السلة'),
        duration: const Duration(seconds: 2),
      ),
    );
  }

  void _updateQuantity(CartItem item, int delta) {
    setState(() {
      item.quantity += delta;
      if (item.quantity <= 0) {
        _cartItems.remove(item);
      }
    });
  }

  double _getCartTotal() {
    return _cartItems.fold(0, (total, item) => total + (item.product.price * item.quantity));
  }

  void _checkout() {
    // Navigate to checkout
    Navigator.pushNamed(context, '/shipment/cart');
  }
}

// Data models
class OneClickProduct {
  String id;
  String name;
  String category;
  TemperatureType temperature;
  double price;
  double weight;
  String deliveryTime;
  String description;
  IconData icon;
  Color color;
  bool isPopular;

  OneClickProduct({
    required this.id,
    required this.name,
    required this.category,
    required this.temperature,
    required this.price,
    required this.weight,
    required this.deliveryTime,
    required this.description,
    required this.icon,
    required this.color,
    required this.isPopular,
  });
}

class CartItem {
  OneClickProduct product;
  int quantity;

  CartItem({
    required this.product,
    required this.quantity,
  });
}

enum TemperatureType { ambient, chilled, frozen, cold }
