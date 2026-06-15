import 'dart:io';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/upload_service.dart';
import '../services/auth_service.dart';

class DocumentUploadScreen extends StatefulWidget {
  const DocumentUploadScreen({super.key});

  @override
  State<DocumentUploadScreen> createState() => _DocumentUploadScreenState();
}

class _DocumentUploadScreenState extends State<DocumentUploadScreen> {
  List<File> _selectedPhotos = [];
  bool _isUploading = false;
  double _uploadProgress = 0;

  Future<void> _pickImages() async {
    final uploadService = Provider.of<UploadService>(context, listen: false);
    final images = await uploadService.pickMultipleImages();

    setState(() {
      _selectedPhotos.addAll(images);
    });
  }

  Future<void> _takePhoto() async {
    final uploadService = Provider.of<UploadService>(context, listen: false);
    final photo = await uploadService.pickImageFromCamera();

    if (photo != null) {
      setState(() {
        _selectedPhotos.add(photo);
      });
    }
  }

  Future<void> _uploadPhotos() async {
    if (_selectedPhotos.isEmpty) return;

    final uploadService = Provider.of<UploadService>(context, listen: false);
    final authService = Provider.of<AuthService>(context, listen: false);

    if (authService.token == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('يرجى تسجيل الدخول أولاً'),
          backgroundColor: Colors.red,
        ),
      );
      return;
    }

    setState(() => _isUploading = true);

    final urls = await uploadService.uploadMultipleFiles(
      _selectedPhotos,
      authService.token!,
    );

    setState(() => _isUploading = false);

    if (urls.isNotEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('تم رفع ${urls.length} صورة بنجاح'),
          backgroundColor: Colors.green,
        ),
      );
      setState(() {
        _selectedPhotos = [];
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('فشل رفع الصور'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  void _removePhoto(int index) {
    setState(() {
      _selectedPhotos.removeAt(index);
    });
  }

  @override
  Widget build(BuildContext context) {
    final uploadService = Provider.of<UploadService>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('رفع المستندات'),
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [Color(0xFF0099D8), Color(0xFF007BA7)],
                  ),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.2),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: const Icon(
                        Icons.cloud_upload,
                        color: Colors.white,
                        size: 32,
                      ),
                    ),
                    const SizedBox(width: 16),
                    const Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'رفع المستندات والصور',
                            style: TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                            ),
                          ),
                          SizedBox(height: 4),
                          Text(
                            'قم برفع صور إثبات التسليم أو المستندات',
                            style: TextStyle(
                              fontSize: 14,
                              color: Colors.white70,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // Upload Type Selection
              const Text(
                'نوع المستند',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                ),
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: _buildUploadTypeCard(
                      icon: Icons.camera_alt,
                      title: 'صور التسليم',
                      isSelected: true,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _buildUploadTypeCard(
                      icon: Icons.description,
                      title: 'مستندات',
                      isSelected: false,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),

              // Selected Photos Grid
              if (_selectedPhotos.isNotEmpty) ...[
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'الصور المختارة (${_selectedPhotos.length})',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    TextButton.icon(
                      onPressed: () => setState(() => _selectedPhotos = []),
                      icon: const Icon(Icons.clear_all, size: 18),
                      label: const Text('مسح الكل'),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                SizedBox(
                  height: 120,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: _selectedPhotos.length,
                    itemBuilder: (context, index) {
                      return Stack(
                        children: [
                          Container(
                            width: 120,
                            height: 120,
                            margin: const EdgeInsets.only(right: 12),
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(12),
                              image: DecorationImage(
                                image: FileImage(_selectedPhotos[index]),
                                fit: BoxFit.cover,
                              ),
                            ),
                          ),
                          Positioned(
                            top: 4,
                            right: 16,
                            child: GestureDetector(
                              onTap: () => _removePhoto(index),
                              child: Container(
                                padding: const EdgeInsets.all(4),
                                decoration: BoxDecoration(
                                  color: Colors.red,
                                  borderRadius: BorderRadius.circular(20),
                                ),
                                child: const Icon(
                                  Icons.close,
                                  color: Colors.white,
                                  size: 16,
                                ),
                              ),
                            ),
                          ),
                        ],
                      );
                    },
                  ),
                ),
                const SizedBox(height: 24),
              ],

              // Action Buttons
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: _takePhoto,
                      icon: const Icon(Icons.camera_alt),
                      label: const Text('التقاط صورة'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF0099D8),
                        padding: const EdgeInsets.symmetric(vertical: 16),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: _pickImages,
                      icon: const Icon(Icons.photo_library),
                      label: const Text('اختيار من المعرض'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF1B2E4D),
                        padding: const EdgeInsets.symmetric(vertical: 16),
                      ),
                    ),
                  ),
                ],
              ),

              const Spacer(),

              // Upload Button
              if (_selectedPhotos.isNotEmpty)
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    onPressed: _isUploading ? null : _uploadPhotos,
                    icon: _isUploading
                        ? const SizedBox(
                            width: 20,
                            height: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                            ),
                          )
                        : const Icon(Icons.cloud_upload),
                    label: Text(
                      _isUploading
                          ? 'جاري الرفع... ${(uploadService.uploadProgress * 100).toInt()}%'
                          : 'رفع ${_selectedPhotos.length} صورة',
                    ),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFFD4AF37),
                      foregroundColor: const Color(0xFF0A1428),
                      padding: const EdgeInsets.symmetric(vertical: 18),
                      textStyle: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildUploadTypeCard({
    required IconData icon,
    required String title,
    required bool isSelected,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: isSelected ? const Color(0xFF0099D8).withOpacity(0.2) : const Color(0xFF1B2E4D),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isSelected ? const Color(0xFF0099D8) : Colors.transparent,
          width: 2,
        ),
      ),
      child: Column(
        children: [
          Icon(
            icon,
            color: isSelected ? const Color(0xFF0099D8) : Colors.white70,
            size: 32,
          ),
          const SizedBox(height: 8),
          Text(
            title,
            style: TextStyle(
              color: isSelected ? const Color(0xFF0099D8) : Colors.white70,
              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
            ),
          ),
        ],
      ),
    );
  }
}
