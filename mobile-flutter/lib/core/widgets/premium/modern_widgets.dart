import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../glass_container.dart';
import '../smooth_animation_system.dart';
import '../../theme/app_theme.dart';

/// Modern Widget System for Premium UI Components
/// Provides modern, interactive widgets with glassmorphism and animations
class ModernWidgetSystem {
  // Widget animation presets
  static AnimationEffect get widgetEntrance => SmoothAnimationSystem.cardEntrance;
  static AnimationEffect get widgetExit => SmoothAnimationSystem.fadeOut;
  static AnimationEffect get interactionFeedback => SmoothAnimationSystem.scaleIn;
}

/// Modern Floating Action Button
class ModernFloatingActionButton extends StatefulWidget {
  final IconData icon;
  final String? label;
  final VoidCallback onPressed;
  final Color? backgroundColor;
  final Color? foregroundColor;
  final Widget? child;
  final bool extended;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernFloatingActionButton({
    Key? key,
    required this.icon,
    this.label,
    required this.onPressed,
    this.backgroundColor,
    this.foregroundColor,
    this.child,
    this.extended = false,
    this.animationDuration = const Duration(milliseconds: 300),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernFloatingActionButton> createState() => _ModernFloatingActionButtonState();
}

class _ModernFloatingActionButtonState extends State<ModernFloatingActionButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<double> _rotationAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );
    _rotationAnimation = Tween<double>(begin: -0.1, end: 0.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Transform.scale(
          scale: _scaleAnimation.value,
          child: Transform.rotate(
            angle: _rotationAnimation.value,
            child: GlassContainer(
              padding: const EdgeInsets.all(16),
              radius: 16,
              backgroundColor: widget.backgroundColor?.withOpacity(0.9) ?? 
                           AppTheme.primary.withOpacity(0.9),
              borderColor: Colors.white.withOpacity(0.3),
              child: widget.extended && widget.label != null
                  ? Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          widget.icon,
                          color: widget.foregroundColor ?? Colors.white,
                          size: 24,
                        ),
                        const SizedBox(width: 12),
                        Text(
                          widget.label!,
                          style: TextStyle(
                            color: widget.foregroundColor ?? Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    )
                  : widget.child ??
                     Icon(
                       widget.icon,
                       color: widget.foregroundColor ?? Colors.white,
                       size: 24,
                     ),
            ),
          ),
        );
      },
    );
  }
}

/// Modern Search Bar
class ModernSearchBar extends StatefulWidget {
  final String? hintText;
  final ValueChanged<String>? onChanged;
  final VoidCallback? onClear;
  final TextEditingController? controller;
  final bool autofocus;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernSearchBar({
    Key? key,
    this.hintText,
    this.onChanged,
    this.onClear,
    this.controller,
    this.autofocus = false,
    this.animationDuration = const Duration(milliseconds: 300),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernSearchBar> createState() => _ModernSearchBarState();
}

class _ModernSearchBarState extends State<ModernSearchBar>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;
  final TextEditingController _textController = TextEditingController();
  bool _isFocused = false;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
    _slideAnimation = Tween<Offset>(begin: const Offset(0, -0.1), end: Offset.zero).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeOut),
    );
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    _textController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return FadeTransition(
          opacity: _fadeAnimation,
          child: SlideTransition(
            position: _slideAnimation,
            child: GlassContainer(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              radius: 12,
              backgroundColor: AppTheme.surface.withOpacity(0.3),
              borderColor: _isFocused ? AppTheme.primary.withOpacity(0.5) : 
                           AppTheme.surfaceLight.withOpacity(0.3),
              child: Row(
                children: [
                  Icon(
                    Icons.search,
                    color: _isFocused ? AppTheme.primary : AppTheme.textSecondary,
                    size: 20,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: TextField(
                      controller: widget.controller ?? _textController,
                      autofocus: widget.autofocus,
                      onChanged: widget.onChanged,
                      onTap: () => setState(() => _isFocused = true),
                      onSubmitted: (_) => setState(() => _isFocused = false),
                      decoration: InputDecoration(
                        hintText: widget.hintText ?? 'بحث...',
                        hintStyle: TextStyle(
                          color: AppTheme.textHint,
                          fontSize: 16,
                        ),
                        border: InputBorder.none,
                        contentPadding: EdgeInsets.zero,
                      ),
                      style: const TextStyle(
                        color: AppTheme.textPrimary,
                        fontSize: 16,
                      ),
                    ),
                  ),
                  if (widget.controller?.text.isNotEmpty == true || 
                      _textController.text.isNotEmpty) ...[
                    const SizedBox(width: 8),
                    MicroInteractionButton(
                      onPressed: () {
                        widget.controller?.clear();
                        _textController.clear();
                        widget.onClear?.call();
                      },
                      child: Icon(
                        Icons.clear,
                        color: AppTheme.textSecondary,
                        size: 20,
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

/// Modern Status Chip
class ModernStatusChip extends StatefulWidget {
  final String label;
  final Color? backgroundColor;
  final Color? textColor;
  final IconData? icon;
  final VoidCallback? onTap;
  final bool selected;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernStatusChip({
    Key? key,
    required this.label,
    this.backgroundColor,
    this.textColor,
    this.icon,
    this.onTap,
    this.selected = false,
    this.animationDuration = const Duration(milliseconds: 200),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernStatusChip> createState() => _ModernStatusChipState();
}

class _ModernStatusChipState extends State<ModernStatusChip>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<Color?> _colorAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 0.9, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );
    _colorAnimation = ColorTween(
      begin: widget.backgroundColor?.withOpacity(0.3) ?? AppTheme.surface.withOpacity(0.3),
      end: widget.backgroundColor?.withOpacity(0.9) ?? AppTheme.primary.withOpacity(0.9),
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeInOut));
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void didUpdateWidget(ModernStatusChip oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.selected != widget.selected) {
      if (widget.selected) {
        _controller.forward();
      } else {
        _controller.reverse();
      }
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Transform.scale(
          scale: _scaleAnimation.value,
          child: GestureDetector(
            onTap: widget.onTap,
            child: GlassContainer(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              radius: 20,
              backgroundColor: widget.selected ? 
                           _colorAnimation.value :
                           (widget.backgroundColor?.withOpacity(0.3) ?? AppTheme.surface.withOpacity(0.3)),
              borderColor: widget.selected ? 
                         (widget.textColor ?? AppTheme.primary).withOpacity(0.5) :
                         AppTheme.surfaceLight.withOpacity(0.3),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (widget.icon != null) ...[
                    Icon(
                      widget.icon,
                      color: widget.textColor ?? AppTheme.textPrimary,
                      size: 16,
                    ),
                    const SizedBox(width: 6),
                  ],
                  Text(
                    widget.label,
                    style: TextStyle(
                      color: widget.textColor ?? AppTheme.textPrimary,
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

/// Modern Progress Indicator
class ModernProgressIndicator extends StatefulWidget {
  final double value;
  final Color? backgroundColor;
  final Color? progressColor;
  final String? label;
  final double height;
  final BorderRadius? borderRadius;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernProgressIndicator({
    Key? key,
    required this.value,
    this.backgroundColor,
    this.progressColor,
    this.label,
    this.height = 8,
    this.borderRadius,
    this.animationDuration = const Duration(milliseconds: 800),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernProgressIndicator> createState() => _ModernProgressIndicatorState();
}

class _ModernProgressIndicatorState extends State<ModernProgressIndicator>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _progressAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _progressAnimation = Tween<double>(begin: 0.0, end: widget.value).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void didUpdateWidget(ModernProgressIndicator oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.value != widget.value) {
      _progressAnimation = Tween<double>(begin: _progressAnimation.value, end: widget.value).animate(
        CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
      );
      _controller.forward(from: 0.0);
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (widget.label != null) ...[
          Text(
            widget.label!,
            style: const TextStyle(
              color: AppTheme.textSecondary,
              fontSize: 12,
              fontWeight: FontWeight.w500,
            ),
          ),
          const SizedBox(height: 8),
        ],
        GlassContainer(
          height: widget.height,
          radius: widget.borderRadius ?? BorderRadius.circular(4),
          backgroundColor: widget.backgroundColor?.withOpacity(0.3) ?? 
                       AppTheme.surface.withOpacity(0.3),
          borderColor: AppTheme.surfaceLight.withOpacity(0.3),
          child: AnimatedBuilder(
            animation: _progressAnimation,
            builder: (context, child) {
              return FractionallySizedBox(
                alignment: Alignment.centerLeft,
                widthFactor: _progressAnimation.value.clamp(0.0, 1.0),
                child: GlassContainer(
                  radius: widget.borderRadius ?? BorderRadius.circular(4),
                  backgroundColor: widget.progressColor ?? AppTheme.primary,
                  borderColor: Colors.white.withOpacity(0.3),
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}

/// Modern Toggle Switch
class ModernToggleSwitch extends StatefulWidget {
  final bool value;
  final ValueChanged<bool>? onChanged;
  final Color? activeColor;
  final Color? inactiveColor;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernToggleSwitch({
    Key? key,
    required this.value,
    this.onChanged,
    this.activeColor,
    this.inactiveColor,
    this.animationDuration = const Duration(milliseconds: 200),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernToggleSwitch> createState() => _ModernToggleSwitchState();
}

class _ModernToggleSwitchState extends State<ModernToggleSwitch>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _slideAnimation;
  late Animation<Color?> _colorAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _slideAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
    _colorAnimation = ColorTween(
      begin: widget.inactiveColor?.withOpacity(0.3) ?? AppTheme.surface.withOpacity(0.3),
      end: widget.activeColor?.withOpacity(0.9) ?? AppTheme.primary.withOpacity(0.9),
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeInOut));
    
    if (widget.value) {
      _controller.value = 1.0;
    }
  }

  @override
  void didUpdateWidget(ModernToggleSwitch oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.value != widget.value) {
      if (widget.value) {
        _controller.forward();
      } else {
        _controller.reverse();
      }
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => widget.onChanged?.call(!widget.value),
      child: AnimatedBuilder(
        animation: _controller,
        builder: (context, child) {
          return GlassContainer(
            width: 56,
            height: 32,
            radius: 16,
            backgroundColor: _colorAnimation.value,
            borderColor: Colors.white.withOpacity(0.3),
            child: Stack(
              children: [
                AnimatedPositioned(
                  duration: widget.animationDuration,
                  curve: Curves.easeInOut,
                  left: widget.value ? 24 : 4,
                  top: 4,
                  child: GlassContainer(
                    width: 24,
                    height: 24,
                    radius: 12,
                    backgroundColor: Colors.white.withOpacity(0.9),
                    borderColor: AppTheme.surfaceLight.withOpacity(0.3),
                    child: Center(
                      child: Container(
                        width: 12,
                        height: 12,
                        decoration: BoxDecoration(
                          color: widget.value ? AppTheme.primary : AppTheme.textHint,
                          borderRadius: BorderRadius.circular(6),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}

/// Modern Badge
class ModernBadge extends StatefulWidget {
  final String text;
  final Color? backgroundColor;
  final Color? textColor;
  final VoidCallback? onTap;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernBadge({
    Key? key,
    required this.text,
    this.backgroundColor,
    this.textColor,
    this.onTap,
    this.animationDuration = const Duration(milliseconds: 300),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernBadge> createState() => _ModernBadgeState();
}

class _ModernBadgeState extends State<ModernBadge>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 0.5, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return FadeTransition(
          opacity: _fadeAnimation,
          child: Transform.scale(
            scale: _scaleAnimation.value,
            child: GestureDetector(
              onTap: widget.onTap,
              child: GlassContainer(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                radius: 12,
                backgroundColor: widget.backgroundColor?.withOpacity(0.9) ?? 
                             AppTheme.error.withOpacity(0.9),
                borderColor: Colors.white.withOpacity(0.3),
                child: Text(
                  widget.text,
                  style: TextStyle(
                    color: widget.textColor ?? Colors.white,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

/// Modern Info Card
class ModernInfoCard extends StatefulWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final Widget? action;
  final Color? backgroundColor;
  final Color? iconColor;
  final VoidCallback? onTap;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernInfoCard({
    Key? key,
    required this.icon,
    required this.title,
    this.subtitle,
    this.action,
    this.backgroundColor,
    this.iconColor,
    this.onTap,
    this.animationDuration = const Duration(milliseconds: 400),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernInfoCard> createState() => _ModernInfoCardState();
}

class _ModernInfoCardState extends State<ModernInfoCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 0.9, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );
    _slideAnimation = Tween<Offset>(begin: const Offset(0, 0.1), end: Offset.zero).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeOut),
    );
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Transform.scale(
          scale: _scaleAnimation.value,
          child: SlideTransition(
            position: _slideAnimation,
            child: GestureDetector(
              onTap: widget.onTap,
              child: GlassContainer(
                padding: const EdgeInsets.all(16),
                radius: 16,
                backgroundColor: widget.backgroundColor?.withOpacity(0.1) ?? 
                             AppTheme.surface.withOpacity(0.1),
                borderColor: widget.iconColor?.withOpacity(0.3) ?? 
                             AppTheme.surfaceLight.withOpacity(0.3),
                child: Row(
                  children: [
                    GlassContainer(
                      width: 48,
                      height: 48,
                      radius: 12,
                      backgroundColor: widget.iconColor?.withOpacity(0.2) ?? 
                                   AppTheme.primary.withOpacity(0.2),
                      borderColor: widget.iconColor?.withOpacity(0.4) ?? 
                                   AppTheme.primary.withOpacity(0.4),
                      child: Icon(
                        widget.icon,
                        color: widget.iconColor ?? AppTheme.primary,
                        size: 24,
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            widget.title,
                            style: const TextStyle(
                              color: AppTheme.textPrimary,
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          if (widget.subtitle != null) ...[
                            const SizedBox(height: 4),
                            Text(
                              widget.subtitle!,
                              style: const TextStyle(
                                color: AppTheme.textSecondary,
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ],
                      ),
                    ),
                    if (widget.action != null) widget.action!,
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

/// Modern Quick Action Button
class ModernQuickActionButton extends StatefulWidget {
  final IconData icon;
  final String label;
  final VoidCallback? onPressed;
  final Color? backgroundColor;
  final Color? foregroundColor;
  final Duration animationDuration;
  final bool isAnimated;

  const ModernQuickActionButton({
    Key? key,
    required this.icon,
    required this.label,
    this.onPressed,
    this.backgroundColor,
    this.foregroundColor,
    this.animationDuration = const Duration(milliseconds: 300),
    this.isAnimated = true,
  }) : super(key: key);

  @override
  State<ModernQuickActionButton> createState() => _ModernQuickActionButtonState();
}

class _ModernQuickActionButtonState extends State<ModernQuickActionButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<Color?> _colorAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );
    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );
    _colorAnimation = ColorTween(
      begin: widget.backgroundColor?.withOpacity(0.3) ?? AppTheme.surface.withOpacity(0.3),
      end: widget.backgroundColor?.withOpacity(0.9) ?? AppTheme.primary.withOpacity(0.9),
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeInOut));
    
    if (widget.isAnimated) {
      _controller.forward();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Transform.scale(
          scale: _scaleAnimation.value,
          child: GestureDetector(
            onTap: widget.onPressed,
            child: GlassContainer(
              padding: const EdgeInsets.all(16),
              radius: 16,
              backgroundColor: _colorAnimation.value,
              borderColor: Colors.white.withOpacity(0.3),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    widget.icon,
                    color: widget.foregroundColor ?? Colors.white,
                    size: 32,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    widget.label,
                    style: TextStyle(
                      color: widget.foregroundColor ?? Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

/// Widget Factory for Easy Widget Creation
class ModernWidgetFactory {
  static Widget createFloatingActionButton({
    required IconData icon,
    String? label,
    required VoidCallback onPressed,
    Color? backgroundColor,
    Color? foregroundColor,
    Widget? child,
    bool extended = false,
  }) {
    return ModernFloatingActionButton(
      icon: icon,
      label: label,
      onPressed: onPressed,
      backgroundColor: backgroundColor,
      foregroundColor: foregroundColor,
      child: child,
      extended: extended,
    );
  }

  static Widget createSearchBar({
    String? hintText,
    ValueChanged<String>? onChanged,
    VoidCallback? onClear,
    TextEditingController? controller,
    bool autofocus = false,
  }) {
    return ModernSearchBar(
      hintText: hintText,
      onChanged: onChanged,
      onClear: onClear,
      controller: controller,
      autofocus: autofocus,
    );
  }

  static Widget createStatusChip({
    required String label,
    Color? backgroundColor,
    Color? textColor,
    IconData? icon,
    VoidCallback? onTap,
    bool selected = false,
  }) {
    return ModernStatusChip(
      label: label,
      backgroundColor: backgroundColor,
      textColor: textColor,
      icon: icon,
      onTap: onTap,
      selected: selected,
    );
  }

  static Widget createProgressIndicator({
    required double value,
    Color? backgroundColor,
    Color? progressColor,
    String? label,
    double height = 8,
    BorderRadius? borderRadius,
  }) {
    return ModernProgressIndicator(
      value: value,
      backgroundColor: backgroundColor,
      progressColor: progressColor,
      label: label,
      height: height,
      borderRadius: borderRadius,
    );
  }

  static Widget createToggleSwitch({
    required bool value,
    ValueChanged<bool>? onChanged,
    Color? activeColor,
    Color? inactiveColor,
  }) {
    return ModernToggleSwitch(
      value: value,
      onChanged: onChanged,
      activeColor: activeColor,
      inactiveColor: inactiveColor,
    );
  }

  static Widget createBadge({
    required String text,
    Color? backgroundColor,
    Color? textColor,
    VoidCallback? onTap,
  }) {
    return ModernBadge(
      text: text,
      backgroundColor: backgroundColor,
      textColor: textColor,
      onTap: onTap,
    );
  }

  static Widget createInfoCard({
    required IconData icon,
    required String title,
    String? subtitle,
    Widget? action,
    Color? backgroundColor,
    Color? iconColor,
    VoidCallback? onTap,
  }) {
    return ModernInfoCard(
      icon: icon,
      title: title,
      subtitle: subtitle,
      action: action,
      backgroundColor: backgroundColor,
      iconColor: iconColor,
      onTap: onTap,
    );
  }

  static Widget createQuickActionButton({
    required IconData icon,
    required String label,
    VoidCallback? onPressed,
    Color? backgroundColor,
    Color? foregroundColor,
  }) {
    return ModernQuickActionButton(
      icon: icon,
      label: label,
      onPressed: onPressed,
      backgroundColor: backgroundColor,
      foregroundColor: foregroundColor,
    );
  }
}
