import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';
import logger from '../utils/logger';
import { 
  ArrowLeft,
  Users,
  Shield,
  Search,
  Plus,
  Trash2,
  UserPlus,
  Lock,
  Unlock,
  Filter,
  Calendar
} from 'lucide-react';

const UserManagement = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRole, setFilterRole] = useState('all');
  const [showAddUser, setShowAddUser] = useState(false);

  const [newUser, setNewUser] = useState({
    name: '',
    email: '',
    password: '',
    role: 'employee',
    phone: '',
    department: ''
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://192.168.1.12:5000/api/users');
      setUsers(response.data);
    } catch (error) {
      logger.error('Error fetching users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://192.168.1.12:5000/api/users', newUser);
      setShowAddUser(false);
      setNewUser({
        name: '',
        email: '',
        password: '',
        role: 'employee',
        phone: '',
        department: ''
      });
      fetchUsers();
    } catch (error) {
      logger.error('Error creating user:', error);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (window.confirm('هل أنت متأكد من حذف هذا المستخدم؟')) {
      try {
        await axios.delete(`http://192.168.1.12:5000/api/users/${userId}`);
        fetchUsers();
      } catch (error) {
        logger.error('Error deleting user:', error);
      }
    }
  };

  const handleToggleStatus = async (userId, currentStatus) => {
    try {
      await axios.patch(`http://192.168.1.12:5000/api/users/${userId}/status`, {
        isActive: !currentStatus
      });
      fetchUsers();
    } catch (error) {
      logger.error('Error toggling user status:', error);
    }
  };

  const filteredUsers = users.filter(u => {
    const matchesSearch = u.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         u.email?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole = filterRole === 'all' || u.role === filterRole;
    return matchesSearch && matchesRole;
  });

  const roleLabels = {
    admin: 'مدير',
    supervisor: 'مشرف',
    accountant: 'محاسب',
    driver: 'سائق',
    client: 'عميل',
    employee: 'موظف'
  };

  const roleColors = {
    admin: 'bg-red-500/20 text-red-400',
    supervisor: 'bg-purple-500/20 text-purple-400',
    accountant: 'bg-blue-500/20 text-blue-400',
    driver: 'bg-green-500/20 text-green-400',
    client: 'bg-yellow-500/20 text-yellow-400',
    employee: 'bg-gray-500/20 text-gray-400'
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-edham-black flex items-center justify-center">
        <div className="text-edham-white text-2xl">جاري التحميل...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-edham-black">
      <div className="bg-edham-dark border-b border-edham-gray">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <button onClick={() => navigate('/dashboard')} className="text-edham-white hover:text-edham-gold transition-colors">
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div className="flex items-center gap-2">
              <div className="bg-edham-white p-1.5 rounded-full">
                <Users className="w-5 h-5 text-edham-black" />
              </div>
              <h1 className="text-xl font-bold text-edham-white">إدارة المستخدمين</h1>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-edham-white">{user?.name}</span>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-edham-dark rounded-xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3">
              <div className="bg-edham-blue/20 p-3 rounded-xl">
                <Users className="w-6 h-6 text-edham-blue" />
              </div>
              <div>
                <p className="text-edham-white/70 text-sm">إجمالي المستخدمين</p>
                <p className="text-2xl font-bold text-edham-white">{users.length}</p>
              </div>
            </div>
          </div>
          <div className="bg-edham-dark rounded-xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3">
              <div className="bg-edham-green/20 p-3 rounded-xl">
                <Users className="w-6 h-6 text-edham-green" />
              </div>
              <div>
                <p className="text-edham-white/70 text-sm">نشطين</p>
                <p className="text-2xl font-bold text-edham-white">{users.filter(u => u.isActive).length}</p>
              </div>
            </div>
          </div>
          <div className="bg-edham-dark rounded-xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3">
              <div className="bg-edham-primary/20 p-3 rounded-xl">
                <Shield className="w-6 h-6 text-edham-primary" />
              </div>
              <div>
                <p className="text-edham-white/70 text-sm">مديرين</p>
                <p className="text-2xl font-bold text-edham-white">{users.filter(u => u.role === 'admin').length}</p>
              </div>
            </div>
          </div>
          <div className="bg-edham-dark rounded-xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3">
              <div className="bg-edham-yellow/20 p-3 rounded-xl">
                <Calendar className="w-6 h-6 text-edham-yellow" />
              </div>
              <div>
                <p className="text-edham-white/70 text-sm">جدد</p>
                <p className="text-2xl font-bold text-edham-white">{users.filter(u => {
                  const thirtyDaysAgo = new Date();
                  thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
                  return new Date(u.createdAt) > thirtyDaysAgo;
                }).length}</p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex flex-col md:flex-row gap-4 mb-6">
          <button
            onClick={() => setShowAddUser(true)}
            className="bg-edham-primary text-white font-bold px-6 py-3 rounded-xl hover:bg-edham-primaryLight transition-all flex items-center justify-center gap-2"
          >
            <UserPlus className="w-5 h-5" />
            إضافة مستخدم
          </button>
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
            <input
              type="text"
              placeholder="بحث في المستخدمين..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-xl text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary"
            />
          </div>
          <select
            value={filterRole}
            onChange={(e) => setFilterRole(e.target.value)}
            className="bg-edham-black border border-edham-gray rounded-xl px-4 py-3 text-edham-white focus:outline-none focus:ring-2 focus:ring-edham-primary"
          >
            <option value="all">كل الأدوار</option>
            <option value="admin">مدير</option>
            <option value="supervisor">مشرف</option>
            <option value="accountant">محاسب</option>
            <option value="driver">سائق</option>
            <option value="client">عميل</option>
            <option value="employee">موظف</option>
          </select>
        </div>

        <div className="bg-edham-dark rounded-2xl border border-edham-gray overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-edham-black">
                <tr>
                  <th className="px-6 py-4 text-right text-edham-white font-semibold">المستخدم</th>
                  <th className="px-6 py-4 text-right text-edham-white font-semibold">البريد الإلكتروني</th>
                  <th className="px-6 py-4 text-right text-edham-white font-semibold">الدور</th>
                  <th className="px-6 py-4 text-right text-edham-white font-semibold">الحالة</th>
                  <th className="px-6 py-4 text-right text-edham-white font-semibold">تاريخ الإنشاء</th>
                  <th className="px-6 py-4 text-right text-edham-white font-semibold">الإجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((userItem) => (
                  <tr key={userItem._id} className="border-t border-edham-gray hover:bg-edham-white/5 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="bg-edham-primary/20 p-2 rounded-full">
                          <span className="text-edham-primary font-bold">{userItem.name?.[0]}</span>
                        </div>
                        <div>
                          <p className="text-edham-white font-semibold">{userItem.name}</p>
                          <p className="text-edham-white/50 text-sm">{userItem.phone || '-'}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-edham-white/70">{userItem.email}</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-xs ${roleColors[userItem.role] || 'bg-gray-500/20 text-gray-400'}`}>
                        {roleLabels[userItem.role] || userItem.role}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-xs ${userItem.isActive ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400'}`}>
                        {userItem.isActive ? 'نشط' : 'غير نشط'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-edham-white/70">{new Date(userItem.createdAt).toLocaleDateString('ar-EG')}</td>
                    <td className="px-6 py-4">
                      <div className="flex gap-2">
                        <button
                          onClick={() => handleToggleStatus(userItem._id, userItem.isActive)}
                          className={`p-2 rounded-lg transition-colors ${userItem.isActive ? 'bg-red-500/20 hover:bg-red-500/30 text-red-400' : 'bg-green-500/20 hover:bg-green-500/30 text-green-400'}`}
                        >
                          {userItem.isActive ? <Lock className="w-4 h-4" /> : <Unlock className="w-4 h-4" />}
                        </button>
                        <button
                          onClick={() => handleDeleteUser(userItem._id)}
                          className="p-2 rounded-lg bg-red-500/20 hover:bg-red-500/30 text-red-400 transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {showAddUser && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-edham-dark rounded-2xl p-6 w-full max-w-md border border-edham-gray">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-edham-white">إضافة مستخدم جديد</h2>
              <button onClick={() => setShowAddUser(false)} className="text-edham-white hover:text-edham-gold transition-colors">✕</button>
            </div>
            <form onSubmit={handleCreateUser} className="space-y-4">
              <div>
                <label className="block text-edham-white mb-2">الاسم</label>
                <input
                  type="text"
                  value={newUser.name}
                  onChange={(e) => setNewUser({...newUser, name: e.target.value})}
                  className="w-full px-4 py-2 bg-edham-black border border-edham-gray rounded-lg text-edham-white"
                  required
                />
              </div>
              <div>
                <label className="block text-edham-white mb-2">البريد الإلكتروني</label>
                <input
                  type="email"
                  value={newUser.email}
                  onChange={(e) => setNewUser({...newUser, email: e.target.value})}
                  className="w-full px-4 py-2 bg-edham-black border border-edham-gray rounded-lg text-edham-white"
                  required
                />
              </div>
              <div>
                <label className="block text-edham-white mb-2">كلمة المرور</label>
                <input
                  type="password"
                  value={newUser.password}
                  onChange={(e) => setNewUser({...newUser, password: e.target.value})}
                  className="w-full px-4 py-2 bg-edham-black border border-edham-gray rounded-lg text-edham-white"
                  required
                />
              </div>
              <div>
                <label className="block text-edham-white mb-2">الدور</label>
                <select
                  value={newUser.role}
                  onChange={(e) => setNewUser({...newUser, role: e.target.value})}
                  className="w-full px-4 py-2 bg-edham-black border border-edham-gray rounded-lg text-edham-white"
                  required
                >
                  <option value="employee">موظف</option>
                  <option value="driver">سائق</option>
                  <option value="client">عميل</option>
                  <option value="supervisor">مشرف</option>
                  <option value="accountant">محاسب</option>
                  <option value="admin">مدير</option>
                </select>
              </div>
              <button
                type="submit"
                className="w-full bg-edham-primary text-white py-3 rounded-xl hover:bg-edham-primaryLight transition-all font-semibold"
              >
                إضافة المستخدم
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserManagement;
