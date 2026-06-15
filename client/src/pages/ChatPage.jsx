/**
 * ============================================
 * 💬 Chat Page - نظام إدهام
 * Edham Logistics - Internal Messaging
 * ============================================
 */

import React, { useState, useEffect, useRef } from 'react';
import { Send, Paperclip, Smile, MoreVertical, Search, User, Phone, Video } from 'lucide-react';
import Button from '../components/UI/Button';
import Input from '../components/UI/Input';

export default function ChatPage() {
  const [selectedChat, setSelectedChat] = useState(null);
  const [message, setMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const messagesEndRef = useRef(null);

  const chats = [
    {
      id: 1,
      name: 'أحمد محمد',
      role: 'سائق',
      avatar: null,
      lastMessage: 'تم تسليم الشحنة بنجاح',
      unread: 2,
      online: true,
      messages: [
        { id: 1, sender: 'other', text: 'السلام عليكم', time: '10:00' },
        { id: 2, sender: 'me', text: 'وعليكم السلام', time: '10:05' },
        { id: 3, sender: 'other', text: 'تم تسليم الشحنة بنجاح', time: '10:10' }
      ]
    },
    {
      id: 2,
      name: 'خالد عبدالله',
      role: 'مشرف',
      avatar: null,
      lastMessage: 'يرجى مراجعة الجدول',
      unread: 0,
      online: true,
      messages: [
        { id: 1, sender: 'other', text: 'يرجى مراجعة الجدول', time: '09:30' }
      ]
    },
    {
      id: 3,
      name: 'سعود علي',
      role: 'محاسب',
      avatar: null,
      lastMessage: 'الفاتورة جاهزة',
      unread: 1,
      online: false,
      messages: [
        { id: 1, sender: 'other', text: 'الفاتورة جاهزة', time: 'أمس' }
      ]
    },
    {
      id: 4,
      name: 'فريق الصيانة',
      role: 'مجموعة',
      avatar: null,
      lastMessage: 'الصيانة مجدولة',
      unread: 0,
      online: false,
      messages: [
        { id: 1, sender: 'other', text: 'الصيانة مجدولة', time: 'أمس' }
      ]
    }
  ];

  const filteredChats = chats.filter(chat =>
    chat.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleSendMessage = () => {
    if (message.trim() && selectedChat) {
      const newMessage = {
        id: Date.now(),
        sender: 'me',
        text: message,
        time: new Date().toLocaleTimeString('ar-SA', { hour: '2-digit', minute: '2-digit' })
      };
      selectedChat.messages.push(newMessage);
      setMessage('');
      scrollToBottom();
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [selectedChat]);

  return (
    <div className="h-screen bg-gray-900 flex">
      {/* Chat List */}
      <div className="w-80 border-l border-gray-700 flex flex-col">
        <div className="p-4 border-b border-gray-700">
          <h1 className="text-2xl font-bold text-white mb-4">المحادثات</h1>
          <div className="relative">
            <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="بحث..."
              className="w-full bg-gray-800 text-white pr-10 pl-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
            />
          </div>
        </div>

        <div className="flex-1 overflow-y-auto">
          {filteredChats.map((chat) => (
            <div
              key={chat.id}
              onClick={() => setSelectedChat(chat)}
              className={`p-4 cursor-pointer hover:bg-gray-800 border-b border-gray-700 ${
                selectedChat?.id === chat.id ? 'bg-gray-800' : ''
              }`}
            >
              <div className="flex items-center gap-3">
                <div className="relative">
                  <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  {chat.online && (
                    <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-gray-900" />
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex justify-between items-center mb-1">
                    <h3 className="text-white font-semibold truncate">{chat.name}</h3>
                    {chat.unread > 0 && (
                      <span className="bg-blue-600 text-white text-xs px-2 py-1 rounded-full">
                        {chat.unread}
                      </span>
                    )}
                  </div>
                  <p className="text-gray-400 text-sm truncate">{chat.lastMessage}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Chat Area */}
      <div className="flex-1 flex flex-col">
        {selectedChat ? (
          <>
            {/* Chat Header */}
            <div className="p-4 border-b border-gray-700 flex items-center justify-between bg-gray-800">
              <div className="flex items-center gap-3">
                <div className="relative">
                  <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                    <User className="w-5 h-5 text-white" />
                  </div>
                  {selectedChat.online && (
                    <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-gray-800" />
                  )}
                </div>
                <div>
                  <h2 className="text-white font-semibold">{selectedChat.name}</h2>
                  <p className="text-gray-400 text-sm">{selectedChat.role}</p>
                </div>
              </div>
              <div className="flex gap-2">
                <button className="p-2 text-gray-400 hover:text-white">
                  <Phone className="w-5 h-5" />
                </button>
                <button className="p-2 text-gray-400 hover:text-white">
                  <Video className="w-5 h-5" />
                </button>
                <button className="p-2 text-gray-400 hover:text-white">
                  <MoreVertical className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Messages */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {selectedChat.messages.map((msg) => (
                <div
                  key={msg.id}
                  className={`flex ${msg.sender === 'me' ? 'justify-end' : 'justify-start'}`}
                >
                  <div
                    className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                      msg.sender === 'me'
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-700 text-white'
                    }`}
                  >
                    <p>{msg.text}</p>
                    <p className={`text-xs mt-1 ${msg.sender === 'me' ? 'text-blue-200' : 'text-gray-400'}`}>
                      {msg.time}
                    </p>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            {/* Message Input */}
            <div className="p-4 border-t border-gray-700 bg-gray-800">
              <div className="flex items-center gap-2">
                <button className="p-2 text-gray-400 hover:text-white">
                  <Paperclip className="w-5 h-5" />
                </button>
                <button className="p-2 text-gray-400 hover:text-white">
                  <Smile className="w-5 h-5" />
                </button>
                <input
                  type="text"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                  placeholder="اكتب رسالتك..."
                  className="flex-1 bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
                />
                <Button
                  onClick={handleSendMessage}
                  icon={<Send className="w-4 h-4" />}
                >
                  إرسال
                </Button>
              </div>
            </div>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <div className="text-center text-gray-400">
              <User className="w-16 h-16 mx-auto mb-4" />
              <p>اختر محادثة للبدء</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
