import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class TrainingModule {
  final String id;
  final String title;
  final String description;
  final String category; // safety, customer_service, vehicle_maintenance, regulations
  final String type; // video, document, quiz, interactive
  final String? contentUrl;
  final int duration; // in minutes
  final bool isRequired;
  final bool isCompleted;
  final double? progress;
  final DateTime? completedAt;
  final double? score; // for quizzes
  final String? thumbnailUrl;

  TrainingModule({
    required this.id,
    required this.title,
    required this.description,
    required this.category,
    required this.type,
    this.contentUrl,
    required this.duration,
    this.isRequired = false,
    this.isCompleted = false,
    this.progress,
    this.completedAt,
    this.score,
    this.thumbnailUrl,
  });

  factory TrainingModule.fromJson(Map<String, dynamic> json) {
    return TrainingModule(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      category: json['category'] ?? '',
      type: json['type'] ?? 'video',
      contentUrl: json['contentUrl'],
      duration: json['duration'] ?? 0,
      isRequired: json['isRequired'] ?? false,
      isCompleted: json['isCompleted'] ?? false,
      progress: json['progress']?.toDouble(),
      completedAt: json['completedAt'] != null ? DateTime.parse(json['completedAt']) : null,
      score: json['score']?.toDouble(),
      thumbnailUrl: json['thumbnailUrl'],
    );
  }
}

class TrainingService extends ChangeNotifier {
  List<TrainingModule> _allModules = [];
  List<TrainingModule> _requiredModules = [];
  List<TrainingModule> _completedModules = [];
  List<TrainingModule> _inProgressModules = [];
  Map<String, dynamic>? _trainingProgress;
  bool _isLoading = false;
  String? _error;

  List<TrainingModule> get allModules => _allModules;
  List<TrainingModule> get requiredModules => _requiredModules;
  List<TrainingModule> get completedModules => _completedModules;
  List<TrainingModule> get inProgressModules => _inProgressModules;
  Map<String, dynamic>? get trainingProgress => _trainingProgress;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch all training modules
  Future<void> fetchTrainingModules(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/training'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _allModules = (data['modules'] as List)
            .map((m) => TrainingModule.fromJson(m))
            .toList();
        
        _updateModuleLists();
        await _fetchTrainingProgress(driverId);
      }
    } catch (e) {
      debugPrint('Error fetching training modules: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update module lists
  void _updateModuleLists() {
    _requiredModules = _allModules.where((m) => m.isRequired).toList();
    _completedModules = _allModules.where((m) => m.isCompleted).toList();
    _inProgressModules = _allModules
        .where((m) => !m.isCompleted && (m.progress ?? 0) > 0)
        .toList();
  }

  // Fetch training progress
  Future<void> _fetchTrainingProgress(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/training/progress'),
      );

      if (response.statusCode == 200) {
        _trainingProgress = jsonDecode(response.body);
      }
    } catch (e) {
      debugPrint('Error fetching training progress: $e');
    }
  }

  // Start training module
  Future<bool> startModule({
    required String driverId,
    required String moduleId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/training/$moduleId/start'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'driverId': driverId,
          'startedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error starting module: $e');
      return false;
    }
  }

  // Update progress
  Future<bool> updateProgress({
    required String moduleId,
    required double progress,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/training/$moduleId/progress'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'progress': progress,
          'updatedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        final index = _allModules.indexWhere((m) => m.id == moduleId);
        if (index != -1) {
          _allModules[index] = TrainingModule(
            id: _allModules[index].id,
            title: _allModules[index].title,
            description: _allModules[index].description,
            category: _allModules[index].category,
            type: _allModules[index].type,
            contentUrl: _allModules[index].contentUrl,
            duration: _allModules[index].duration,
            isRequired: _allModules[index].isRequired,
            isCompleted: _allModules[index].isCompleted,
            progress: progress,
            completedAt: _allModules[index].completedAt,
            score: _allModules[index].score,
            thumbnailUrl: _allModules[index].thumbnailUrl,
          );
          _updateModuleLists();
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating progress: $e');
      return false;
    }
  }

  // Complete module
  Future<bool> completeModule({
    required String driverId,
    required String moduleId,
    double? score,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/training/$moduleId/complete'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'driverId': driverId,
          'score': score,
          'completedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        final index = _allModules.indexWhere((m) => m.id == moduleId);
        if (index != -1) {
          _allModules[index] = TrainingModule(
            id: _allModules[index].id,
            title: _allModules[index].title,
            description: _allModules[index].description,
            category: _allModules[index].category,
            type: _allModules[index].type,
            contentUrl: _allModules[index].contentUrl,
            duration: _allModules[index].duration,
            isRequired: _allModules[index].isRequired,
            isCompleted: true,
            progress: 100.0,
            completedAt: DateTime.now(),
            score: score,
            thumbnailUrl: _allModules[index].thumbnailUrl,
          );
          _updateModuleLists();
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error completing module: $e');
      return false;
    }
  }

  // Submit quiz answers
  Future<Map<String, dynamic>> submitQuiz({
    required String moduleId,
    required Map<String, dynamic> answers,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/training/$moduleId/quiz'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'answers': answers,
          'submittedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error submitting quiz: $e');
      return {};
    }
  }

  // Get modules by category
  List<TrainingModule> getModulesByCategory(String category) {
    return _allModules.where((m) => m.category == category).toList();
  }

  // Get required modules not completed
  List<TrainingModule> getRequiredNotCompleted() {
    return _requiredModules.where((m) => !m.isCompleted).toList();
  }

  // Calculate overall progress
  double calculateOverallProgress() {
    if (_allModules.isEmpty) return 0.0;
    
    final totalProgress = _allModules.fold<double>(
      0.0,
      (sum, m) => sum + (m.progress ?? 0.0),
    );
    
    return totalProgress / _allModules.length;
  }

  // Get training certificate
  Future<String?> getTrainingCertificate(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/training/certificate'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['certificateUrl'];
      }
      return null;
    } catch (e) {
      debugPrint('Error getting training certificate: $e');
      return null;
    }
  }

  // Get training statistics
  Future<Map<String, dynamic>> getTrainingStatistics(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/training/statistics'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching training statistics: $e');
      return {};
    }
  }
}

// Training Categories
class TrainingCategory {
  static const String safety = 'safety';
  static const String customerService = 'customer_service';
  static const String vehicleMaintenance = 'vehicle_maintenance';
  static const String regulations = 'regulations';
  static const String navigation = 'navigation';
  static const String emergency = 'emergency';
}

// Training Types
class TrainingType {
  static const String video = 'video';
  static const String document = 'document';
  static const String quiz = 'quiz';
  static const String interactive = 'interactive';
}
