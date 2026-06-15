import 'dart:async';
import 'package:flutter/foundation.dart';
import 'logger_service.dart';

/// Background Task Service
/// Manages background tasks and periodic operations
class BackgroundTaskService {
  static final BackgroundTaskService _instance = BackgroundTaskService._internal();
  factory BackgroundTaskService() => _instance;
  BackgroundTaskService._internal();

  final Map<String, Timer> _timers = {};
  final Map<String, bool> _taskStatus = {};

  /// Schedule a periodic task
  void schedulePeriodic(
    String taskId,
    Duration interval,
    Future<void> Function() task, {
    bool immediate = false,
  }) {
    // Cancel existing timer if any
    cancel(taskId);

    if (immediate) {
      _executeTask(taskId, task);
    }

    _timers[taskId] = Timer.periodic(interval, (_) {
      _executeTask(taskId, task);
    });

    logger.logNotification('Scheduled periodic task: $taskId (every ${interval.inSeconds}s)');
  }

  /// Schedule a one-time delayed task
  void scheduleOnce(
    String taskId,
    Duration delay,
    Future<void> Function() task,
  ) {
    cancel(taskId);

    _timers[taskId] = Timer(delay, () {
      _executeTask(taskId, task);
      _timers.remove(taskId);
    });

    logger.logNotification('Scheduled one-time task: $taskId (in ${delay.inSeconds}s)');
  }

  /// Execute a task with error handling
  Future<void> _executeTask(String taskId, Future<void> Function() task) async {
    try {
      _taskStatus[taskId] = true;
      logger.logPerformance('Task $taskId started', Stopwatch()..start());
      
      await task();
      
      logger.logPerformance('Task $taskId completed', Stopwatch()..start());
    } catch (e, stackTrace) {
      logger.logError('Task $taskId failed', error: e, stackTrace: stackTrace);
    } finally {
      _taskStatus[taskId] = false;
    }
  }

  /// Cancel a scheduled task
  void cancel(String taskId) {
    if (_timers.containsKey(taskId)) {
      _timers[taskId]!.cancel();
      _timers.remove(taskId);
      _taskStatus.remove(taskId);
      logger.logNotification('Cancelled task: $taskId');
    }
  }

  /// Check if a task is running
  bool isRunning(String taskId) {
    return _taskStatus[taskId] ?? false;
  }

  /// Check if a task is scheduled
  bool isScheduled(String taskId) {
    return _timers.containsKey(taskId);
  }

  /// Cancel all tasks
  void cancelAll() {
    for (final timer in _timers.values) {
      timer.cancel();
    }
    _timers.clear();
    _taskStatus.clear();
    logger.logNotification('Cancelled all background tasks');
  }

  /// Get list of scheduled tasks
  List<String> get scheduledTasks => _timers.keys.toList();

  /// Dispose
  void dispose() {
    cancelAll();
  }
}

// Global background task service
BackgroundTaskService get backgroundTasks => BackgroundTaskService();
