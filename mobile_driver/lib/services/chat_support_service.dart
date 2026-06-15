import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ChatMessage {
  final String id;
  final String senderId;
  final String senderName;
  final String message;
  final DateTime timestamp;
  final bool isFromDriver;
  final String? attachmentUrl;
  final String? attachmentType;

  ChatMessage({
    required this.id,
    required this.senderId,
    required this.senderName,
    required this.message,
    required this.timestamp,
    required this.isFromDriver,
    this.attachmentUrl,
    this.attachmentType,
  });

  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    return ChatMessage(
      id: json['id'] ?? '',
      senderId: json['senderId'] ?? '',
      senderName: json['senderName'] ?? '',
      message: json['message'] ?? '',
      timestamp: DateTime.parse(json['timestamp'] ?? DateTime.now().toIso8601String()),
      isFromDriver: json['isFromDriver'] ?? false,
      attachmentUrl: json['attachmentUrl'],
      attachmentType: json['attachmentType'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'senderId': senderId,
      'senderName': senderName,
      'message': message,
      'timestamp': timestamp.toIso8601String(),
      'isFromDriver': isFromDriver,
      'attachmentUrl': attachmentUrl,
      'attachmentType': attachmentType,
    };
  }
}

class ChatSupportService extends ChangeNotifier {
  List<ChatMessage> _messages = [];
  bool _isTyping = false;
  bool _isConnected = false;
  String? _currentTicketId;
  List<Map<String, dynamic>> _supportAgents = [];
  bool _isLoading = false;
  String? _error;

  List<ChatMessage> get messages => _messages;
  bool get isTyping => _isTyping;
  bool get isConnected => _isConnected;
  String? get currentTicketId => _currentTicketId;
  List<Map<String, dynamic>> get supportAgents => _supportAgents;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';
  StreamSubscription? _messageSubscription;

  // Initialize chat
  Future<void> initializeChat(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      // Check for existing active ticket
      await _checkActiveTicket(driverId);

      if (_currentTicketId == null) {
        // Create new ticket
        await createSupportTicket(driverId);
      }

      // Load messages
      await loadMessages(_currentTicketId!);

      // Connect to WebSocket for real-time messages
      _connectToChat();

      _isConnected = true;
    } catch (e) {
      debugPrint('Error initializing chat: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Check for active ticket
  Future<void> _checkActiveTicket(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/support/active-ticket?driverId=$driverId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['ticketId'] != null) {
          _currentTicketId = data['ticketId'];
        }
      }
    } catch (e) {
      debugPrint('Error checking active ticket: $e');
    }
  }

  // Create support ticket
  Future<bool> createSupportTicket(String driverId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/support/tickets'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'driverId': driverId,
          'subject': 'استفسار عام',
          'priority': 'normal',
          'status': 'open',
          'createdAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        final data = jsonDecode(response.body);
        _currentTicketId = data['ticketId'];
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error creating support ticket: $e');
      return false;
    }
  }

  // Load messages
  Future<void> loadMessages(String ticketId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/support/tickets/$ticketId/messages'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _messages = (data['messages'] as List)
            .map((m) => ChatMessage.fromJson(m))
            .toList();
        notifyListeners();
      }
    } catch (e) {
      debugPrint('Error loading messages: $e');
    }
  }

  // Send message
  Future<bool> sendMessage({
    required String message,
    String? attachmentUrl,
    String? attachmentType,
  }) async {
    try {
      final newMessage = ChatMessage(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        senderId: 'driver', // Will be replaced with actual driver ID
        senderName: 'السائق',
        message: message,
        timestamp: DateTime.now(),
        isFromDriver: true,
        attachmentUrl: attachmentUrl,
        attachmentType: attachmentType,
      );

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/support/tickets/$_currentTicketId/messages'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(newMessage.toJson()),
      );

      if (response.statusCode == 201) {
        _messages.add(newMessage);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error sending message: $e');
      return false;
    }
  }

  // Connect to chat (WebSocket simulation)
  void _connectToChat() {
    // In production, this would use WebSocket
    // For now, we'll simulate with polling
    Timer.periodic(const Duration(seconds: 5), (timer) async {
      if (_currentTicketId != null) {
        await loadMessages(_currentTicketId!);
      }
    });
  }

  // Get support agents
  Future<void> fetchSupportAgents() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/support/agents'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _supportAgents = List<Map<String, dynamic>>.from(data['agents'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching support agents: $e');
    }
  }

  // Send typing indicator
  Future<void> sendTypingIndicator(bool isTyping) async {
    try {
      await http.post(
        Uri.parse('$_apiBaseUrl/support/tickets/$_currentTicketId/typing'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'isTyping': isTyping}),
      );
    } catch (e) {
      debugPrint('Error sending typing indicator: $e');
    }
  }

  // Close ticket
  Future<bool> closeTicket(String reason) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/support/tickets/$_currentTicketId/close'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'reason': reason}),
      );

      if (response.statusCode == 200) {
        _currentTicketId = null;
        _messages.clear();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error closing ticket: $e');
      return false;
    }
  }

  // Rate support
  Future<bool> rateSupport({
    required String ticketId,
    required int rating,
    String? feedback,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/support/tickets/$ticketId/rate'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'rating': rating,
          'feedback': feedback,
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error rating support: $e');
      return false;
    }
  }

  // Get ticket history
  Future<List<Map<String, dynamic>>> getTicketHistory(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/support/tickets/history?driverId=$driverId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['tickets'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching ticket history: $e');
      return [];
    }
  }

  // Upload attachment
  Future<String?> uploadAttachment(String filePath) async {
    try {
      // This would use the upload service
      // For now, return mock URL
      return 'https://example.com/attachment.jpg';
    } catch (e) {
      debugPrint('Error uploading attachment: $e');
      return null;
    }
  }

  // Get quick replies
  Future<List<String>> getQuickReplies() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/support/quick-replies'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<String>.from(data['replies'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching quick replies: $e');
      return [];
    }
  }

  // Get FAQ
  Future<List<Map<String, dynamic>>> getFAQ() async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/support/faq'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['faq'] ?? []);
      }
      return [];
    } catch (e) {
      debugPrint('Error fetching FAQ: $e');
      return [];
    }
  }

  @override
  void dispose() {
    _messageSubscription?.cancel();
    super.dispose();
  }
}

// Ticket Priority
class TicketPriority {
  static const String low = 'low';
  static const String normal = 'normal';
  static const String high = 'high';
  static const String urgent = 'urgent';
}

// Ticket Status
class TicketStatus {
  static const String open = 'open';
  static const String inProgress = 'in_progress';
  static const String pending = 'pending';
  static const String resolved = 'resolved';
  static const String closed = 'closed';
}
