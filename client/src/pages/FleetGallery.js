import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Truck, Filter, ZoomIn } from 'lucide-react';
import Logo from '../components/Logo';

const FleetGallery = () => {
  const navigate = useNavigate();
  const [selectedImage, setSelectedImage] = React.useState(null);

  const vehicles = [
    {
      id: 1,
      name: 'شاحنة نقل مبرد كبيرة',
      nameEn: 'Large Refrigerated Truck',
      image: '/Screenshot 2026-04-18 221548.png',
      description: 'شاحنة كبيرة لنقل البضائع المبردة بسعة عالية',
      descriptionEn: 'Large truck for refrigerated goods transport with high capacity'
    },
    {
      id: 2,
      name: 'شاحنة نقل ثقيلة',
      nameEn: 'Heavy Duty Truck',
      image: '/Screenshot 2026-04-18 221606.png',
      description: 'شاحنة ثقيلة لنقل البضائع الكبيرة والثقيلة',
      descriptionEn: 'Heavy duty truck for large and heavy cargo transport'
    },
    {
      id: 3,
      name: 'مركبة نقل متوسطة',
      nameEn: 'Medium Transport Vehicle',
      image: '/Screenshot 2026-04-18 224751.png',
      description: 'مركبة متوسطة الحجم للنقل السريع داخل المدن',
      descriptionEn: 'Medium-sized vehicle for fast city transport'
    },
    {
      id: 4,
      name: 'شاحنة نقل سريعة',
      nameEn: 'Fast Delivery Truck',
      image: '/Screenshot 2026-04-18 224802.png',
      description: 'شاحنة سريعة للتوصيل الفوري',
      descriptionEn: 'Fast truck for immediate delivery'
    },
    {
      id: 5,
      name: 'مركبة نقل صغيرة',
      nameEn: 'Small Transport Vehicle',
      image: '/Screenshot 2026-04-18 224815.png',
      description: 'مركبة صغيرة للنقل المحلي السريع',
      descriptionEn: 'Small vehicle for fast local transport'
    },
    {
      id: 6,
      name: 'شاحنة نقل مبرد متوسطة',
      nameEn: 'Medium Refrigerated Truck',
      image: '/Screenshot 2026-04-18 224823.png',
      description: 'شاحنة متوسطة لنقل البضائع المبردة',
      descriptionEn: 'Medium truck for refrigerated goods transport'
    },
    {
      id: 7,
      name: 'شاحنة نقل بضائع',
      nameEn: 'Cargo Truck',
      image: '/Screenshot 2026-04-18 224845.png',
      description: 'شاحنة متعددة الاستخدامات لنقل البضائع',
      descriptionEn: 'Multi-purpose truck for cargo transport'
    },
    {
      id: 8,
      name: 'شاحنة نقل ضخمة',
      nameEn: 'Large Cargo Truck',
      image: '/Screenshot 2026-04-18 224902.png',
      description: 'شاحنة ضخمة لنقل البضائع الكبيرة',
      descriptionEn: 'Large truck for big cargo transport'
    },
    {
      id: 9,
      name: 'مركبة نقل متعددة',
      nameEn: 'Multi-purpose Vehicle',
      image: '/Screenshot 2026-04-18 224914.png',
      description: 'مركبة متعددة الاستخدامات',
      descriptionEn: 'Multi-purpose vehicle'
    }
  ];

  return (
    <div className="min-h-screen bg-edham-black">
      {/* Header */}
      <div className="bg-edham-dark border-b border-edham-gray">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <button onClick={() => navigate('/')} className="text-edham-white hover:text-edham-gold transition-colors">
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div className="flex items-center gap-2">
              <div className="bg-edham-white p-1.5 rounded-full">
                <Logo size="sm" />
              </div>
              <h1 className="text-xl font-bold text-edham-white">معرض الأسطول</h1>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {/* Page Title */}
        <div className="text-center mb-12">
          <h2 className="text-4xl md:text-5xl font-bold text-edham-white mb-4">
            أسطولنا
          </h2>
          <p className="text-xl text-edham-white/70 mb-2">
            أسطول حديث ومتطور لنقل البضائع المبردة
          </p>
          <p className="text-edham-white/50">Modern and advanced fleet for refrigerated goods transport</p>
        </div>

        {/* Gallery Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {vehicles.map((vehicle) => (
            <div key={vehicle.id} className="bg-edham-dark rounded-2xl overflow-hidden group cursor-pointer" onClick={() => setSelectedImage(vehicle)}>
              <div className="relative h-64 overflow-hidden">
                <img 
                  src={vehicle.image} 
                  alt={vehicle.name} 
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" 
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <div className="bg-edham-primary/80 p-4 rounded-full">
                    <ZoomIn className="w-8 h-8 text-white" />
                  </div>
                </div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">{vehicle.name}</h3>
                <p className="text-edham-gold text-sm mb-3 font-semibold">{vehicle.nameEn}</p>
                <p className="text-edham-white/70 text-sm mb-2">{vehicle.description}</p>
                <p className="text-edham-white/50 text-xs">{vehicle.descriptionEn}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Image Modal */}
      {selectedImage && (
        <div className="fixed inset-0 bg-black/90 backdrop-blur-sm flex items-center justify-center z-50 p-4" onClick={() => setSelectedImage(null)}>
          <div className="max-w-4xl w-full">
            <div className="relative">
              <img src={selectedImage.image} alt={selectedImage.name} className="w-full h-auto rounded-2xl" />
              <button 
                onClick={() => setSelectedImage(null)}
                className="absolute -top-12 right-0 text-edham-white hover:text-edham-gold transition-colors"
              >
                <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <div className="mt-4 text-center">
              <h3 className="text-2xl font-bold text-edham-white mb-2">{selectedImage.name}</h3>
              <p className="text-edham-gold text-sm mb-3 font-semibold">{selectedImage.nameEn}</p>
              <p className="text-edham-white/70 mb-2">{selectedImage.description}</p>
              <p className="text-edham-white/50 text-sm">{selectedImage.descriptionEn}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FleetGallery;
