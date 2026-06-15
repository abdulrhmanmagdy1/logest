import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class DriverDocument {
  final String id;
  final String documentType;
  final String documentName;
  final String? fileUrl;
  final DateTime issueDate;
  final DateTime? expiryDate;
  final String status; // valid, expired, expiring_soon, pending
  final String? notes;
  final bool isVerified;

  DriverDocument({
    required this.id,
    required this.documentType,
    required this.documentName,
    this.fileUrl,
    required this.issueDate,
    this.expiryDate,
    required this.status,
    this.notes,
    this.isVerified = false,
  });

  factory DriverDocument.fromJson(Map<String, dynamic> json) {
    return DriverDocument(
      id: json['id'] ?? '',
      documentType: json['documentType'] ?? '',
      documentName: json['documentName'] ?? '',
      fileUrl: json['fileUrl'],
      issueDate: DateTime.parse(json['issueDate'] ?? DateTime.now().toIso8601String()),
      expiryDate: json['expiryDate'] != null ? DateTime.parse(json['expiryDate']) : null,
      status: json['status'] ?? 'pending',
      notes: json['notes'],
      isVerified: json['isVerified'] ?? false,
    );
  }
}

class DocumentsManagementService extends ChangeNotifier {
  List<DriverDocument> _documents = [];
  List<DriverDocument> _expiringSoon = [];
  List<DriverDocument> _expired = [];
  bool _isLoading = false;
  String? _error;

  List<DriverDocument> get documents => _documents;
  List<DriverDocument> get expiringSoon => _expiringSoon;
  List<DriverDocument> get expired => _expired;
  bool get isLoading => _isLoading;
  String? get error => _error;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Fetch all documents
  Future<void> fetchDocuments(String driverId) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.get(
        Uri.parse('$_apiBaseUrl/driver/$driverId/documents'),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _documents = (data['documents'] as List)
            .map((d) => DriverDocument.fromJson(d))
            .toList();
        
        _updateDocumentStatus();
      }
    } catch (e) {
      debugPrint('Error fetching documents: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update document status based on expiry
  void _updateDocumentStatus() {
    final now = DateTime.now();
    final thirtyDaysFromNow = now.add(const Duration(days: 30));

    _expiringSoon = _documents
        .where((d) =>
            d.expiryDate != null &&
            d.expiryDate!.isAfter(now) &&
            d.expiryDate!.isBefore(thirtyDaysFromNow))
        .toList();

    _expired = _documents
        .where((d) =>
            d.expiryDate != null && d.expiryDate!.isBefore(now))
        .toList();
  }

  // Upload document
  Future<bool> uploadDocument({
    required String driverId,
    required String documentType,
    required String documentName,
    required String fileUrl,
    required DateTime issueDate,
    DateTime? expiryDate,
    String? notes,
  }) async {
    try {
      _isLoading = true;
      notifyListeners();

      final response = await http.post(
        Uri.parse('$_apiBaseUrl/driver/$driverId/documents'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'documentType': documentType,
          'documentName': documentName,
          'fileUrl': fileUrl,
          'issueDate': issueDate.toIso8601String(),
          'expiryDate': expiryDate?.toIso8601String(),
          'notes': notes,
          'status': 'pending',
        }),
      );

      if (response.statusCode == 201) {
        await fetchDocuments(driverId);
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error uploading document: $e');
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Update document
  Future<bool> updateDocument({
    required String documentId,
    required String documentName,
    DateTime? expiryDate,
    String? notes,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$_apiBaseUrl/documents/$documentId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'documentName': documentName,
          'expiryDate': expiryDate?.toIso8601String(),
          'notes': notes,
        }),
      );

      if (response.statusCode == 200) {
        final index = _documents.indexWhere((d) => d.id == documentId);
        if (index != -1) {
          // Update local document
          await fetchDocuments(''); // Refresh all
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error updating document: $e');
      return false;
    }
  }

  // Delete document
  Future<bool> deleteDocument(String documentId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_apiBaseUrl/documents/$documentId'),
      );

      if (response.statusCode == 200) {
        _documents.removeWhere((d) => d.id == documentId);
        _updateDocumentStatus();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting document: $e');
      return false;
    }
  }

  // Verify document
  Future<bool> verifyDocument(String documentId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/documents/$documentId/verify'),
      );

      if (response.statusCode == 200) {
        final index = _documents.indexWhere((d) => d.id == documentId);
        if (index != -1) {
          _documents[index] = DriverDocument(
            id: _documents[index].id,
            documentType: _documents[index].documentType,
            documentName: _documents[index].documentName,
            fileUrl: _documents[index].fileUrl,
            issueDate: _documents[index].issueDate,
            expiryDate: _documents[index].expiryDate,
            status: _documents[index].status,
            notes: _documents[index].notes,
            isVerified: true,
          );
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error verifying document: $e');
      return false;
    }
  }

  // Get document by type
  DriverDocument? getDocumentByType(String documentType) {
    try {
      return _documents.firstWhere((d) => d.documentType == documentType);
    } catch (e) {
      return null;
    }
  }

  // Check if all required documents are valid
  bool areAllDocumentsValid() {
    final requiredTypes = [
      'driving_license',
      'vehicle_registration',
      'insurance',
      'id_card',
    ];

    for (var type in requiredTypes) {
      final doc = getDocumentByType(type);
      if (doc == null || doc.status != 'valid' || !doc.isVerified) {
        return false;
      }
    }
    return true;
  }

  // Get documents expiry summary
  Map<String, dynamic> getDocumentsExpirySummary() {
    return {
      'total': _documents.length,
      'valid': _documents.where((d) => d.status == 'valid').length,
      'expiringSoon': _expiringSoon.length,
      'expired': _expired.length,
      'pending': _documents.where((d) => d.status == 'pending').length,
    };
  }

  // Request document verification
  Future<bool> requestVerification(String documentId) async {
    try {
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/documents/$documentId/request-verification'),
      );

      if (response.statusCode == 200) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error requesting verification: $e');
      return false;
    }
  }
}

// Document Types
class DocumentType {
  static const String drivingLicense = 'driving_license';
  static const String vehicleRegistration = 'vehicle_registration';
  static const String insurance = 'insurance';
  static const String idCard = 'id_card';
  static const String passport = 'passport';
  static const String medicalCertificate = 'medical_certificate';
  static const String trainingCertificate = 'training_certificate';
  static const String backgroundCheck = 'background_check';
  static const String other = 'other';
}

// Document Status
class DocumentStatus {
  static const String valid = 'valid';
  static const String expired = 'expired';
  static const String expiringSoon = 'expiring_soon';
  static const String pending = 'pending';
  static const String rejected = 'rejected';
}
