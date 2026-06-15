import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class SurveyQuestion {
  final String id;
  final String question;
  final String type; // rating, text, multiple_choice, yes_no
  final List<String>? options;
  final bool isRequired;
  final int? minRating;
  final int? maxRating;

  SurveyQuestion({
    required this.id,
    required this.question,
    required this.type,
    this.options,
    this.isRequired = false,
    this.minRating,
    this.maxRating,
  });

  factory SurveyQuestion.fromJson(Map<String, dynamic> json) {
    return SurveyQuestion(
      id: json['id'] ?? '',
      question: json['question'] ?? '',
      type: json['type'] ?? 'text',
      options: json['options'] != null ? List<String>.from(json['options']) : null,
      isRequired: json['isRequired'] ?? false,
      minRating: json['minRating'],
      maxRating: json['maxRating'],
    );
  }
}

class PostTripSurveyService extends ChangeNotifier {
  List<SurveyQuestion> _surveyQuestions = [];
  Map<String, dynamic>? _currentSurvey;
  List<Map<String, dynamic>> _surveyHistory = [];
  bool _isLoading = false;
  String? _error;

  List<SurveyQuestion> get surveyQuestions => _surveyQuestions;
  Map<String, dynamic>? get currentSurvey => _currentSurvey;
  List<Map<String, dynamic>> get surveyHistory => _surveyHistory;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Get survey template for trip
  Future<void> getSurveyTemplate(String tripType) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/surveys/template?tripType=$tripType'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _surveyQuestions = (data['questions'] as List)
            .map((q) => SurveyQuestion.fromJson(q))
            .toList();
      }
    } catch (e) {
      debugPrint('Error fetching survey template: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Submit survey
  Future<bool> submitSurvey({
    required String tripId,
    required String driverId,
    required Map<String, dynamic> answers,
    String? additionalComments,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/surveys/submit'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'tripId': tripId,
          'driverId': driverId,
          'answers': answers,
          'additionalComments': additionalComments,
          'submittedAt': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        _currentSurvey = jsonDecode(response.body);
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error submitting survey: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Get survey history
  Future<void> fetchSurveyHistory(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/surveys/history?driverId=$driverId'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _surveyHistory = List<Map<String, dynamic>>.from(data['surveys'] ?? []);
      }
    } catch (e) {
      debugPrint('Error fetching survey history: $e');
    }
  }

  // Get survey analytics
  Future<Map<String, dynamic>> getSurveyAnalytics({
    String? tripId,
    String? driverId,
    String period = 'month',
  }) async {
    try {
      String url = '$_apiBaseUrl/surveys/analytics?period=$period';
      if (tripId != null) url += '&tripId=$tripId';
      if (driverId != null) url += '&driverId=$driverId';

      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching survey analytics: $e');
      return {};
    }
  }

  // Get default survey questions
  List<SurveyQuestion> getDefaultSurveyQuestions() {
    return [
      SurveyQuestion(
        id: 'route_difficulty',
        question: 'كم كان صعوبة المسار؟',
        type: 'rating',
        minRating: 1,
        maxRating: 5,
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'traffic_conditions',
        question: 'كيف كانت حالة المرور؟',
        type: 'multiple_choice',
        options: ['ممتازة', 'جيدة', 'متوسطة', 'سيئة', 'سيئة جداً'],
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'vehicle_condition',
        question: 'كيف كانت حالة المركبة؟',
        type: 'rating',
        minRating: 1,
        maxRating: 5,
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'customer_interaction',
        question: 'كيف كان تفاعل العميل؟',
        type: 'rating',
        minRating: 1,
        maxRating: 5,
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'delivery_location',
        question: 'هل كان موقع التسليم سهل الوصول؟',
        type: 'yes_no',
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'loading_time',
        question: 'كم استغرق وقت التحميل؟',
        type: 'multiple_choice',
        options: ['أقل من 15 دقيقة', '15-30 دقيقة', '30-60 دقيقة', 'أكثر من ساعة'],
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'unloading_time',
        question: 'كم استغرق وقت التفريغ؟',
        type: 'multiple_choice',
        options: ['أقل من 15 دقيقة', '15-30 دقيقة', '30-60 دقيقة', 'أكثر من ساعة'],
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'issues_encountered',
        question: 'هل واجهت أي مشاكل أثناء الرحلة؟',
        type: 'yes_no',
        isRequired: true,
      ),
      SurveyQuestion(
        id: 'issues_description',
        question: 'وصف المشاكل (إن وجدت)',
        type: 'text',
        isRequired: false,
      ),
      SurveyQuestion(
        id: 'suggestions',
        question: 'هل لديك أي اقتراحات لتحسين الرحلات؟',
        type: 'text',
        isRequired: false,
      ),
      SurveyQuestion(
        id: 'overall_satisfaction',
        question: 'ما هو رضاك العام عن الرحلة؟',
        type: 'rating',
        minRating: 1,
        maxRating: 10,
        isRequired: true,
      ),
    ];
  }

  // Validate survey answers
  Map<String, dynamic> validateSurveyAnswers(Map<String, dynamic> answers) {
    final errors = <String, String>{};
    bool isValid = true;

    for (var question in _surveyQuestions) {
      if (question.isRequired && !answers.containsKey(question.id)) {
        errors[question.id] = 'هذا السؤال مطلوب';
        isValid = false;
      }

      if (answers.containsKey(question.id)) {
        final answer = answers[question.id];
        
        if (question.type == 'rating') {
          final rating = answer is int ? answer : int.tryParse(answer.toString());
          if (rating == null || 
              rating < (question.minRating ?? 1) || 
              rating > (question.maxRating ?? 5)) {
            errors[question.id] = 'التقييم يجب أن يكون بين ${question.minRating} و ${question.maxRating}';
            isValid = false;
          }
        }

        if (question.type == 'multiple_choice') {
          if (question.options != null && !question.options!.contains(answer)) {
            errors[question.id] = 'الخيار غير صالح';
            isValid = false;
          }
        }
      }
    }

    return {
      'isValid': isValid,
      'errors': errors,
    };
  }

  // Calculate survey score
  double calculateSurveyScore(Map<String, dynamic> answers) {
    int totalScore = 0;
    int maxScore = 0;
    int ratingQuestions = 0;

    for (var question in _surveyQuestions) {
      if (question.type == 'rating') {
        ratingQuestions++;
        final answer = answers[question.id];
        final rating = answer is int ? answer : int.tryParse(answer.toString());
        
        if (rating != null) {
          final minRating = question.minRating ?? 1;
          final maxRating = question.maxRating ?? 5;
          final normalizedScore = ((rating - minRating) / (maxRating - minRating)) * 100;
          totalScore += normalizedScore.toInt();
          maxScore += 100;
        }
      }
    }

    if (maxScore == 0) return 0.0;
    return totalScore / maxScore;
  }

  // Get survey insights
  Future<Map<String, dynamic>> getSurveyInsights(String driverId) async {
    try {
      final response = await http.get(
        Uri.parse('$_apiBaseUrl/surveys/insights?driverId=$driverId'),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
      return {};
    } catch (e) {
      debugPrint('Error fetching survey insights: $e');
      return {};
    }
  }
}

// Survey Types
class SurveyType {
  static const String standard = 'standard';
  static const String longDistance = 'long_distance';
  static const String hazardous = 'hazardous';
  static const String refrigerated = 'refrigerated';
  static const String urgent = 'urgent';
}
