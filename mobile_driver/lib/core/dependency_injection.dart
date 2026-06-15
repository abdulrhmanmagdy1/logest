/// Dependency Injection Container
/// Simple service locator for dependency injection
class DIContainer {
  static final DIContainer _instance = DIContainer._internal();
  factory DIContainer() => _instance;
  DIContainer._internal();

  final Map<Type, dynamic> _services = {};
  final Map<Type, dynamic Function()> _factories = {};

  /// Register a singleton service
  void registerSingleton<T>(T instance) {
    _services[T] = instance;
  }

  /// Register a factory
  void registerFactory<T>(T Function() factory) {
    _factories[T] = factory;
  }

  /// Register a lazy singleton
  void registerLazySingleton<T>(T Function() factory) {
    _factories[T] = () {
      final instance = factory();
      _services[T] = instance;
      _factories.remove(T);
      return instance;
    };
  }

  /// Get a service
  T resolve<T>() {
    // Check for singleton
    if (_services.containsKey(T)) {
      return _services[T] as T;
    }

    // Check for factory
    if (_factories.containsKey(T)) {
      return _factories[T]!() as T;
    }

    throw Exception('Service $T not registered');
  }

  /// Check if service is registered
  bool isRegistered<T>() {
    return _services.containsKey(T) || _factories.containsKey(T);
  }

  /// Unregister a service
  void unregister<T>() {
    _services.remove(T);
    _factories.remove(T);
  }

  /// Clear all services
  void clear() {
    _services.clear();
    _factories.clear();
  }
}

/// Global DI instance
final di = DIContainer();
