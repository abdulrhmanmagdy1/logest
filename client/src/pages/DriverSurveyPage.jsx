/**
 * ============================================
 * 📝 Driver Survey Page - نظام إدهام
 * Edham Logistics - Driver Survey Interface
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  FileText, CheckCircle2, Star, MessageSquare, Clock,
  Send, AlertCircle, ChevronRight, ChevronLeft,
  Truck, MapPin, Users, TrendingUp, Award
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const DriverSurveyPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [surveys, setSurveys] = useState([]);
  const [currentSurvey, setCurrentSurvey] = useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [completedSurveys, setCompletedSurveys] = useState([]);

  useEffect(() => {
    fetchSurveys();
  }, []);

  const fetchSurveys = async () => {
    try {
      const [surveysRes, completedRes] = await Promise.all([
        api.get('/surveys/driver/available'),
        api.get('/surveys/driver/completed')
      ]);

      setSurveys(surveysRes.data.surveys || []);
      setCompletedSurveys(completedRes.data.surveys || []);
    } catch (error) {
      console.error('Error fetching surveys:', error);
      showToast('فشل تحميل الاستبيانات', 'error');
    } finally {
      setLoading(false);
    }
  };

  const startSurvey = (survey) => {
    setCurrentSurvey(survey);
    setCurrentQuestionIndex(0);
    setAnswers({});
  };

  const handleAnswer = (questionId, answer) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: answer
    }));
  };

  const nextQuestion = () => {
    if (currentQuestionIndex < currentSurvey.questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1);
    }
  };

  const prevQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(prev => prev - 1);
    }
  };

  const submitSurvey = async () => {
    try {
      setSubmitting(true);

      // Validate all questions are answered
      const unansweredQuestions = currentSurvey.questions.filter(
        q => !answers[q.id] && q.required
      );

      if (unansweredQuestions.length > 0) {
        showToast('يرجى الإجابة على جميع الأسئلة المطلوبة', 'error');
        return;
      }

      const submissionData = {
        surveyId: currentSurvey.id,
        answers: Object.entries(answers).map(([questionId, answer]) => ({
          questionId,
          answer: typeof answer === 'object' ? answer : { value: answer }
        })),
        submittedAt: new Date()
      };

      await api.post('/surveys/submit', submissionData);

      showToast('تم إرسال الاستبيان بنجاح', 'success');
      setCurrentSurvey(null);
      setAnswers({});
      fetchSurveys(); // Refresh surveys
    } catch (error) {
      console.error('Error submitting survey:', error);
      showToast('فشل إرسال الاستبيان', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const renderQuestion = (question) => {
    const currentAnswer = answers[question.id];

    switch (question.type) {
      case 'rating':
        return (
          <div className="space-y-4">
            <div className="flex justify-center space-x-2 space-x-reverse">
              {[1, 2, 3, 4, 5].map((rating) => (
                <button
                  key={rating}
                  onClick={() => handleAnswer(question.id, rating)}
                  className={`p-3 rounded-lg transition-all ${
                    currentAnswer === rating
                      ? 'bg-blue-600 text-white scale-110'
                      : 'bg-gray-100 hover:bg-gray-200'
                  }`}
                >
                  <Star className={`w-6 h-6 ${currentAnswer === rating ? 'fill-current' : ''}`} />
                </button>
              ))}
            </div>
            <div className="flex justify-between text-sm text-gray-600">
              <span>سيء جداً</span>
              <span>ممتاز</span>
            </div>
          </div>
        );

      case 'multiple_choice':
        return (
          <div className="space-y-3">
            {question.options.map((option, index) => (
              <label
                key={index}
                className={`flex items-center p-4 border rounded-lg cursor-pointer transition-colors ${
                  currentAnswer === option
                    ? 'border-blue-600 bg-blue-50'
                    : 'border-gray-200 hover:bg-gray-50'
                }`}
              >
                <input
                  type="radio"
                  name={question.id}
                  value={option}
                  checked={currentAnswer === option}
                  onChange={(e) => handleAnswer(question.id, e.target.value)}
                  className="ml-3"
                />
                <span className="text-right">{option}</span>
              </label>
            ))}
          </div>
        );

      case 'checkboxes':
        return (
          <div className="space-y-3">
            {question.options.map((option, index) => (
              <label
                key={index}
                className={`flex items-center p-4 border rounded-lg cursor-pointer transition-colors ${
                  currentAnswer?.includes(option)
                    ? 'border-blue-600 bg-blue-50'
                    : 'border-gray-200 hover:bg-gray-50'
                }`}
              >
                <input
                  type="checkbox"
                  value={option}
                  checked={currentAnswer?.includes(option) || false}
                  onChange={(e) => {
                    const current = currentAnswer || [];
                    if (e.target.checked) {
                      handleAnswer(question.id, [...current, option]);
                    } else {
                      handleAnswer(question.id, current.filter(item => item !== option));
                    }
                  }}
                  className="ml-3"
                />
                <span className="text-right">{option}</span>
              </label>
            ))}
          </div>
        );

      case 'text':
        return (
          <textarea
            value={currentAnswer?.value || ''}
            onChange={(e) => handleAnswer(question.id, { value: e.target.value })}
            placeholder="اكتب إجابتك هنا..."
            className="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
            rows={4}
          />
        );

      case 'number':
        return (
          <input
            type="number"
            value={currentAnswer?.value || ''}
            onChange={(e) => handleAnswer(question.id, { value: e.target.value })}
            placeholder="أدخل الرقم..."
            className="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        );

      case 'scale':
        return (
          <div className="space-y-4">
            <div className="flex justify-between text-sm text-gray-600">
              <span>{question.scaleLabels?.low || 'غير موافق'}</span>
              <span>{question.scaleLabels?.high || 'موافق بشدة'}</span>
            </div>
            <div className="flex justify-center space-x-2 space-x-reverse">
              {Array.from({ length: question.scaleMax || 10 }, (_, i) => i + 1).map((value) => (
                <button
                  key={value}
                  onClick={() => handleAnswer(question.id, value)}
                  className={`w-12 h-12 rounded-lg font-medium transition-all ${
                    currentAnswer === value
                      ? 'bg-blue-600 text-white scale-110'
                      : 'bg-gray-100 hover:bg-gray-200'
                  }`}
                >
                  {value}
                </button>
              ))}
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  const getProgressPercentage = () => {
    if (!currentSurvey) return 0;
    return ((currentQuestionIndex + 1) / currentSurvey.questions.length) * 100;
  };

  const isQuestionAnswered = (question) => {
    const answer = answers[question.id];
    if (!question.required) return true;
    if (question.type === 'checkboxes') {
      return answer && answer.length > 0;
    }
    return answer !== undefined && answer !== null && answer !== '';
  };

  const canProceed = () => {
    if (!currentSurvey) return false;
    const currentQuestion = currentSurvey.questions[currentQuestionIndex];
    return isQuestionAnswered(currentQuestion);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل الاستبيانات...</p>
        </div>
      </div>
    );
  }

  if (currentSurvey) {
    const currentQuestion = currentSurvey.questions[currentQuestionIndex];
    const isLastQuestion = currentQuestionIndex === currentSurvey.questions.length - 1;

    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100" dir="rtl">
        <div className="max-w-4xl mx-auto px-4 py-8">
          {/* Header */}
          <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
            <div className="flex items-center justify-between mb-4">
              <h1 className="text-2xl font-bold text-gray-900">{currentSurvey.title}</h1>
              <button
                onClick={() => setCurrentSurvey(null)}
                className="text-gray-400 hover:text-gray-600"
              >
                ×
              </button>
            </div>
            
            {/* Progress */}
            <div className="mb-4">
              <div className="flex justify-between text-sm text-gray-600 mb-2">
                <span>السؤال {currentQuestionIndex + 1} من {currentSurvey.questions.length}</span>
                <span>{Math.round(getProgressPercentage())}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${getProgressPercentage()}%` }}
                />
              </div>
            </div>

            {currentSurvey.description && (
              <p className="text-gray-600">{currentSurvey.description}</p>
            )}
          </div>

          {/* Question */}
          <motion.div
            key={currentQuestionIndex}
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            className="bg-white rounded-lg shadow-sm p-6"
          >
            <div className="mb-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-2">
                {currentQuestion.text}
              </h2>
              {currentQuestion.required && (
                <p className="text-sm text-red-600 flex items-center">
                  <AlertCircle className="w-4 h-4 ml-1" />
                  مطلوب
                </p>
              )}
              {currentQuestion.description && (
                <p className="text-gray-600 mt-2">{currentQuestion.description}</p>
              )}
            </div>

            <div className="mb-8">
              {renderQuestion(currentQuestion)}
            </div>

            {/* Navigation */}
            <div className="flex items-center justify-between">
              <button
                onClick={prevQuestion}
                disabled={currentQuestionIndex === 0}
                className={`flex items-center space-x-2 space-x-reverse px-6 py-2 rounded-lg transition-colors ${
                  currentQuestionIndex === 0
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                <ChevronRight className="w-4 h-4" />
                <span>السابق</span>
              </button>

              {isLastQuestion ? (
                <button
                  onClick={submitSurvey}
                  disabled={!canProceed() || submitting}
                  className={`flex items-center space-x-2 space-x-reverse px-6 py-2 rounded-lg transition-colors ${
                    !canProceed() || submitting
                      ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                      : 'bg-green-600 text-white hover:bg-green-700'
                  }`}
                >
                  <Send className="w-4 h-4" />
                  <span>{submitting ? 'جاري الإرسال...' : 'إرسال الاستبيان'}</span>
                </button>
              ) : (
                <button
                  onClick={nextQuestion}
                  disabled={!canProceed()}
                  className={`flex items-center space-x-2 space-x-reverse px-6 py-2 rounded-lg transition-colors ${
                    !canProceed()
                      ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                      : 'bg-blue-600 text-white hover:bg-blue-700'
                  }`}
                >
                  <span>التالي</span>
                  <ChevronLeft className="w-4 h-4" />
                </button>
              )}
            </div>
          </motion.div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50" dir="rtl">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center space-x-4 space-x-reverse">
            <FileText className="w-8 h-8 text-blue-600" />
            <div>
              <h1 className="text-2xl font-bold text-gray-900">استبيانات السائقين</h1>
              <p className="text-sm text-gray-600">شارك برأيك لتحسين خدماتنا</p>
            </div>
          </div>
        </div>
      </div>

      {/* Statistics */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white rounded-lg shadow-sm p-4 border"
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">الاستبيانات المتاحة</p>
                <p className="text-2xl font-bold text-blue-600">{surveys.length}</p>
              </div>
              <FileText className="w-8 h-8 text-blue-600" />
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
                <p className="text-sm text-gray-600">الاستبيانات المكتملة</p>
                <p className="text-2xl font-bold text-green-600">{completedSurveys.length}</p>
              </div>
              <CheckCircle2 className="w-8 h-8 text-green-600" />
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
                <p className="text-sm text-gray-600">متوسط التقييم</p>
                <p className="text-2xl font-bold text-yellow-600">4.2</p>
              </div>
              <Star className="w-8 h-8 text-yellow-600" />
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
                <p className="text-sm text-gray-600">نقاط المكافأة</p>
                <p className="text-2xl font-bold text-purple-600">150</p>
              </div>
              <Award className="w-8 h-8 text-purple-600" />
            </div>
          </motion.div>
        </div>
      </div>

      {/* Surveys List */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {surveys.map((survey, index) => {
            const isCompleted = completedSurveys.some(cs => cs.surveyId === survey.id);
            
            return (
              <motion.div
                key={survey.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                className={`bg-white rounded-lg shadow-sm overflow-hidden border hover:shadow-lg transition-shadow ${
                  isCompleted ? 'opacity-75' : ''
                }`}
              >
                <div className="p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex-1">
                      <h3 className="text-lg font-semibold text-gray-900 mb-2">
                        {survey.title}
                      </h3>
                      <p className="text-gray-600 text-sm line-clamp-2">
                        {survey.description}
                      </p>
                    </div>
                    {isCompleted && (
                      <CheckCircle2 className="w-6 h-6 text-green-600 mr-2" />
                    )}
                  </div>

                  <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <Clock className="w-4 h-4" />
                      <span>{survey.estimatedTime} دقيقة</span>
                    </div>
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <FileText className="w-4 h-4" />
                      <span>{survey.questions.length} سؤال</span>
                    </div>
                  </div>

                  {survey.reward && (
                    <div className="bg-purple-50 text-purple-700 px-3 py-2 rounded-lg text-sm mb-4">
                      مكافأة: {survey.reward.points} نقطة
                    </div>
                  )}

                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        survey.category === 'performance' ? 'bg-blue-100 text-blue-700' :
                        survey.category === 'satisfaction' ? 'bg-green-100 text-green-700' :
                        survey.category === 'safety' ? 'bg-red-100 text-red-700' :
                        'bg-gray-100 text-gray-700'
                      }`}>
                        {survey.category === 'performance' ? 'الأداء' :
                         survey.category === 'satisfaction' ? 'الرضا' :
                         survey.category === 'safety' ? 'السلامة' :
                         survey.category}
                      </span>
                      {survey.priority === 'high' && (
                        <span className="px-2 py-1 bg-red-100 text-red-700 rounded-full text-xs font-medium">
                          مهم
                        </span>
                      )}
                    </div>

                    <button
                      onClick={() => startSurvey(survey)}
                      disabled={isCompleted}
                      className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                        isCompleted
                          ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                          : 'bg-blue-600 text-white hover:bg-blue-700'
                      }`}
                    >
                      {isCompleted ? 'مكتمل' : 'ابدأ الاستبيان'}
                    </button>
                  </div>
                </div>
              </motion.div>
            );
          })}
        </div>

        {surveys.length === 0 && (
          <div className="text-center py-12">
            <FileText className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">لا توجد استبيانات متاحة</h3>
            <p className="text-gray-500">سيتم إعلامك عند توفر استبيانات جديدة</p>
          </div>
        )}
      </div>

      {/* Completed Surveys Section */}
      {completedSurveys.length > 0 && (
        <div className="max-w-7xl mx-auto px-4 py-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">الاستبيانات المكتملة</h2>
          <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                      الاستبيان
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                      تاريخ الإنجاز
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                      المكافأة
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                      الحالة
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {completedSurveys.map((completed, index) => (
                    <tr key={index} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {completed.surveyTitle}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {new Date(completed.completedAt).toLocaleDateString('ar-SA')}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {completed.reward?.points || 0} نقطة
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          مكتمل
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DriverSurveyPage;
