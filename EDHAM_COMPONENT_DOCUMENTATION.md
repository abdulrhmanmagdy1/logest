# 🧩 Edham Logistics Component Documentation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Button Components](#button-components)
3. [Input Components](#input-components)
4. [Card Components](#card-components)
5. [Status Components](#status-components)
6. [Icon Components](#icon-components)
7. [Progress Components](#progress-components)
8. [Navigation Components](#navigation-components)
9. [Chart Components](#chart-components)
10. [Table Components](#table-components)
11. [Usage Guidelines](#usage-guidelines)
12. [Implementation Examples](#implementation-examples)

---

## 🎯 Overview

This documentation provides comprehensive details about all reusable components in the Edham Logistics Design System. Each component is designed to be:

- **Consistent**: Uniform appearance and behavior
- **Accessible**: WCAG 2.1 compliant
- **Responsive**: Works across all screen sizes
- **Themeable**: Supports light/dark modes
- **RTL Ready**: Supports Arabic text direction
- **Customizable**: Flexible styling options

---

## 🔘 Button Components

### Primary Button

**Purpose**: Main call-to-action buttons

**File**: `component_button_primary.xml`

**Usage**:
```xml
<include layout="@layout/component_button_primary"
    android:id="@+id/primary_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: `Widget.Edham.Button.Primary`
- **Background**: `color_primary_500`
- **Text Color**: `text_on_primary`
- **Padding**: 24dp horizontal, 12dp vertical
- **Border Radius**: 12dp
- **Elevation**: 2dp

**Variants**:
- Small: `Widget.Edham.Button.Primary.Small`
- Medium: `Widget.Edham.Button.Primary` (default)
- Large: `Widget.Edham.Button.Primary.Large`

**States**:
- Default: Solid background
- Pressed: Darker background with ripple
- Disabled: 50% opacity
- Loading: Show progress indicator

### Secondary Button

**Purpose**: Secondary actions, less prominent

**File**: `component_button_secondary.xml`

**Usage**:
```xml
<include layout="@layout/component_button_secondary"
    android:id="@+id/secondary_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: `Widget.Edham.Button.Secondary`
- **Background**: Transparent
- **Border**: 2dp `color_secondary_500`
- **Text Color**: `color_secondary_500`
- **Padding**: 24dp horizontal, 12dp vertical
- **Border Radius**: 12dp

### Icon Button

**Purpose**: Action buttons with icons only

**File**: `component_icon_button.xml`

**Usage**:
```xml
<include layout="@layout/component_icon_button"
    android:id="@+id/icon_button"
    android:layout_width="48dp"
    android:layout_height="48dp" />
```

**Properties**:
- **Style**: `Widget.Edham.Button.Icon`
- **Size**: 48dp minimum touch target
- **Icon**: 24dp default
- **Background**: `color_primary_500`
- **Icon Tint**: `text_secondary`

### Floating Action Button (FAB)

**Purpose**: Primary screen action, floating

**File**: `component_floating_action_button.xml`

**Usage**:
```xml
<include layout="@layout/component_floating_action_button"
    android:id="@+id/fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end" />
```

**Properties**:
- **Style**: `Widget.Edham.Button.FAB`
- **Background**: `color_primary_500`
- **Icon**: Default add icon
- **Elevation**: 5dp
- **Size**: 56dp default

---

## 📝 Input Components

### Text Input Field

**Purpose**: Standard text input

**File**: `component_input_field.xml`

**Usage**:
```xml
<include layout="@layout/component_input_field"
    android:id="@+id/text_input"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: `Widget.Edham.TextInputLayout`
- **Background**: `surface_primary`
- **Border**: 2dp `border_primary`
- **Hint**: `text_tertiary`
- **Padding**: 16dp horizontal, 12dp vertical
- **Corner Radius**: 12dp

**Variants**:
- **Outlined**: `Widget.Edham.TextInputLayout.OutlinedBox`
- **Filled**: `Widget.Edham.TextInputLayout.FilledBox`
- **Search**: `Widget.Edham.TextInputLayout.Search`
- **Email**: `Widget.Edham.TextInputLayout.Email`
- **Password**: `Widget.Edham.TextInputLayout.Password`

**States**:
- Default: Normal state
- Focused: Highlighted border
- Error: Red border and message
- Disabled: Reduced opacity
- Success: Green border and checkmark

---

## 🃏 Card Components

### Default Card

**Purpose**: Content container with information

**File**: `component_card_default.xml`

**Usage**:
```xml
<include layout="@layout/component_card_default"
    android:id="@+id/default_card"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: `Widget.Edham.Card`
- **Background**: `surface_primary`
- **Elevation**: 2dp
- **Padding**: 16dp
- **Margin**: 8dp
- **Corner Radius**: 12dp

**Content Structure**:
```xml
<!-- Card Title -->
<TextView style="@style/TextAppearance.Edham.Card.Title" />

<!-- Card Subtitle -->
<TextView style="@style/TextAppearance.Edham.Card.Subtitle" />

<!-- Card Content -->
<TextView style="@style/TextAppearance.Edham.Card.Description" />

<!-- Card Metadata -->
<TextView style="@style/TextAppearance.Edham.Card.Metadata" />
```

**Variants**:
- **Elevated**: `Widget.Edham.Card` (default)
- **Outlined**: `Widget.Edham.Card.Outlined`
- **Filled**: `Widget.Edham.Card.Filled`
- **Interactive**: `Widget.Edham.Card.Interactive`

**Logistics-Specific Cards**:
- **Shipment**: `Widget.Edham.Card.Shipment`
- **Driver**: `Widget.Edham.Card.Driver`
- **Vehicle**: `Widget.Edham.Card.Vehicle`
- **Route**: `Widget.Edham.Card.Route`

---

## 🚦 Status Components

### Status Badge

**Purpose**: Small status indicators

**File**: `component_status_badge.xml`

**Usage**:
```xml
<include layout="@layout/component_status_badge"
    android:id="@+id/status_badge"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: `Widget.Edham.StatusBadge`
- **Background**: Varies by status
- **Text**: `TextAppearance.Edham.StatusLabel`
- **Padding**: 12dp horizontal, 6dp vertical
- **Corner Radius**: 12dp

**Status Types**:
- **Shipment**: Created, Accepted, Preparing, Loaded, In Transit, Arrived, Delivered, Cancelled, Delayed, Returned, On Hold
- **Driver**: Online, Offline, Busy, On Duty, Break, Unavailable
- **Vehicle**: Available, In Use, Maintenance, Out of Service, Charging, Parking

**Usage Example**:
```kotlin
// Set shipment status
statusBadge.setText("In Transit")
statusBadge.setBackgroundResource(R.drawable.status_in_transit_background)
```

### Chip Component

**Purpose**: Filter tags and selectable items

**File**: `component_chip.xml`

**Usage**:
```xml
<include layout="@layout/component_chip"
    android:id="@+id/chip"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: `Widget.Material3.Chip.Filter`
- **Background**: `color_primary_50`
- **Border**: 1dp `color_primary_200`
- **Text**: `TextAppearance.Edham.LabelMedium`
- **Close Icon**: Visible by default

---

## 🎯 Icon Components

### Icon Button

**Purpose**: Icon-only actions

**File**: `component_icon_button.xml`

**Usage**:
```xml
<include layout="@layout/component_icon_button"
    android:id="@+id/icon_button"
    android:layout_width="48dp"
    android:layout_height="48dp" />
```

**Properties**:
- **Style**: `Widget.Edham.Button.Icon`
- **Size**: 48dp minimum touch target
- **Icon**: 24dp default
- **Background**: `color_primary_500`
- **Icon Tint**: `text_secondary`

**Icon Categories**:
- **Navigation**: Menu, back, forward
- **Actions**: Add, edit, delete, share
- **Communication**: Call, message, email
- **Media**: Play, pause, stop
- **Social**: Like, share, comment

**Icon Sizes**:
- **XS**: 12dp
- **SM**: 16dp
- **MD**: 20dp
- **LG**: 24dp (default)
- **XL**: 32dp
- **XXL**: 40dp
- **XXXL**: 48dp

---

## ⏳ Progress Components

### Progress Indicator

**Purpose**: Loading states and progress feedback

**File**: `component_progress_indicator.xml`

**Usage**:
```xml
<include layout="@layout/component_progress_indicator"
    android:id="@+id/progress_indicator"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Properties**:
- **Style**: Circular progress indicator
- **Size**: 24dp
- **Color**: `color_primary_500`
- **Text**: Loading message below indicator

**Variants**:
- **Circular**: Default circular spinner
- **Linear**: Horizontal progress bar
- **Determinate**: Shows actual progress
- **Indeterminate**: Continuous animation

**States**:
- **Loading**: Show spinning indicator
- **Success**: Green checkmark
- **Error**: Red error icon
- **Complete**: Hide indicator

---

## 🧭 Navigation Components

### Bottom Navigation

**Purpose**: Primary app navigation

**Usage**:
```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_navigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:menu="@menu/bottom_navigation_menu"
    app:labelVisibilityMode="selected"
    app:itemIconTint="@color/bottom_nav_colors" />
```

**Properties**:
- **Style**: Material3 bottom navigation
- **Items**: 3-5 maximum recommended
- **Icons**: 24dp default
- **Labels**: Show on selected only
- **Background**: `surface_primary`

### App Bar

**Purpose**: Screen-level navigation and actions

**Usage**:
```xml
<com.google.android.material.appbar.AppBarLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        app:title="Screen Title"
        app:navigationIcon="@drawable/ic_back" />
        
</com.google.android.material.appbar.AppBarLayout>
```

---

## 📊 Chart Components

### Line Chart

**Purpose**: Trend visualization over time

**Usage**:
```xml
<com.github.mikephil.charting.charts.LineChart
    android:id="@+id/line_chart"
    style="@style/Widget.Edham.Chart.Line"
    android:layout_width="match_parent"
    android:layout_height="200dp" />
```

**Properties**:
- **Style**: `Widget.Edham.Chart.Line`
- **Size**: 200dp height default
- **Colors**: Chart color palette
- **Grid**: Subtle grid lines
- **Animation**: 1000ms duration

### Bar Chart

**Purpose**: Comparison data visualization

**Usage**:
```xml
<com.github.mikephil.charting.charts.BarChart
    android:id="@+id/bar_chart"
    style="@style/Widget.Edham.Chart.Bar"
    android:layout_width="match_parent"
    android:layout_height="200dp" />
```

### Pie Chart

**Purpose**: Proportion visualization

**Usage**:
```xml
<com.github.mikephil.charting.charts.PieChart
    android:id="@+id/pie_chart"
    style="@style/Widget.Edham.Chart.Pie"
    android:layout_width="200dp"
    android:layout_height="200dp" />
```

**Chart Types**:
- **Line**: Trends over time
- **Bar**: Comparisons
- **Pie**: Proportions
- **Donut**: Ring chart
- **Area**: Filled line chart
- **Scatter**: Point distribution
- **Radar**: Multi-dimensional data
- **Gauge**: Progress indicator

---

## 📋 Table Components

### Standard Table

**Purpose**: Structured data display

**Usage**:
```xml
<TableLayout
    android:id="@+id/data_table"
    style="@style/Widget.Edham.Table"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <!-- Table Header -->
    <TableRow style="@style/Widget.Edham.Table.Header">
        <TextView style="@style/TextAppearance.Edham.Table.Header" />
        <TextView style="@style/TextAppearance.Edham.Table.Header" />
        <TextView style="@style/TextAppearance.Edham.Table.Header" />
    </TableRow>
    
    <!-- Table Rows -->
    <TableRow style="@style/Widget.Edham.Table.Row">
        <TextView style="@style/TextAppearance.Edham.Table.Cell" />
        <TextView style="@style/TextAppearance.Edham.Table.Cell" />
        <TextView style="@style/TextAppearance.Edham.Table.Cell" />
    </TableRow>
    
</TableLayout>
```

**Properties**:
- **Style**: `Widget.Edham.Table`
- **Header**: Bold text, background color
- **Rows**: Alternating colors optional
- **Cells**: Consistent padding and alignment
- **Borders**: Optional borders

**Table Variants**:
- **Standard**: Clean design
- **Bordered**: Full borders
- **Striped**: Alternating row colors
- **Compact**: Reduced spacing

---

## 📚 Usage Guidelines

### Component Selection

**Choose Primary Button for**:
- Main call-to-action
- Form submission
- Critical actions
- Primary navigation

**Choose Secondary Button for**:
- Secondary actions
- Cancel operations
- Less important actions
- Alternative options

**Choose Card for**:
- Content grouping
- Information display
- Interactive items
- Data presentation

**Choose Status Badge for**:
- Status indicators
- Filter tags
- Small labels
- State communication

### Best Practices

#### Do's
✅ Use components consistently
✅ Follow accessibility guidelines
✅ Test on various screen sizes
✅ Support dark mode
✅ Include proper labels
✅ Handle all states
✅ Provide feedback
✅ Consider RTL layouts

#### Don'ts
❌ Don't mix component styles
❌ Don't hard-code values
❌ Don't skip accessibility
❌ Don't ignore touch targets
❌ Don't use inconsistent spacing
❌ Don't forget error states
❌ Don't ignore performance
❌ Don't skip testing

---

## 💻 Implementation Examples

### Form Implementation

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="@dimen/spacing_lg">
    
    <!-- Title -->
    <TextView
        style="@style/TextAppearance.Edham.TitleMedium"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Create Shipment"
        android:layout_marginBottom="@dimen/spacing_md" />
    
    <!-- Input Fields -->
    <include layout="@layout/component_input_field"
        android:id="@+id/origin_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/spacing_md" />
    
    <include layout="@layout/component_input_field"
        android:id="@+id/destination_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="@dimen/spacing_lg" />
    
    <!-- Buttons -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="end">
        
        <include layout="@layout/component_button_secondary"
            android:id="@+id/cancel_button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginEnd="@dimen/spacing_md" />
        
        <include layout="@layout/component_button_primary"
            android:id="@+id/create_button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
            
    </LinearLayout>
    
</LinearLayout>
```

### Dashboard Implementation

```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- App Bar -->
    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            app:title="Dashboard" />
            
    </com.google.android.material.appbar.AppBarLayout>
    
    <!-- Content -->
    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fillViewport="true">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="@dimen/spacing_md">
            
            <!-- Stats Cards -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_marginBottom="@dimen/spacing_lg">
                
                <include layout="@layout/component_card_default"
                    android:id="@+id/active_shipments_card"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:layout_marginEnd="@dimen/spacing_sm" />
                
                <include layout="@layout/component_card_default"
                    android:id="@+id/delivered_card"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:layout_marginStart="@dimen/spacing_sm" />
                    
            </LinearLayout>
            
            <!-- Chart -->
            <com.github.mikephil.charting.charts.LineChart
                android:id="@+id/performance_chart"
                style="@style/Widget.Edham.Chart.Line"
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:layout_marginBottom="@dimen/spacing_lg" />
            
            <!-- Recent Shipments -->
            <TextView
                style="@style/TextAppearance.Edham.TitleMedium"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Recent Shipments"
                android:layout_marginBottom="@dimen/spacing_md" />
            
            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/recent_shipments_list"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:nestedScrollingEnabled="false" />
                
        </LinearLayout>
        
    </androidx.core.widget.NestedScrollView>
    
    <!-- FAB -->
    <include layout="@layout/component_floating_action_button"
        android:id="@+id/add_shipment_fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="@dimen/spacing_lg" />
        
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### Kotlin Implementation

```kotlin
class ShipmentFormFragment : Fragment() {
    
    private lateinit var binding: FragmentShipmentFormBinding
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShipmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupInputFields()
        setupButtons()
        setupValidation()
    }
    
    private fun setupInputFields() {
        // Configure origin input
        binding.originInput.hint = "Origin Address"
        binding.originInput.editText?.inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
        
        // Configure destination input
        binding.destinationInput.hint = "Destination Address"
        binding.destinationInput.editText?.inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
    }
    
    private fun setupButtons() {
        binding.createButton.setOnClickListener {
            if (validateForm()) {
                createShipment()
            }
        }
        
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupValidation() {
        binding.originInput.editText?.addTextChangedListener {
            validateOriginInput()
        }
        
        binding.destinationInput.editText?.addTextChangedListener {
            validateDestinationInput()
        }
    }
    
    private fun validateForm(): Boolean {
        val isOriginValid = validateOriginInput()
        val isDestinationValid = validateDestinationInput()
        
        binding.createButton.isEnabled = isOriginValid && isDestinationValid
        
        return isOriginValid && isDestinationValid
    }
    
    private fun validateOriginInput(): Boolean {
        val origin = binding.originInput.editText?.text.toString()
        val isValid = origin.isNotBlank()
        
        if (!isValid) {
            binding.originInput.error = "Origin address is required"
        } else {
            binding.originInput.error = null
        }
        
        return isValid
    }
    
    private fun validateDestinationInput(): Boolean {
        val destination = binding.destinationInput.editText?.text.toString()
        val isValid = destination.isNotBlank()
        
        if (!isValid) {
            binding.destinationInput.error = "Destination address is required"
        } else {
            binding.destinationInput.error = null
        }
        
        return isValid
    }
    
    private fun createShipment() {
        // Show loading state
        binding.createButton.isEnabled = false
        binding.createButton.text = "Creating..."
        
        // Create shipment logic here
        val origin = binding.originInput.editText?.text.toString()
        val destination = binding.destinationInput.editText?.text.toString()
        
        // Simulate API call
        lifecycleScope.launch {
            delay(2000)
            
            // Reset button state
            binding.createButton.isEnabled = true
            binding.createButton.text = "Create Shipment"
            
            // Navigate to success
            findNavController().navigate(
                ShipmentFormFragmentDirections.actionShipmentFormToShipmentSuccess()
            )
        }
    }
}
```

---

## 🎉 Summary

The Edham Logistics Component Library provides:

✅ **Complete Component Set** - All necessary UI components
✅ **Consistent Design** - Unified appearance and behavior
✅ **Accessibility Support** - WCAG 2.1 compliant
✅ **Theme Support** - Light/dark mode ready
✅ **RTL Support** - Arabic text direction
✅ **Responsive Design** - Works on all screen sizes
✅ **Easy Integration** - Simple include statements
✅ **Comprehensive Documentation** - Detailed usage guides

### Key Benefits

1. **Development Speed**: Pre-built components accelerate development
2. **Consistency**: Ensures uniform user experience
3. **Quality**: Tested and optimized components
4. **Maintainability**: Centralized component management
5. **Scalability**: Easy to extend and customize

### Next Steps

1. **Integrate Components**: Start using components in your layouts
2. **Customize Styles**: Adapt to specific needs
3. **Test Thoroughly**: Verify functionality and appearance
4. **Gather Feedback**: Collect user and developer feedback
5. **Iterate**: Improve and expand component library

---

*Last updated: May 2026*
*Version: 1.0*
*Maintained by: Edham Logistics Design Team*
