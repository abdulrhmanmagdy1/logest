# 🎨 Edham Logistics UI Guidelines

## 📋 Table of Contents

1. [Design Principles](#design-principles)
2. [Color System](#color-system)
3. [Typography](#typography)
4. [Spacing & Layout](#spacing--layout)
5. [Components](#components)
6. [Icons & Imagery](#icons--imagery)
7. [Accessibility](#accessibility)
8. [Responsive Design](#responsive-design)
9. [Dark Mode](#dark-mode)
10. [RTL Support](#rtl-support)
11. [Animation & Motion](#animation--motion)
12. [Brand Guidelines](#brand-guidelines)

---

## 🎯 Design Principles

### 1. Clarity First
- **Purpose**: Every element should have a clear purpose
- **Hierarchy**: Establish clear visual hierarchy
- **Simplicity**: Keep interfaces clean and uncluttered
- **Consistency**: Maintain consistent patterns across the app

### 2. User-Centered
- **Efficiency**: Minimize steps to complete tasks
- **Intuitive**: Follow established patterns users expect
- **Feedback**: Provide clear feedback for all interactions
- **Error Prevention**: Design to prevent errors before they happen

### 3. Professional & Modern
- **Quality**: High-quality visual design
- **Performance**: Fast and responsive interactions
- **Trust**: Build trust through consistent, reliable design
- **Innovation**: Modern design patterns and interactions

### 4. Logistics-Focused
- **Relevance**: Design specifically for logistics workflows
- **Efficiency**: Optimize for fleet management tasks
- **Data-Driven**: Present information clearly and effectively
- **Mobile-First**: Designed for drivers and managers on the go

---

## 🎨 Color System

### Primary Brand Colors
- **Primary**: `#FF6B35` - Main brand color for primary actions
- **Secondary**: `#8B5CF6` - Secondary brand color for accents
- **Tertiary**: `#22C55E` - Tertiary color for success states

### Semantic Colors
- **Success**: `#22C55E` - Completed actions, positive feedback
- **Warning**: `#F59E0B` - Caution, pending states
- **Error**: `#EF4444` - Errors, critical issues
- **Info**: `#3B82F6` - Information, neutral states

### Logistics-Specific Colors
- **Shipment Status**: 11 distinct colors for different states
- **Driver Status**: 6 colors for driver availability
- **Vehicle Status**: 6 colors for vehicle conditions
- **Route Types**: 5 colors for route classification

### Color Usage Guidelines

#### Do's
- ✅ Use primary color for main CTAs
- ✅ Use semantic colors appropriately for status
- ✅ Maintain 4.5:1 contrast ratio for accessibility
- ✅ Use dark mode variants for night usage

#### Don'ts
- ❌ Don't use semantic colors for decorative purposes
- ❌ Don't mix too many colors in one screen
- ❌ Don't rely solely on color for information
- ❌ Don't use low-contrast combinations

---

## 📝 Typography

### Font Hierarchy
- **Display**: 57sp, 45sp, 36sp - Hero sections, splash screens
- **Headline**: 32sp, 28sp, 24sp - Page headers, section titles
- **Title**: 22sp, 16sp, 14sp - Card titles, important labels
- **Body**: 16sp, 14sp, 12sp - Main content, descriptions
- **Label**: 14sp, 12sp, 11sp - Buttons, tags, form labels

### Typography Guidelines

#### Font Families
- **Primary**: Inter - Clean, modern sans-serif
- **Secondary**: Inter Medium - Emphasis and headings
- **Arabic**: Cairo - Optimized for Arabic text
- **Monospace**: JetBrains Mono - Tracking numbers, codes

#### Best Practices
- ✅ Use appropriate text sizes for content hierarchy
- ✅ Maintain consistent line spacing (1.3-1.6)
- ✅ Use proper letter spacing for readability
- ✅ Consider Arabic RTL text direction
- ✅ Use monospace for tracking numbers

#### Typography in Arabic
- ✅ Zero letter spacing for Arabic text
- ✅ Increased line height for readability
- ✅ RTL text direction for Arabic content
- ✅ LTR for numbers and tracking codes
- ✅ Proper font feature settings

---

## 📏 Spacing & Layout

### Spacing System
- **Base Unit**: 4dp (1/3 of Material Design's 12dp)
- **Scale**: 4, 8, 12, 16, 20, 24, 32, 40, 48, 64, 80, 96dp

### Component Spacing
- **Cards**: 16dp padding, 8dp margin
- **Buttons**: 24dp horizontal, 12dp vertical padding
- **Inputs**: 16dp horizontal, 12dp vertical padding
- **Badges**: 12dp horizontal, 6dp vertical padding

### Grid System
- **Columns**: 12-column responsive grid
- **Breakpoints**: 
  - Mobile: 0-359px (4 columns)
  - Tablet: 360-839px (8 columns)
  - Desktop: 840px+ (12 columns)

### Layout Guidelines
- ✅ Use consistent spacing throughout
- ✅ Follow 8dp grid where possible
- ✅ Maintain proper touch targets (48dp minimum)
- ✅ Use responsive layouts for different screen sizes
- ✅ Consider RTL layout for Arabic

---

## 🧩 Components

### Buttons
- **Primary**: Solid background, main actions
- **Secondary**: Outlined, secondary actions
- **Tertiary**: Text only, tertiary actions
- **Icon**: Icon only, compact actions
- **FAB**: Floating action, primary screen action

### Cards
- **Default**: Standard content container
- **Elevated**: With shadow for hierarchy
- **Outlined**: Border only, subtle
- **Interactive**: Clickable with feedback
- **Status**: Color-coded for logistics states

### Inputs
- **Text**: Standard text input
- **Search**: With search icon and clear button
- **Email**: With email icon
- **Password**: With toggle visibility
- **Phone**: With phone icon and country code

### Status Indicators
- **Badges**: Small status labels
- **Chips**: Filter tags
- **Progress**: Loading states
- **Alerts**: Error/success messages

### Component Guidelines
- ✅ Use components consistently
- ✅ Follow Material Design 3 patterns
- ✅ Provide proper feedback on interaction
- ✅ Support dark mode variants
- ✅ Include accessibility labels

---

## 🎯 Icons & Imagery

### Icon System
- **Sizes**: 12, 16, 20, 24, 32, 40, 48, 64dp
- **Styles**: Filled, outlined, rounded, two-tone
- **Colors**: Semantic colors for status, neutral for actions
- **Usage**: Consistent style throughout app

### Logistics Icons
- **Shipment**: Package, truck, delivery icons
- **Driver**: Person, phone, message icons
- **Vehicle**: Car, truck, maintenance icons
- **Route**: Map, navigation, location icons

### Icon Guidelines
- ✅ Use consistent icon style
- ✅ Provide accessibility labels
- ✅ Use semantic colors for status
- ✅ Maintain 24dp default size
- ✅ Consider RTL icon direction

---

## ♿ Accessibility

### Visual Accessibility
- **Contrast**: Minimum 4.5:1 for normal text
- **Size**: Minimum 16px for body text
- **Color**: Don't rely solely on color
- **Spacing**: Adequate touch targets (48dp minimum)

### Content Accessibility
- **Labels**: Descriptive labels for all controls
- **Alternatives**: Text alternatives for images
- **Structure**: Proper heading hierarchy
- **Navigation**: Logical tab order

### Accessibility Guidelines
- ✅ Test with screen readers
- ✅ Provide high contrast options
- ✅ Support larger text sizes
- ✅ Include accessibility labels
- ✅ Test with various disabilities

---

## 📱 Responsive Design

### Breakpoints
- **Mobile**: 0-359px
- **Tablet**: 360-839px
- **Desktop**: 840px+

### Responsive Strategies
- **Layout**: Adaptive layouts for different screens
- **Typography**: Scalable text sizes
- **Components**: Responsive component variants
- **Navigation**: Context-appropriate navigation

### Responsive Guidelines
- ✅ Design mobile-first
- ✅ Test on various screen sizes
- ✅ Use flexible layouts
- ✅ Consider device constraints
- ✅ Optimize for touch interactions

---

## 🌙 Dark Mode

### Dark Theme Colors
- **Background**: Deep blacks and dark grays
- **Text**: Light grays and whites
- **Accent**: Slightly muted brand colors
- **Shadows**: White/light shadows

### Dark Mode Guidelines
- ✅ Provide automatic theme switching
- ✅ Maintain brand identity in dark
- ✅ Test in various lighting conditions
- ✅ Consider battery optimization
- ✅ Support user preference

---

## 🔄 RTL Support

### Arabic Localization
- **Text Direction**: RTL for Arabic content
- **Layout**: Mirrored layouts for RTL
- **Icons**: Directional icons adapt to RTL
- **Navigation**: RTL navigation patterns

### RTL Guidelines
- ✅ Test with Arabic text
- ✅ Mirror layouts appropriately
- ✅ Consider mixed content (LTR/RTL)
- ✅ Maintain visual balance
- ✅ Test on RTL devices

---

## 🎬 Animation & Motion

### Animation Principles
- **Purposeful**: Animations should have purpose
- **Natural**: Follow natural motion patterns
- **Responsive**: Provide immediate feedback
- **Consistent**: Consistent timing and easing

### Animation Types
- **Micro**: Button presses, state changes
- **Transitions**: Screen transitions, layout changes
- **Loading**: Progress indicators, skeleton screens
- **Feedback**: Success/error animations

### Animation Guidelines
- ✅ Keep animations under 300ms
- ✅ Use consistent easing functions
- ✅ Respect user motion preferences
- ✅ Provide feedback for all interactions
- ✅ Test performance impact

---

## 🏢 Brand Guidelines

### Brand Identity
- **Logo**: Consistent logo usage
- **Colors**: Primary brand colors
- **Typography**: Brand fonts and hierarchy
- **Voice**: Professional, helpful tone

### Brand Application
- **Consistency**: Maintain brand consistency
- **Quality**: High-quality visual design
- **Trust**: Build trust through reliability
- **Innovation**: Modern, forward-thinking

### Brand Guidelines
- ✅ Use brand colors appropriately
- ✅ Maintain logo integrity
- ✅ Follow brand voice guidelines
- ✅ Keep design professional
- ✅ Reflect logistics expertise

---

## 📚 Implementation Checklist

### Design Phase
- [ ] Follow color system guidelines
- [ ] Use proper typography hierarchy
- [ ] Apply consistent spacing
- [ ] Design responsive layouts
- [ ] Include accessibility considerations
- [ ] Plan for dark mode
- [ ] Consider RTL requirements

### Development Phase
- [ ] Implement design tokens
- [ ] Use component library
- [ ] Test on various devices
- [ ] Validate accessibility
- [ ] Performance testing
- [ ] User testing
- [ ] Documentation updates

### Quality Assurance
- [ ] Visual consistency check
- [ ] Accessibility testing
- [ ] Performance testing
- [ ] User experience testing
- [ ] Cross-platform testing
- [ ] Documentation review
- [ ] Final approval

---

## 🔧 Tools & Resources

### Design Tools
- **Figma**: Primary design tool
- **Design Tokens**: Consistent token system
- **Component Library**: Reusable components
- **Style Guide**: Comprehensive guidelines

### Development Tools
- **Material Design 3**: Component library
- **Android Studio**: Development environment
- **Testing**: Accessibility and performance testing
- **Documentation**: API and component docs

### Resources
- **Material Design Guidelines**: Google's design system
- **Accessibility Guidelines**: WCAG 2.1 standards
- **Arabic Typography**: RTL design resources
- **Performance**: Android optimization guides

---

## 🎉 Conclusion

These UI guidelines provide a comprehensive foundation for creating consistent, accessible, and professional user interfaces for Edham Logistics. By following these guidelines, we ensure:

✅ **Consistency** across all platforms and screens
✅ **Accessibility** for all users
✅ **Professional** appearance and interactions
✅ **Efficiency** in development and maintenance
✅ **Scalability** for future growth
✅ **User Satisfaction** through excellent UX

Remember: These guidelines are living documents. They should be updated regularly based on user feedback, new requirements, and industry best practices.

---

*Last updated: May 2026*
*Version: 1.0*
*Maintained by: Edham Logistics Design Team*
