import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../constants/index.dart';
import '../utils/validators.dart';

/// Custom Text Field
/// Professional text input with validation support
class CustomTextField extends StatelessWidget {
  final String? label;
  final String? hint;
  final TextEditingController? controller;
  final String? Function(String?)? validator;
  final TextInputType keyboardType;
  final bool obscureText;
  final Widget? prefixIcon;
  final Widget? suffixIcon;
  final int? maxLines;
  final int? maxLength;
  final bool readOnly;
  final VoidCallback? onTap;
  final ValueChanged<String>? onChanged;
  final bool autofocus;
  final FocusNode? focusNode;
  final List<TextInputFormatter>? inputFormatters;
  final String? errorText;
  final bool isRequired;

  const CustomTextField({
    super.key,
    this.label,
    this.hint,
    this.controller,
    this.validator,
    this.keyboardType = TextInputType.text,
    this.obscureText = false,
    this.prefixIcon,
    this.suffixIcon,
    this.maxLines = 1,
    this.maxLength,
    this.readOnly = false,
    this.onTap,
    this.onChanged,
    this.autofocus = false,
    this.focusNode,
    this.inputFormatters,
    this.errorText,
    this.isRequired = false,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (label != null) ...[
          Row(
            children: [
              Text(
                label!,
                style: TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                  color: AppColors.textPrimary,
                ),
              ),
              if (isRequired) ...[
                const SizedBox(width: 4),
                Text(
                  '*',
                  style: TextStyle(
                    color: AppColors.error,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ],
          ),
          const SizedBox(height: 8),
        ],
        TextFormField(
          controller: controller,
          validator: validator,
          keyboardType: keyboardType,
          obscureText: obscureText,
          maxLines: maxLines,
          maxLength: maxLength,
          readOnly: readOnly,
          onTap: onTap,
          onChanged: onChanged,
          autofocus: autofocus,
          focusNode: focusNode,
          inputFormatters: inputFormatters,
          style: TextStyle(color: AppColors.textPrimary),
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: TextStyle(color: AppColors.textMuted),
            prefixIcon: prefixIcon,
            suffixIcon: suffixIcon,
            errorText: errorText,
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 16,
            ),
          ),
        ),
      ],
    );
  }
}

/// Email Field
/// Specialized email input with validation
class EmailField extends StatelessWidget {
  final TextEditingController? controller;
  final bool isRequired;
  final String? label;
  final String? hint;

  const EmailField({
    super.key,
    this.controller,
    this.isRequired = true,
    this.label,
    this.hint,
  });

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: controller,
      label: label ?? 'البريد الإلكتروني',
      hint: hint ?? 'example@email.com',
      keyboardType: TextInputType.emailAddress,
      prefixIcon: Icon(Icons.email_outlined, color: AppColors.textSecondary),
      isRequired: isRequired,
      validator: (value) {
        if (isRequired && (value == null || value.isEmpty)) {
          return 'البريد الإلكتروني مطلوب';
        }
        if (value != null && value.isNotEmpty && !value.isValidEmail) {
          return 'البريد الإلكتروني غير صالح';
        }
        return null;
      },
    );
  }
}

/// Password Field
/// Specialized password input with toggle visibility
class PasswordField extends StatefulWidget {
  final TextEditingController? controller;
  final bool isRequired;
  final String? label;
  final String? hint;
  final String? Function(String?)? validator;

  const PasswordField({
    super.key,
    this.controller,
    this.isRequired = true,
    this.label,
    this.hint,
    this.validator,
  });

  @override
  State<PasswordField> createState() => _PasswordFieldState();
}

class _PasswordFieldState extends State<PasswordField> {
  bool _obscureText = true;

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: widget.controller,
      label: widget.label ?? 'كلمة المرور',
      hint: widget.hint ?? '••••••••',
      keyboardType: TextInputType.visiblePassword,
      obscureText: _obscureText,
      prefixIcon: Icon(Icons.lock_outline, color: AppColors.textSecondary),
      suffixIcon: IconButton(
        icon: Icon(
          _obscureText ? Icons.visibility_off : Icons.visibility,
          color: AppColors.textSecondary,
        ),
        onPressed: () {
          setState(() {
            _obscureText = !_obscureText;
          });
        },
      ),
      isRequired: widget.isRequired,
      validator: widget.validator ??
          (value) {
            if (widget.isRequired && (value == null || value.isEmpty)) {
              return 'كلمة المرور مطلوبة';
            }
            if (value != null && value.isNotEmpty && value.length < 8) {
              return 'كلمة المرور يجب أن تكون 8 أحرف على الأقل';
            }
            return null;
          },
    );
  }
}

/// Phone Field
/// Specialized phone input for Saudi numbers
class PhoneField extends StatelessWidget {
  final TextEditingController? controller;
  final bool isRequired;
  final String? label;
  final String? hint;

  const PhoneField({
    super.key,
    this.controller,
    this.isRequired = true,
    this.label,
    this.hint,
  });

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: controller,
      label: label ?? 'رقم الجوال',
      hint: hint ?? '05XXXXXXXX',
      keyboardType: TextInputType.phone,
      prefixIcon: Icon(Icons.phone_outlined, color: AppColors.textSecondary),
      isRequired: isRequired,
      inputFormatters: [
        FilteringTextInputFormatter.digitsOnly,
        LengthLimitingTextInputFormatter(10),
      ],
      validator: (value) {
        if (isRequired && (value == null || value.isEmpty)) {
          return 'رقم الجوال مطلوب';
        }
        if (value != null && value.isNotEmpty && !value.isValidPhone) {
          return 'رقم الجوال غير صالح (مثال: 0501234567)';
        }
        return null;
      },
    );
  }
}

/// Search Field
/// Specialized search input
class SearchField extends StatelessWidget {
  final TextEditingController? controller;
  final ValueChanged<String>? onChanged;
  final ValueChanged<String>? onSubmitted;
  final VoidCallback? onClear;
  final String? hint;
  final bool autofocus;

  const SearchField({
    super.key,
    this.controller,
    this.onChanged,
    this.onSubmitted,
    this.onClear,
    this.hint,
    this.autofocus = false,
  });

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: controller,
      hint: hint ?? 'بحث...',
      prefixIcon: Icon(Icons.search, color: AppColors.textSecondary),
      suffixIcon: controller?.text.isNotEmpty ?? false
          ? IconButton(
              icon: Icon(Icons.clear, color: AppColors.textSecondary),
              onPressed: () {
                controller?.clear();
                onClear?.call();
              },
            )
          : null,
      onChanged: onChanged,
      autofocus: autofocus,
    );
  }
}
