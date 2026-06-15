// ============================================
// 💰 Accounting & Billing System
// Automatic Invoice Generation & Debt Management
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class AccountingBillingSystem extends StatefulWidget {
  const AccountingBillingSystem({super.key});

  @override
  State<AccountingBillingSystem> createState() => _AccountingBillingSystemState();
}

class _AccountingBillingSystemState extends State<AccountingBillingSystem>
    with TickerProviderStateMixin {
  late AnimationController _dashboardController;
  late AnimationController _invoicesController;
  late AnimationController _paymentsController;
  
  // Data
  List<Invoice> _invoices = [];
  List<Payment> _payments = [];
  List<CustomerAccount> _customerAccounts = [];
  String _selectedPeriod = 'current';
  String _selectedStatus = 'all';
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadData();
  }

  void _initializeAnimations() {
    _dashboardController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _invoicesController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _paymentsController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _dashboardController.forward();
    _invoicesController.forward();
    _paymentsController.forward();
  }

  void _loadData() {
    _invoices = [
      Invoice(
        id: 'INV-001',
        customerId: 'CUST-001',
        customerName: 'شركة النقل السريع',
        amount: 2500.0,
        dueDate: DateTime.now().add(const Duration(days: 15)),
        issueDate: DateTime.now().subtract(const Duration(days: 30)),
        status: InvoiceStatus.paid,
        trips: [
          Trip(id: 'TRIP-001', route: 'الرياض → جدة', date: DateTime.now().subtract(const Duration(days: 28)), amount: 1500.0),
          Trip(id: 'TRIP-002', route: 'جدة → الدمام', date: DateTime.now().subtract(const Duration(days: 25)), amount: 1000.0),
        ],
      ),
      Invoice(
        id: 'INV-002',
        customerId: 'CUST-002',
        customerName: 'مستودع الأدوية الطبية',
        amount: 3200.0,
        dueDate: DateTime.now().add(const Duration(days: 7)),
        issueDate: DateTime.now().subtract(const Duration(days: 23)),
        status: InvoiceStatus.pending,
        trips: [
          Trip(id: 'TRIP-003', route: 'جدة → الرياض', date: DateTime.now().subtract(const Duration(days: 22)), amount: 1800.0),
          Trip(id: 'TRIP-004', route: 'الرياض → القصيم', date: DateTime.now().subtract(const Duration(days: 20)), amount: 1400.0),
        ],
      ),
      Invoice(
        id: 'INV-003',
        customerId: 'CUST-003',
        customerName: 'شركة الأغذية الفاخرة',
        amount: 1800.0,
        dueDate: DateTime.now().subtract(const Duration(days: 5)),
        issueDate: DateTime.now().subtract(const Duration(days: 35)),
        status: InvoiceStatus.overdue,
        trips: [
          Trip(id: 'TRIP-005', route: 'مكة → المدينة', date: DateTime.now().subtract(const Duration(days: 34)), amount: 1800.0),
        ],
      ),
      Invoice(
        id: 'INV-004',
        customerId: 'CUST-004',
        customerName: 'مصنع المواد الكيميائية',
        amount: 4500.0,
        dueDate: DateTime.now().add(const Duration(days: 30)),
        issueDate: DateTime.now().subtract(const Duration(days: 5)),
        status: InvoiceStatus.draft,
        trips: [
          Trip(id: 'TRIP-006', route: 'القصيم → حائل', date: DateTime.now().subtract(const Duration(days: 4)), amount: 2500.0),
          Trip(id: 'TRIP-007', route: 'حائل → تبوك', date: DateTime.now().subtract(const Duration(days: 2)), amount: 2000.0),
        ],
      ),
    ];

    _payments = [
      Payment(
        id: 'PAY-001',
        invoiceId: 'INV-001',
        customerId: 'CUST-001',
        amount: 2500.0,
        paymentDate: DateTime.now().subtract(const Duration(days: 10)),
        method: PaymentMethod.bankTransfer,
        status: PaymentStatus.completed,
        reference: 'BT-2024-001',
      ),
      Payment(
        id: 'PAY-002',
        invoiceId: 'INV-002',
        customerId: 'CUST-002',
        amount: 1600.0,
        paymentDate: DateTime.now().subtract(const Duration(days: 3)),
        method: PaymentMethod.creditCard,
        status: PaymentStatus.completed,
        reference: 'CC-2024-002',
      ),
      Payment(
        id: 'PAY-003',
        invoiceId: 'INV-002',
        customerId: 'CUST-002',
        amount: 800.0,
        paymentDate: DateTime.now().subtract(const Duration(days: 1)),
        method: PaymentMethod.cash,
        status: PaymentStatus.pending,
        reference: 'CSH-2024-003',
      ),
    ];

    _customerAccounts = [
      CustomerAccount(
        id: 'CUST-001',
        name: 'شركة النقل السريع',
        balance: 0.0,
        creditLimit: 10000.0,
        totalInvoiced: 25000.0,
        totalPaid: 25000.0,
        lastPaymentDate: DateTime.now().subtract(const Duration(days: 10)),
        status: AccountStatus.active,
      ),
      CustomerAccount(
        id: 'CUST-002',
        name: 'مستودع الأدوية الطبية',
        balance: 800.0,
        creditLimit: 15000.0,
        totalInvoiced: 18000.0,
        totalPaid: 17200.0,
        lastPaymentDate: DateTime.now().subtract(const Duration(days: 3)),
        status: AccountStatus.active,
      ),
      CustomerAccount(
        id: 'CUST-003',
        name: 'شركة الأغذية الفاخرة',
        balance: 1800.0,
        creditLimit: 8000.0,
        totalInvoiced: 12000.0,
        totalPaid: 10200.0,
        lastPaymentDate: DateTime.now().subtract(const Duration(days: 45)),
        status: AccountStatus.overdue,
      ),
      CustomerAccount(
        id: 'CUST-004',
        name: 'مصنع المواد الكيميائية',
        balance: 4500.0,
        creditLimit: 20000.0,
        totalInvoiced: 30000.0,
        totalPaid: 25500.0,
        lastPaymentDate: DateTime.now().subtract(const Duration(days: 20)),
        status: AccountStatus.active,
      ),
    ];
  }

  @override
  void dispose() {
    _dashboardController.dispose();
    _invoicesController.dispose();
    _paymentsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: CustomScrollView(
        slivers: [
          // Dashboard Stats
          SliverToBoxAdapter(
            child: _buildDashboardStats(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Cash Flow Overview
          SliverToBoxAdapter(
            child: _buildCashFlowOverview(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Recent Invoices
          SliverToBoxAdapter(
            child: _buildRecentInvoices(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 24)),
          
          // Customer Accounts
          SliverToBoxAdapter(
            child: _buildCustomerAccounts(),
          ),
          
          const SliverPadding(padding: EdgeInsets.only(bottom: 80)),
        ],
      ),
      floatingActionButton: GlowingButton(
        text: 'فاتورة جديدة',
        onPressed: _createNewInvoice,
        color: AppTheme.primary,
        icon: Icons.add_circle,
      ),
    );
  }

  PreferredSizeWidget _buildPremiumAppBar() {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      title: Row(
        children: [
          GlassContainer(
            width: 40,
            height: 40,
            radius: 20,
            backgroundColor: AppTheme.primary.withOpacity(0.1),
            borderColor: AppTheme.primary.withOpacity(0.3),
            child: const Icon(
              Icons.account_balance,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'المحاسبة والفواتير',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: _exportReports,
          icon: const Icon(Icons.download, color: AppTheme.primary),
        ),
        IconButton(
          onPressed: _showCalendar,
          icon: const Icon(Icons.calendar_today, color: AppTheme.primary),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildDashboardStats() {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'نظرة عامة مالية',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate(controller: _dashboardController)
            .fadeIn(delay: const Duration(milliseconds: 200)),
          
          const SizedBox(height: 20),
          
          // Stats Cards
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'الإيرادات الشهرية',
                  value: '${_getMonthlyRevenue().toStringAsFixed(0)}',
                  change: '+15%',
                  icon: Icons.trending_up,
                  color: AppTheme.success,
                  animationDelay: const Duration(milliseconds: 300),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'الديون المستحقة',
                  value: '${_getTotalOutstanding().toStringAsFixed(0)}',
                  change: '+3 عملاء',
                  icon: Icons.money_off,
                  color: AppTheme.error,
                  animationDelay: const Duration(milliseconds: 400),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          Row(
            children: [
              Expanded(
                child: PremiumStatCard(
                  title: 'الفواتير غير المدفوعة',
                  value: '${_getUnpaidInvoices().length}',
                  change: '2 متأخرة',
                  icon: Icons.receipt_long,
                  color: AppTheme.accent,
                  animationDelay: const Duration(milliseconds: 500),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: PremiumStatCard(
                  title: 'متوسط الدفع',
                  value: '${_getAveragePaymentDays()} يوم',
                  change: '-2 يوم',
                  icon: Icons.timer,
                  color: AppTheme.primary,
                  animationDelay: const Duration(milliseconds: 600),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCashFlowOverview() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GlassContainer(
        padding: const EdgeInsets.all(20),
        radius: 20,
        backgroundColor: Colors.white.withOpacity(0.05),
        borderColor: AppTheme.primary.withOpacity(0.2),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'تدفق النقدية',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.bold,
              ),
            ),
            
            const SizedBox(height: 20),
            
            // Cash Flow Chart Placeholder
            Container(
              height: 200,
              decoration: BoxDecoration(
                color: AppTheme.surface.withOpacity(0.3),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppTheme.textHint.withOpacity(0.2)),
              ),
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.insert_chart,
                      color: AppTheme.textHint,
                      size: 48,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'رسم بياني لتدفق النقدية',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textHint,
                      ),
                    ),
                    Text(
                      'الإيرادات والمصروفات الشهرية',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            
            const SizedBox(height: 20),
            
            // Cash Flow Summary
            Row(
              children: [
                Expanded(
                child: _buildCashFlowItem(
                  'الإيرادات',
                  '${_getMonthlyRevenue().toStringAsFixed(0)} ريال',
                  AppTheme.success,
                  Icons.arrow_upward,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildCashFlowItem(
                  'المصروفات',
                  '${_getMonthlyExpenses().toStringAsFixed(0)} ريال',
                  AppTheme.error,
                  Icons.arrow_downward,
                ),
                ),
                const SizedBox(width: 12),
                Expanded(
                child: _buildCashFlowItem(
                  'صافي التدفق',
                  '${(_getMonthlyRevenue() - _getMonthlyExpenses()).toStringAsFixed(0)} ريال',
                  AppTheme.primary,
                  Icons.account_balance_wallet,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _dashboardController)
      .fadeIn(delay: const Duration(milliseconds: 700));
  }

  Widget _buildCashFlowItem(String label, String value, Color color, IconData icon) {
    return GlassContainer(
      padding: const EdgeInsets.all(12),
      radius: 12,
      backgroundColor: color.withOpacity(0.1),
      borderColor: color.withOpacity(0.3),
      child: Column(
        children: [
          Icon(icon, color: color, size: 20),
          const SizedBox(height: 8),
          Text(
            value,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: color,
              fontWeight: FontWeight.bold,
            ),
          ),
          Text(
            label,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRecentInvoices() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'الفواتير الحديثة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Row(
                children: [
                  _buildPeriodChip('current', 'الشهر الحالي'),
                  _buildPeriodChip('last', 'الشهر الماضي'),
                  _buildPeriodChip('quarter', 'الربع'),
                  _buildPeriodChip('year', 'السنة'),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Invoice List
          ..._getFilteredInvoices().map((invoice) => _buildInvoiceCard(invoice)),
        ],
      ),
    );
  }

  Widget _buildPeriodChip(String period, String label) {
    final isSelected = _selectedPeriod == period;
    
    return Padding(
      padding: const EdgeInsets.only(left: 8),
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedPeriod = period;
          });
        },
        child: GlassContainer(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          radius: 12,
          backgroundColor: isSelected 
            ? AppTheme.primary.withOpacity(0.2)
            : Colors.white.withOpacity(0.05),
          borderColor: isSelected 
            ? AppTheme.primary.withOpacity(0.4)
            : AppTheme.textHint.withOpacity(0.2),
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: isSelected ? AppTheme.primary : AppTheme.textSecondary,
              fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildInvoiceCard(Invoice invoice) {
    Color statusColor = _getInvoiceStatusColor(invoice.status);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: statusColor.withOpacity(0.2),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getInvoiceStatusText(invoice.status),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: statusColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                invoice.id,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Customer and Amount
          Row(
            children: [
              Icon(
                Icons.business,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                invoice.customerName,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppTheme.textPrimary,
                ),
              ),
              const Spacer(),
              Text(
                '${invoice.amount.toStringAsFixed(0)} ريال',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppTheme.primary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Dates and Trips
          Row(
            children: [
              Icon(
                Icons.calendar_today,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                'تاريخ الإصدار: ${_formatDate(invoice.issueDate)}',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Icon(
                Icons.event,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                'تاريخ الاستحقاق: ${_formatDate(invoice.dueDate)}',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: invoice.dueDate.isBefore(DateTime.now()) 
                    ? AppTheme.error 
                    : AppTheme.textSecondary,
                  fontWeight: invoice.dueDate.isBefore(DateTime.now()) 
                    ? FontWeight.w600 
                    : FontWeight.normal,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Trips Summary
          Row(
            children: [
              Icon(
                Icons.local_shipping,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                '${invoice.trips.length} رحلات',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Text(
                invoice.trips.map((trip) => trip.route).join(' • '),
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
          
          // Actions
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: GlowingButton(
                  text: 'عرض',
                  onPressed: () => _viewInvoiceDetails(invoice),
                  color: AppTheme.primary,
                  height: 36,
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: GlowingButton(
                  text: 'تحصيل',
                  onPressed: () => _collectPayment(invoice),
                  color: AppTheme.success,
                  height: 36,
                ),
              ),
              const SizedBox(width: 8),
              GlassContainer(
                width: 36,
                height: 36,
                radius: 18,
                backgroundColor: Colors.white.withOpacity(0.05),
                borderColor: AppTheme.textHint.withOpacity(0.2),
                child: IconButton(
                  onPressed: () => _showInvoiceOptions(invoice),
                  icon: const Icon(
                    Icons.more_vert,
                    color: AppTheme.textHint,
                    size: 16,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _invoicesController)
      .fadeIn(delay: const Duration(milliseconds: 200))
      .slideX(begin: -0.1, end: 0);
  }

  Widget _buildCustomerAccounts() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'حسابات العملاء',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Customer Accounts List
          ..._customerAccounts.map((account) => _buildCustomerAccountCard(account)),
        ],
      ),
    );
  }

  Widget _buildCustomerAccountCard(CustomerAccount account) {
    Color statusColor = _getAccountStatusColor(account.status);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: statusColor.withOpacity(0.2),
      child: Column(
        children: [
          // Header
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getAccountStatusText(account.status),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: statusColor,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const Spacer(),
              Text(
                account.id,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textHint,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Customer Name and Balance
          Row(
            children: [
              Icon(
                Icons.business,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                account.name,
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Spacer(),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    '${account.balance.toStringAsFixed(0)} ريال',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      color: account.balance > 0 ? AppTheme.error : AppTheme.success,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    'الرصيد الحالي',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Credit Limit Usage
          Row(
            children: [
              Icon(
                Icons.credit_card,
                color: AppTheme.textSecondary,
                size: 16,
              ),
              const SizedBox(width: 8),
              Text(
                'حد الائتمان: ${account.creditLimit.toStringAsFixed(0)} ريال',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
              const Spacer(),
              Text(
                '${((account.totalInvoiced / account.creditLimit) * 100).toStringAsFixed(0)}% مستخدم',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          // Progress Bar
          Container(
            height: 6,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.1),
              borderRadius: BorderRadius.circular(3),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerLeft,
              widthFactor: (account.totalInvoiced / account.creditLimit).clamp(0.0, 1.0),
              child: Container(
                decoration: BoxDecoration(
                  color: account.totalInvoiced / account.creditLimit > 0.8 
                    ? AppTheme.error 
                    : account.totalInvoiced / account.creditLimit > 0.6
                      ? AppTheme.accent
                      : AppTheme.success,
                  borderRadius: BorderRadius.circular(3),
                ),
              ),
            ),
          ),
          
          const SizedBox(height: 16),
          
          // Stats Row
          Row(
            children: [
              Expanded(
                child: _buildAccountStat(
                  'إجمالي الفواتير',
                  '${account.totalInvoiced.toStringAsFixed(0)}',
                  Icons.receipt_long,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildAccountStat(
                  'إجمالي المدفوع',
                  '${account.totalPaid.toStringAsFixed(0)}',
                  Icons.payment,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildAccountStat(
                  'آخر دفع',
                  _formatDate(account.lastPaymentDate),
                  Icons.history,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _paymentsController)
      .fadeIn(delay: const Duration(milliseconds: 200))
      .slideY(begin: 0.1, end: 0);
  }

  Widget _buildAccountStat(String label, String value, IconData icon) {
    return Column(
      children: [
        Icon(icon, color: AppTheme.textSecondary, size: 16),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textPrimary,
            fontWeight: FontWeight.w600,
          ),
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppTheme.textSecondary,
          ),
        ),
      ],
    );
  }

  // Helper methods
  List<Invoice> _getFilteredInvoices() {
    // Filter based on selected period
    DateTime now = DateTime.now();
    DateTime startDate;
    
    switch (_selectedPeriod) {
      case 'current':
        startDate = DateTime(now.year, now.month, 1);
        break;
      case 'last':
        startDate = DateTime(now.year, now.month - 1, 1);
        break;
      case 'quarter':
        startDate = DateTime(now.year, ((now.month - 1) ~/ 3) * 3 + 1, 1);
        break;
      case 'year':
        startDate = DateTime(now.year, 1, 1);
        break;
      default:
        startDate = DateTime(now.year, now.month, 1);
    }
    
    return _invoices.where((invoice) => invoice.issueDate.isAfter(startDate)).toList();
  }

  double _getMonthlyRevenue() {
    final now = DateTime.now();
    return _payments
        .where((payment) => 
            payment.paymentDate.month == now.month && 
            payment.paymentDate.year == now.year &&
            payment.status == PaymentStatus.completed)
        .fold(0.0, (sum, payment) => sum + payment.amount);
  }

  double _getMonthlyExpenses() {
    // This would calculate actual expenses
    return 45000.0; // Placeholder
  }

  double _getTotalOutstanding() {
    return _customerAccounts.fold(0.0, (sum, account) => sum + account.balance);
  }

  List<Invoice> _getUnpaidInvoices() {
    return _invoices.where((invoice) => 
        invoice.status == InvoiceStatus.pending || 
        invoice.status == InvoiceStatus.overdue).toList();
  }

  int _getAveragePaymentDays() {
    // Calculate average payment days
    return 18; // Placeholder
  }

  Color _getInvoiceStatusColor(InvoiceStatus status) {
    switch (status) {
      case InvoiceStatus.paid:
        return AppTheme.success;
      case InvoiceStatus.pending:
        return AppTheme.accent;
      case InvoiceStatus.overdue:
        return AppTheme.error;
      case InvoiceStatus.draft:
        return AppTheme.textHint;
      default:
        return AppTheme.textHint;
    }
  }

  String _getInvoiceStatusText(InvoiceStatus status) {
    switch (status) {
      case InvoiceStatus.paid:
        return 'مدفوعة';
      case InvoiceStatus.pending:
        return 'معلقة';
      case InvoiceStatus.overdue:
        return 'متأخرة';
      case InvoiceStatus.draft:
        return 'مسودة';
      default:
        return 'غير معروف';
    }
  }

  Color _getAccountStatusColor(AccountStatus status) {
    switch (status) {
      case AccountStatus.active:
        return AppTheme.success;
      case AccountStatus.overdue:
        return AppTheme.error;
      case AccountStatus.suspended:
        return AppTheme.accent;
      default:
        return AppTheme.textHint;
    }
  }

  String _getAccountStatusText(AccountStatus status) {
    switch (status) {
      case AccountStatus.active:
        return 'نشط';
      case AccountStatus.overdue:
        return 'متأخر';
      case AccountStatus.suspended:
        return 'معلق';
      default:
        return 'غير معروف';
    }
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }

  // Action methods
  void _createNewInvoice() {
    // Navigate to create invoice screen
  }

  void _exportReports() {
    // Export financial reports
  }

  void _showCalendar() {
    // Show payment calendar
  }

  void _viewInvoiceDetails(Invoice invoice) {
    // Navigate to invoice details
  }

  void _collectPayment(Invoice invoice) {
    // Collect payment for invoice
  }

  void _showInvoiceOptions(Invoice invoice) {
    // Show invoice options menu
  }
}

// Data models
enum InvoiceStatus { paid, pending, overdue, draft }
enum PaymentStatus { pending, completed, failed, cancelled }
enum PaymentMethod { cash, bankTransfer, creditCard, check }
enum AccountStatus { active, overdue, suspended }

class Invoice {
  String id;
  String customerId;
  String customerName;
  double amount;
  DateTime dueDate;
  DateTime issueDate;
  InvoiceStatus status;
  List<Trip> trips;

  Invoice({
    required this.id,
    required this.customerId,
    required this.customerName,
    required this.amount,
    required this.dueDate,
    required this.issueDate,
    required this.status,
    required this.trips,
  });
}

class Trip {
  String id;
  String route;
  DateTime date;
  double amount;

  Trip({
    required this.id,
    required this.route,
    required this.date,
    required this.amount,
  });
}

class Payment {
  String id;
  String invoiceId;
  String customerId;
  double amount;
  DateTime paymentDate;
  PaymentMethod method;
  PaymentStatus status;
  String reference;

  Payment({
    required this.id,
    required this.invoiceId,
    required this.customerId,
    required this.amount,
    required this.paymentDate,
    required this.method,
    required this.status,
    required this.reference,
  });
}

class CustomerAccount {
  String id;
  String name;
  double balance;
  double creditLimit;
  double totalInvoiced;
  double totalPaid;
  DateTime lastPaymentDate;
  AccountStatus status;

  CustomerAccount({
    required this.id,
    required this.name,
    required this.balance,
    required this.creditLimit,
    required this.totalInvoiced,
    required this.totalPaid,
    required this.lastPaymentDate,
    required this.status,
  });
}
