import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../../core/navigation/app_router.dart';

class PremiumTrainingScreen extends StatefulWidget {
  const PremiumTrainingScreen({super.key});

  @override
  State<PremiumTrainingScreen> createState() => _PremiumTrainingScreenState();
}

class _PremiumTrainingScreenState extends State<PremiumTrainingScreen> {

  // Navigation helper method
  void _navigateToLogin() {
    AppRouter.goLogin(context);
  }

  // ألوان احترافية لشركة شحن
  static const Color corporateBlue = Color(0xFF1E3A8A);
  static const Color accentOrange = Color(0xFFF97316);
  static const Color backgroundDark = Color(0xFF0F172A);
  static const Color surfaceLight = Color(0xFF1E293B);
  static const Color textWhite = Color(0xFFFFFFFF);
  static const Color textGray = Color(0xFF94A3B8);

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final isMobile = screenWidth < 768;
    final isTablet = screenWidth >= 768 && screenWidth < 1024;
    final isDesktop = screenWidth >= 1024;
    
    return Scaffold(
      backgroundColor: backgroundDark,
      body: Directionality(
        textDirection: TextDirection.rtl,
        child: SafeArea(
          child: LayoutBuilder(
            builder: (context, constraints) {
              if (isDesktop) {
                return _buildDesktopLayout();
              } else if (isTablet) {
                return _buildTabletLayout();
              } else {
                return _buildMobileLayout();
              }
            },
          ),
        ),
      ),
    );
  }

  // Desktop Layout
  Widget _buildDesktopLayout() {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(48),
        child: Column(
          children: [
            // Header Section
            Container(
              width: double.infinity,
              constraints: const BoxConstraints(maxWidth: 1200),
              height: 400,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [
                    corporateBlue,
                    corporateBlue.withOpacity(0.8),
                  ],
                ),
                borderRadius: BorderRadius.circular(20),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.3),
                    blurRadius: 20,
                    offset: const Offset(0, 10),
                  ),
                ],
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // Truck Image
                  Container(
                    width: 300,
                    height: 250,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: accentOrange,
                        width: 4,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.4),
                          blurRadius: 20,
                          offset: const Offset(0, 10),
                        ),
                      ],
                    ),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(12),
                      child: Image.asset(
                        'assets/images/truck.jpg',
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) {
                          return Container(
                            decoration: BoxDecoration(
                              color: accentOrange.withOpacity(0.1),
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: Icon(
                              Icons.local_shipping,
                              color: accentOrange,
                              size: 80,
                            ),
                          );
                        },
                      ),
                    ),
                  ).animate().scale(duration: 800.ms, curve: Curves.elasticOut),
                  
                  // Company Info
                  Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'إدهام للنقل اللوجستي',
                        style: GoogleFonts.cairo(
                          fontSize: 48,
                          fontWeight: FontWeight.bold,
                          color: textWhite,
                          letterSpacing: 2,
                        ),
                      ).animate().fadeIn(delay: 400.ms).slideY(begin: 0.3, end: 0),
                      
                      const SizedBox(height: 16),
                      
                      Text(
                        'ثقة في كل شحنة',
                        style: GoogleFonts.cairo(
                          fontSize: 24,
                          color: textWhite.withOpacity(0.9),
                          fontWeight: FontWeight.w500,
                        ),
                      ).animate().fadeIn(delay: 600.ms).slideY(begin: 0.3, end: 0),
                      
                      const SizedBox(height: 32),
                      
                      // CTA Button
                      SizedBox(
                        width: 200,
                        height: 60,
                        child: ElevatedButton(
                          onPressed: _navigateToLogin,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: accentOrange,
                            foregroundColor: textWhite,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(30),
                            ),
                            elevation: 0,
                          ),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                'ابدأ الآن',
                                style: GoogleFonts.cairo(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(width: 8),
                              const Icon(Icons.arrow_forward, size: 24),
                            ],
                          ),
                        ).animate().fadeIn(delay: 800.ms).scale(begin: const Offset(0.9, 0.9)),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            
            const SizedBox(height: 48),
            
            // Services Section
            Container(
              constraints: const BoxConstraints(maxWidth: 1200),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'خدماتنا الاحترافية',
                    style: GoogleFonts.cairo(
                      fontSize: 36,
                      fontWeight: FontWeight.bold,
                      color: textWhite,
                    ),
                  ).animate().fadeIn(delay: 1000.ms),
                  
                  const SizedBox(height: 32),
                  
                  // Service Cards Grid
                  GridView.count(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    crossAxisCount: 4,
                    crossAxisSpacing: 24,
                    mainAxisSpacing: 24,
                    childAspectRatio: 1,
                    children: [
                      _buildServiceCard(Icons.gps_fixed, 'تتبع مباشر', 'GPS متقدم', 0),
                      _buildServiceCard(Icons.security, 'أمان شامل', 'تأمين كامل', 200),
                      _buildServiceCard(Icons.schedule, 'توصيل سريع', 'في الوقت المحدد', 400),
                      _buildServiceCard(Icons.support_agent, 'دعم 24/7', 'فريق متخصص', 600),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // Tablet Layout
  Widget _buildTabletLayout() {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          children: [
            // Header Section
            Container(
              width: double.infinity,
              height: 350,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [
                    corporateBlue,
                    corporateBlue.withOpacity(0.8),
                  ],
                ),
                borderRadius: BorderRadius.circular(20),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.3),
                    blurRadius: 15,
                    offset: const Offset(0, 8),
                  ),
                ],
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Truck Image
                  Container(
                    width: 250,
                    height: 180,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: accentOrange,
                        width: 3,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.3),
                          blurRadius: 15,
                          offset: const Offset(0, 8),
                        ),
                      ],
                    ),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(13),
                      child: Image.asset(
                        'assets/images/truck.jpg',
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) {
                          return Container(
                            decoration: BoxDecoration(
                              color: accentOrange.withOpacity(0.1),
                              borderRadius: BorderRadius.circular(13),
                            ),
                            child: Icon(
                              Icons.local_shipping,
                              color: accentOrange,
                              size: 70,
                            ),
                          );
                        },
                      ),
                    ),
                  ).animate().scale(duration: 800.ms, curve: Curves.elasticOut),
                  
                  const SizedBox(height: 24),
                  
                  // Company Name
                  Text(
                    'إدهام للنقل اللوجستي',
                    style: GoogleFonts.cairo(
                      fontSize: 36,
                      fontWeight: FontWeight.bold,
                      color: textWhite,
                      letterSpacing: 1,
                    ),
                    textAlign: TextAlign.center,
                  ).animate().fadeIn(delay: 400.ms).slideY(begin: 0.3, end: 0),
                  
                  const SizedBox(height: 8),
                  
                  // Tagline
                  Text(
                    'ثقة في كل شحنة',
                    style: GoogleFonts.cairo(
                      fontSize: 20,
                      color: textWhite.withOpacity(0.9),
                      fontWeight: FontWeight.w500,
                    ),
                    textAlign: TextAlign.center,
                  ).animate().fadeIn(delay: 600.ms).slideY(begin: 0.3, end: 0),
                ],
              ),
            ),
            
            const SizedBox(height: 32),
            
            // Content Section
            Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Services Title
                  Text(
                    'خدماتنا الاحترافية',
                    style: GoogleFonts.cairo(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: textWhite,
                    ),
                  ).animate().fadeIn(delay: 800.ms),
                  
                  const SizedBox(height: 24),
                  
                  // Service Cards Grid
                  GridView.count(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    crossAxisCount: 2,
                    crossAxisSpacing: 16,
                    mainAxisSpacing: 16,
                    childAspectRatio: 1.2,
                    children: [
                      _buildServiceCard(Icons.gps_fixed, 'تتبع مباشر', 'GPS متقدم', 0),
                      _buildServiceCard(Icons.security, 'أمان شامل', 'تأمين كامل', 200),
                      _buildServiceCard(Icons.schedule, 'توصيل سريع', 'في الوقت المحدد', 400),
                      _buildServiceCard(Icons.support_agent, 'دعم 24/7', 'فريق متخصص', 600),
                    ],
                  ),
                  
                  const SizedBox(height: 32),
                  
                  // CTA Button
                  SizedBox(
                    width: double.infinity,
                    height: 56,
                    child: ElevatedButton(
                      onPressed: _navigateToLogin,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: accentOrange,
                        foregroundColor: textWhite,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(28),
                        ),
                        elevation: 0,
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            'ابدأ الآن',
                            style: GoogleFonts.cairo(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(width: 8),
                          const Icon(Icons.arrow_forward, size: 20),
                        ],
                      ),
                    ).animate().fadeIn(delay: 1000.ms).scale(begin: const Offset(0.9, 0.9)),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // Mobile Layout
  Widget _buildMobileLayout() {
    return SingleChildScrollView(
      child: Column(
        children: [
          // Header Section
          Container(
            width: double.infinity,
            height: 300,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [
                  corporateBlue,
                  corporateBlue.withOpacity(0.8),
                ],
              ),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Truck Image
                Container(
                  width: 180,
                  height: 120,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: accentOrange,
                      width: 2,
                    ),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.3),
                        blurRadius: 10,
                        offset: const Offset(0, 5),
                      ),
                    ],
                  ),
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(10),
                    child: Image.asset(
                      'assets/images/truck.jpg',
                      fit: BoxFit.cover,
                      errorBuilder: (context, error, stackTrace) {
                        return Container(
                          decoration: BoxDecoration(
                            color: accentOrange.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: Icon(
                            Icons.local_shipping,
                            color: accentOrange,
                            size: 50,
                          ),
                        );
                      },
                    ),
                  ),
                ).animate().scale(duration: 800.ms, curve: Curves.elasticOut),
                
                const SizedBox(height: 20),
                
                // Company Name
                Text(
                  'إدهام للنقل اللوجستي',
                  style: GoogleFonts.cairo(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: textWhite,
                    letterSpacing: 0.5,
                  ),
                  textAlign: TextAlign.center,
                ).animate().fadeIn(delay: 400.ms).slideY(begin: 0.3, end: 0),
                
                const SizedBox(height: 8),
                
                // Tagline
                Text(
                  'ثقة في كل شحنة',
                  style: GoogleFonts.cairo(
                    fontSize: 16,
                    color: textWhite.withOpacity(0.9),
                    fontWeight: FontWeight.w500,
                  ),
                  textAlign: TextAlign.center,
                ).animate().fadeIn(delay: 600.ms).slideY(begin: 0.3, end: 0),
              ],
            ),
          ),
          
          // Content Section
          Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Services Title
                Text(
                  'خدماتنا الاحترافية',
                  style: GoogleFonts.cairo(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: textWhite,
                  ),
                ).animate().fadeIn(delay: 800.ms),
                
                const SizedBox(height: 20),
                
                // Service Cards Grid
                GridView.count(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  crossAxisCount: 2,
                  crossAxisSpacing: 12,
                  mainAxisSpacing: 12,
                  childAspectRatio: 1.1,
                  children: [
                    _buildServiceCard(Icons.gps_fixed, 'تتبع مباشر', 'GPS', 0),
                    _buildServiceCard(Icons.security, 'أمان شامل', 'تأمين', 200),
                    _buildServiceCard(Icons.schedule, 'توصيل سريع', 'سريع', 400),
                    _buildServiceCard(Icons.support_agent, 'دعم 24/7', 'دعم', 600),
                  ],
                ),
                
                const SizedBox(height: 24),
                
                // CTA Button
                SizedBox(
                  width: double.infinity,
                  height: 50,
                  child: ElevatedButton(
                    onPressed: _navigateToLogin,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: accentOrange,
                      foregroundColor: textWhite,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(25),
                      ),
                      elevation: 0,
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'ابدأ الآن',
                          style: GoogleFonts.cairo(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(width: 8),
                        const Icon(Icons.arrow_forward, size: 18),
                      ],
                    ),
                  ).animate().fadeIn(delay: 1000.ms).scale(begin: const Offset(0.9, 0.9)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildServiceCard(IconData icon, String title, String subtitle, int delay) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: surfaceLight,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: accentOrange.withOpacity(0.3),
          width: 1,
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              color: accentOrange.withOpacity(0.1),
              borderRadius: BorderRadius.circular(24),
            ),
            child: Icon(
              icon,
              color: accentOrange,
              size: 24,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            title,
            style: GoogleFonts.cairo(
              fontSize: 14,
              fontWeight: FontWeight.bold,
              color: textWhite,
            ),
            textAlign: TextAlign.center,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 4),
          Text(
            subtitle,
            style: GoogleFonts.cairo(
              fontSize: 11,
              color: textGray,
            ),
            textAlign: TextAlign.center,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    ).animate().fadeIn(delay: delay.ms).scale(begin: const Offset(0.8, 0.8));
  }
}

