/**
 * ============================================
 * 🛒 Client Shop Page - نظام إدهام
 * Edham Logistics - Client Shop Interface
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  ShoppingBag, Truck, Package, MapPin, CreditCard,
  Wallet, Plus, Minus, Star, Clock, CheckCircle,
  AlertCircle, ArrowRight, Filter, Search, ChevronDown,
  DollarSign, FileText, TrendingUp, BarChart3
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const ClientShopPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();
  
  const [loadingServices, setLoadingServices] = useState([]);
  const [cart, setCart] = useState([]);
  const [balance, setBalance] = useState(0);
  const [stats, setStats] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [searchTerm, setSearchTerm] = useState("");
  const [showCheckoutModal, setShowCheckoutModal] = useState(false);
  const [selectedPayment, setSelectedPayment] = useState("balance");
  const [loading, setLoading] = useState(true);

  // Loading service categories
  const categories = [
    { id: "all", name: "جميع الخدمات", icon: Package },
    { id: "refrigerated", name: "نقل مبرد", icon: Truck },
    { id: "heavy", name: "حمولة ثقيلة", icon: Package },
    { id: "express", name: "توصيل سريع", icon: Clock },
    { id: "longDistance", name: "مسافات طويلة", icon: MapPin },
  ];

  // Sample loading services
  const sampleServices = [
    {
      id: 1,
      name: "نقل مواد غذائية مبردة",
      category: "refrigerated",
      price: 500,
      duration: "2-3 ساعات",
      capacity: "طن 1",
      rating: 4.8,
      description: "نقل مواد غذائية تحتاج لتبريد مستمر",
      image: "/api/placeholder/300/200"
    },
    {
      id: 2,
      name: "نقل أثاث منزلي",
      category: "heavy",
      price: 800,
      duration: "4-6 ساعات",
      capacity: "طن 3",
      rating: 4.9,
      description: "نقل أثاث ومنزلقات مع التغليف",
      image: "/api/placeholder/300/200"
    },
    {
      id: 3,
      name: "توصيل طارئ داخل المدينة",
      category: "express",
      price: 300,
      duration: "1-2 ساعة",
      capacity: "500 كجم",
      rating: 4.7,
      description: "توصيل سريع خلال ساعتين",
      image: "/api/placeholder/300/200"
    },
    {
      id: 4,
      name: "نقل بين المدن",
      category: "longDistance",
      price: 1500,
      duration: "1-2 يوم",
      capacity: "طن 5",
      rating: 4.6,
      description: "نقل بين المدن الرئيسية",
      image: "/api/placeholder/300/200"
    },
  ];

  useEffect(() => {
    fetchClientData();
  }, []);

  const fetchClientData = async () => {
    try {
      const [balanceRes, statsRes, servicesRes] = await Promise.all([
        api.get("/client/balance"),
        api.get("/client/stats"),
        api.get("/services/loading")
      ]);

      setBalance(balanceRes.data.balance || 0);
      setStats(statsRes.data);
      setLoadingServices(servicesRes.data || sampleServices);
    } catch (error) {
      console.error("Error fetching client data:", error);
      // Use sample data if API fails
      setBalance(2500);
      setStats({
        totalOrders: 45,
        completedOrders: 42,
        pendingOrders: 3,
        totalSpent: 18500
      });
      setLoadingServices(sampleServices);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = (service) => {
    const existingItem = cart.find(item => item.id === service.id);
    if (existingItem) {
      setCart(cart.map(item => 
        item.id === service.id 
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, { ...service, quantity: 1 }]);
    }
    showToast("تمت إضافة الخدمة للسلة", "success");
  };

  const removeFromCart = (serviceId) => {
    setCart(cart.filter(item => item.id !== serviceId));
  };

  const updateQuantity = (serviceId, quantity) => {
    if (quantity <= 0) {
      removeFromCart(serviceId);
    } else {
      setCart(cart.map(item => 
        item.id === serviceId 
          ? { ...item, quantity }
          : item
      ));
    }
  };

  const getTotalPrice = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const handleCheckout = async () => {
    try {
      const orderData = {
        items: cart,
        totalAmount: getTotalPrice(),
        paymentMethod: selectedPayment,
        deliveryAddress: user.address,
        notes: ""
      };

      const response = await api.post("/orders/create", orderData);
      
      showToast("تم إنشاء الطلب بنجاح!", "success");
      setCart([]);
      setShowCheckoutModal(false);
      
      // Refresh balance
      fetchClientData();
    } catch (error) {
      showToast("فشل إنشاء الطلب", "error");
      console.error("Checkout error:", error);
    }
  };

  const filteredServices = loadingServices.filter(service => {
    const matchesCategory = selectedCategory === "all" || service.category === selectedCategory;
    const matchesSearch = service.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         service.description.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل المتجر...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100" dir="rtl">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4 space-x-reverse">
              <ShoppingBag className="w-8 h-8 text-blue-600" />
              <div>
                <h1 className="text-2xl font-bold text-gray-900">متجر إدهام للخدمات اللوجستية</h1>
                <p className="text-sm text-gray-600">اطلب حمولتك بسهولة وأمان</p>
              </div>
            </div>
            
            <div className="flex items-center space-x-4 space-x-reverse">
              {/* Balance Card */}
              <div className="bg-gradient-to-r from-green-500 to-green-600 text-white px-4 py-2 rounded-lg">
                <div className="flex items-center space-x-2 space-x-reverse">
                  <Wallet className="w-5 h-5" />
                  <div>
                    <p className="text-xs opacity-90">الرصيد المتاح</p>
                    <p className="text-lg font-bold">{balance.toLocaleString()} ريال</p>
                  </div>
                </div>
              </div>

              {/* Cart */}
              <button
                onClick={() => setShowCheckoutModal(true)}
                className="relative bg-blue-600 text-white p-3 rounded-lg hover:bg-blue-700 transition-colors"
              >
                <ShoppingBag className="w-6 h-6" />
                {cart.length > 0 && (
                  <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs w-6 h-6 rounded-full flex items-center justify-center">
                    {cart.reduce((sum, item) => sum + item.quantity, 0)}
                  </span>
                )}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Stats Cards */}
      {stats && (
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">إجمالي الطلبات</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.totalOrders}</p>
                </div>
                <Package className="w-8 h-8 text-blue-600" />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">الطلبات المكتملة</p>
                  <p className="text-2xl font-bold text-green-600">{stats.completedOrders}</p>
                </div>
                <CheckCircle className="w-8 h-8 text-green-600" />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">الطلبات المعلقة</p>
                  <p className="text-2xl font-bold text-yellow-600">{stats.pendingOrders}</p>
                </div>
                <Clock className="w-8 h-8 text-yellow-600" />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">إجمالي المصروفات</p>
                  <p className="text-2xl font-bold text-gray-900">{stats.totalSpent.toLocaleString()} ريال</p>
                </div>
                <DollarSign className="w-8 h-8 text-gray-600" />
              </div>
            </motion.div>
          </div>
        </div>
      )}

      {/* Categories and Search */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="bg-white rounded-lg shadow-sm p-4 mb-6 border">
          <div className="flex flex-col md:flex-row gap-4">
            {/* Search */}
            <div className="flex-1 relative">
              <Search className="absolute right-3 top-3 w-5 h-5 text-gray-400" />
              <input
                type="text"
                placeholder="ابحث عن خدمة..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pr-10 pl-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            {/* Categories */}
            <div className="flex gap-2 overflow-x-auto">
              {categories.map((category) => {
                const Icon = category.icon;
                return (
                  <button
                    key={category.id}
                    onClick={() => setSelectedCategory(category.id)}
                    className={`flex items-center space-x-2 space-x-reverse px-4 py-2 rounded-lg whitespace-nowrap transition-colors ${
                      selectedCategory === category.id
                        ? "bg-blue-600 text-white"
                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span className="text-sm font-medium">{category.name}</span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        {/* Services Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredServices.map((service, index) => (
            <motion.div
              key={service.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="bg-white rounded-lg shadow-sm overflow-hidden border hover:shadow-lg transition-shadow"
            >
              {/* Service Image */}
              <div className="h-48 bg-gradient-to-br from-blue-100 to-indigo-100 flex items-center justify-center">
                <Truck className="w-16 h-16 text-blue-600" />
              </div>

              {/* Service Details */}
              <div className="p-4">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg font-semibold text-gray-900">{service.name}</h3>
                  <div className="flex items-center space-x-1 space-x-reverse">
                    <Star className="w-4 h-4 text-yellow-500 fill-current" />
                    <span className="text-sm text-gray-600">{service.rating}</span>
                  </div>
                </div>

                <p className="text-gray-600 text-sm mb-3">{service.description}</p>

                <div className="flex items-center justify-between text-sm text-gray-500 mb-3">
                  <div className="flex items-center space-x-1 space-x-reverse">
                    <Clock className="w-4 h-4" />
                    <span>{service.duration}</span>
                  </div>
                  <div className="flex items-center space-x-1 space-x-reverse">
                    <Package className="w-4 h-4" />
                    <span>{service.capacity}</span>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-2xl font-bold text-blue-600">{service.price} ريال</p>
                  </div>
                  <button
                    onClick={() => addToCart(service)}
                    className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2 space-x-reverse"
                  >
                    <Plus className="w-4 h-4" />
                    <span>أضف للسلة</span>
                  </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {/* Checkout Modal */}
      {showCheckoutModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">سلة المشتريات</h2>
                <button
                  onClick={() => setShowCheckoutModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              {cart.length === 0 ? (
                <div className="text-center py-8">
                  <ShoppingBag className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500">السلة فارغة</p>
                </div>
              ) : (
                <>
                  {/* Cart Items */}
                  <div className="space-y-4 mb-6">
                    {cart.map((item) => (
                      <div key={item.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                        <div className="flex-1">
                          <h4 className="font-medium text-gray-900">{item.name}</h4>
                          <p className="text-sm text-gray-600">{item.price} ريال × {item.quantity}</p>
                        </div>
                        <div className="flex items-center space-x-2 space-x-reverse">
                          <button
                            onClick={() => updateQuantity(item.id, item.quantity - 1)}
                            className="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center"
                          >
                            <Minus className="w-4 h-4" />
                          </button>
                          <span className="w-8 text-center">{item.quantity}</span>
                          <button
                            onClick={() => updateQuantity(item.id, item.quantity + 1)}
                            className="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center"
                          >
                            <Plus className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => removeFromCart(item.id)}
                            className="w-8 h-8 rounded-full bg-red-100 hover:bg-red-200 text-red-600 flex items-center justify-center mr-2"
                          >
                            ×
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Payment Method */}
                  <div className="mb-6">
                    <h3 className="font-medium text-gray-900 mb-3">طريقة الدفع</h3>
                    <div className="space-y-2">
                      <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50">
                        <input
                          type="radio"
                          name="payment"
                          value="balance"
                          checked={selectedPayment === "balance"}
                          onChange={(e) => setSelectedPayment(e.target.value)}
                          className="ml-3"
                        />
                        <Wallet className="w-5 h-5 ml-2 text-green-600" />
                        <span>من الرصيد ({balance.toLocaleString()} ريال)</span>
                      </label>
                      <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50">
                        <input
                          type="radio"
                          name="payment"
                          value="card"
                          checked={selectedPayment === "card"}
                          onChange={(e) => setSelectedPayment(e.target.value)}
                          className="ml-3"
                        />
                        <CreditCard className="w-5 h-5 ml-2 text-blue-600" />
                        <span>بطاقة ائتمانية</span>
                      </label>
                      <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50">
                        <input
                          type="radio"
                          name="payment"
                          value="cash"
                          checked={selectedPayment === "cash"}
                          onChange={(e) => setSelectedPayment(e.target.value)}
                          className="ml-3"
                        />
                        <DollarSign className="w-5 h-5 ml-2 text-gray-600" />
                        <span>دفع عند الاستلام</span>
                      </label>
                    </div>
                  </div>

                  {/* Total */}
                  <div className="border-t pt-4">
                    <div className="flex items-center justify-between mb-4">
                      <span className="text-lg font-semibold">الإجمالي:</span>
                      <span className="text-2xl font-bold text-blue-600">{getTotalPrice()} ريال</span>
                    </div>

                    <button
                      onClick={handleCheckout}
                      disabled={selectedPayment === "balance" && balance < getTotalPrice()}
                      className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                    >
                      {selectedPayment === "balance" && balance < getTotalPrice()
                        ? "الرصيد غير كافي"
                        : "إتمام الطلب"
                      }
                    </button>
                  </div>
                </>
              )}
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default ClientShopPage;
