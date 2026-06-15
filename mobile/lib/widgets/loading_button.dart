import 'package:flutter/material.dart';

class LoadingButton extends StatefulWidget {
  final bool isLoading;
  final VoidCallback onPressed;
  final String text;
  final Color backgroundColor;
  final Color textColor;
  final EdgeInsetsGeometry padding;
  final double borderRadius;
  final double? width;
  final double height;
  final Widget? child;
  final bool enabled;

  const LoadingButton({
    Key? key,
    required this.isLoading,
    required this.onPressed,
    required this.text,
    this.backgroundColor = const Color(0xFFF97316),
    this.textColor = Colors.white,
    this.padding = const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
    this.borderRadius = 8.0,
    this.width,
    this.height = 48.0,
    this.child,
    this.enabled = true,
  }) : super(key: key);

  @override
  State<LoadingButton> createState() => _LoadingButtonState();
}

class _LoadingButtonState extends State<LoadingButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 150),
      vsync: this,
    );
    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: 0.95,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    ));
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _scaleAnimation,
      builder: (context, child) {
        return Transform.scale(
          scale: _scaleAnimation.value,
          child: SizedBox(
            width: widget.width,
            height: widget.height,
            child: ElevatedButton(
              onPressed: widget.enabled && !widget.isLoading ? _handlePress : null,
              style: ElevatedButton.styleFrom(
                backgroundColor: widget.backgroundColor,
                foregroundColor: widget.textColor,
                padding: widget.padding,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(widget.borderRadius),
                ),
                elevation: 8,
                shadowColor: widget.backgroundColor.withOpacity(0.3),
                disabledBackgroundColor: widget.backgroundColor.withOpacity(0.5),
                disabledForegroundColor: widget.textColor.withOpacity(0.7),
              ),
              child: widget.isLoading
                  ? Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2.0,
                            valueColor: AlwaysStoppedAnimation<Color>(
                              widget.textColor,
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Text(
                          'جاري التحميل...',
                          style: TextStyle(
                            color: widget.textColor,
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    )
                  : widget.child ??
                      Text(
                        widget.text,
                        style: TextStyle(
                          color: widget.textColor,
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
            ),
          ),
        );
      },
    );
  }

  void _handlePress() {
    _animationController.forward().then((_) {
      _animationController.reverse();
    });
    widget.onPressed();
  }
}
