// ============================================
// 💬 Chat List Screen - قائمة المحادثات
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class ChatListScreen extends StatefulWidget {
  const ChatListScreen({super.key});

  @override
  State<ChatListScreen> createState() => _ChatListScreenState();
}

class _ChatListScreenState extends State<ChatListScreen> {
  int _selectedFilter = 0;
  final List<String> _filters = ['الكل', 'الدعم', 'السائقين', 'العملاء'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'المحادثات',
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
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.more_vert, color: Colors.white),
          ),
        ],
      ),
      body: Column(
        children: [
          // Filter Chips
          _buildFilterChips(),
          
          const SizedBox(height: 8),
          
          // Chat List
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _getChats().length,
              itemBuilder: (context, index) {
                final chat = _getChats()[index];
                return _ChatListItem(chat: chat);
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        backgroundColor: AppTheme.primaryColor,
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildFilterChips() {
    return Container(
      height: 50,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _filters.length,
        itemBuilder: (context, index) {
          final isSelected = _selectedFilter == index;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(_filters[index]),
              selected: isSelected,
              onSelected: (selected) {
                setState(() {
                  _selectedFilter = index;
                });
              },
              selectedColor: AppTheme.primaryColor,
              backgroundColor: AppTheme.cardBackground,
              labelStyle: TextStyle(
                color: isSelected ? Colors.white : AppTheme.textSecondary,
              ),
            ),
          );
        },
      ),
    );
  }

  List<ChatModel> _getChats() {
    return [
      ChatModel(
        id: '1',
        name: 'خالد السائق',
        avatar: null,
        lastMessage: 'وصلت لموقع الاستلام، جاري تحميل الشحنة',
        time: '10:45',
        unreadCount: 2,
        isOnline: true,
        type: ChatType.driver,
      ),
      ChatModel(
        id: '2',
        name: 'عميل - أسواق التميمي',
        avatar: null,
        lastMessage: 'ممتاز، شكراً على التواصل السريع',
        time: '09:30',
        unreadCount: 0,
        isOnline: false,
        type: ChatType.client,
      ),
      ChatModel(
        id: '3',
        name: 'الدعم الفني',
        avatar: null,
        lastMessage: 'تم حل المشكلة، هل تحتاج مساعدة أخرى؟',
        time: 'أمس',
        unreadCount: 0,
        isOnline: true,
        type: ChatType.support,
        isOfficial: true,
      ),
      ChatModel(
        id: '4',
        name: 'أحمد المحاسب',
        avatar: null,
        lastMessage: 'تم إصدار الفاتورة #2024-156',
        time: 'أمس',
        unreadCount: 1,
        isOnline: true,
        type: ChatType.internal,
      ),
      ChatModel(
        id: '5',
        name: 'مجموعة السائقين',
        avatar: null,
        lastMessage: 'محمد: تم تسليم الشحنة رقم 1234',
        time: '2 دقائق',
        unreadCount: 5,
        isOnline: false,
        type: ChatType.group,
        memberCount: 12,
      ),
    ];
  }
}

class _ChatListItem extends StatelessWidget {
  final ChatModel chat;

  const _ChatListItem({required this.chat});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(12),
      ),
      child: ListTile(
        onTap: () {},
        contentPadding: const EdgeInsets.all(12),
        leading: Stack(
          children: [
            Container(
              width: 56,
              height: 56,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: chat.isOfficial
                      ? [Colors.purple, Colors.purple.shade800]
                      : [AppTheme.primaryColor, AppTheme.primaryDark],
                ),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Center(
                child: chat.avatar != null
                    ? ClipRRect(
                        borderRadius: BorderRadius.circular(16),
                        child: Image.network(chat.avatar!),
                      )
                    : Text(
                        chat.name.split(' ').map((e) => e[0]).take(2).join(''),
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                          fontSize: 18,
                        ),
                      ),
              ),
            ),
            if (chat.isOnline)
              Positioned(
                right: 2,
                bottom: 2,
                child: Container(
                  width: 14,
                  height: 14,
                  decoration: BoxDecoration(
                    color: AppTheme.successColor,
                    border: Border.all(
                      color: AppTheme.cardBackground,
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(7),
                  ),
                ),
              ),
          ],
        ),
        title: Row(
          children: [
            Expanded(
              child: Text(
                chat.name,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 15,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ),
            if (chat.isOfficial)
              Container(
                margin: const EdgeInsets.only(right: 8),
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: Colors.purple.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text(
                  'رسمي',
                  style: TextStyle(
                    color: Colors.purple,
                    fontSize: 10,
                  ),
                ),
              ),
            if (chat.type == ChatType.group)
              Container(
                margin: const EdgeInsets.only(right: 8),
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: AppTheme.primaryColor.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  '${chat.memberCount}',
                  style: TextStyle(
                    color: AppTheme.primaryColor,
                    fontSize: 10,
                  ),
                ),
              ),
          ],
        ),
        subtitle: Padding(
          padding: const EdgeInsets.only(top: 4),
          child: Text(
            chat.lastMessage,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 13,
            ),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              chat.time,
              style: TextStyle(
                color: chat.unreadCount > 0
                    ? AppTheme.primaryColor
                    : AppTheme.textSecondary,
                fontSize: 12,
              ),
            ),
            if (chat.unreadCount > 0) ...[
              const SizedBox(height: 4),
              Container(
                padding: const EdgeInsets.all(6),
                decoration: BoxDecoration(
                  color: AppTheme.primaryColor,
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Text(
                  chat.unreadCount.toString(),
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

// Models
enum ChatType { driver, client, support, internal, group }

class ChatModel {
  final String id;
  final String name;
  final String? avatar;
  final String lastMessage;
  final String time;
  final int unreadCount;
  final bool isOnline;
  final ChatType type;
  final bool isOfficial;
  final int? memberCount;

  ChatModel({
    required this.id,
    required this.name,
    this.avatar,
    required this.lastMessage,
    required this.time,
    required this.unreadCount,
    required this.isOnline,
    required this.type,
    this.isOfficial = false,
    this.memberCount,
  });
}
