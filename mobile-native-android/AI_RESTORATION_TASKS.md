# 🤖 AI Restoration Tasks — Edham Logistics Android

> **Purpose:** Restore the 587 `.bak` files **incrementally** without breaking the build.
> A working APK already builds (`app/build/outputs/apk/debug/app-debug.apk`).
> **Do NOT regress this baseline.** After every restoration task you must run a full debug build and prove it still passes.

---

## 0. Mandatory Reading (Project Context)

### 0.1 Tech Stack — Locked Versions
| Tool | Version | Notes |
|---|---|---|
| Android Gradle Plugin | `8.1.4` | `build.gradle.kts` root |
| Kotlin | `1.9.21` | **Do NOT upgrade** — affects Compose compiler |
| Compose Compiler | `1.5.7` | Pinned for Kotlin 1.9.21 (`app/build.gradle.kts:74`) |
| `compileSdk` / `targetSdk` | `34` | |
| `minSdk` | `24` | |
| Java target | `17` | |

### 0.2 Currently DISABLED Features (DO NOT re-enable casually)
- **Hilt / Dagger** — KAPT is commented out in `app/build.gradle.kts`. **Files using `@HiltAndroidApp`, `@AndroidEntryPoint`, `@Inject` will NOT compile** until KAPT + Hilt plugin are re-enabled. See section 4.
- **Room compiler** — same KAPT block. `@Dao`, `@Entity`, `@Database` annotation classes **will not generate code**.
- **Onboarding flow** (`IntroViewPagerActivity`) — temporarily bypassed in `SplashActivity`.
- **Old Hilt-driven `MainActivity`** — replaced with a stub (`MainActivity.kt`). The original is at `MainActivity.kt.bak`.

### 0.3 Build Command
Always use:
```powershell
cd d:\logest\mobile-native-android
.\gradlew.bat assembleDebug --console=plain 2>&1 | Tee-Object build.log
```
Build is currently **green**. Any task ends with a green build, or it is reverted.

### 0.4 Memory Configuration
`gradle.properties` has been tuned to avoid OOM:
- Gradle daemon: 6 GB heap
- Kotlin daemon (out-of-process): 4 GB heap
- Daemon enabled, parallel build, caching, incremental Kotlin
**Do NOT modify `gradle.properties` without explicit approval.**

---

## 1. Master Rules (Apply to EVERY Task)

1. **One file (or one tightly-coupled group) per task.** Never restore more than ~5 files in a single change without an intermediate green build.
2. **Always rename `Foo.kt.bak` → `Foo.kt`**, do not copy. Preserves git diff clarity.
3. **Read the file FIRST** — most `.bak` files have legitimate code that just needs minor fixes.
4. **Fix imports before fixing logic.** Most cascading errors disappear when imports point to actually-existing classes.
5. **Forbidden shortcuts:**
   - ❌ Wrapping bodies in `try { } catch { }` to silence errors.
   - ❌ Replacing failing logic with `TODO()` without a tracking comment `// TODO(restore): ...`.
   - ❌ Adding `@Suppress("unused")` to dodge warnings instead of removing dead code.
   - ❌ Stubbing out a method to return `null` / `0` / `emptyList()` if it changes runtime behavior — mark with `// STUB(restore):` comment instead.
6. **After every task, verify build is green** with `assembleDebug`.
7. **If a task introduces > 50 new compile errors**, abort and split it.
8. **Never delete a `.bak` file** without first restoring its functionality OR confirming it is unused (no `import` references from any `.kt` file, not in `AndroidManifest.xml`, not in nav graph, not in layout `tools:context`).

---

## 2. Known-Good Reference Symbols

These exist and are safe to import. Use them as the canonical source of truth.

### 2.1 Models (single canonical location: `com.edham.logistics.models`)
- `Driver`, `Shipment`, `ShipmentStatus`, `Notification`, `Payment`, `Report`, `Setting`, `Task`, `SecuritySetting`, `SupportTicket`, `TeamStatistics`, `InventoryItem`, `DriverPerformance`, `FuelTransactionEntity`, `VehicleFuelData`

### 2.2 Domain Models (richer, DDD-style, `com.edham.logistics.domain.model`)
- `Driver`, `Shipment`, `Vehicle`, `User`, `Notification`, `Payment`, `Report`, `Setting`, `SecuritySetting`, `SupportTicket`, `Task`, `TeamStatistics`, `InventoryItem`, `VehicleFuelData`, `FuelTransactionEntity`

> **Disambiguation rule:** UI-bound code uses `com.edham.logistics.models.*`. Repository / use-case / mapper code uses `com.edham.logistics.domain.model.*`. **Never `import` both in the same file.**

### 2.3 Resources (string keys safe to use)
All Arabic strings in `res/values/strings.xml`. English defaults exist in the same file (lines 323+) for keys also defined in `res/values-ar/strings.xml`.

### 2.4 Status Colors (added during stabilization)
`R.color.status_pending`, `status_pending_bg`, `status_delivered`, `status_delivered_bg`, `status_cancelled`, `status_cancelled_bg` — see `res/values/legacy_ui_colors.xml`.

---

## 3. Forbidden Symbols (DO NOT introduce them in new code)

These types were referenced by the broken codebase but **DO NOT EXIST** anywhere. Any `.bak` file referencing them must either:
- Have the reference deleted, OR
- Have a minimal definition created **explicitly listed in the task description**.

| Symbol | Action |
|---|---|
| `TrafficApi` | Delete reference. There is no traffic API integration. |
| `WeatherService` | Delete reference. |
| `MapState` | Replace with a simple `enum class MapState { LOADING, READY, ERROR }` if needed. |
| `GeofenceEntity` | Create a Room `@Entity data class` with fields: `id`, `latitude`, `longitude`, `radius`, `type`, `isActive`, `dwellTimeThreshold`. |
| `WorkflowTransitionEntity` | Same — create only when restoring `ShipmentWorkflowEngine`. |
| `FleetAssignmentEntity` | Same. |
| `RoleBasedScreenLock` (the `.java` file with Kotlin code) | Rename to `.kt`, fix syntax, OR keep `.bak` until explicit task. |
| `IntroViewPagerActivity` | Restoring requires creating ViewPager2 onboarding. Defer until requested. |

---

## 4. Hilt Re-enablement Procedure (run ONCE before restoring any `@Inject` file)

**Trigger:** When the first task explicitly says "requires Hilt".

Steps in order:
1. Edit `app/build.gradle.kts`:
   - Uncomment `id("kotlin-kapt")` and `id("dagger.hilt.android.plugin")` plugin lines.
   - Uncomment all `kapt(...)` lines (Hilt compiler, Room compiler).
   - Uncomment Hilt + Hilt-navigation-fragment dependency lines.
2. Restore `LogisticsApplication.kt.bak` content (annotated `@HiltAndroidApp`).
3. Add the original `MainActivity.kt.bak` content back, **but** verify all injected types (`TokenManager`, `AuthStateManager`, `AppNavigationController`, `NavigationController`, `SessionExpiredHandler`) resolve. If any of them is in `.bak`, restore those FIRST.
4. Rebuild. Errors will likely be in the order of hundreds — split per package.

---

## 5. Restoration Roadmap (execute in this order)

> Each phase below is a separate task. Complete + verify build between phases.

### Phase A — Foundation (no Hilt, no Compose)
**Goal:** Restore non-DI utility classes that other layers depend on.

| # | Files | Notes |
|---|---|---|
| A.1 | `core/utils/*.kt.bak` | Constants, formatters, validators. Pure Kotlin. |
| A.2 | `core/extensions/*.kt.bak` | View / Context extensions (`gone`, `visible`, `showSnackbar`, etc.) |
| A.3 | `models/*.kt.bak` (if any remaining) | Plain data classes only. |
| A.4 | `domain/model/*.kt.bak` | Domain entities. No framework dependencies. |
| A.5 | `error/*.kt.bak` | Error result types, exceptions. |

### Phase B — Data Layer (no Hilt yet — manual constructors)
| # | Files | Notes |
|---|---|---|
| B.1 | `data/local/entity/*.kt.bak` | Room entities. Add `@Entity` annotations. **Cannot test until KAPT enabled.** |
| B.2 | `data/local/dao/*.kt.bak` | DAO interfaces. Same KAPT caveat. |
| B.3 | `data/remote/api/*.kt.bak` | Retrofit interfaces. |
| B.4 | `data/remote/dto/*.kt.bak` | DTOs (data transfer objects). |
| B.5 | `data/remote/mapper/*.kt.bak` | DTO ↔ Domain mappers. Pure functions. |
| B.6 | `data/repository/*.kt.bak` | Concrete repositories. Constructor-injectable. |

### Phase C — Domain Layer
| # | Files | Notes |
|---|---|---|
| C.1 | `domain/repository/*.kt.bak` | Repository interfaces. |
| C.2 | `domain/usecase/*.kt.bak` | Use cases. **Each is a single class with one `invoke()`** — restore individually. |
| C.3 | `feature/*/domain/repository/*.kt.bak` | Per-feature repos. |
| C.4 | `feature/*/domain/usecase/*.kt.bak` | Per-feature use cases. **221 files in `feature/`** — group by feature folder. |

### Phase D — Hilt Enablement (run section 4)

### Phase E — Presentation / UI (XML-based fragments)
| # | Files | Notes |
|---|---|---|
| E.1 | Adapters in `adapters/` and `presentation/adapter/` | RecyclerView adapters. Verify each ViewHolder layout exists in `res/layout/`. |
| E.2 | ViewModels in `presentation/*/`, `feature/*/presentation/viewmodel/` | Hilt-injected. |
| E.3 | Fragments in `ui/`, `feature/*/presentation/ui/` | Verify nav graph references. |
| E.4 | Activities — `MainActivity` last (replaces stub). |

### Phase F — Compose Screens
| # | Files | Notes |
|---|---|---|
| F.1 | `core/ui/theme/Color.kt.bak` (if .bak'd) | Define `EdhamOrange`, `SuccessGreen`, `ErrorRed`, `WarningYellow` as `Color`. |
| F.2 | `core/ui/AnalyticsComponents.kt.bak` (if any) | Compose components. |
| F.3 | `user/UserManagementAdminScreen.kt.bak` (if any) | Full Compose screens. |

### Phase G — Heavy Subsystems (highest risk, restore last)
| Module | File count | Notes |
|---|---|---|
| `feature/` | 221 | Restore one feature subdirectory at a time. Start with `feature/auth/`. |
| `core/` | 69 | Many cross-cutting concerns — restore in dependency order. |
| `tracking/` | 11 | Location services, geofences. Requires `GeofenceEntity` decision (section 3). |
| `sync/` | 4 | Background sync — depends on Room + WorkManager. |
| `analytics/` | 25 | Charts (MPAndroidChart). Mostly UI-only. |
| `fleet/` | 25 | Fleet management. Depends on data layer. |

---

## 6. Per-Task Template

When you (the AI) accept a task, follow this template literally:

```
TASK: Restore <file or group>

STEP 1 — Read
  - View .bak file content
  - Identify external symbols used
  - Check each symbol exists (use grep_search across .kt files, NOT .bak)

STEP 2 — Plan
  - List dependencies that must be restored first
  - If > 3 dependencies are .bak, STOP and request user split the task

STEP 3 — Restore
  - Rename Foo.kt.bak → Foo.kt
  - Fix imports first
  - Fix forbidden symbols (section 3)
  - Replace @Inject usage with manual constructors if Hilt is still disabled

STEP 4 — Verify
  - Run: .\gradlew.bat assembleDebug --console=plain 2>&1 | Tee-Object build.log
  - If green → DONE
  - If errors < 10 and isolated → fix in same task
  - If errors > 10 → revert (rename Foo.kt back to Foo.kt.bak) and report

STEP 5 — Document
  - Add entry to RESTORATION_LOG.md with:
    - Date / file restored
    - Symbols introduced (so future tasks know they exist)
    - Any deferred work (TODO / STUB markers added)
```

---

## 7. Common Patterns When Editing `.bak` Files

### 7.1 Replace `@AndroidEntryPoint` while Hilt is disabled
```kotlin
// Before
@AndroidEntryPoint
class MyFragment : Fragment() {
    @Inject lateinit var manager: SomeManager
}

// After (until Phase D)
class MyFragment : Fragment() {
    private val manager: SomeManager by lazy { SomeManager(requireContext()) }
}
```

### 7.2 Replace `@HiltViewModel` while Hilt is disabled
```kotlin
// Before
@HiltViewModel
class FooViewModel @Inject constructor(private val repo: FooRepository) : ViewModel()

// After
class FooViewModel(private val repo: FooRepository) : ViewModel() {
    class Factory(private val repo: FooRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FooViewModel(repo) as T
    }
}
```

### 7.3 Missing `R` import
Always add `import com.edham.logistics.R` when using `R.drawable.*`, `R.string.*`, etc.

### 7.4 Disambiguating `Driver` / `Shipment`
- If file is in `presentation/` or `ui/` or root → use `com.edham.logistics.models.*`.
- If file is in `data/`, `domain/`, or `feature/*/data|domain/` → use `com.edham.logistics.domain.model.*`.

---

## 8. Quick Health Check Commands

```powershell
# How many .bak files remain?
(Get-ChildItem app/src/main/java -Recurse -Filter *.bak).Count

# How many compile errors in current build.log?
(Get-Content build.log | Select-String '^e: file:').Count

# Files still .bak grouped by module
Get-ChildItem app/src/main/java/com/edham/logistics -Recurse -Filter *.bak |
  ForEach-Object { ($_.FullName -split 'logistics\\')[1] -replace '\\[^\\]+$','' } |
  Group-Object | Sort-Object Count -Descending | Format-Table Count, Name

# Build
.\gradlew.bat assembleDebug --console=plain 2>&1 | Tee-Object build.log

# Stop daemons (use only if hung)
.\gradlew.bat --stop
```

---

## 9. Definition of Done (Whole Project)

The restoration is complete when **all** of the following are true:
1. `Get-ChildItem app/src/main/java -Recurse -Filter *.bak` returns 0 files.
2. `assembleDebug` is green.
3. APK installs on a real Android 7+ device and the splash screen → main UI flow works.
4. Hilt is fully re-enabled (Phase D done).
5. `RESTORATION_LOG.md` documents every restored file.
6. No `// STUB(restore):` markers remain (all replaced with real implementations).

---

## 10. List of Backed-up Files

The full list of 587 backed-up files is in `BACKUP_FILES_LIST.txt` (relative paths under `app/src/main/java/com/edham/logistics/`).

Module distribution snapshot:
- `feature/` → 221 files (largest, highest priority feature folders: `auth`, `admin`, `accountant`)
- `core/` → 69 files
- `analytics/` → 25 files
- `fleet/` → 25 files
- `presentation/` → 21 files
- `finance/` → 21 files
- `performance/` → 20 files
- `offline/` → 20 files
- `notification/` → 17 files
- (root files) → 16 files
- adapters → 13 files, shipment → 12, data → 12, tracking → 11, ui → 11, maintenance → 9
- (smaller modules) → ≤ 7 each

---

## 11. First Task to Hand to the AI (copy-paste ready)

```
Read AI_RESTORATION_TASKS.md from project root and confirm you understand
the rules in sections 1, 3, and 6. Then execute Phase A.1:

  Restore all .bak files under app/src/main/java/com/edham/logistics/core/utils/

After restoring, run a debug build and report:
  - Files restored (count + names)
  - Compile errors before/after
  - Any forbidden symbols you needed to handle

Do NOT proceed to A.2 in the same response.
```
