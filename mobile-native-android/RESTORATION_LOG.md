# Restoration Log

> Append a new entry **after every restoration task**. Most-recent first.

| Date | Task | Files restored | Symbols introduced | TODO/STUB markers added | Build status |
|------|------|----------------|--------------------|-------------------------|--------------|
| 2026-05-16 | Initial stabilization | 0 (587 → .bak) | `R.color.status_pending`, `R.color.status_pending_bg`, `R.color.status_delivered`, `R.color.status_delivered_bg`, `R.color.status_cancelled`, `R.color.status_cancelled_bg` | `MainActivity` reduced to a stub; `LogisticsApplication` no longer Hilt-annotated; `SplashActivity` skips onboarding | ✅ green |

---

## Entry template

```markdown
| 2026-MM-DD | <short task name> | N (file1.kt, file2.kt) | <new public symbols> | <markers> | ✅/❌ |
```

## Notes on the baseline (2026-05-16)

- Renamed 587 broken `.kt` files to `.kt.bak`.
- Compose enabled with kotlinCompilerExtensionVersion `1.5.7` (Kotlin 1.9.21).
- Fixed `<com.google.android.material.switchmaterial.Switch>` → `SwitchMaterial` in `fragment_notification_settings.xml`.
- Renamed `RoleBasedScreenLock.java` (Kotlin code in a .java file) to `.java.bak`.
- Added missing `import com.edham.logistics.R` to `tracking/RealTimeLocationService.kt`.
- Hilt / KAPT remain disabled until Phase D of `AI_RESTORATION_TASKS.md`.
