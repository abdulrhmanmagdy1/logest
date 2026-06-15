// ============================================
// 🔐 Auth Bloc - State Management
// ============================================

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../../../../core/models/user_model.dart';

// Events
abstract class AuthEvent extends Equatable {
  const AuthEvent();
  @override
  List<Object> get props => [];
}

class CheckAuthStatus extends AuthEvent {}

class LoginRequested extends AuthEvent {
  final String email;
  final String password;

  const LoginRequested({
    required this.email,
    required this.password,
  });

  @override
  List<Object> get props => [email, password];
}

class LogoutRequested extends AuthEvent {}

class DemoLoginRequested extends AuthEvent {
  final UserRole role;

  const DemoLoginRequested({required this.role});

  @override
  List<Object> get props => [role];
}

// States
abstract class AuthState extends Equatable {
  const AuthState();
  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

class Authenticated extends AuthState {
  final User user;

  const Authenticated({required this.user});

  @override
  List<Object> get props => [user];
}

class Unauthenticated extends AuthState {}

class AuthError extends AuthState {
  final String message;

  const AuthError({required this.message});

  @override
  List<Object> get props => [message];
}

// Bloc
class AuthBloc extends Bloc<AuthEvent, AuthState> {
  AuthBloc() : super(AuthInitial()) {
    on<CheckAuthStatus>(_onCheckAuthStatus);
    on<LoginRequested>(_onLoginRequested);
    on<LogoutRequested>(_onLogoutRequested);
    on<DemoLoginRequested>(_onDemoLoginRequested);
  }

  Future<void> _onCheckAuthStatus(
    CheckAuthStatus event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    // TODO: Check token storage
    await Future.delayed(const Duration(milliseconds: 500));
    emit(Unauthenticated());
  }

  Future<void> _onLoginRequested(
    LoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      // TODO: Call API
      await Future.delayed(const Duration(seconds: 1));
      
      // Demo user
      final user = User(
        id: '1',
        firstName: 'محمد',
        lastName: 'أحمد',
        email: event.email,
        role: UserRole.client,
        status: UserStatus.active,
      );
      
      emit(Authenticated(user: user));
    } catch (e) {
      emit(AuthError(message: e.toString()));
    }
  }

  Future<void> _onLogoutRequested(
    LogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    // TODO: Clear storage
    await Future.delayed(const Duration(milliseconds: 300));
    emit(Unauthenticated());
  }

  Future<void> _onDemoLoginRequested(
    DemoLoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    await Future.delayed(const Duration(seconds: 1));
    
    late User user;
    
    switch (event.role) {
      case UserRole.client:
        user = User(
          id: '1',
          firstName: 'عبدالله',
          lastName: 'المحمد',
          email: 'client@edham.com',
          role: UserRole.client,
          status: UserStatus.active,
        );
        break;
      case UserRole.driver:
        user = User(
          id: '2',
          firstName: 'خالد',
          lastName: 'السائق',
          email: 'driver@edham.com',
          role: UserRole.driver,
          status: UserStatus.active,
        );
        break;
      default:
        user = User(
          id: '1',
          firstName: 'عبدالله',
          lastName: 'المحمد',
          email: 'client@edham.com',
          role: UserRole.client,
          status: UserStatus.active,
        );
    }
    
    emit(Authenticated(user: user));
  }
}
