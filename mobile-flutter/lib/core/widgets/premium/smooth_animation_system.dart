// ============================================
// ✨ Smooth Animation System
// Premium Animations with Micro-interactions & Feedback
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../theme/app_theme.dart';

class SmoothAnimationSystem {
  // Animation durations
  static const Duration fast = Duration(milliseconds: 200);
  static const Duration medium = Duration(milliseconds: 400);
  static const Duration slow = Duration(milliseconds: 600);
  static const Duration extraSlow = Duration(milliseconds: 800);

  // Animation curves
  static const Curve easeInOut = Curves.easeInOut;
  static const Curve easeOut = Curves.easeOut;
  static const Curve easeIn = Curves.easeIn;
  static const Curve bounceOut = Curves.bounceOut;
  static const Curve elasticOut = Curves.elasticOut;

  // Default animation effects
  static EffectEntry fadeIn({Duration delay = Duration.zero}) {
    return EffectEntry(
      animate: (widget) => widget.animate().fadeIn(
        delay: delay,
        duration: medium,
        curve: easeOut,
      ),
    );
  }

  static EffectEntry slideUp({Duration delay = Duration.zero}) {
    return EffectEntry(
      animate: (widget) => widget.animate().slideY(
        begin: 0.3,
        end: 0,
        delay: delay,
        duration: medium,
        curve: easeOut,
      ),
    );
  }

  static EffectEntry slideRight({Duration delay = Duration.zero}) {
    return EffectEntry(
      animate: (widget) => widget.animate().slideX(
        begin: -0.3,
        end: 0,
        delay: delay,
        duration: medium,
        curve: easeOut,
      ),
    );
  }

  static EffectEntry scaleIn({Duration delay = Duration.zero}) {
    return EffectEntry(
      animate: (widget) => widget.animate().scale(
        begin: const Offset(0.8, 0.8),
        end: const Offset(1, 1),
        delay: delay,
        duration: medium,
        curve: easeOut,
      ),
    );
  }

  static EffectEntry shimmer({Duration delay = Duration.zero}) {
    return EffectEntry(
      animate: (widget) => widget.animate().shimmer(
        delay: delay,
        duration: extraSlow,
        color: AppTheme.primary.withOpacity(0.3),
      ),
    );
  }

  // Combined animations
  static List<EffectEntry> cardEntrance({Duration delay = Duration.zero}) {
    return [
      fadeIn(delay: delay),
      slideUp(delay: delay),
      scaleIn(delay: delay),
    ];
  }

  static List<EffectEntry> listItemEntrance({Duration delay = Duration.zero}) {
    return [
      fadeIn(delay: delay),
      slideRight(delay: delay),
    ];
  }

  static List<EffectEntry> statCardEntrance({Duration delay = Duration.zero}) {
    return [
      fadeIn(delay: delay),
      scaleIn(delay: delay),
      shimmer(delay: delay + medium),
    ];
  }
}

class EffectEntry {
  final Widget Function(Widget) animate;

  EffectEntry({required this.animate});
}

class AnimatedCounter extends StatefulWidget {
  final int value;
  final Duration duration;
  final String? prefix;
  final String? suffix;
  final TextStyle? style;
  final bool isAnimated;

  const AnimatedCounter({
    super.key,
    required this.value,
    this.duration = const Duration(milliseconds: 800),
    this.prefix,
    this.suffix,
    this.style,
    this.isAnimated = true,
  });

  @override
  State<AnimatedCounter> createState() => _AnimatedCounterState();
}

class _AnimatedCounterState extends State<AnimatedCounter>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<int> _counterAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.duration,
      vsync: this,
    );

    _counterAnimation = IntTween(
      begin: 0,
      end: widget.value,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: SmoothAnimationSystem.easeOut,
    ));

    if (widget.isAnimated) {
      _controller.forward();
    } else {
      _controller.value = 1.0;
    }
  }

  @override
  void didUpdateWidget(AnimatedCounter oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.value != widget.value) {
      _controller.reset();
      _counterAnimation = IntTween(
        begin: oldWidget.value,
        end: widget.value,
      ).animate(CurvedAnimation(
        parent: _controller,
        curve: SmoothAnimationSystem.easeOut,
      ));
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
      animation: _counterAnimation,
      builder: (context, child) {
        return Text(
          '${widget.prefix ?? ''}${_counterAnimation.value}${widget.suffix ?? ''}',
          style: widget.style,
        );
      },
    );
  }
}

class SmoothProgressIndicator extends StatefulWidget {
  final double progress;
  final Color color;
  final double height;
  final BorderRadius borderRadius;
  final Duration duration;
  final bool isAnimated;

  const SmoothProgressIndicator({
    super.key,
    required this.progress,
    required this.color,
    this.height = 8,
    this.borderRadius = const BorderRadius.all(Radius.circular(4)),
    this.duration = const Duration(milliseconds: 600),
    this.isAnimated = true,
  });

  @override
  State<SmoothProgressIndicator> createState() => _SmoothProgressIndicatorState();
}

class _SmoothProgressIndicatorState extends State<SmoothProgressIndicator>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _progressAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.duration,
      vsync: this,
    );

    _progressAnimation = Tween<double>(
      begin: 0.0,
      end: widget.progress,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: SmoothAnimationSystem.easeOut,
    ));

    if (widget.isAnimated) {
      _controller.forward();
    } else {
      _controller.value = 1.0;
    }
  }

  @override
  void didUpdateWidget(SmoothProgressIndicator oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.progress != widget.progress) {
      _controller.reset();
      _progressAnimation = Tween<double>(
        begin: oldWidget.progress,
        end: widget.progress,
      ).animate(CurvedAnimation(
        parent: _controller,
        curve: SmoothAnimationSystem.easeOut,
      ));
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
    return Container(
      height: widget.height,
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.1),
        borderRadius: widget.borderRadius,
      ),
      child: AnimatedBuilder(
        animation: _progressAnimation,
        builder: (context, child) {
          return FractionallySizedBox(
            alignment: Alignment.centerLeft,
            widthFactor: _progressAnimation.value,
            child: Container(
              decoration: BoxDecoration(
                color: widget.color,
                borderRadius: widget.borderRadius,
              ),
            ),
          );
        },
      ),
    );
  }
}

class PulsingDot extends StatefulWidget {
  final Color color;
  final double size;
  final Duration pulseDuration;

  const PulsingDot({
    super.key,
    required this.color,
    this.size = 12,
    this.pulseDuration = const Duration(milliseconds: 1500),
  });

  @override
  State<PulsingDot> createState() => _PulsingDotState();
}

class _PulsingDotState extends State<PulsingDot>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<double> _opacityAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.pulseDuration,
      vsync: this,
    );

    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: 1.2,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: SmoothAnimationSystem.easeInOut,
    ));

    _opacityAnimation = Tween<double>(
      begin: 1.0,
      end: 0.3,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: SmoothAnimationSystem.easeInOut,
    ));

    _controller.repeat(reverse: true);
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
        return Container(
          width: widget.size * _scaleAnimation.value,
          height: widget.size * _scaleAnimation.value,
          decoration: BoxDecoration(
            color: widget.color.withOpacity(_opacityAnimation.value),
            shape: BoxShape.circle,
          ),
        );
      },
    );
  }
}

class SmoothAnimatedContainer extends StatefulWidget {
  final Widget child;
  final Duration duration;
  final Curve curve;
  final VoidCallback? onComplete;

  const SmoothAnimatedContainer({
    super.key,
    required this.child,
    this.duration = const Duration(milliseconds: 300),
    this.curve = Curves.easeOut,
    this.onComplete,
  });

  @override
  State<SmoothAnimatedContainer> createState() => _SmoothAnimatedContainerState();
}

class _SmoothAnimatedContainerState extends State<SmoothAnimatedContainer>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.duration,
      vsync: this,
    );

    _animation = CurvedAnimation(
      parent: _controller,
      curve: widget.curve,
    );

    _controller.forward().then((_) {
      widget.onComplete?.call();
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: _animation,
      child: SlideTransition(
        position: Tween<Offset>(
          begin: const Offset(0, 0.1),
          end: Offset.zero,
        ).animate(_animation),
        child: widget.child,
      ),
    );
  }
}

class MicroInteractionButton extends StatefulWidget {
  final Widget child;
  final VoidCallback? onPressed;
  final Duration animationDuration;
  final double pressScale;

  const MicroInteractionButton({
    super.key,
    required this.child,
    this.onPressed,
    this.animationDuration = const Duration(milliseconds: 100),
    this.pressScale = 0.95,
  });

  @override
  State<MicroInteractionButton> createState() => _MicroInteractionButtonState();
}

class _MicroInteractionButtonState extends State<MicroInteractionButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    );

    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: widget.pressScale,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeInOut,
    ));
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _handleTapDown(TapDownDetails details) {
    _controller.forward();
  }

  void _handleTapUp(TapUpDetails details) {
    _controller.reverse();
    widget.onPressed?.call();
  }

  void _handleTapCancel() {
    _controller.reverse();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTapDown: _handleTapDown,
      onTapUp: _handleTapUp,
      onTapCancel: _handleTapCancel,
      child: AnimatedBuilder(
        animation: _scaleAnimation,
        builder: (context, child) {
          return Transform.scale(
            scale: _scaleAnimation.value,
            child: widget.child,
          );
        },
      ),
    );
  }
}
