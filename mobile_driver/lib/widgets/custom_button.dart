import 'package:flutter/material.dart';
import '../constants/index.dart';

/// Custom Button
/// Reusable button with multiple variants
class CustomButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final ButtonType type;
  final ButtonSize size;
  final IconData? icon;
  final bool isLoading;
  final bool isFullWidth;

  const CustomButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.type = ButtonType.primary,
    this.size = ButtonSize.medium,
    this.icon,
    this.isLoading = false,
    this.isFullWidth = false,
  });

  @override
  Widget build(BuildContext context) {
    final buttonStyle = _getButtonStyle();
    final padding = _getPadding();
    final textStyle = _getTextStyle();

    Widget button;

    if (type == ButtonType.primary) {
      button = ElevatedButton(
        onPressed: isLoading ? null : onPressed,
        style: buttonStyle,
        child: _buildContent(textStyle),
      );
    } else if (type == ButtonType.outlined) {
      button = OutlinedButton(
        onPressed: isLoading ? null : onPressed,
        style: buttonStyle,
        child: _buildContent(textStyle),
      );
    } else {
      button = TextButton(
        onPressed: isLoading ? null : onPressed,
        style: buttonStyle,
        child: _buildContent(textStyle),
      );
    }

    if (isFullWidth) {
      return SizedBox(
        width: double.infinity,
        child: button,
      );
    }

    return button;
  }

  Widget _buildContent(TextStyle textStyle) {
    if (isLoading) {
      return SizedBox(
        height: textStyle.fontSize,
        width: textStyle.fontSize,
        child: CircularProgressIndicator(
          strokeWidth: 2,
          valueColor: AlwaysStoppedAnimation<Color>(
            type == ButtonType.primary ? Colors.white : AppColors.primary,
          ),
        ),
      );
    }

    if (icon != null) {
      return Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: textStyle.fontSize),
          const SizedBox(width: 8),
          Text(text, style: textStyle),
        ],
      );
    }

    return Text(text, style: textStyle);
  }

  ButtonStyle _getButtonStyle() {
    switch (type) {
      case ButtonType.primary:
        return ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          padding: _getPadding(),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          elevation: 4,
        );
      case ButtonType.secondary:
        return ElevatedButton.styleFrom(
          backgroundColor: AppColors.secondary,
          foregroundColor: Colors.white,
          padding: _getPadding(),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          elevation: 4,
        );
      case ButtonType.outlined:
        return OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: BorderSide(color: AppColors.primary, width: 2),
          padding: _getPadding(),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        );
      case ButtonType.text:
        return TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          padding: _getPadding(),
        );
    }
  }

  EdgeInsets _getPadding() {
    switch (size) {
      case ButtonSize.small:
        return const EdgeInsets.symmetric(horizontal: 16, vertical: 8);
      case ButtonSize.medium:
        return const EdgeInsets.symmetric(horizontal: 24, vertical: 12);
      case ButtonSize.large:
        return const EdgeInsets.symmetric(horizontal: 32, vertical: 16);
    }
  }

  TextStyle _getTextStyle() {
    final fontSize = switch (size) {
      ButtonSize.small => 14.0,
      ButtonSize.medium => 16.0,
      ButtonSize.large => 18.0,
    };

    return TextStyle(
      fontSize: fontSize,
      fontWeight: FontWeight.w600,
    );
  }
}

/// Button Types
enum ButtonType {
  primary,
  secondary,
  outlined,
  text,
}

/// Button Sizes
enum ButtonSize {
  small,
  medium,
  large,
}
