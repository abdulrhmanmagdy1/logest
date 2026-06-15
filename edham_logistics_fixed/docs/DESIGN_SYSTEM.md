# 🎨 Edham Logistics Design System

## 📋 Overview

The Edham Logistics Design System is a comprehensive guide for creating premium, enterprise-grade logistics applications with cinematic UI/UX. This system emphasizes glassmorphism, dark themes with orange accents, and sophisticated animations to deliver a professional logistics platform experience.

## 🎯 Design Philosophy

### Core Principles
- **Premium First**: Every element should feel enterprise-grade and professional
- **Cinematic Experience**: Smooth animations and transitions that delight users
- **Glassmorphism**: Modern glass effects with blur, transparency, and depth
- **Dark Theme**: Professional dark backgrounds with vibrant orange accents
- **Arabic First**: RTL support with Cairo typography for Arabic text
- **Accessibility**: High contrast ratios and clear visual hierarchy

## 🎨 Color Palette

### Primary Colors
```dart
// Brand Orange (Edham Primary)
static const Color primary = Color(0xFFF97316);  // Bright Orange
static const Color primaryDark = Color(0xFFEA580C);  // Dark Orange
static const Color primaryLight = Color(0xFFFDBA74);  // Light Orange

// Dark Theme Backgrounds
static const Color background = Color(0xFF0A1128);  // Deep Navy
static const Color surface = Color(0xFF1E293B);  // Dark Gray
static const Color card = Color(0xFF334155);  // Medium Gray
```

### Semantic Colors
```dart
// Success
static const Color success = Color(0xFF10B981);  // Emerald
static const Color successLight = Color(0xFF34D399);  // Light Emerald

// Accent
static const Color accent = Color(0xFF8B5CF6);  // Purple
static const Color accentLight = Color(0xFFA78BFA);  // Light Purple

// Error
static const Color error = Color(0xFFEF4444);  // Red
static const Color errorLight = Color(0xFFF87171);  // Light Red

// Text Colors
static const Color textPrimary = Color(0xFFF1F5F9);  // Light Gray
static const Color textSecondary = Color(0xFF94A3B8);  // Medium Gray
static const Color textHint = Color(0xFF64748B);  // Dark Gray
```

## 📐 Typography

### Font Family
- **Primary**: Cairo (Google Fonts) - Optimized for Arabic
- **Secondary**: Inter - For English text and numbers

### Type Scale
```dart
// Display
static TextStyle displayLarge = TextStyle(
  fontSize: 32,
  fontWeight: FontWeight.bold,
  color: textPrimary,
  fontFamily: 'Cairo',
);

static TextStyle displayMedium = TextStyle(
  fontSize: 28,
  fontWeight: FontWeight.bold,
  color: textPrimary,
  fontFamily: 'Cairo',
);

// Headlines
static TextStyle headlineLarge = TextStyle(
  fontSize: 24,
  fontWeight: FontWeight.bold,
  color: textPrimary,
  fontFamily: 'Cairo',
);

static TextStyle headlineMedium = TextStyle(
  fontSize: 20,
  fontWeight: FontWeight.w600,
  color: textPrimary,
  fontFamily: 'Cairo',
);

// Titles
static TextStyle titleLarge = TextStyle(
  fontSize: 18,
  fontWeight: FontWeight.w600,
  color: textPrimary,
  fontFamily: 'Cairo',
);

static TextStyle titleMedium = TextStyle(
  fontSize: 16,
  fontWeight: FontWeight.w500,
  color: textPrimary,
  fontFamily: 'Cairo',
);

// Body
static TextStyle bodyLarge = TextStyle(
  fontSize: 16,
  fontWeight: FontWeight.normal,
  color: textPrimary,
  fontFamily: 'Cairo',
);

static TextStyle bodyMedium = TextStyle(
  fontSize: 14,
  fontWeight: FontWeight.normal,
  color: textSecondary,
  fontFamily: 'Cairo',
);

// Small
static TextStyle bodySmall = TextStyle(
  fontSize: 12,
  fontWeight: FontWeight.normal,
  color: textHint,
  fontFamily: 'Cairo',
);
```

## 🎭 Glassmorphism Effects

### Glass Container
```dart
class GlassContainer extends StatelessWidget {
  final Widget child;
  final double radius;
  final Color backgroundColor;
  final Color borderColor;
  final List<BoxShadow> boxShadow;
  final EdgeInsetsGeometry padding;
  final double blur;

  const GlassContainer({
    required this.child,
    this.radius = 16,
    this.backgroundColor = Colors.white.withOpacity(0.05),
    this.borderColor = Colors.white.withOpacity(0.1),
    this.boxShadow = AppShadows.glassmorphism,
    this.padding = const EdgeInsets.all(16),
    this.blur = 10,
  });
}
```

### Shadow System
```dart
class AppShadows {
  static List<BoxShadow> glassmorphism = [
    BoxShadow(
      color: Colors.black.withOpacity(0.1),
      blurRadius: 10,
      spreadRadius: 0,
      offset: const Offset(0, 4),
    ),
    BoxShadow(
      color: Colors.white.withOpacity(0.05),
      blurRadius: 20,
      spreadRadius: 0,
      offset: const Offset(0, -2),
    ),
  ];

  static List<BoxShadow> glowing(Color color, {double intensity = 0.3}) {
    return [
      BoxShadow(
        color: color.withOpacity(intensity),
        blurRadius: 20,
        spreadRadius: 0,
        offset: const Offset(0, 4),
      ),
      BoxShadow(
        color: color.withOpacity(intensity * 0.5),
        blurRadius: 40,
        spreadRadius: 0,
        offset: const Offset(0, 8),
      ),
    ];
  }
}
```

## 🎬 Animations

### Animation Patterns
- **Staggered Animations**: Elements appear in sequence with delays
- **Slide Animations**: Smooth slide transitions between screens
- **Scale Animations**: Subtle scale effects for buttons and cards
- **Shimmer Effects**: Premium shimmer for loading states
- **Hero Animations**: Smooth transitions between matching elements

### Animation Durations
```dart
class AnimationDurations {
  static const Duration fast = Duration(milliseconds: 200);
  static const Duration medium = Duration(milliseconds: 400);
  static const Duration slow = Duration(milliseconds: 800);
  static const Duration extraSlow = Duration(milliseconds: 1200);
}
```

### Animation Curves
```dart
class AnimationCurves {
  static const Curve easeOut = Curves.easeOut;
  static const Curve easeInOut = Curves.easeInOut;
  static const Curve bounceOut = Curves.bounceOut;
  static const Curve elasticOut = Curves.elasticOut;
}
```

## 🧩 Components

### Premium Buttons
```dart
class GlowingButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;
  final Color color;
  final IconData? icon;
  final double height;
  final bool isLoading;

  const GlowingButton({
    required this.text,
    required this.onPressed,
    required this.color,
    this.icon,
    this.height = 48,
    this.isLoading = false,
  });
}
```

### Premium Stat Cards
```dart
class PremiumStatCard extends StatelessWidget {
  final String title;
  final String value;
  final String change;
  final IconData icon;
  final Color color;
  final Duration animationDelay;

  const PremiumStatCard({
    required this.title,
    required this.value,
    required this.change,
    required this.icon,
    required this.color,
    this.animationDelay = Duration.zero,
  });
}
```

### Premium Input Fields
```dart
class PremiumTextField extends StatelessWidget {
  final String labelText;
  final IconData? prefixIcon;
  final bool obscureText;
  final TextInputType keyboardType;
  final String? Function(String?) validator;
  final TextEditingController controller;

  const PremiumTextField({
    required this.labelText,
    this.prefixIcon,
    this.obscureText = false,
    this.keyboardType = TextInputType.text,
    this.validator,
    required this.controller,
  });
}
```

## 📐 Spacing System

### Spacing Scale
```dart
class Spacing {
  static const double xs = 4;
  static const double sm = 8;
  static const double md = 16;
  static const double lg = 24;
  static const double xl = 32;
  static const double xxl = 48;
  static const double xxxl = 64;
}
```

### Padding Guidelines
- **Cards**: 16-24px padding
- **Sections**: 24-32px padding
- **Elements**: 8-16px spacing
- **Screens**: 20px horizontal padding

## 🎯 Iconography

### Icon Library
- **Primary**: Lucide Icons
- **Secondary**: Material Icons
- **Custom**: Edham Logistics branded icons

### Icon Sizes
```dart
class IconSizes {
  static const double xs = 16;
  static const double sm = 20;
  static const double md = 24;
  static const double lg = 32;
  static const double xl = 40;
  static const double xxl = 48;
}
```

## 📱 Screen Templates

### Dashboard Layout
```dart
class DashboardTemplate extends StatelessWidget {
  final Widget header;
  final List<Widget> cards;
  final Widget bottomNav;

  const DashboardTemplate({
    required this.header,
    required this.cards,
    required this.bottomNav,
  });
}
```

### Form Layout
```dart
class FormTemplate extends StatelessWidget {
  final String title;
  final List<Widget> fields;
  final Widget submitButton;

  const FormTemplate({
    required this.title,
    required this.fields,
    required this.submitButton,
  });
}
```

## 🎨 Brand Guidelines

### Logo Usage
- **Primary**: Full logo with text
- **Secondary**: Icon only
- **Minimum Size**: 32px height
- **Clear Space**: 2x logo height

### Color Usage
- **Primary Orange**: For CTAs, highlights, and brand elements
- **Dark Backgrounds**: For main content areas
- **White/Gray**: For text and subtle elements
- **Semantic Colors**: For status and feedback

## 🚀 Implementation Guidelines

### Performance
- Use `flutter_animate` for smooth animations
- Implement lazy loading for large lists
- Optimize image assets for mobile
- Use `const` widgets where possible

### Accessibility
- Maintain 4.5:1 contrast ratio for text
- Use semantic labels for screen readers
- Implement proper focus management
- Support both RTL and LTR layouts

### Responsive Design
- Design for mobile-first approach
- Use flexible layouts with `Flex` and `Expanded`
- Implement proper keyboard handling
- Test on various screen sizes

## 📚 Component Library

### Premium Widgets
1. **GlassContainer** - Glassmorphism container
2. **GlowingButton** - Animated button with glow effect
3. **PremiumStatCard** - Statistics card with animations
4. **PremiumTextField** - Styled input field
5. **FloatingNavBar** - Premium navigation bar
6. **ShimmerLoading** - Loading animation
7. **PremiumChip** - Styled chip component
8. **GlassCard** - Card with glass effect

### Layout Components
1. **PremiumAppBar** - App bar with glass effect
2. **GlassBottomSheet** - Modal with glass effect
3. **PremiumDialog** - Dialog with glass effect
4. **GlassListTile** - List tile with glass effect
5. **PremiumSection** - Section with title and content

---

## 🎯 Usage Examples

### Creating a Premium Screen
```dart
class PremiumScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: GlassContainer(
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  Text(
                    'Premium Title',
                    style: Theme.of(context).textTheme.displayLarge,
                  ).animate().fadeIn().slideY(),
                  const SizedBox(height: Spacing.md),
                  PremiumStatCard(
                    title: 'Active Shipments',
                    value: '24',
                    change: '+3 Today',
                    icon: Icons.local_shipping,
                    color: AppTheme.primary,
                  ).animate().fadeIn(delay: const Duration(milliseconds: 200)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
```

### Creating a Premium Button
```dart
GlowingButton(
  text: 'Get Started',
  onPressed: () => Navigator.pushNamed(context, '/home'),
  color: AppTheme.primary,
  icon: Icons.arrow_forward,
).animate().scale().shimmer();
```

---

## 🏆 Conclusion

This design system provides the foundation for creating premium, enterprise-grade logistics applications with the Edham Logistics brand. By following these guidelines, developers can ensure consistency, accessibility, and a professional user experience across all applications.

For questions or contributions, please contact the design team at design@edhamlogistics.com
