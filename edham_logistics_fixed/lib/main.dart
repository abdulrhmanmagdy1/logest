// ============================================
// 🚀 Edham Logistics - Flutter App Entry Point
// Premium Enterprise Logistics Platform
// ============================================

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_animate/flutter_animate.dart';

import 'core/theme/app_theme.dart';
import 'core/navigation/app_router.dart';
import 'features/auth/presentation/bloc/auth_bloc.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Set preferred orientations
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);
  
  // Set system UI overlay style with premium dark theme
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.light,
      systemNavigationBarColor: AppTheme.background,
      systemNavigationBarIconBrightness: Brightness.light,
      systemNavigationBarDividerColor: AppTheme.surface,
    ),
  );
  
  runApp(const EdhamApp());
}

class EdhamApp extends StatelessWidget {
  const EdhamApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(create: (_) => AuthBloc()),
      ],
      child: MaterialApp.router(
        title: 'Edham Logistics - Enterprise Platform',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.darkTheme,
        routerConfig: AppRouter.router,
        builder: (context, child) {
          return ScrollConfiguration(
            behavior: const ScrollBehavior().copyWith(
              physics: const BouncingScrollPhysics(
                parent: AlwaysScrollableScrollPhysics(),
              ),
            ),
            child: child!.animate().fadeIn(),
          );
        },
      ),
    );
  }
}
