import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final GlobalKey<ScaffoldState> scaffoldKey;
  final String userName;
  final String userRole;
  final String userAvatar;
  final List<Widget>? actions;
  final bool automaticallyImplyLeading;
  final double? elevation;

  const CustomAppBar({
    Key? key,
    required this.title,
    required this.scaffoldKey,
    required this.userName,
    required this.userRole,
    this.userAvatar = '',
    this.actions,
    this.automaticallyImplyLeading = true,
    this.elevation,
  }) : super(key: key);

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF1A1F2E), Color(0xFF0A0E1A)],
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 10,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: AppBar(
        title: Text(
          title,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: elevation ?? 0,
        automaticallyImplyLeading: automaticallyImplyLeading,
        leading: IconButton(
          icon: const Icon(
            Icons.menu,
            color: Colors.white,
            size: 24,
          ),
          onPressed: () {
            scaffoldKey.currentState?.openDrawer();
          },
        ),
        actions: actions ?? _buildDefaultActions(),
        centerTitle: true,
      ),
    )
        .animate()
        .slideY(begin: -0.1, end: 0, duration: 300.ms)
        .fadeIn(duration: 300.ms);
  }

  List<Widget> _buildDefaultActions() {
    return [
      // Notifications Button
      IconButton(
        icon: Stack(
          children: [
            const Icon(
              Icons.notifications_outlined,
              color: Colors.white,
              size: 24,
            ),
            Positioned(
              top: 8,
              right: 8,
              child: Container(
                width: 8,
                height: 8,
                decoration: const BoxDecoration(
                  color: Color(0xFFF97316),
                  shape: BoxShape.circle,
                ),
              ),
            ),
          ],
        ),
        onPressed: () {
          // TODO: Navigate to notifications
        },
      ),
      
      const SizedBox(width: 8),
      
      // User Avatar or Profile Button
      userAvatar.isNotEmpty
          ? CircleAvatar(
              radius: 16,
              backgroundImage: NetworkImage(userAvatar),
              backgroundColor: const Color(0xFFF97316),
            )
          : Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFFF97316), Color(0xFFEA580C)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(16),
              ),
              child: const Icon(
                Icons.person,
                color: Colors.white,
                size: 18,
              ),
            ),
      
      const SizedBox(width: 16),
    ];
  }
}

class AppBarTitle extends StatelessWidget {
  final String title;
  final String? subtitle;

  const AppBarTitle({
    Key? key,
    required this.title,
    this.subtitle,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (subtitle != null) ...[
          const SizedBox(height: 2),
          Text(
            subtitle!,
            style: TextStyle(
              color: Colors.white.withOpacity(0.7),
              fontSize: 12,
            ),
          ),
        ],
      ],
    );
  }
}
