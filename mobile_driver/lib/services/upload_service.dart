import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path/path.dart' as path;

class UploadService extends ChangeNotifier {
  bool _isUploading = false;
  double _uploadProgress = 0;
  List<String> _uploadedUrls = [];

  bool get isUploading => _isUploading;
  double get uploadProgress => _uploadProgress;
  List<String> get uploadedUrls => _uploadedUrls;

  final String _baseUrl = 'http://your-api-url.com/api';
  final ImagePicker _picker = ImagePicker();

  // Pick image from camera
  Future<File?> pickImageFromCamera() async {
    try {
      final XFile? pickedFile = await _picker.pickImage(
        source: ImageSource.camera,
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      if (pickedFile != null) {
        return File(pickedFile.path);
      }
      return null;
    } catch (e) {
      debugPrint('Error picking image from camera: $e');
      return null;
    }
  }

  // Pick image from gallery
  Future<File?> pickImageFromGallery() async {
    try {
      final XFile? pickedFile = await _picker.pickImage(
        source: ImageSource.gallery,
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      if (pickedFile != null) {
        return File(pickedFile.path);
      }
      return null;
    } catch (e) {
      debugPrint('Error picking image from gallery: $e');
      return null;
    }
  }

  // Pick multiple images
  Future<List<File>> pickMultipleImages() async {
    try {
      final List<XFile> pickedFiles = await _picker.pickMultiImage(
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      return pickedFiles.map((file) => File(file.path)).toList();
    } catch (e) {
      debugPrint('Error picking multiple images: $e');
      return [];
    }
  }

  // Upload single file
  Future<String?> uploadFile(File file, String token, {String type = 'image'}) async {
    try {
      _isUploading = true;
      _uploadProgress = 0;
      notifyListeners();

      final fileName = path.basename(file.path);
      final extension = path.extension(file.path).toLowerCase();

      // Determine media type
      MediaType? mediaType;
      if (['.jpg', '.jpeg'].contains(extension)) {
        mediaType = MediaType('image', 'jpeg');
      } else if (extension == '.png') {
        mediaType = MediaType('image', 'png');
      } else if (extension == '.pdf') {
        mediaType = MediaType('application', 'pdf');
      }

      // Create multipart request
      final request = http.MultipartRequest(
        'POST',
        Uri.parse('$_baseUrl/upload'),
      );

      // Add headers
      request.headers['Authorization'] = 'Bearer $token';

      // Add file
      request.files.add(
        http.MultipartFile(
          'file',
          file.readAsBytes().asStream(),
          await file.length(),
          filename: fileName,
          contentType: mediaType,
        ),
      );

      // Add metadata
      request.fields['type'] = type;

      // Send request with progress tracking
      final response = await request.send();

      // Listen to upload progress
      response.stream.listen(
        (List<int> value) {
          // Progress tracking logic here if needed
        },
        onDone: () {
          _uploadProgress = 1.0;
          notifyListeners();
        },
      );

      // Get response
      final responseData = await response.stream.bytesToString();
      final jsonResponse = jsonDecode(responseData);

      if (response.statusCode == 200 || response.statusCode == 201) {
        final url = jsonResponse['url'] as String?;
        if (url != null) {
          _uploadedUrls.add(url);
          return url;
        }
      }

      return null;
    } catch (e) {
      debugPrint('Error uploading file: $e');
      return null;
    } finally {
      _isUploading = false;
      _uploadProgress = 0;
      notifyListeners();
    }
  }

  // Upload multiple files
  Future<List<String>> uploadMultipleFiles(List<File> files, String token) async {
    final List<String> urls = [];

    for (var i = 0; i < files.length; i++) {
      final file = files[i];
      final url = await uploadFile(file, token);
      if (url != null) {
        urls.add(url);
      }
    }

    return urls;
  }

  // Upload delivery proof photos
  Future<List<String>> uploadDeliveryProof(
    List<File> photos,
    String tripId,
    String token,
  ) async {
    try {
      _isUploading = true;
      notifyListeners();

      final urls = await uploadMultipleFiles(photos, token);

      // Update trip with photo URLs
      if (urls.isNotEmpty) {
        await http.post(
          Uri.parse('$_baseUrl/trips/$tripId/delivery-proof'),
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer $token',
          },
          body: jsonEncode({
            'photos': urls,
            'timestamp': DateTime.now().toIso8601String(),
          }),
        );
      }

      return urls;
    } catch (e) {
      debugPrint('Error uploading delivery proof: $e');
      return [];
    } finally {
      _isUploading = false;
      notifyListeners();
    }
  }

  // Upload document
  Future<String?> uploadDocument(
    File file,
    String documentType,
    String token,
  ) async {
    try {
      _isUploading = true;
      notifyListeners();

      final fileName = path.basename(file.path);

      final request = http.MultipartRequest(
        'POST',
        Uri.parse('$_baseUrl/upload/document'),
      );

      request.headers['Authorization'] = 'Bearer $token';
      request.files.add(
        http.MultipartFile(
          'document',
          file.readAsBytes().asStream(),
          await file.length(),
          filename: fileName,
          contentType: MediaType('application', 'pdf'),
        ),
      );

      request.fields['documentType'] = documentType;

      final response = await request.send();
      final responseData = await response.stream.bytesToString();
      final jsonResponse = jsonDecode(responseData);

      if (response.statusCode == 200 || response.statusCode == 201) {
        return jsonResponse['url'] as String?;
      }

      return null;
    } catch (e) {
      debugPrint('Error uploading document: $e');
      return null;
    } finally {
      _isUploading = false;
      notifyListeners();
    }
  }

  // Delete uploaded file
  Future<bool> deleteFile(String fileUrl, String token) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/upload'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: jsonEncode({'url': fileUrl}),
      );

      if (response.statusCode == 200) {
        _uploadedUrls.remove(fileUrl);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Error deleting file: $e');
      return false;
    }
  }

  // Clear uploaded URLs
  void clearUploadedUrls() {
    _uploadedUrls = [];
    notifyListeners();
  }

  // Show image picker dialog
  Future<File?> showImagePickerDialog(BuildContext context) async {
    return showDialog<File?>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('اختر مصدر الصورة'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ListTile(
                leading: const Icon(Icons.camera_alt, color: Color(0xFF0099D8)),
                title: const Text('الكاميرا'),
                onTap: () async {
                  final file = await pickImageFromCamera();
                  Navigator.of(context).pop(file);
                },
              ),
              ListTile(
                leading: const Icon(Icons.photo_library, color: Color(0xFF0099D8)),
                title: const Text('معرض الصور'),
                onTap: () async {
                  final file = await pickImageFromGallery();
                  Navigator.of(context).pop(file);
                },
              ),
            ],
          ),
        );
      },
    );
  }
}
