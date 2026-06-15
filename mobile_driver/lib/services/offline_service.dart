import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class OfflineService extends ChangeNotifier {
  Database? _database;
  bool _isOnline = true;
  bool _isSyncing = false;
  int _pendingSyncCount = 0;

  bool get isOnline => _isOnline;
  bool get isSyncing => _isSyncing;
  int get pendingSyncCount => _pendingSyncCount;

  final String _apiBaseUrl = 'http://your-api-url.com/api';

  // Initialize database
  Future<void> initialize() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'edham_offline.db');

    _database = await openDatabase(
      path,
      version: 1,
      onCreate: _onCreateDatabase,
    );

    // Check connectivity
    await _checkConnectivity();

    // Listen to connectivity changes
    Connectivity().onConnectivityChanged.listen((result) async {
      await _checkConnectivity();
      if (_isOnline) {
        await syncPendingData();
      }
    });

    // Count pending sync items
    await _countPendingSync();
  }

  Future<void> _onCreateDatabase(Database db, int version) async {
    // Trips table
    await db.execute('''
      CREATE TABLE trips (
        id TEXT PRIMARY KEY,
        status TEXT,
        pickup_address TEXT,
        delivery_address TEXT,
        customer_name TEXT,
        customer_phone TEXT,
        price REAL,
        weight REAL,
        distance REAL,
        created_at TEXT,
        updated_at TEXT,
        synced INTEGER DEFAULT 0
      )
    ''');

    // Trip updates table
    await db.execute('''
      CREATE TABLE trip_updates (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        trip_id TEXT,
        status TEXT,
        latitude REAL,
        longitude REAL,
        timestamp TEXT,
        synced INTEGER DEFAULT 0
      )
    ''');

    // Locations table
    await db.execute('''
      CREATE TABLE locations (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        trip_id TEXT,
        latitude REAL,
        longitude REAL,
        speed REAL,
        timestamp TEXT,
        synced INTEGER DEFAULT 0
      )
    ''');

    // Documents table
    await db.execute('''
      CREATE TABLE documents (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        trip_id TEXT,
        file_path TEXT,
        file_type TEXT,
        uploaded INTEGER DEFAULT 0
      )
    ''');

    // Sync queue table
    await db.execute('''
      CREATE TABLE sync_queue (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        entity_type TEXT,
        entity_id TEXT,
        action TEXT,
        data TEXT,
        timestamp TEXT,
        synced INTEGER DEFAULT 0
      )
    ''');
  }

  // Check connectivity
  Future<void> _checkConnectivity() async {
    final connectivityResult = await Connectivity().checkConnectivity();
    _isOnline = connectivityResult != ConnectivityResult.none;
    notifyListeners();
  }

  // Save trip offline
  Future<void> saveTripOffline(Map<String, dynamic> trip) async {
    if (_database == null) return;

    await _database!.insert(
      'trips',
      {
        'id': trip['_id'],
        'status': trip['status'],
        'pickup_address': trip['pickupAddress'],
        'delivery_address': trip['deliveryAddress'],
        'customer_name': trip['customerName'],
        'customer_phone': trip['customerPhone'],
        'price': trip['price'],
        'weight': trip['weight'],
        'distance': trip['distance'],
        'created_at': trip['createdAt'],
        'updated_at': trip['updatedAt'],
        'synced': 0,
      },
      conflictAlgorithm: ConflictAlgorithm.replace,
    );

    await _addToSyncQueue('trips', trip['_id'], 'update', trip);
    await _countPendingSync();
  }

  // Save location update offline
  Future<void> saveLocationUpdate({
    required String tripId,
    required double latitude,
    required double longitude,
    double? speed,
  }) async {
    if (_database == null) return;

    await _database!.insert('locations', {
      'trip_id': tripId,
      'latitude': latitude,
      'longitude': longitude,
      'speed': speed ?? 0.0,
      'timestamp': DateTime.now().toIso8601String(),
      'synced': 0,
    });

    await _addToSyncQueue('locations', tripId, 'update', {
      'latitude': latitude,
      'longitude': longitude,
      'speed': speed,
      'timestamp': DateTime.now().toIso8601String(),
    });

    await _countPendingSync();
  }

  // Save trip status update offline
  Future<void> saveTripStatusUpdate({
    required String tripId,
    required String status,
  }) async {
    if (_database == null) return;

    await _database!.insert('trip_updates', {
      'trip_id': tripId,
      'status': status,
      'timestamp': DateTime.now().toIso8601String(),
      'synced': 0,
    });

    await _addToSyncQueue('trip_updates', tripId, 'update', {
      'status': status,
      'timestamp': DateTime.now().toIso8601String(),
    });

    await _countPendingSync();
  }

  // Save document offline
  Future<void> saveDocumentOffline({
    required String tripId,
    required String filePath,
    required String fileType,
  }) async {
    if (_database == null) return;

    await _database!.insert('documents', {
      'trip_id': tripId,
      'file_path': filePath,
      'file_type': fileType,
      'uploaded': 0,
    });

    await _addToSyncQueue('documents', tripId, 'upload', {
      'file_path': filePath,
      'file_type': fileType,
    });

    await _countPendingSync();
  }

  // Add to sync queue
  Future<void> _addToSyncQueue(
    String entityType,
    String entityId,
    String action,
    Map<String, dynamic> data,
  ) async {
    if (_database == null) return;

    await _database!.insert('sync_queue', {
      'entity_type': entityType,
      'entity_id': entityId,
      'action': action,
      'data': jsonEncode(data),
      'timestamp': DateTime.now().toIso8601String(),
      'synced': 0,
    });
  }

  // Sync pending data
  Future<void> syncPendingData() async {
    if (_database == null || !_isOnline || _isSyncing) return;

    _isSyncing = true;
    notifyListeners();

    try {
      // Sync locations
      await _syncLocations();

      // Sync trip updates
      await _syncTripUpdates();

      // Sync documents
      await _syncDocuments();

      // Sync trips
      await _syncTrips();

      // Process sync queue
      await _processSyncQueue();

      await _countPendingSync();
    } catch (e) {
      debugPrint('Error syncing data: $e');
    } finally {
      _isSyncing = false;
      notifyListeners();
    }
  }

  Future<void> _syncLocations() async {
    if (_database == null) return;

    final locations = await _database!.query(
      'locations',
      where: 'synced = ?',
      whereArgs: [0],
    );

    for (var location in locations) {
      try {
        final response = await http.post(
          Uri.parse('$_apiBaseUrl/trips/${location['trip_id']}/location'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'latitude': location['latitude'],
            'longitude': location['longitude'],
            'speed': location['speed'],
            'timestamp': location['timestamp'],
          }),
        );

        if (response.statusCode == 200) {
          await _database!.update(
            'locations',
            {'synced': 1},
            where: 'id = ?',
            whereArgs: [location['id']],
          );
        }
      } catch (e) {
        debugPrint('Error syncing location: $e');
      }
    }
  }

  Future<void> _syncTripUpdates() async {
    if (_database == null) return;

    final updates = await _database!.query(
      'trip_updates',
      where: 'synced = ?',
      whereArgs: [0],
    );

    for (var update in updates) {
      try {
        final response = await http.patch(
          Uri.parse('$_apiBaseUrl/trips/${update['trip_id']}/status'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'status': update['status'],
          }),
        );

        if (response.statusCode == 200) {
          await _database!.update(
            'trip_updates',
            {'synced': 1},
            where: 'id = ?',
            whereArgs: [update['id']],
          );
        }
      } catch (e) {
        debugPrint('Error syncing trip update: $e');
      }
    }
  }

  Future<void> _syncDocuments() async {
    if (_database == null) return;

    final documents = await _database!.query(
      'documents',
      where: 'uploaded = ?',
      whereArgs: [0],
    );

    for (var doc in documents) {
      try {
        // Upload document (implementation depends on upload service)
        // For now, mark as uploaded
        await _database!.update(
          'documents',
          {'uploaded': 1},
          where: 'id = ?',
          whereArgs: [doc['id']],
        );
      } catch (e) {
        debugPrint('Error syncing document: $e');
      }
    }
  }

  Future<void> _syncTrips() async {
    if (_database == null) return;

    final trips = await _database!.query(
      'trips',
      where: 'synced = ?',
      whereArgs: [0],
    );

    for (var trip in trips) {
      try {
        final response = await http.put(
          Uri.parse('$_apiBaseUrl/trips/${trip['id']}'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'status': trip['status'],
            'updatedAt': DateTime.now().toIso8601String(),
          }),
        );

        if (response.statusCode == 200) {
          await _database!.update(
            'trips',
            {'synced': 1},
            where: 'id = ?',
            whereArgs: [trip['id']],
          );
        }
      } catch (e) {
        debugPrint('Error syncing trip: $e');
      }
    }
  }

  Future<void> _processSyncQueue() async {
    if (_database == null) return;

    final queueItems = await _database!.query(
      'sync_queue',
      where: 'synced = ?',
      whereArgs: [0],
      orderBy: 'timestamp ASC',
    );

    for (var item in queueItems) {
      try {
        // Process each sync queue item
        // Implementation depends on entity type and action
        await _database!.update(
          'sync_queue',
          {'synced': 1},
          where: 'id = ?',
          whereArgs: [item['id']],
        );
      } catch (e) {
        debugPrint('Error processing sync queue item: $e');
      }
    }
  }

  // Count pending sync items
  Future<void> _countPendingSync() async {
    if (_database == null) return;

    final count = await _database!.rawQuery(
      'SELECT COUNT(*) as count FROM sync_queue WHERE synced = 0',
    );
    _pendingSyncCount = count.first['count'] as int;
    notifyListeners();
  }

  // Get offline trips
  Future<List<Map<String, dynamic>>> getOfflineTrips() async {
    if (_database == null) return [];

    final trips = await _database!.query('trips');
    return trips.map((t) => Map<String, dynamic>.from(t)).toList();
  }

  // Get pending sync count by type
  Future<Map<String, int>> getPendingSyncCountByType() async {
    if (_database == null) return {};

    final result = await _database!.rawQuery('''
      SELECT entity_type, COUNT(*) as count
      FROM sync_queue
      WHERE synced = 0
      GROUP BY entity_type
    ''');

    return Map.fromEntries(
      result.map((row) => MapEntry(
        row['entity_type'] as String,
        row['count'] as int,
      )),
    );
  }

  // Clear old synced data
  Future<void> clearOldSyncedData({int daysOld = 7}) async {
    if (_database == null) return;

    final cutoffDate = DateTime.now()
        .subtract(Duration(days: daysOld))
        .toIso8601String();

    await _database!.delete(
      'sync_queue',
      where: 'synced = 1 AND timestamp < ?',
      whereArgs: [cutoffDate],
    );

    await _database!.delete(
      'locations',
      where: 'synced = 1 AND timestamp < ?',
      whereArgs: [cutoffDate],
    );

    await _database!.delete(
      'trip_updates',
      where: 'synced = 1 AND timestamp < ?',
      whereArgs: [cutoffDate],
    );
  }

  @override
  void dispose() {
    _database?.close();
    super.dispose();
  }
}
