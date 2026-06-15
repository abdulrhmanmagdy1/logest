import React from "react";

const Logo = ({ size = "md", className = "" }) => {
  const sizes = {
    sm: "w-6 h-6 text-lg",
    md: "w-8 h-8 text-xl",
    lg: "w-12 h-12 text-2xl",
    xl: "w-16 h-16 text-3xl",
  };

  return (
    <div className={`bg-edham-primary rounded-full flex items-center justify-center ${sizes[size]} ${className}`}>
      <span className="text-white font-bold">إ</span>
    </div>
  );
};

export default Logo;
