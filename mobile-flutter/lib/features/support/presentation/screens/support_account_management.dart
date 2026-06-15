// ============================================
// 🛠️ Support & Account Management
// Direct Chat System & Account Customization
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/glass_container.dart';
import '../../../../core/widgets/premium/glowing_button.dart';
import '../../../../core/widgets/premium/premium_stat_card.dart';
import '../../../../core/design_system/shadows.dart';

class SupportAccountManagement extends StatefulWidget {
  const SupportAccountManagement({super.key});

  @override
  State<SupportAccountManagement> createState() => _SupportAccountManagementState();
}

class _SupportAccountManagementState extends State<SupportAccountManagement>
    with TickerProviderStateMixin {
  late AnimationController _supportController;
  late AnimationController _accountController;
  late AnimationController _chatController;
  
  // Data
  List<SupportTicket> _tickets = [];
  List<ChatMessage> _chatMessages = [];
  List<AccountSetting> _accountSettings = [];
  UserProfile _userProfile = UserProfile.empty();
  
  // State
  bool _isChatActive = false;
  bool _isTyping = false;
  final TextEditingController _messageController = TextEditingController();
  String _selectedCategory = 'all';
  
  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadData();
  }

  void _initializeAnimations() {
    _supportController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    
    _accountController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    
    _chatController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );
    
    _supportController.forward();
    _accountController.forward();
  }

  void _loadData() {
    _tickets = [
      SupportTicket(
        id: 'TKT-001',
        title: 'مشكلة في تتبع الشحنة',
        description: 'لا يمكنني تتبع شحنتي #EDH-1001',
        category: TicketCategory.shipment,
        priority: TicketPriority.high,
        status: TicketStatus.open,
        createdAt: DateTime.now().subtract(const Duration(hours: 2)),
        lastUpdate: DateTime.now().subtract(const Duration(minutes: 30)),
        assignedTo: 'أحمد محمد',
        messages: 3,
      ),
      SupportTicket(
        id: 'TKT-002',
        title: 'استفسار عن الأسعار',
        description: 'أريد معرفة أسعار الشحنات الدولية',
        category: TicketCategory.billing,
        priority: TicketPriority.normal,
        status: TicketStatus.inProgress,
        createdAt: DateTime.now().subtract(const Duration(hours: 6)),
        lastUpdate: DateTime.now().subtract(const Duration(hours: 1)),
        assignedTo: 'سارة أحمد',
        messages: 5,
      ),
      SupportTicket(
        id: 'TKT-003',
        title: 'مشكلة في الدخول',
        description: 'لا أستطيع الدخول إلى حسابي',
        category: TicketCategory.technical,
        priority: TicketPriority.urgent,
        status: TicketStatus.resolved,
        createdAt: DateTime.now().subtract(const Duration(days: 1)),
        lastUpdate: DateTime.now().subtract(const Duration(hours: 12)),
        assignedTo: 'خالد علي',
        messages: 8,
      ),
    ];

    _chatMessages = [
      ChatMessage(
        id: 'MSG-001',
        text: 'مرحباً! كيف يمكنني مساعدتك اليوم؟',
        sender: 'support',
        senderName: 'الدعم الفني',
        timestamp: DateTime.now().subtract(const Duration(minutes: 5)),
        isSupport: true,
      ),
      ChatMessage(
        id: 'MSG-002',
        text: 'لدي مشكلة في تتبع شحنتي',
        sender: 'user',
        senderName: 'أنت',
        timestamp: DateTime.now().subtract(const Duration(minutes: 4)),
        isSupport: false,
      ),
      ChatMessage(
        id: 'MSG-003',
        text: 'بالتأكيد، سأقوم بمساعدتك في حل هذه المشكلة. هل يمكنك تزويدي برقم الشحنة؟',
        sender: 'support',
        senderName: 'الدعم الفني',
        timestamp: DateTime.now().subtract(const Duration(minutes: 3)),
        isSupport: true,
      ),
      ChatMessage(
        id: 'MSG-004',
        text: 'رقم الشحنة هو EDH-1001',
        sender: 'user',
        senderName: 'أنت',
        timestamp: DateTime.now().subtract(const Duration(minutes: 2)),
        isSupport: false,
      ),
    ];

    _accountSettings = [
      AccountSetting(
        category: 'الملف الشخصي',
        settings: [
          SettingItem(
            title: 'اسم المستخدم',
            value: 'محمد أحمد',
            type: SettingType.text,
            icon: Icons.person,
          ),
          SettingItem(
            title: 'البريد الإلكتروني',
            value: 'mohammed@edham.com',
            type: SettingType.email,
            icon: Icons.email,
          ),
          SettingItem(
            title: 'رقم الهاتف',
            value: '+966 50 123 4567',
            type: SettingType.phone,
            icon: Icons.phone,
          ),
        ],
      ),
      AccountSetting(
        category: 'الإعدادات',
        settings: [
          SettingItem(
            title: 'اللغة',
            value: 'العربية',
            type: SettingType.dropdown,
            icon: Icons.language,
            options: ['العربية', 'English'],
          ),
          SettingItem(
            title: 'المنطقة الزمنية',
            value: 'GMT+3',
            type: SettingType.dropdown,
            icon: Icons.access_time,
            options: ['GMT+3', 'GMT+4', 'GMT+2'],
          ),
          SettingItem(
            title: 'العملة',
            value: 'ريال سعودي',
            type: SettingType.dropdown,
            icon: Icons.attach_money,
            options: ['ريال سعودي', 'درهم إماراتي', 'دينار كويتي'],
          ),
        ],
      ),
      AccountSetting(
        category: 'الخصوصية والأمان',
        settings: [
          SettingItem(
            title: 'المصادقة الثنائية',
            value: 'مفعلة',
            type: SettingType.toggle,
            icon: Icons.security,
          ),
          SettingItem(
            title: 'الإشعارات',
            value: 'مفعلة',
            type: SettingType.toggle,
            icon: Icons.notifications,
          ),
          SettingItem(
            title: 'مشاركة الموقع',
            value: 'معطلة',
            type: SettingType.toggle,
            icon: Icons.location_on,
          ),
        ],
      ),
    ];

    _userProfile = UserProfile(
      id: 'USR-001',
      name: 'محمد أحمد',
      email: 'mohammed@edham.com',
      phone: '+966 50 123 4567',
      avatar: 'https://example.com/avatar.jpg',
      role: 'عميل',
      joinDate: DateTime.now().subtract(const Duration(days: 365)),
      lastLogin: DateTime.now().subtract(const Duration(hours: 2)),
      totalShipments: 45,
      completedShipments: 42,
      averageRating: 4.8,
      memberLevel: MemberLevel.gold,
    );
  }

  @override
  void dispose() {
    _supportController.dispose();
    _accountController.dispose();
    _chatController.dispose();
    _messageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      appBar: _buildPremiumAppBar(),
      body: Column(
        children: [
          // Tab Bar
          _buildTabBar(),
          
          // Tab Content
          Expanded(
            child: TabBarView(
              children: [
                _buildSupportTab(),
                _buildAccountTab(),
              ],
            ),
          ),
        ],
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
              Icons.headset_mic,
              color: AppTheme.primary,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Text(
            'الدعم الفني والحساب',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
      actions: [
        IconButton(
          onPressed: _showHelp,
          icon: const Icon(Icons.help_outline, color: AppTheme.primary),
        ),
        const SizedBox(width: 20),
      ],
    );
  }

  Widget _buildTabBar() {
    return GlassContainer(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(8),
      radius: 20,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.primary.withOpacity(0.2),
      child: TabBar(
        indicator: BoxDecoration(
          color: AppTheme.primary.withOpacity(0.2),
          borderRadius: BorderRadius.circular(16),
        ),
        labelColor: AppTheme.primary,
        unselectedLabelColor: AppTheme.textSecondary,
        tabs: const [
          Tab(
            icon: Icon(Icons.support_agent),
            text: 'الدعم الفني',
          ),
          Tab(
            icon: Icon(Icons.account_circle),
            text: 'إدارة الحساب',
          ),
        ],
      ),
    );
  }

  Widget _buildSupportTab() {
    return Column(
      children: [
        // Support Stats
        _buildSupportStats(),
        
        // Support Content
        Expanded(
          child: _isChatActive ? _buildChatInterface() : _buildSupportOptions(),
        ),
      ],
    );
  }

  Widget _buildSupportStats() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Row(
        children: [
          Expanded(
            child: PremiumStatCard(
              title: 'التذاكر المفتوحة',
              value: '${_tickets.where((t) => t.status == TicketStatus.open).length}',
              change: '2 عاجلة',
              icon: Icons.support_agent,
              color: AppTheme.primary,
              animationDelay: const Duration(milliseconds: 200),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: PremiumStatCard(
              title: 'متوسط وقت الاستجابة',
              value: '5 دقائق',
              change: 'سريع',
              icon: Icons.timer,
              color: AppTheme.success,
              animationDelay: const Duration(milliseconds: 400),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSupportOptions() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Quick Actions
          Text(
            'خيارات سريعة',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ).animate(controller: _supportController)
            .fadeIn(delay: const Duration(milliseconds: 200)),
          
          const SizedBox(height: 16),
          
          Row(
            children: [
              Expanded(
                child: _buildQuickAction(
                  'محادثة فورية',
                  Icons.chat,
                  AppTheme.primary,
                  () => _startChat(),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildQuickAction(
                  'تذكرة جديدة',
                  Icons.add_ticket,
                  AppTheme.accent,
                  () => _createTicket(),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 24),
          
          // Recent Tickets
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'التذاكر الأخيرة',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: AppTheme.textPrimary,
                  fontWeight: FontWeight.bold,
                ),
              ),
              TextButton(
                onPressed: _viewAllTickets,
                child: Text(
                  'عرض الكل',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.primary,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 16),
          
          // Ticket List
          ..._tickets.map((ticket) => _buildTicketCard(ticket)),
          
          const SizedBox(height: 24),
          
          // FAQ Section
          Text(
            'الأسئلة الشائعة',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 16),
          
          ..._buildFAQItems(),
        ],
      ),
    );
  }

  Widget _buildQuickAction(String title, IconData icon, Color color, VoidCallback onTap) {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 16,
      backgroundColor: color.withOpacity(0.1),
      borderColor: color.withOpacity(0.3),
      child: InkWell(
        onTap: onTap,
        child: Column(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                color: color.withOpacity(0.2),
                borderRadius: BorderRadius.circular(30),
              ),
              child: Icon(icon, color: color, size: 30),
            ),
            const SizedBox(height: 12),
            Text(
              title,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppTheme.textPrimary,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ),
    ).animate(controller: _supportController)
      .fadeIn(delay: const Duration(milliseconds: 300))
      .scale(begin: const Offset(0.9, 0.9), end: const Offset(1, 1));
  }

  Widget _buildTicketCard(SupportTicket ticket) {
    Color statusColor = _getTicketStatusColor(ticket.status);
    Color priorityColor = _getTicketPriorityColor(ticket.priority);
    
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: statusColor.withOpacity(0.2),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: priorityColor.withOpacity(0.2),
              borderRadius: BorderRadius.circular(20),
            ),
            child: Icon(
              _getTicketCategoryIcon(ticket.category),
              color: priorityColor,
              size: 20,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      ticket.title,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const Spacer(),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: statusColor.withOpacity(0.2),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        _getTicketStatusText(ticket.status),
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: statusColor,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  ticket.description,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: AppTheme.textSecondary,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Text(
                      ticket.assignedTo,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textHint,
                      ),
                    ),
                    const Spacer(),
                    Text(
                      '${ticket.messages} رسائل',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textHint,
                      ),
                    ),
                    const Spacer(),
                    Text(
                      _formatTime(ticket.lastUpdate),
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.textHint,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    ).animate().fadeIn(delay: const Duration(milliseconds: 200)).slideX(begin: -0.1, end: 0);
  }

  List<Widget> _buildFAQItems() {
    final faqItems = [
      FAQItem(
        question: 'كيف يمكنني تتبع شحنتي؟',
        answer: 'يمكنك تتبع شحنتك من خلال تطبيق الموبايل أو الموقع الإلكتروني باستخدام رقم الشحنة.',
      ),
      FAQItem(
        question: 'ما هي طرق الدفع المتاحة؟',
        answer: 'نقبل الدفع عبر البطاقات الائتمانية، التحويل البنكي، والدفع عند الاستلام.',
      ),
      FAQItem(
        question: 'كيف يمكنني تغيير عنوان التوصيل؟',
        answer: 'يمكنك تغيير عنوان التوصيل قبل بدء الرحلة من خلال قسم الشحنات في حسابك.',
      ),
    ];
    
    return faqItems.map((faq) => _buildFAQItem(faq)).toList();
  }

  Widget _buildFAQItem(FAQItem faq) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.textHint.withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(Icons.help_outline, color: AppTheme.primary, size: 20),
              const SizedBox(width: 12),
              Expanded(
                child: Text(
                  faq.question,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              Icon(Icons.expand_more, color: AppTheme.textHint),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            faq.answer,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppTheme.textSecondary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildChatInterface() {
    return Column(
      children: [
        // Chat Header
        GlassContainer(
          padding: const EdgeInsets.all(16),
          margin: const EdgeInsets.all(20),
          radius: 16,
          backgroundColor: AppTheme.primary.withOpacity(0.1),
          borderColor: AppTheme.primary.withOpacity(0.3),
          child: Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: AppTheme.primary.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: const Icon(Icons.support_agent, color: AppTheme.primary),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'الدعم الفني',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text(
                      _isTyping ? 'يكتب...' : 'متصل',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: AppTheme.success,
                      ),
                    ),
                  ],
                ),
              ),
              IconButton(
                onPressed: () => setState(() => _isChatActive = false),
                icon: const Icon(Icons.close, color: AppTheme.textHint),
              ),
            ],
          ),
        ),
        
        // Messages
        Expanded(
          child: ListView.builder(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            itemCount: _chatMessages.length,
            itemBuilder: (context, index) {
              final message = _chatMessages[index];
              return _buildChatMessage(message);
            },
          ),
        ),
        
        // Message Input
        GlassContainer(
          margin: const EdgeInsets.all(20),
          padding: const EdgeInsets.all(16),
          radius: 20,
          backgroundColor: Colors.white.withOpacity(0.05),
          borderColor: AppTheme.primary.withOpacity(0.2),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _messageController,
                  style: const TextStyle(color: AppTheme.textPrimary),
                  decoration: InputDecoration(
                    hintText: 'اكتب رسالتك...',
                    hintStyle: const TextStyle(color: AppTheme.textHint),
                    border: InputBorder.none,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              GlowingButton(
                text: 'إرسال',
                onPressed: _sendMessage,
                color: AppTheme.primary,
                height: 40,
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildChatMessage(ChatMessage message) {
    final isSupport = message.isSupport;
    
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      child: Row(
        mainAxisAlignment: isSupport ? MainAxisAlignment.start : MainAxisAlignment.end,
        children: [
          if (isSupport) ...[
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: AppTheme.primary.withOpacity(0.2),
                borderRadius: BorderRadius.circular(16),
              ),
              child: const Icon(Icons.support_agent, color: AppTheme.primary, size: 16),
            ),
            const SizedBox(width: 8),
          ],
          Flexible(
            child: GlassContainer(
              padding: const EdgeInsets.all(12),
              radius: 16,
              backgroundColor: isSupport 
                ? AppTheme.primary.withOpacity(0.1)
                : AppTheme.success.withOpacity(0.1),
              borderColor: isSupport 
                ? AppTheme.primary.withOpacity(0.3)
                : AppTheme.success.withOpacity(0.3),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    message.senderName,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: isSupport ? AppTheme.primary : AppTheme.success,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    message.text,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.textPrimary,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    _formatTime(message.timestamp),
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textHint,
                    ),
                  ),
                ],
              ),
            ),
          ),
          if (!isSupport) ...[
            const SizedBox(width: 8),
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: AppTheme.success.withOpacity(0.2),
                borderRadius: BorderRadius.circular(16),
              ),
              child: const Icon(Icons.person, color: AppTheme.success, size: 16),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildAccountTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // User Profile Card
          _buildUserProfileCard(),
          
          const SizedBox(height: 24),
          
          // Account Settings
          ..._accountSettings.map((setting) => _buildAccountSetting(setting)),
          
          const SizedBox(height: 24),
          
          // Danger Zone
          _buildDangerZone(),
        ],
      ),
    );
  }

  Widget _buildUserProfileCard() {
    return GlassContainer(
      padding: const EdgeInsets.all(20),
      radius: 20,
      backgroundColor: AppTheme.primary.withOpacity(0.1),
      borderColor: AppTheme.primary.withOpacity(0.3),
      child: Column(
        children: [
          Row(
            children: [
              Container(
                width: 80,
                height: 80,
                decoration: BoxDecoration(
                  color: AppTheme.primary.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(40),
                ),
                child: const Icon(Icons.person, color: AppTheme.primary, size: 40),
              ),
              const SizedBox(width: 20),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _userProfile.name,
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        color: AppTheme.textPrimary,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _userProfile.email,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: _getMemberLevelColor(_userProfile.memberLevel).withOpacity(0.2),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        _getMemberLevelText(_userProfile.memberLevel),
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: _getMemberLevelColor(_userProfile.memberLevel),
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 20),
          
          // Stats
          Row(
            children: [
              Expanded(
                child: _buildProfileStat(
                  'إجمالي الشحنات',
                  '${_userProfile.totalShipments}',
                  Icons.local_shipping,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildProfileStat(
                  'الشحنات المكتملة',
                  '${_userProfile.completedShipments}',
                  Icons.check_circle,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildProfileStat(
                  'متوسط التقييم',
                  '${_userProfile.averageRating.toStringAsFixed(1)}',
                  Icons.star,
                ),
              ),
            ],
          ),
        ],
      ),
    ).animate(controller: _accountController)
      .fadeIn()
      .scale(begin: const Offset(0.9, 0.9), end: const Offset(1, 1));
  }

  Widget _buildProfileStat(String label, String value, IconData icon) {
    return Column(
      children: [
        Icon(icon, color: AppTheme.primary, size: 20),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
            color: AppTheme.textPrimary,
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
    );
  }

  Widget _buildAccountSetting(AccountSetting setting) {
    return GlassContainer(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: Colors.white.withOpacity(0.05),
      borderColor: AppTheme.textHint.withOpacity(0.2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            setting.category,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              color: AppTheme.textPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          ...setting.settings.map((item) => _buildSettingItem(item)),
        ],
      ),
    );
  }

  Widget _buildSettingItem(SettingItem item) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          Icon(item.icon, color: AppTheme.primary, size: 20),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.title,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppTheme.textPrimary,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                if (item.type != SettingType.toggle)
                  Text(
                    item.value,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
                  ),
              ],
            ),
          ),
          if (item.type == SettingType.toggle)
            Switch(
              value: item.value == 'مفعلة',
              onChanged: (value) => _updateSetting(item, value ? 'مفعلة' : 'معطلة'),
              activeColor: AppTheme.primary,
            ),
          if (item.type == SettingType.dropdown)
            Icon(Icons.keyboard_arrow_down, color: AppTheme.textHint),
        ],
      ),
    );
  }

  Widget _buildDangerZone() {
    return GlassContainer(
      padding: const EdgeInsets.all(16),
      radius: 16,
      backgroundColor: AppTheme.error.withOpacity(0.1),
      borderColor: AppTheme.error.withOpacity(0.3),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(Icons.warning, color: AppTheme.error),
              const SizedBox(width: 8),
              Text(
                'منطقة الخطر',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  color: AppTheme.error,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          GlassContainer(
            padding: const EdgeInsets.all(12),
            radius: 12,
            backgroundColor: Colors.white.withOpacity(0.05),
            child: Row(
              children: [
                Icon(Icons.delete, color: AppTheme.error, size: 20),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    'حذف الحساب',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppTheme.error,
                    ),
                  ),
                ),
                GlowingButton(
                  text: 'حذف',
                  onPressed: _deleteAccount,
                  color: AppTheme.error,
                  height: 32,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // Helper methods
  Color _getTicketStatusColor(TicketStatus status) {
    switch (status) {
      case TicketStatus.open:
        return AppTheme.error;
      case TicketStatus.inProgress:
        return AppTheme.accent;
      case TicketStatus.resolved:
        return AppTheme.success;
      case TicketStatus.closed:
        return AppTheme.textHint;
      default:
        return AppTheme.textHint;
    }
  }

  String _getTicketStatusText(TicketStatus status) {
    switch (status) {
      case TicketStatus.open:
        return 'مفتوح';
      case TicketStatus.inProgress:
        return 'قيد المعالجة';
      case TicketStatus.resolved:
        return 'تم الحل';
      case TicketStatus.closed:
        return 'مغلق';
      default:
        return 'غير معروف';
    }
  }

  Color _getTicketPriorityColor(TicketPriority priority) {
    switch (priority) {
      case TicketPriority.urgent:
        return AppTheme.error;
      case TicketPriority.high:
        return AppTheme.accent;
      case TicketPriority.normal:
        return AppTheme.primary;
      case TicketPriority.low:
        return AppTheme.success;
      default:
        return AppTheme.textHint;
    }
  }

  IconData _getTicketCategoryIcon(TicketCategory category) {
    switch (category) {
      case TicketCategory.shipment:
        return Icons.local_shipping;
      case TicketCategory.billing:
        return Icons.attach_money;
      case TicketCategory.technical:
        return Icons.settings;
      case TicketCategory.general:
        return Icons.help;
      default:
        return Icons.help_outline;
    }
  }

  Color _getMemberLevelColor(MemberLevel level) {
    switch (level) {
      case MemberLevel.bronze:
        return Colors.brown;
      case MemberLevel.silver:
        return Colors.grey;
      case MemberLevel.gold:
        return Colors.amber;
      case MemberLevel.platinum:
        return AppTheme.primary;
      default:
        return AppTheme.textHint;
    }
  }

  String _getMemberLevelText(MemberLevel level) {
    switch (level) {
      case MemberLevel.bronze:
        return 'برونزي';
      case MemberLevel.silver:
        return 'فضي';
      case MemberLevel.gold:
        return 'ذهبي';
      case MemberLevel.platinum:
        return 'بلاتيني';
      default:
        return 'عادي';
    }
  }

  String _formatTime(DateTime timestamp) {
    final now = DateTime.now();
    final difference = now.difference(timestamp);
    
    if (difference.inMinutes == 0) {
      return 'الآن';
    } else if (difference.inMinutes < 60) {
      return 'منذ ${difference.inMinutes} دقيقة';
    } else if (difference.inHours < 24) {
      return 'منذ ${difference.inHours} ساعة';
    } else {
      return 'منذ ${difference.inDays} يوم';
    }
  }

  // Action methods
  void _startChat() {
    setState(() {
      _isChatActive = true;
    });
  }

  void _createTicket() {
    // Navigate to create ticket screen
  }

  void _viewAllTickets() {
    // Navigate to all tickets screen
  }

  void _sendMessage() {
    if (_messageController.text.trim().isEmpty) return;
    
    setState(() {
      _chatMessages.add(ChatMessage(
        id: 'MSG-${DateTime.now().millisecondsSinceEpoch}',
        text: _messageController.text,
        sender: 'user',
        senderName: 'أنت',
        timestamp: DateTime.now(),
        isSupport: false,
      ));
      _messageController.clear();
    });
    
    // Simulate support response
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _chatMessages.add(ChatMessage(
            id: 'MSG-${DateTime.now().millisecondsSinceEpoch}',
            text: 'شكراً لرسالتك. سأقوم بمساعدتك قريباً.',
            sender: 'support',
            senderName: 'الدعم الفني',
            timestamp: DateTime.now(),
            isSupport: true,
          ));
        });
      }
    });
  }

  void _updateSetting(SettingItem item, String value) {
    setState(() {
      item.value = value;
    });
  }

  void _deleteAccount() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: AppTheme.background,
        title: const Text(
          'حذف الحساب',
          style: TextStyle(color: AppTheme.textPrimary),
        ),
        content: const Text(
          'هل أنت متأكد من حذف حسابك؟ هذا الإجراء لا يمكن التراجع عنه.',
          style: TextStyle(color: AppTheme.textSecondary),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('إلغاء'),
          ),
          GlowingButton(
            text: 'حذف',
            onPressed: () {
              Navigator.pop(context);
              // Delete account logic
            },
            color: AppTheme.error,
          ),
        ],
      ),
    );
  }

  void _showHelp() {
    // Show help dialog
  }
}

// Data models
enum TicketStatus { open, inProgress, resolved, closed }
enum TicketPriority { urgent, high, normal, low }
enum TicketCategory { shipment, billing, technical, general }
enum SettingType { text, email, phone, dropdown, toggle }
enum MemberLevel { bronze, silver, gold, platinum }

class SupportTicket {
  String id;
  String title;
  String description;
  TicketCategory category;
  TicketPriority priority;
  TicketStatus status;
  DateTime createdAt;
  DateTime lastUpdate;
  String assignedTo;
  int messages;

  SupportTicket({
    required this.id,
    required this.title,
    required this.description,
    required this.category,
    required this.priority,
    required this.status,
    required this.createdAt,
    required this.lastUpdate,
    required this.assignedTo,
    required this.messages,
  });
}

class ChatMessage {
  String id;
  String text;
  String sender;
  String senderName;
  DateTime timestamp;
  bool isSupport;

  ChatMessage({
    required this.id,
    required this.text,
    required this.sender,
    required this.senderName,
    required this.timestamp,
    required this.isSupport,
  });
}

class AccountSetting {
  String category;
  List<SettingItem> settings;

  AccountSetting({
    required this.category,
    required this.settings,
  });
}

class SettingItem {
  String title;
  String value;
  SettingType type;
  IconData icon;
  List<String>? options;

  SettingItem({
    required this.title,
    required this.value,
    required this.type,
    required this.icon,
    this.options,
  });
}

class UserProfile {
  String id;
  String name;
  String email;
  String phone;
  String avatar;
  String role;
  DateTime joinDate;
  DateTime lastLogin;
  int totalShipments;
  int completedShipments;
  double averageRating;
  MemberLevel memberLevel;

  UserProfile({
    required this.id,
    required this.name,
    required this.email,
    required this.phone,
    required this.avatar,
    required this.role,
    required this.joinDate,
    required this.lastLogin,
    required this.totalShipments,
    required this.completedShipments,
    required this.averageRating,
    required this.memberLevel,
  });

  static UserProfile empty() => UserProfile(
    id: '',
    name: '',
    email: '',
    phone: '',
    avatar: '',
    role: '',
    joinDate: DateTime.now(),
    lastLogin: DateTime.now(),
    totalShipments: 0,
    completedShipments: 0,
    averageRating: 0.0,
    memberLevel: MemberLevel.bronze,
  );
}

class FAQItem {
  String question;
  String answer;

  FAQItem({
    required this.question,
    required this.answer,
  });
}
