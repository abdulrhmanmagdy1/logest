import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

class AuthService extends ChangeNotifier {
  String? _token;
  String? _userRole;
  Map<String, dynamic>? _user;

  String? get token => _token;
  String? get userRole => _userRole;
  Map<String, dynamic>? get user => _user;
  bool get isAuthenticated => _token != null;

  final String baseUrl = 'http://192.168.1.12:5000/api';

  Future<bool> login(String email, String password) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/auth/login'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'email': email,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        _token = data['token'];
        
        // Get user info
        await _getUserInfo();
        
        // Save token locally
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('token', _token!);
        
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      print('Login error: $e');
      return false;
    }
  }

  Future<void> _getUserInfo() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/auth/me'),
        headers: {'Authorization': 'Bearer $_token'},
      );

      if (response.statusCode == 200) {
        _user = json.decode(response.body);
        _userRole = _user!['role'];
      }
    } catch (e) {
      print('Get user info error: $e');
    }
  }

  Future<bool> checkAuth() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _token = prefs.getString('token');
      
      if (_token != null) {
        await _getUserInfo();
        return true;
      }
      return false;
    } catch (e) {
      print('Check auth error: $e');
      return false;
    }
  }

  Future<void> logout() async {
    _token = null;
    _userRole = null;
    _user = null;
    
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    
    notifyListeners();
  }
}
