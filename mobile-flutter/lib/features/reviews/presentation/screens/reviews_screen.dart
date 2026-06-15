// ============================================
// ⭐ Reviews Screen - التقييمات والمراجعات
// ============================================

import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class ReviewsScreen extends StatefulWidget {
  const ReviewsScreen({super.key});

  @override
  State<ReviewsScreen> createState() => _ReviewsScreenState();
}

class _ReviewsScreenState extends State<ReviewsScreen> {
  int _selectedTab = 0;
  final List<String> _tabs = ['الكل', '5 نجوم', '4 نجوم', '3+ نجوم'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.backgroundColor,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          'التقييمات',
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
            icon: const Icon(Icons.sort, color: Colors.white),
          ),
        ],
      ),
      body: Column(
        children: [
          // Rating Summary
          _buildRatingSummary(),
          
          const SizedBox(height: 16),
          
          // Rating Breakdown
          _buildRatingBreakdown(),
          
          const SizedBox(height: 16),
          
          // Filter Tabs
          _buildFilterTabs(),
          
          const SizedBox(height: 8),
          
          // Reviews List
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _getReviews().length,
              itemBuilder: (context, index) {
                final review = _getReviews()[index];
                return _ReviewCard(review: review);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRatingSummary() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.primaryColor, AppTheme.primaryDark],
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Big Rating
          Column(
            children: [
              Row(
                children: [
                  const Text(
                    '4.8',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 48,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(width: 8),
                  Icon(
                    Icons.star,
                    color: AppTheme.warningColor,
                    size: 32,
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Text(
                'من 5 نجوم',
                style: TextStyle(
                  color: Colors.white.withOpacity(0.8),
                  fontSize: 14,
                ),
              ),
            ],
          ),
          
          const SizedBox(width: 40),
          
          // Stats
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildStatRow('إجمالي التقييمات', '156'),
              const SizedBox(height: 4),
              _buildStatRow('النسبة المئوية', '96%'),
              const SizedBox(height: 4),
              _buildStatRow('الشهر الحالي', '+12'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatRow(String label, String value) {
    return Row(
      children: [
        Text(
          label,
          style: TextStyle(
            color: Colors.white.withOpacity(0.7),
            fontSize: 12,
          ),
        ),
        const SizedBox(width: 8),
        Text(
          value,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 12,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }

  Widget _buildRatingBreakdown() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        children: [
          _buildRatingBar(5, 120, 156),
          const SizedBox(height: 8),
          _buildRatingBar(4, 24, 156),
          const SizedBox(height: 8),
          _buildRatingBar(3, 8, 156),
          const SizedBox(height: 8),
          _buildRatingBar(2, 4, 156),
          const SizedBox(height: 8),
          _buildRatingBar(1, 0, 156),
        ],
      ),
    );
  }

  Widget _buildRatingBar(int stars, int count, int total) {
    final percentage = (count / total * 100).toInt();
    
    return Row(
      children: [
        Text(
          '$stars',
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 12,
          ),
        ),
        const SizedBox(width: 4),
        Icon(
          Icons.star,
          color: AppTheme.warningColor,
          size: 12,
        ),
        const SizedBox(width: 8),
        
        // Progress Bar
        Expanded(
          child: Container(
            height: 6,
            decoration: BoxDecoration(
              color: AppTheme.backgroundColor,
              borderRadius: BorderRadius.circular(3),
            ),
            child: FractionallySizedBox(
              alignment: Alignment.centerRight,
              widthFactor: count / total,
              child: Container(
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [AppTheme.warningColor, AppTheme.warningColor.withOpacity(0.7)],
                  ),
                  borderRadius: BorderRadius.circular(3),
                ),
              ),
            ),
          ),
        ),
        
        const SizedBox(width: 8),
        
        Text(
          '$count',
          style: TextStyle(
            color: AppTheme.textSecondary,
            fontSize: 12,
          ),
        ),
      ],
    );
  }

  Widget _buildFilterTabs() {
    return Container(
      height: 40,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _tabs.length,
        itemBuilder: (context, index) {
          final isSelected = _selectedTab == index;
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(_tabs[index]),
              selected: isSelected,
              onSelected: (selected) {
                setState(() {
                  _selectedTab = index;
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

  List<ReviewModel> _getReviews() {
    return [
      ReviewModel(
        id: '1',
        reviewerName: 'شركة الأمل للمواد الغذائية',
        reviewerAvatar: null,
        rating: 5,
        date: '15 يناير 2024',
        content: 'خدمة ممتازة جداً! السائق كان محترف والشحنة وصلت في الوقت المحدد ودرجة الحرارة كانت مثالية طوال الرحلة. شكراً لفريق إدهام.',
        shipmentId: 'EDH-2024-156',
        isVerified: true,
        helpfulCount: 12,
        ratings: {
          'punctuality': 5,
          'professionalism': 5,
          'cargoHandling': 5,
          'communication': 5,
          'temperatureMaintenance': 5,
        },
      ),
      ReviewModel(
        id: '2',
        reviewerName: 'مؤسسة النور للصيدليات',
        reviewerAvatar: null,
        rating: 4,
        date: '12 يناير 2024',
        content: 'تجربة جيدة بشكل عام. التواصل كان ممتاز ولكن هناك تأخير بسيط في الوصول بسبب الازدحام. أنصح بالتعامل معهم.',
        shipmentId: 'EDH-2024-148',
        isVerified: true,
        helpfulCount: 8,
        ratings: {
          'punctuality': 3,
          'professionalism': 5,
          'cargoHandling': 4,
          'communication': 5,
          'temperatureMaintenance': 5,
        },
      ),
      ReviewModel(
        id: '3',
        reviewerName: 'أحمد العلي',
        reviewerAvatar: null,
        rating: 5,
        date: '10 يناير 2024',
        content: 'تعاملت معهم لأول مرة وكانت تجربة رائعة! النظام التقني يسمح بمتابعة الشحنة لحظة بلحظة وهذا شيء ممتاز جداً.',
        shipmentId: 'EDH-2024-142',
        isVerified: true,
        helpfulCount: 15,
        ratings: {
          'punctuality': 5,
          'professionalism': 5,
          'cargoHandling': 5,
          'communication': 4,
          'temperatureMaintenance': 5,
        },
      ),
    ];
  }
}

class _ReviewCard extends StatelessWidget {
  final ReviewModel review;

  const _ReviewCard({required this.review});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Avatar
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [AppTheme.primaryColor, AppTheme.primaryDark],
                  ),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Center(
                  child: Text(
                    review.reviewerName.split(' ').map((e) => e[0]).take(2).join(''),
                    style: const TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                    ),
                  ),
                ),
              ),
              
              const SizedBox(width: 12),
              
              // Info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      review.reviewerName,
                      style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                        fontSize: 15,
                      ),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      review.date,
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 12,
                      ),
                    ),
                    const SizedBox(height: 4),
                    // Rating Stars
                    Row(
                      children: [
                        ...List.generate(5, (index) {
                          return Icon(
                            index < review.rating
                                ? Icons.star
                                : Icons.star_border,
                            color: AppTheme.warningColor,
                            size: 16,
                          );
                        }),
                        const SizedBox(width: 8),
                        if (review.isVerified)
                          Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 6,
                              vertical: 2,
                            ),
                            decoration: BoxDecoration(
                              color: AppTheme.successColor.withOpacity(0.1),
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Icon(
                                  Icons.verified,
                                  color: AppTheme.successColor,
                                  size: 12,
                                ),
                                const SizedBox(width: 2),
                                Text(
                                  'موثق',
                                  style: TextStyle(
                                    color: AppTheme.successColor,
                                    fontSize: 10,
                                  ),
                                ),
                              ],
                            ),
                          ),
                      ],
                    ),
                  ],
                ),
              ),
              
              // Shipment ID
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppTheme.primaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Text(
                  review.shipmentId,
                  style: TextStyle(
                    color: AppTheme.primaryColor,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Review Content
          Text(
            review.content,
            style: TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 14,
              height: 1.5,
            ),
          ),
          
          // Detailed Ratings (if available)
          if (review.ratings != null) ...[
            const SizedBox(height: 12),
            _buildDetailedRatings(review.ratings!),
          ],
          
          const SizedBox(height: 12),
          
          // Footer
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Helpful Button
              TextButton.icon(
                onPressed: () {},
                icon: Icon(
                  Icons.thumb_up_outlined,
                  color: AppTheme.textSecondary,
                  size: 18,
                ),
                label: Text(
                  'مفيد (${review.helpfulCount})',
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
              ),
              
              // Report Button
              TextButton.icon(
                onPressed: () {},
                icon: Icon(
                  Icons.flag_outlined,
                  color: AppTheme.textSecondary,
                  size: 18,
                ),
                label: Text(
                  'إبلاغ',
                  style: TextStyle(
                    color: AppTheme.textSecondary,
                    fontSize: 12,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDetailedRatings(Map<String, int> ratings) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppTheme.backgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Wrap(
        spacing: 16,
        runSpacing: 8,
        children: ratings.entries.map((entry) {
          final labels = {
            'punctuality': 'الالتزام بالوقت',
            'professionalism': 'الاحترافية',
            'cargoHandling': 'التعامل مع الشحنة',
            'communication': 'التواصل',
            'temperatureMaintenance': 'حفظ درجة الحرارة',
          };
          
          return Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                labels[entry.key] ?? entry.key,
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontSize: 11,
                ),
              ),
              const SizedBox(width: 4),
              Row(
                children: [
                  Text(
                    '${entry.value}',
                    style: TextStyle(
                      color: AppTheme.warningColor,
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Icon(
                    Icons.star,
                    color: AppTheme.warningColor,
                    size: 10,
                  ),
                ],
              ),
            ],
          );
        }).toList(),
      ),
    );
  }
}

// Model
class ReviewModel {
  final String id;
  final String reviewerName;
  final String? reviewerAvatar;
  final int rating;
  final String date;
  final String content;
  final String shipmentId;
  final bool isVerified;
  final int helpfulCount;
  final Map<String, int>? ratings;

  ReviewModel({
    required this.id,
    required this.reviewerName,
    this.reviewerAvatar,
    required this.rating,
    required this.date,
    required this.content,
    required this.shipmentId,
    this.isVerified = false,
    this.helpfulCount = 0,
    this.ratings,
  });
}
