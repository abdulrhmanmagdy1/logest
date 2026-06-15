# EDHAM Splash Screen & Onboarding Design System

## Table of Contents
1. [Overview](#overview)
2. [Design Philosophy](#design-philosophy)
3. [Color System](#color-system)
4. [Typography](#typography)
5. [Splash Screen Design](#splash-screen-design)
6. [Onboarding Screens](#onboarding-screens)
7. [Animations & Transitions](#animations--transitions)
8. [Technical Implementation](#technical-implementation)
9. [Responsive Design](#responsive-design)
10. [Accessibility Features](#accessibility-features)

---

## Overview

This document defines the complete design system for EDHAM's mobile application splash screen and onboarding flow. The design emphasizes premium visual aesthetics, smooth animations, and an engaging user experience that reflects the brand's commitment to excellence in logistics services.

### Key Objectives
- Create a memorable first impression
- Communicate brand values effectively
- Guide users through app features intuitively
- Maintain consistency with EDHAM's visual identity
- Ensure smooth performance across all devices

---

## Design Philosophy

### Core Principles
1. **Premium Experience**: High-quality visuals and animations
2. **Brand Consistency**: Maintain EDHAM's orange and dark theme
3. **User-Centric**: Focus on clarity and ease of understanding
4. **Performance**: Smooth animations without compromising speed
5. **Accessibility**: Inclusive design for all users

### Visual Style
- **Modern & Professional**: Clean, contemporary design
- **Trustworthy**: Convey reliability and expertise
- **Dynamic**: Subtle animations that enhance engagement
- **Authentic**: Real logistics imagery and scenarios

---

## Color System

### Primary Colors
```css
/* Brand Colors */
--edham-orange: #F97316;
--edham-orange-dark: #EA580C;
--edham-orange-light: #FB923C;

/* Dark Theme Colors */
--bg-primary: #0A0E1A;
--bg-secondary: #1F2937;
--bg-tertiary: #111827;
--surface-dark: #374151;

/* Text Colors */
--text-primary: #FFFFFF;
--text-secondary: #9CA3AF;
--text-muted: #6B7280;

/* Accent Colors */
--accent-blue: #60A5FA;
--accent-green: #10B981;
--accent-yellow: #F59E0B;
--accent-red: #EF4444;
```

### Gradient Overlays
```css
/* Sunset Gradient for Splash */
--gradient-sunset: linear-gradient(180deg, 
    rgba(249, 115, 22, 0.1) 0%, 
    rgba(10, 14, 26, 0.8) 50%, 
    rgba(10, 14, 26, 0.95) 100%);

/* Card Gradient */
--gradient-card: linear-gradient(135deg, 
    rgba(31, 41, 55, 0.9) 0%, 
    rgba(17, 24, 39, 0.9) 100%);
```

---

## Typography

### Font Hierarchy
```css
/* Headings */
.font-display {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    font-weight: 700;
    line-height: 1.2;
}

.font-heading {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    font-weight: 600;
    line-height: 1.3;
}

/* Body Text */
.font-body {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    font-weight: 400;
    line-height: 1.5;
}

.font-caption {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    font-weight: 400;
    line-height: 1.4;
}
```

### Font Sizes
```css
/* Mobile Scale */
.text-4xl { font-size: 2.25rem; } /* 36px - Main Title */
.text-2xl { font-size: 1.5rem; }  /* 24px - Section Title */
.text-xl  { font-size: 1.25rem; } /* 20px - Card Title */
.text-base { font-size: 1rem; }   /* 16px - Body */
.text-sm  { font-size: 0.875rem; } /* 14px - Caption */
```

---

## Splash Screen Design

### Layout Structure
```
┌─────────────────────────────────┐
│                                 │
│         [Status Bar]            │
│                                 │
│                                 │
│                                 │
│         [Logo Area]             │
│                                 │
│                                 │
│                                 │
│       [Main Title]              │
│                                 │
│     [Subtitle Text]             │
│                                 │
│                                 │
│                                 │
│      [Start Button]             │
│                                 │
│                                 │
│    [Navigation Dots]            │
│                                 │
└─────────────────────────────────┘
```

### Visual Elements

#### Background Image
- **Content**: Orange truck on desert highway at sunset
- **Style**: Photorealistic with warm golden hour lighting
- **Composition**: Truck positioned slightly off-center, road leading into distance
- **Color Palette**: Warm oranges, deep blues, golden yellows

#### Logo Placement
```css
.logo-container {
    position: absolute;
    top: 20%;
    left: 50%;
    transform: translateX(-50%);
    z-index: 10;
}

.logo-icon {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #F97316, #EA580C);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 20px 40px rgba(249, 115, 22, 0.3);
    animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
    0%, 100% { transform: translateY(0px); }
    50% { transform: translateY(-10px); }
}
```

#### Title Section
```css
.title-section {
    position: absolute;
    bottom: 35%;
    left: 50%;
    transform: translateX(-50%);
    text-align: center;
    z-index: 10;
}

.main-title {
    font-size: 2.25rem;
    font-weight: 700;
    color: #FFFFFF;
    margin-bottom: 0.5rem;
    text-shadow: 0 2px 20px rgba(0, 0, 0, 0.5);
    animation: titleFadeIn 1s ease-out 0.5s both;
}

.subtitle {
    font-size: 1rem;
    color: #9CA3AF;
    text-shadow: 0 1px 10px rgba(0, 0, 0, 0.5);
    animation: subtitleFadeIn 1s ease-out 0.7s both;
}

@keyframes titleFadeIn {
    from { 
        opacity: 0;
        transform: translateY(20px);
    }
    to { 
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes subtitleFadeIn {
    from { 
        opacity: 0;
        transform: translateY(15px);
    }
    to { 
        opacity: 1;
        transform: translateY(0);
    }
}
```

#### Start Button
```css
.start-button {
    position: absolute;
    bottom: 15%;
    left: 50%;
    transform: translateX(-50%);
    background: linear-gradient(135deg, #F97316, #EA580C);
    color: #FFFFFF;
    padding: 16px 48px;
    border-radius: 50px;
    font-size: 1.125rem;
    font-weight: 600;
    border: none;
    cursor: pointer;
    box-shadow: 0 10px 30px rgba(249, 115, 22, 0.4);
    animation: buttonPulse 2s ease-in-out infinite 1s both;
    transition: all 0.3s ease;
}

.start-button:hover {
    transform: translateX(-50%) translateY(-2px);
    box-shadow: 0 15px 40px rgba(249, 115, 22, 0.5);
}

.start-button:active {
    transform: translateX(-50%) translateY(0);
}

@keyframes buttonPulse {
    0%, 100% { 
        transform: translateX(-50%) scale(1);
        box-shadow: 0 10px 30px rgba(249, 115, 22, 0.4);
    }
    50% { 
        transform: translateX(-50%) scale(1.05);
        box-shadow: 0 15px 40px rgba(249, 115, 22, 0.6);
    }
}
```

#### Navigation Dots
```css
.navigation-dots {
    position: absolute;
    bottom: 8%;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 8px;
    z-index: 10;
}

.dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(156, 163, 175, 0.3);
    transition: all 0.3s ease;
}

.dot.active {
    width: 24px;
    border-radius: 4px;
    background: #F97316;
}
```

---

## Onboarding Screens

### Screen 1: Welcome to EDHAM

#### Layout
```
┌─────────────────────────────────┐
│         [Status Bar]            │
├─────────────────────────────────┤
│                                 │
│         [Skip Button]           │
│                                 │
│                                 │
│      [Truck Illustration]       │
│                                 │
│                                 │
│     "مرحباً بك في إدهام"       │
│                                 │
│ "نظام شحن متكامل لإدارة        │
│      شحناتك بكل سهولة"         │
│                                 │
│                                 │
│                                 │
│      [Navigation Dots]         │
│                                 │
│        [Next Button]            │
└─────────────────────────────────┘
```

#### Content
- **Title**: "مرحباً بك في إدهام" (Welcome to EDHAM)
- **Description**: "نظام شحن متكامل لإدارة شحناتك بكل سهولة" (Integrated shipping system to manage your shipments with ease)
- **Visual**: EDHAM truck on modern highway
- **Animation**: Truck drives in from left, stops in center

### Screen 2: Live Tracking

#### Layout
```
┌─────────────────────────────────┐
│         [Status Bar]            │
├─────────────────────────────────┤
│         [Skip Button]           │
│                                 │
│                                 │
│      [Map Illustration]         │
│    [Route Path Animation]       │
│                                 │
│   "تتبع شحناتك لحظة بلحظة"    │
│                                 │
│ "راقب شحناتك في الوقت الفعلي   │
│   واعرف موقعها بدقة"            │
│                                 │
│      [Navigation Dots]         │
│                                 │
│        [Next Button]            │
└─────────────────────────────────┘
```

#### Content
- **Title**: "تتبع شحناتك لحظة بلحظة" (Track your shipments moment by moment)
- **Description**: "راقب شحناتك في الوقت الفعلي واعرف موقعها بدقة" (Monitor your shipments in real-time and know their location precisely)
- **Visual**: Interactive map with animated route
- **Animation**: Route path draws itself, location pins pulse

### Screen 3: Fleet Management

#### Layout
```
┌─────────────────────────────────┐
│         [Status Bar]            │
├─────────────────────────────────┤
│         [Skip Button]           │
│                                 │
│                                 │
│     [Fleet Illustration]       │
│   [Multiple Trucks Animation]   │
│                                 │
│   "إدارة أسطولك بذكاء"        │
│                                 │
│ "تحكم كامل في مركباتك وسائقيك │
│        من مكان واحد"           │
│                                 │
│      [Navigation Dots]         │
│                                 │
│        [Next Button]            │
└─────────────────────────────────┘
```

#### Content
- **Title**: "إدارة أسطولك بذكاء" (Manage your fleet smartly)
- **Description**: "تحكم كامل في مركباتك وسائقيك من مكان واحد" (Full control of your vehicles and drivers from one place)
- **Visual**: Multiple trucks in fleet formation
- **Animation**: Trucks arrange themselves in formation

### Screen 4: Analytics & Reports

#### Layout
```
┌─────────────────────────────────┐
│         [Status Bar]            │
├─────────────────────────────────┤
│         [Skip Button]           │
│                                 │
│                                 │
│    [Charts Illustration]       │
│  [Animated Graphs & Charts]     │
│                                 │
│  "تقارير وتحليلات شاملة"       │
│                                 │
│ "احصل على رؤى واضحة لتطوير     │
│         عملك"                   │
│                                 │
│      [Navigation Dots]         │
│                                 │
│     [Get Started Button]        │
└─────────────────────────────────┘
```

#### Content
- **Title**: "تقارير وتحليلات شاملة" (Comprehensive reports and analytics)
- **Description**: "احصل على رؤى واضحة لتطوير عملك" (Get clear insights to develop your business)
- **Visual**: Animated charts and graphs
- **Animation**: Charts grow and animate with data

---

## Animations & Transitions

### Page Transitions
```css
/* Slide Transition */
.page-transition-enter {
    opacity: 0;
    transform: translateX(100%);
}

.page-transition-enter-active {
    opacity: 1;
    transform: translateX(0);
    transition: all 0.5s ease-out;
}

.page-transition-exit {
    opacity: 1;
    transform: translateX(0);
}

.page-transition-exit-active {
    opacity: 0;
    transform: translateX(-100%);
    transition: all 0.5s ease-out;
}
```

### Element Animations
```css
/* Fade In Up */
.fade-in-up {
    animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Scale In */
.scale-in {
    animation: scaleIn 0.6s ease-out;
}

@keyframes scaleIn {
    from {
        opacity: 0;
        transform: scale(0.8);
    }
    to {
        opacity: 1;
        transform: scale(1);
    }
}

/* Pulse Animation */
.pulse {
    animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
    0%, 100% {
        transform: scale(1);
        opacity: 1;
    }
    50% {
        transform: scale(1.05);
        opacity: 0.8;
    }
}
```

### Loading States
```css
/* Skeleton Loading */
.skeleton {
    background: linear-gradient(90deg, 
        rgba(156, 163, 175, 0.1) 25%, 
        rgba(156, 163, 175, 0.2) 50%, 
        rgba(156, 163, 175, 0.1) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
    0% { background-position: -200% 0; }
    100% { background-position: 200% 0; }
}
```

---

## Technical Implementation

### Component Structure
```javascript
// React Native Components
import React, { useState, useEffect } from 'react';
import { View, Text, Animated, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';

// Splash Screen Component
const SplashScreen = () => {
    const [fadeAnim] = useState(new Animated.Value(0));
    const [slideAnim] = useState(new Animated.Value(50));
    
    useEffect(() => {
        Animated.parallel([
            Animated.timing(fadeAnim, {
                toValue: 1,
                duration: 1000,
                delay: 500,
                useNativeDriver: true,
            }),
            Animated.timing(slideAnim, {
                toValue: 0,
                duration: 800,
                delay: 300,
                useNativeDriver: true,
            })
        ]).start();
    }, []);

    return (
        <View style={styles.container}>
            <Animated.View 
                style={[
                    styles.logoContainer,
                    { opacity: fadeAnim, transform: [{ translateY: slideAnim }] }
                ]}
            >
                {/* Logo Content */}
            </Animated.View>
        </View>
    );
};
```

### Animation Hooks
```javascript
// Custom Animation Hook
const useAnimation = (delay = 0, duration = 1000) => {
    const [animValue] = useState(new Animated.Value(0));
    
    useEffect(() => {
        const timer = setTimeout(() => {
            Animated.timing(animValue, {
                toValue: 1,
                duration,
                useNativeDriver: true,
            }).start();
        }, delay);
        
        return () => clearTimeout(timer);
    }, [delay, duration]);
    
    return animValue;
};
```

### Performance Optimization
```javascript
// Optimized Image Loading
import FastImage from 'react-native-fast-image';

const OptimizedImage = ({ source, style }) => {
    return (
        <FastImage
            style={style}
            source={source}
            resizeMode={FastImage.resizeMode.cover}
            cacheKey={source.uri}
            placeholder={false}
        />
    );
};
```

---

## Responsive Design

### Breakpoints
```css
/* Mobile Devices */
@media (max-width: 480px) {
    .main-title { font-size: 1.875rem; }
    .subtitle { font-size: 0.875rem; }
    .start-button { padding: 14px 40px; }
}

/* Large Mobile/Small Tablet */
@media (min-width: 481px) and (max-width: 768px) {
    .main-title { font-size: 2.25rem; }
    .subtitle { font-size: 1rem; }
    .start-button { padding: 16px 48px; }
}

/* Tablet */
@media (min-width: 769px) {
    .main-title { font-size: 2.5rem; }
    .subtitle { font-size: 1.125rem; }
    .start-button { padding: 18px 56px; }
}
```

### Adaptive Layout
```javascript
// Responsive Hook
import { useWindowDimensions } from 'react-native';

const useResponsiveLayout = () => {
    const { width, height } = useWindowDimensions();
    
    const isSmallMobile = width < 480;
    const isMobile = width >= 480 && width < 768;
    const isTablet = width >= 768;
    
    return {
        containerPadding: isSmallMobile ? 20 : isMobile ? 24 : 32,
        logoSize: isSmallMobile ? 60 : isMobile ? 80 : 100,
        titleSize: isSmallMobile ? 28 : isMobile ? 36 : 42,
        buttonWidth: isSmallMobile ? '80%' : isMobile ? '70%' : '60%',
    };
};
```

---

## Accessibility Features

### Screen Reader Support
```javascript
// Accessibility Props
const AccessibleButton = ({ title, onPress }) => {
    return (
        <TouchableOpacity
            onPress={onPress}
            accessible={true}
            accessibilityLabel={title}
            accessibilityRole="button"
            accessibilityHint="Double tap to proceed"
        >
            <Text>{title}</Text>
        </TouchableOpacity>
    );
};
```

### High Contrast Mode
```css
/* High Contrast Styles */
@media (prefers-contrast: high) {
    .start-button {
        background: #000000;
        border: 3px solid #FFFFFF;
        color: #FFFFFF;
    }
    
    .main-title {
        color: #FFFFFF;
        text-shadow: 2px 2px 0px #000000;
    }
}
```

### Reduced Motion
```css
/* Reduced Motion Support */
@media (prefers-reduced-motion: reduce) {
    * {
        animation-duration: 0.01ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.01ms !important;
    }
    
    .start-button {
        animation: none;
        transform: none;
    }
}
```

### Font Scaling
```javascript
// Dynamic Font Scaling
import { PixelRatio } from 'react-native';

const getFontSize = (baseSize) => {
    const scale = PixelRatio.getFontScale();
    return baseSize * Math.min(scale, 1.5);
};
```

---

## Implementation Guidelines

### Image Assets Required
1. **Splash Background**: High-quality desert highway sunset (1920x1080)
2. **Logo Icons**: Multiple sizes (60x60, 80x80, 100x100)
3. **Onboarding Illustrations**: 
   - Truck on highway (800x600)
   - Map with route (800x600)
   - Fleet formation (800x600)
   - Analytics charts (800x600)

### Animation Timing
- **Logo Appearance**: 0.5s delay, 0.8s duration
- **Title Fade In**: 0.7s delay, 1s duration
- **Button Pulse**: 1s delay, 2s infinite loop
- **Page Transitions**: 0.5s duration

### Performance Targets
- **Initial Load**: < 2 seconds
- **Animation FPS**: 60 FPS
- **Memory Usage**: < 100MB
- **Battery Impact**: Minimal

---

## Testing Checklist

### Visual Testing
- [ ] All screens display correctly on different device sizes
- [ ] Colors appear accurately across devices
- [ ] Text is readable in all lighting conditions
- [ ] Animations are smooth and performant

### Functionality Testing
- [ ] Navigation between screens works correctly
- [ ] Skip buttons function properly
- [ ] Start/Next buttons respond to touch
- [ ] Screen reader announces all elements

### Performance Testing
- [ ] Load times meet targets
- [ ] Memory usage remains within limits
- [ ] Battery drain is minimal
- [ ] No frame drops during animations

---

## Conclusion

This design system provides a comprehensive foundation for creating an exceptional splash screen and onboarding experience for the EDHAM logistics application. The combination of premium visuals, smooth animations, and thoughtful user experience design will create a lasting positive impression on users while effectively communicating the app's value proposition.

The modular component structure and performance optimizations ensure that the implementation will be maintainable, scalable, and efficient across all supported devices and platforms.

---

*Last Updated: May 2026*
*Version: 1.0*
*Design Team: EDHAM UX Team*
