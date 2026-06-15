// ============================================
// 📄 Documents Screen - إدارة المستندات
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class DocumentsScreen extends StatefulWidget {
  const DocumentsScreen({super.key});

  @override
  State<DocumentsScreen> createState() => _DocumentsScreenState();
}

class _DocumentsScreenState extends State<DocumentsScreen> {
  int _selectedCategory = 0;
  final List<String> _categories = ['الكل', 'عقود', 'فواتير', 'إيصالات', 'شهادات', 'تأمين'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'المستندات',
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
            icon: const Icon(Icons.filter_list, color: Colors.white),
          ),
        ],
      ),
      body: Column(
        children: [
          // Storage Usage
          _buildStorageUsage(),
          
          const SizedBox(height: 16),
          
          // Category Filter
          _buildCategoryFilter(),
          
          const SizedBox(height: 8),
          
          // Documents Grid
          Expanded(
            child: GridView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                childAspectRatio: 0.85,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
              ),
              itemCount: _getDocuments().length,
              itemBuilder: (context, index) {
                final doc = _getDocuments()[index];
                return _DocumentCard(document: doc);
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {},
        backgroundColor: AppTheme.primaryColor,
        icon: const Icon(Icons.upload_file),
        label: const Text('رفع مستند'),
      ),
    );
  }

  Widget _buildStorageUsage() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'استخدام التخزين',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                '4.2 GB من 10 GB',
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 13,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          
          // Progress Bar
          Container(
            height: 8,
            decoration: BoxDecoration(
              color: AppTheme.backgroundColor,
              borderRadius: BorderRadius.circular(4),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerRight,
              widthFactor: 0.42,
              child: Container(
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [AppTheme.primaryColor, AppTheme.primaryDark],
                  ),
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
            ),
          ),
          
          const SizedBox(height: 12),
          
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              _buildFileTypeStat(Icons.picture_as_pdf, Colors.red, 'PDF', '42%'),
              _buildFileTypeStat(Icons.image, Colors.blue, 'صور', '28%'),
              _buildFileTypeStat(Icons.description, Colors.green, 'مستندات', '18%'),
              _buildFileTypeStat(Icons.folder, Colors.orange, 'أخرى', '12%'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildFileTypeStat(IconData icon, Color color, String label, String percent) {
    return Row(
      children: [
        Icon(icon, color: color, size: 16),
        const SizedBox(width: 4),
        Text(
          '$label $percent',
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 11,
          ),
        ),
      ],
    );
  }

  Widget _buildCategoryFilter() {
    return Container(
      height: 40,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _categories.length,
        itemBuilder: (context, index) {
          final isSelected = _selectedCategory == index;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(_categories[index]),
              selected: isSelected,
              onSelected: (selected) {
                setState(() {
                  _selectedCategory = index;
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

  List<DocumentModel> _getDocuments() {
    return [
      DocumentModel(
        id: '1',
        name: 'عقد الخدمة 2024.pdf',
        type: 'contract',
        extension: 'pdf',
        size: '2.4 MB',
        date: '15 يناير 2024',
        isSigned: true,
        thumbnail: null,
        hasOcr: true,
      ),
      DocumentModel(
        id: '2',
        name: 'فاتورة EDH-2024-156.pdf',
        type: 'invoice',
        extension: 'pdf',
        size: '856 KB',
        date: '14 يناير 2024',
        isSigned: false,
        thumbnail: null,
      ),
      DocumentModel(
        id: '3',
        name: 'إيصال تسليم الشحنة.jpg',
        type: 'receipt',
        extension: 'jpg',
        size: '1.2 MB',
        date: '13 يناير 2024',
        isSigned: true,
        thumbnail: null,
      ),
      DocumentModel(
        id: '4',
        name: 'شهادة التأمين.pdf',
        type: 'insurance',
        extension: 'pdf',
        size: '1.8 MB',
        date: '1 يناير 2024',
        isSigned: true,
        thumbnail: null,
      ),
      DocumentModel(
        id: '5',
        name: 'رخصة السائق.jpg',
        type: 'license',
        extension: 'jpg',
        size: '756 KB',
        date: '10 يناير 2024',
        isSigned: false,
        thumbnail: null,
        hasQr: true,
      ),
      DocumentModel(
        id: '6',
        name: 'تقرير الصيانة.pdf',
        type: 'maintenance',
        extension: 'pdf',
        size: '3.2 MB',
        date: '5 يناير 2024',
        isSigned: true,
        thumbnail: null,
      ),
    ];
  }
}

class _DocumentCard extends StatelessWidget {
  final DocumentModel document;

  const _DocumentCard({required this.document});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header with actions
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // File Icon
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: _getFileColor(document.extension).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(
                  _getFileIcon(document.extension),
                  color: _getFileColor(document.extension),
                  size: 20,
                ),
              ),
              
              // Actions
              Row(
                children: [
                  if (document.isSigned)
                    Container(
                      padding: const EdgeInsets.all(4),
                      decoration: BoxDecoration(
                        color: AppTheme.successColor.withOpacity(0.1),
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        Icons.verified,
                        color: AppTheme.successColor,
                        size: 14,
                      ),
                    ),
                  const SizedBox(width: 4),
                  PopupMenuButton<String>(
                    icon: Icon(
                      Icons.more_vert,
                      color: AppTheme.textSecondary,
                      size: 18,
                    ),
                    itemBuilder: (context) => [
                      const PopupMenuItem(
                        value: 'view',
                        child: Text('عرض'),
                      ),
                      const PopupMenuItem(
                        value: 'download',
                        child: Text('تحميل'),
                      ),
                      const PopupMenuItem(
                        value: 'share',
                        child: Text('مشاركة'),
                      ),
                      const PopupMenuItem(
                        value: 'delete',
                        child: Text('حذف'),
                      ),
                    ],
                  ),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Document Name
          Text(
            document.name,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 13,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          
          const Spacer(),
          
          // Footer
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Type Badge
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppTheme.primaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Text(
                  _getTypeLabel(document.type),
                  style: TextStyle(
                    color: AppTheme.primaryColor,
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              
              // Size
              Text(
                document.size,
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 11,
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 8),
          
          // Date & Features
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                document.date,
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 10,
                ),
              ),
              Row(
                children: [
                  if (document.hasOcr)
                    Icon(
                      Icons.document_scanner,
                      color: AppTheme.primaryColor,
                      size: 14,
                    ),
                  if (document.hasQr)
                    Icon(
                      Icons.qr_code,
                      color: AppTheme.primaryColor,
                      size: 14,
                    ),
                ],
              ),
            ],
          ),
        ],
      ),
    );
  }

  Color _getFileColor(String extension) {
    switch (extension.toLowerCase()) {
      case 'pdf':
        return Colors.red;
      case 'jpg':
      case 'jpeg':
      case 'png':
        return Colors.blue;
      case 'doc':
      case 'docx':
        return Colors.blue.shade700;
      case 'xls':
      case 'xlsx':
        return Colors.green;
      default:
        return Colors.grey;
    }
  }

  IconData _getFileIcon(String extension) {
    switch (extension.toLowerCase()) {
      case 'pdf':
        return Icons.picture_as_pdf;
      case 'jpg':
      case 'jpeg':
      case 'png':
        return Icons.image;
      case 'doc':
      case 'docx':
        return Icons.description;
      case 'xls':
      case 'xlsx':
        return Icons.table_chart;
      default:
        return Icons.insert_drive_file;
    }
  }

  String _getTypeLabel(String type) {
    switch (type) {
      case 'contract':
        return 'عقد';
      case 'invoice':
        return 'فاتورة';
      case 'receipt':
        return 'إيصال';
      case 'insurance':
        return 'تأمين';
      case 'license':
        return 'رخصة';
      case 'maintenance':
        return 'صيانة';
      default:
        return 'مستند';
    }
  }
}

// Model
class DocumentModel {
  final String id;
  final String name;
  final String type;
  final String extension;
  final String size;
  final String date;
  final bool isSigned;
  final String? thumbnail;
  final bool hasOcr;
  final bool hasQr;

  DocumentModel({
    required this.id,
    required this.name,
    required this.type,
    required this.extension,
    required this.size,
    required this.date,
    this.isSigned = false,
    this.thumbnail,
    this.hasOcr = false,
    this.hasQr = false,
  });
}
