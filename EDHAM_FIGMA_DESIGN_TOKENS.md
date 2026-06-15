# 🎨 Edham Logistics Design System - Figma Design Tokens

## 📋 Overview

This document contains the complete Figma Design Tokens for Edham Logistics Design System. These tokens are structured to be directly importable into Figma for maintaining consistency between design and development.

## 🎯 Design Token Structure

### 1. Color Tokens

#### Primary Brand Colors
```json
{
  "color": {
    "brand": {
      "primary": {
        "50": "#FFF8F5",
        "100": "#FFEBE5",
        "200": "#FFD4C7",
        "300": "#FFB5A0",
        "400": "#FF906D",
        "500": "#FF6B35",
        "600": "#E55A2B",
        "700": "#C74A20",
        "800": "#A83D1A",
        "900": "#8B3116",
        "950": "#5A1F0E"
      },
      "secondary": {
        "50": "#FAF5FF",
        "100": "#F3EDFF",
        "200": "#E5D6FF",
        "300": "#D4BBFF",
        "400": "#C29EFF",
        "500": "#A78BFA",
        "600": "#8B5CF6",
        "700": "#7C3AED",
        "800": "#6D28D9",
        "900": "#5B21B6",
        "950": "#4C1D95"
      },
      "tertiary": {
        "50": "#F0FDF4",
        "100": "#DCFCE7",
        "200": "#BBF7D0",
        "300": "#86EFAC",
        "400": "#4ADE80",
        "500": "#22C55E",
        "600": "#16A34A",
        "700": "#15803D",
        "800": "#166534",
        "900": "#14532D",
        "950": "#052E16"
      }
    }
  }
}
```

#### Neutral Colors
```json
{
  "color": {
    "neutral": {
      "0": "#FFFFFF",
      "50": "#FAFAFA",
      "100": "#F5F5F5",
      "200": "#E5E5E5",
      "300": "#D4D4D4",
      "400": "#A3A3A3",
      "500": "#737373",
      "600": "#525252",
      "700": "#404040",
      "800": "#262626",
      "900": "#171717",
      "950": "#0A0A0A"
    }
  }
}
```

#### Semantic Colors
```json
{
  "color": {
    "semantic": {
      "success": {
        "50": "#F0FDF4",
        "100": "#DCFCE7",
        "200": "#BBF7D0",
        "300": "#86EFAC",
        "400": "#4ADE80",
        "500": "#22C55E",
        "600": "#16A34A",
        "700": "#15803D",
        "800": "#166534",
        "900": "#14532D",
        "950": "#052E16"
      },
      "warning": {
        "50": "#FFFBEB",
        "100": "#FEF3C7",
        "200": "#FDE68A",
        "300": "#FCD34D",
        "400": "#FBBF24",
        "500": "#F59E0B",
        "600": "#D97706",
        "700": "#B45309",
        "800": "#92400E",
        "900": "#78350F",
        "950": "#451A03"
      },
      "error": {
        "50": "#FEF2F2",
        "100": "#FEE2E2",
        "200": "#FECACA",
        "300": "#FCA5A5",
        "400": "#F87171",
        "500": "#EF4444",
        "600": "#DC2626",
        "700": "#B91C1C",
        "800": "#991B1B",
        "900": "#7F1D1D",
        "950": "#450A0A"
      },
      "info": {
        "50": "#EFF6FF",
        "100": "#DBEAFE",
        "200": "#BFDBFE",
        "300": "#93C5FD",
        "400": "#60A5FA",
        "500": "#3B82F6",
        "600": "#2563EB",
        "700": "#1D4ED8",
        "800": "#1E40AF",
        "900": "#1E3A8A",
        "950": "#172554"
      }
    }
  }
}
```

#### Logistics Specific Colors
```json
{
  "color": {
    "logistics": {
      "shipment": {
        "created": "#737373",
        "accepted": "#3B82F6",
        "preparing": "#8B5CF6",
        "loaded": "#F59E0B",
        "in_transit": "#22C55E",
        "arrived": "#06B6D4",
        "delivered": "#059669",
        "cancelled": "#EF4444",
        "delayed": "#F97316",
        "returned": "#8B5CF6",
        "on_hold": "#F59E0B"
      },
      "driver": {
        "online": "#22C55E",
        "offline": "#737373",
        "busy": "#F59E0B",
        "on_duty": "#3B82F6",
        "break": "#F97316",
        "unavailable": "#EF4444"
      },
      "vehicle": {
        "available": "#22C55E",
        "in_use": "#F59E0B",
        "maintenance": "#EF4444",
        "out_of_service": "#737373",
        "charging": "#3B82F6",
        "parking": "#8B5CF6"
      },
      "route": {
        "primary": "#FF6B35",
        "alternative": "#8B5CF6",
        "optimal": "#22C55E",
        "traffic": "#F59E0B",
        "blocked": "#EF4444"
      },
      "marker": {
        "pickup": "#3B82F6",
        "delivery": "#22C55E",
        "current": "#FF6B35",
        "driver": "#8B5CF6",
        "waypoint": "#F59E0B",
        "destination": "#EF4444"
      }
    }
  }
}
```

### 2. Typography Tokens

#### Font Families
```json
{
  "typography": {
    "fontFamily": {
      "primary": "Inter, sans-serif",
      "secondary": "Inter Medium, sans-serif",
      "tertiary": "Inter Condensed, sans-serif",
      "mono": "JetBrains Mono, monospace",
      "arabic": "Cairo, sans-serif",
      "brand": "Inter Light, sans-serif"
    }
  }
}
```

#### Typography Scale
```json
{
  "typography": {
    "textStyle": {
      "displayLarge": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 57,
        "fontWeight": 400,
        "lineHeight": 68.4,
        "letterSpacing": -0.025,
        "paragraphSpacing": 4
      },
      "displayMedium": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 45,
        "fontWeight": 400,
        "lineHeight": 54,
        "letterSpacing": -0.025,
        "paragraphSpacing": 4
      },
      "displaySmall": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 36,
        "fontWeight": 400,
        "lineHeight": 43.2,
        "letterSpacing": 0,
        "paragraphSpacing": 2
      },
      "headlineLarge": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 32,
        "fontWeight": 700,
        "lineHeight": 40,
        "letterSpacing": 0,
        "paragraphSpacing": 2
      },
      "headlineMedium": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 28,
        "fontWeight": 700,
        "lineHeight": 35,
        "letterSpacing": 0,
        "paragraphSpacing": 2
      },
      "headlineSmall": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 24,
        "fontWeight": 700,
        "lineHeight": 30,
        "letterSpacing": 0,
        "paragraphSpacing": 2
      },
      "titleLarge": {
        "fontFamily": "{typography.fontFamily.secondary}",
        "fontSize": 22,
        "fontWeight": 700,
        "lineHeight": 28.6,
        "letterSpacing": 0,
        "paragraphSpacing": 2
      },
      "titleMedium": {
        "fontFamily": "{typography.fontFamily.secondary}",
        "fontSize": 16,
        "fontWeight": 700,
        "lineHeight": 20.8,
        "letterSpacing": 0.1,
        "paragraphSpacing": 2
      },
      "titleSmall": {
        "fontFamily": "{typography.fontFamily.secondary}",
        "fontSize": 14,
        "fontWeight": 700,
        "lineHeight": 18.2,
        "letterSpacing": 0.1,
        "paragraphSpacing": 1
      },
      "bodyLarge": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 16,
        "fontWeight": 400,
        "lineHeight": 24,
        "letterSpacing": 0.5,
        "paragraphSpacing": 2
      },
      "bodyMedium": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 14,
        "fontWeight": 400,
        "lineHeight": 21,
        "letterSpacing": 0.25,
        "paragraphSpacing": 1
      },
      "bodySmall": {
        "fontFamily": "{typography.fontFamily.primary}",
        "fontSize": 12,
        "fontWeight": 400,
        "lineHeight": 18,
        "letterSpacing": 0.4,
        "paragraphSpacing": 1
      },
      "labelLarge": {
        "fontFamily": "{typography.fontFamily.secondary}",
        "fontSize": 14,
        "fontWeight": 700,
        "lineHeight": 18.2,
        "letterSpacing": 0.1,
        "paragraphSpacing": 1
      },
      "labelMedium": {
        "fontFamily": "{typography.fontFamily.secondary}",
        "fontSize": 12,
        "fontWeight": 700,
        "lineHeight": 15.6,
        "letterSpacing": 0.5,
        "paragraphSpacing": 1
      },
      "labelSmall": {
        "fontFamily": "{typography.fontFamily.secondary}",
        "fontSize": 11,
        "fontWeight": 700,
        "lineHeight": 14.3,
        "letterSpacing": 0.5,
        "paragraphSpacing": 1
      }
    }
  }
}
```

### 3. Spacing Tokens

#### Base Spacing System
```json
{
  "spacing": {
    "unit": 4,
    "xs": 4,
    "sm": 8,
    "md": 12,
    "lg": 16,
    "xl": 20,
    "xxl": 24,
    "xxxl": 32,
    "xxxxl": 40,
    "huge": 48,
    "massive": 64,
    "giant": 80,
    "enormous": 96
  }
}
```

#### Component Spacing
```json
{
  "spacing": {
    "component": {
      "padding": 16,
      "margin": 8,
      "spacing": 12,
      "gap": 4
    },
    "card": {
      "padding": 16,
      "margin": 8,
      "spacing": 12,
      "gap": 4,
      "inner": 8
    },
    "button": {
      "paddingHorizontal": 24,
      "paddingVertical": 12,
      "margin": 8,
      "gap": 8,
      "iconPadding": 8
    },
    "input": {
      "paddingHorizontal": 16,
      "paddingVertical": 12,
      "margin": 8,
      "labelSpacing": 4,
      "errorSpacing": 4,
      "helperSpacing": 4
    }
  }
}
```

### 4. Shadow Tokens

#### Elevation Levels
```json
{
  "shadow": {
    "level0": {
      "x": 0,
      "y": 0,
      "blur": 0,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0)"
    },
    "level1": {
      "x": 0,
      "y": 1,
      "blur": 2,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.05)"
    },
    "level2": {
      "x": 0,
      "y": 2,
      "blur": 4,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.08)"
    },
    "level3": {
      "x": 0,
      "y": 4,
      "blur": 8,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.10)"
    },
    "level4": {
      "x": 0,
      "y": 4,
      "blur": 12,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.15)"
    },
    "level5": {
      "x": 0,
      "y": 6,
      "blur": 16,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.20)"
    },
    "level6": {
      "x": 0,
      "y": 8,
      "blur": 20,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.25)"
    },
    "level7": {
      "x": 0,
      "y": 10,
      "blur": 24,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.30)"
    },
    "level8": {
      "x": 0,
      "y": 12,
      "blur": 28,
      "spread": 0,
      "color": "rgba(0, 0, 0, 0.35)"
    }
  }
}
```

### 5. Border Radius Tokens

```json
{
  "borderRadius": {
    "none": 0,
    "xs": 4,
    "sm": 8,
    "md": 12,
    "lg": 16,
    "xl": 20,
    "xxl": 24,
    "xxxl": 32,
    "full": 9999
  }
}
```

### 6. Icon Tokens

#### Icon Sizes
```json
{
  "icon": {
    "size": {
      "xs": 12,
      "sm": 16,
      "md": 20,
      "lg": 24,
      "xl": 32,
      "xxl": 40,
      "xxxl": 48,
      "huge": 64
    }
  }
}
```

### 7. Breakpoint Tokens

```json
{
  "breakpoint": {
    "xs": 0,
    "sm": 360,
    "md": 600,
    "lg": 840,
    "xl": 1200,
    "xxl": 1600
  }
}
```

### 8. Grid Tokens

```json
{
  "grid": {
    "columns": {
      "xs": 4,
      "sm": 4,
      "md": 8,
      "lg": 12,
      "xl": 12,
      "xxl": 12
    },
    "gutter": {
      "xs": 4,
      "sm": 8,
      "md": 16,
      "lg": 16,
      "xl": 24,
      "xxl": 24
    },
    "margin": {
      "xs": 8,
      "sm": 12,
      "md": 16,
      "lg": 20,
      "xl": 24,
      "xxl": 32
    }
  }
}
```

## 🎨 Figma Token Structure

### Token Naming Convention
```
{category}/{subcategory}/{property}-{variant}
```

Examples:
- `color/brand/primary-500`
- `typography/textStyle/headlineLarge`
- `spacing/component/padding`
- `shadow/level2`
- `borderRadius/md`

### Token Groups

#### 1. Colors
- `color/brand/*` - Brand colors
- `color/neutral/*` - Neutral colors
- `color/semantic/*` - Semantic colors
- `color/logistics/*` - Logistics-specific colors

#### 2. Typography
- `typography/fontFamily/*` - Font families
- `typography/textStyle/*` - Text styles
- `typography/lineHeight/*` - Line heights
- `typography/letterSpacing/*` - Letter spacing

#### 3. Spacing
- `spacing/unit/*` - Base spacing units
- `spacing/component/*` - Component spacing
- `spacing/layout/*` - Layout spacing

#### 4. Effects
- `shadow/level*` - Shadow levels
- `borderRadius/*` - Border radius values
- `opacity/*` - Opacity values

#### 5. Layout
- `grid/*` - Grid system
- `breakpoint/*` - Breakpoints
- `icon/size/*` - Icon sizes

## 🚀 Implementation Guide

### 1. Importing to Figma

1. **Create Token File**: Create a new JSON file with the tokens above
2. **Import to Figma**: Use Figma's "Import design tokens" feature
3. **Apply Styles**: Apply tokens to components and styles
4. **Create Components**: Build reusable components using tokens

### 2. Token Usage

#### Colors
```css
/* Using color tokens */
background: {color/brand/primary-500};
border-color: {color/semantic/success-500};
text-color: {color/neutral/900};
```

#### Typography
```css
/* Using typography tokens */
font-family: {typography/fontFamily/primary};
font-size: {typography/textStyle/headlineLarge.fontSize};
font-weight: {typography/textStyle/headlineLarge.fontWeight};
line-height: {typography/textStyle/headlineLarge.lineHeight};
```

#### Spacing
```css
/* Using spacing tokens */
padding: {spacing/component/padding};
margin: {spacing/component/margin};
gap: {spacing/component/gap};
```

#### Shadows
```css
/* Using shadow tokens */
box-shadow: {shadow/level3.x} {shadow/level3.y} {shadow/level3.blur} {shadow/level3.color};
```

### 3. Component Examples

#### Button Component
```json
{
  "button": {
    "primary": {
      "backgroundColor": "{color/brand/primary-500}",
      "textColor": "{color/neutral/0}",
      "borderRadius": "{borderRadius/md}",
      "paddingHorizontal": "{spacing/component/button/paddingHorizontal}",
      "paddingVertical": "{spacing/component/button/paddingVertical}",
      "typography": "{typography/textStyle/labelLarge}",
      "shadow": "{shadow/level2}"
    },
    "secondary": {
      "backgroundColor": "{color/neutral/0}",
      "textColor": "{color/brand/primary-500}",
      "borderColor": "{color/brand/primary-500}",
      "borderRadius": "{borderRadius/md}",
      "paddingHorizontal": "{spacing/component/button/paddingHorizontal}",
      "paddingVertical": "{spacing/component/button/paddingVertical}",
      "typography": "{typography/textStyle/labelLarge}",
      "shadow": "{shadow/level1}"
    }
  }
}
```

#### Card Component
```json
{
  "card": {
    "default": {
      "backgroundColor": "{color/neutral/0}",
      "borderRadius": "{borderRadius/md}",
      "padding": "{spacing/component/card/padding}",
      "margin": "{spacing/component/card/margin}",
      "shadow": "{shadow/level2}"
    },
    "elevated": {
      "backgroundColor": "{color/neutral/0}",
      "borderRadius": "{borderRadius/md}",
      "padding": "{spacing/component/card/padding}",
      "margin": "{spacing/component/card/margin}",
      "shadow": "{shadow/level4}"
    }
  }
}
```

## 🔄 Sync Process

### 1. Design to Development
1. **Update Tokens**: Make changes in Figma
2. **Export Tokens**: Export updated tokens as JSON
3. **Sync to Code**: Update Android XML files
4. **Test Components**: Verify component updates

### 2. Development to Design
1. **Update Code**: Make changes in Android XML
2. **Extract Tokens**: Extract token values
3. **Import to Figma**: Update Figma tokens
4. **Sync Components**: Update Figma components

## 📱 Platform-Specific Considerations

### Android
- Use XML resource files
- Support dark theme with `values-night/`
- Support RTL with `values-ar/`
- Use Material Design 3 components

### Figma
- Use JSON token format
- Create component variants
- Support dark/light modes
- Support RTL layouts

## 🎯 Best Practices

### 1. Token Organization
- Group related tokens
- Use consistent naming
- Document token usage
- Version control tokens

### 2. Token Usage
- Use tokens for all values
- Avoid hard-coded values
- Create component styles
- Maintain consistency

### 3. Maintenance
- Regular token audits
- Update documentation
- Sync changes regularly
- Test across platforms

## 🔧 Advanced Features

### 1. Token Aliases
```json
{
  "color": {
    "primary": "{color/brand/primary-500}",
    "textPrimary": "{color/neutral/900}",
    "background": "{color/neutral/0}"
  }
}
```

### 2. Composite Tokens
```json
{
  "button": {
    "primary": {
      "backgroundColor": "{color/brand/primary-500}",
      "textColor": "{color/neutral/0}",
      "borderRadius": "{borderRadius/md}",
      "padding": "{spacing/component/button/padding}",
      "typography": "{typography/textStyle/labelLarge}"
    }
  }
}
```

### 3. Responsive Tokens
```json
{
  "spacing": {
    "component": {
      "padding": {
        "xs": "{spacing/sm}",
        "sm": "{spacing/md}",
        "md": "{spacing/lg}",
        "lg": "{spacing/xl}",
        "xl": "{spacing/xxl}"
      }
    }
  }
}
```

## 📚 Resources

### Documentation
- [Figma Design Tokens Guide](https://help.figma.com/hc/en-us/articles/360056449594)
- [Design Tokens W3C Community Group](https://www.w3.org/community/design-tokens/)
- [Material Design 3 Tokens](https://m3.material.io/styles/design-tokens)

### Tools
- [Figma Tokens Plugin](https://www.figma.com/community/plugin/734453161653394619)
- [Style Dictionary](https://amzn.github.io/style-dictionary/)
- [Tokens Studio](https://tokens.studio/)

---

## 🎉 Summary

This comprehensive Figma Design Token system provides:

✅ **Complete Color System** - Brand, semantic, and logistics-specific colors
✅ **Typography Hierarchy** - Full text style system
✅ **Spacing System** - Consistent spacing and layout
✅ **Shadow System** - Professional elevation levels
✅ **Component Tokens** - Reusable component styles
✅ **Responsive Design** - Breakpoint-based tokens
✅ **Platform Support** - Android and Figma compatibility
✅ **RTL Support** - Arabic typography and layout
✅ **Dark Mode** - Complete dark theme support

Use these tokens to maintain consistency between design and development, ensuring a cohesive and professional Edham Logistics experience across all platforms.
