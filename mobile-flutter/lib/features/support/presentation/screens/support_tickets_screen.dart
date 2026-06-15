// ============================================
// 🎫 Support Tickets Screen - تذاكر الدعم الفني
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class SupportTicketsScreen extends StatefulWidget {
  const SupportTicketsScreen({super.key});

  @override
  State<SupportTicketsScreen> createState() => _SupportTicketsScreenState();
}

class _SupportTicketsScreenState extends State<SupportTicketsScreen> {
  int _selectedStatus = 0;
  final List<String> _statuses = ['الكل', 'مفتوح', 'قيد التنفيذ', 'محلول'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'الدعم الفني',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.search, color: Colors.white),
          ),
        ],
      ),
      body: Column(
        children: [
          // Quick Stats
          _buildQuickStats(),
          
          const SizedBox(height: 16),
          
          // Status Filter
          _buildStatusFilter(),
          
          const SizedBox(height: 8),
          
          // Tickets List
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _getTickets().length,
              itemBuilder: (context, index) {
                final ticket = _getTickets()[index];
                return _TicketCard(ticket: ticket);
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {},
        backgroundColor: AppTheme.primaryColor,
        icon: const Icon(Icons.add),
        label: const Text('تذكرة جديدة'),
      ),
    );
  }

  Widget _buildQuickStats() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.primaryColor, AppTheme.primaryDark],
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _StatItem(
            icon: Icons.confirmation_number,
            value: '12',
            label: 'تذاكري',
          ),
          _StatItem(
            icon: Icons.pending_actions,
            value: '3',
            label: 'مفتوح',
          ),
          _StatItem(
            icon: Icons.check_circle,
            value: '9',
            label: 'محلول',
          ),
        ],
      ),
    );
  }

  Widget _buildStatusFilter() {
    return Container(
      height: 40,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _statuses.length,
        itemBuilder: (context, index) {
          final isSelected = _selectedStatus == index;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(_statuses[index]),
              selected: isSelected,
              onSelected: (selected) {
                setState(() {
                  _selectedStatus = index;
                });
              },
              selectedColor: AppTheme.primaryColor,
              backgroundColor: AppTheme.cardBackground,
              labelStyle: TextStyle(
                color: isSelected ? Colors.white : AppTheme.textSecondary,
                fontSize: 13,
              ),
            ),
          );
        },
      ),
    );
  }

  List<TicketModel> _getTickets() {
    return [
      TicketModel(
        id: 'TICKET-20240115-001',
        subject: 'مشكلة في حساب الفواتير',
        description: 'لا يمكنني رؤية فاتورة الشحنة الأخيرة',
        category: 'billing',
        priority: 'high',
        status: 'open',
        createdAt: '15 يناير 2024',
        lastUpdate: 'منذ 2 ساعة',
        hasNewMessage: true,
      ),
      TicketModel(
        id: 'TICKET-20240114-002',
        subject: 'تأخير في شحنة EDH-2024-156',
        description: 'الشحنة متأخرة عن الموعد المحدد بـ 3 ساعات',
        category: 'shipment',
        priority: 'urgent',
        status: 'in_progress',
        createdAt: '14 يناير 2024',
        lastUpdate: 'منذ ساعة',
        assignedTo: 'فريق العمليات',
        hasNewMessage: true,
      ),
      TicketModel(
        id: 'TICKET-20240110-003',
        subject: 'طلب إضافة ميزة تقرير Excel',
        description: 'أحتاج تصدير التقارير الشهرية بصيغة Excel',
        category: 'feature',
        priority: 'low',
        status: 'resolved',
        createdAt: '10 يناير 2024',
        lastUpdate: '12 يناير 2024',
        satisfaction: 5,
      ),
    ];
  }
}

class _StatItem extends StatelessWidget {
  final IconData icon;
  final String value;
  final String label;

  const _StatItem({
    required this.icon,
    required this.value,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Icon(icon, color: Colors.white70, size: 24),
        const SizedBox(height: 8),
        Text(
          value,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            color: Colors.white.withOpacity(0.8),
            fontSize: 12,
          ),
        ),
      ],
    );
  }
}

class _TicketCard extends StatelessWidget {
  final TicketModel ticket;

  const _TicketCard({required this.ticket});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: ticket.hasNewMessage
            ? Border.all(color: AppTheme.primaryColor.withOpacity(0.5))
            : null,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Ticket Number & Category
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: _getCategoryColor(ticket.category).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(6),
                    ),
                    child: Text(
                      _getCategoryLabel(ticket.category),
                      style: TextStyle(
                        color: _getCategoryColor(ticket.category),
                        fontSize: 11,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    ticket.id,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              
              // Priority
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: _getPriorityColor(ticket.priority).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Text(
                  _getPriorityLabel(ticket.priority),
                  style: TextStyle(
                    color: _getPriorityColor(ticket.priority),
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Subject
          Text(
            ticket.subject,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          
          const SizedBox(height: 4),
          
          // Description
          Text(
            ticket.description,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 13,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          
          const SizedBox(height: 12),
          
          // Footer
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Status
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: _getStatusColor(ticket.status).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(
                      _getStatusIcon(ticket.status),
                      color: _getStatusColor(ticket.status),
                      size: 14,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      _getStatusLabel(ticket.status),
                      style: TextStyle(
                        color: _getStatusColor(ticket.status),
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
              
              // Time & Indicators
              Row(
                children: [
                  if (ticket.satisfaction != null) ...[
                    Row(
                      children: [
                        Icon(
                          Icons.star,
                          color: AppTheme.warningColor,
                          size: 14,
                        ),
                        const SizedBox(width: 2),
                        Text(
                          '${ticket.satisfaction}',
                          style: TextStyle(
                            color: AppTheme.warningColor,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(width: 12),
                  ],
                  if (ticket.assignedTo != null) ...[
                    Icon(
                      Icons.person,
                      color: AppTheme.primaryColor,
                      size: 14,
                    ),
                    const SizedBox(width: 12),
                  ],
                  Text(
                    ticket.lastUpdate,
                    style: TextStyle(
                      color: AppTheme.textSecondary,
                      fontSize: 11,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Color _getCategoryColor(String category) {
    switch (category) {
      case 'billing':
        return Colors.green;
      case 'shipment':
        return Colors.blue;
      case 'technical':
        return Colors.red;
      case 'feature':
        return Colors.purple;
      default:
        return Colors.grey;
    }
  }

  String _getCategoryLabel(String category) {
    switch (category) {
      case 'billing':
        return 'فواتير';
      case 'shipment':
        return 'شحنة';
      case 'technical':
        return 'تقني';
      case 'feature':
        return 'ميزة';
      default:
        return 'أخرى';
    }
  }

  Color _getPriorityColor(String priority) {
    switch (priority) {
      case 'low':
        return Colors.grey;
      case 'medium':
        return Colors.blue;
      case 'high':
        return Colors.orange;
      case 'urgent':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  String _getPriorityLabel(String priority) {
    switch (priority) {
      case 'low':
        return 'منخفض';
      case 'medium':
        return 'متوسط';
      case 'high':
        return 'عالي';
      case 'urgent':
        return 'عاجل';
      default:
        return 'عادي';
    }
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'open':
        return AppTheme.warningColor;
      case 'in_progress':
        return AppTheme.primaryColor;
      case 'resolved':
        return AppTheme.successColor;
      case 'closed':
        return AppTheme.textSecondary;
      default:
        return Colors.grey;
    }
  }

  String _getStatusLabel(String status) {
    switch (status) {
      case 'open':
        return 'مفتوح';
      case 'in_progress':
        return 'قيد التنفيذ';
      case 'resolved':
        return 'محلول';
      case 'closed':
        return 'مغلق';
      default:
        return 'مفتوح';
    }
  }

  IconData _getStatusIcon(String status) {
    switch (status) {
      case 'open':
        return Icons.radio_button_unchecked;
      case 'in_progress':
        return Icons.timelapse;
      case 'resolved':
        return Icons.check_circle;
      case 'closed':
        return Icons.close;
      default:
        return Icons.radio_button_unchecked;
    }
  }
}

// Model
class TicketModel {
  final String id;
  final String subject;
  final String description;
  final String category;
  final String priority;
  final String status;
  final String createdAt;
  final String lastUpdate;
  final String? assignedTo;
  final bool hasNewMessage;
  final int? satisfaction;

  TicketModel({
    required this.id,
    required this.subject,
    required this.description,
    required this.category,
    required this.priority,
    required this.status,
    required this.createdAt,
    required this.lastUpdate,
    this.assignedTo,
    this.hasNewMessage = false,
    this.satisfaction,
  });
}
