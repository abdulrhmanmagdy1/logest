# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ============================================
# 📋 ProGuard Rules - Edham Logistics Production
# ============================================

# Keep all model classes
-keep class com.edham.logistics.data.models.** { *; }
-keep class com.edham.logistics.data.remote.api.dto.** { *; }

# Keep all API interfaces
-keep interface com.edham.logistics.data.remote.api.** { *; }

# Keep all Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep all Hilt/Dagger classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <methods>;
}

# Keep all Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.**

# Keep all Retrofit classes
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Keep all Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# Keep all Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep all lifecycle components
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Keep all navigation components
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# Keep all Material Design components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Keep all application classes
-keep class com.edham.logistics.** { *; }

# Keep all enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep all Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep all Serializable implementations
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep all ViewBinding classes
-keep class * extends androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# Keep all data binding classes
-keep class * extends androidx.databinding.ViewDataBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# Keep all custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

# Keep all fragment classes
-keep public class * extends androidx.fragment.app.Fragment {
    public <init>();
    public void set*(...);
    *** get*();
}

# Keep all activity classes
-keep public class * extends androidx.appcompat.app.AppCompatActivity {
    public <init>();
    public void set*(...);
    *** get*();
}

# Keep all broadcast receiver classes
-keep public class * extends android.content.BroadcastReceiver {
    public <init>();
    public void onReceive(android.content.Context, android.content.Intent);
}

# Keep all service classes
-keep public class * extends android.app.Service {
    public <init>();
    public void onCreate();
    public void onDestroy();
    public void onStart(android.content.Intent, int);
    public int onStartCommand(android.content.Intent, int, int);
    public android.os.IBinder onBind(android.content.Intent);
    public boolean onUnbind(android.content.Intent);
}

# Keep all content provider classes
-keep public class * extends android.content.ContentProvider {
    public <init>();
    public boolean onCreate();
}

# Keep all native libraries
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all JNI methods
-keepclasseswithmembernames class * {
    public <methods> native <methods>;
}

# Keep all reflection usage
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @java.lang.reflect.* <fields>;
    @java.lang.reflect.* <methods>;
}

# Keep all encryption and security classes
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# Keep all certificate classes
-keep class java.security.cert.** { *; }

# Keep all keystore classes
-keep class java.security.KeyStore { *; }

# Keep all logging classes (but disable in production)
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}

# Remove all debug logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
}

# Keep all test classes (only for testing)
-keep class **.Test { *; }
-keep class **.Test* { *; }

# Keep all mock classes (only for testing)
-keep class **.Mock { *; }
-keep class **.Mock* { *; }

# Keep all stub classes (only for testing)
-keep class **.Stub { *; }
-keep class **.Stub* { *; }

# Optimization settings
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Obfuscation settings
-useuniqueclassmembernames
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Keep all annotation classes
-keep @interface * { *; }
-keepclassmembers class * {
    @* <fields>;
    @* <methods>;
}

# Keep all BuildConfig
-keep class com.edham.logistics.BuildConfig { *; }

# Keep all R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep all manifest classes
-keep class **.Manifest { *; }
-keep class **.Manifest$* { *; }

# Remove all unused code
-dontwarn java.lang.invoke.**
-dontwarn java.lang.reflect.**
-dontwarn javax.lang.model.**
-dontwarn javax.annotation.processing.**

# Keep all lambda expressions
-keepnames class ** { *; }
-keepclassmembers class * {
    synthetic <methods>;
}

# Keep all inline functions
-keep class kotlin.jvm.internal.** { *; }
-dontwarn kotlin.jvm.internal.**

# Keep all sealed classes
-keep class * implements kotlin.sealed.SealedClass { *; }

# Keep all data classes
-keep class * implements kotlin.Metadata { *; }

# Keep all companion objects
-keep class ** {
    static ** Companion;
}

# Keep all extension functions
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Keep all coroutine scopes
-keepnames class kotlinx.coroutines.CoroutineScope { *; }
-keepnames class kotlinx.coroutines.SupervisorJob { *; }

# Keep all flow classes
-keep class kotlinx.coroutines.flow.** { *; }

# Keep all state flow classes
-keep class kotlinx.coroutines.flow.StateFlow { *; }
-keep class kotlinx.coroutines.flow.SharedFlow { *; }

# Keep all live data classes
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }

# Keep all view model classes
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.AndroidViewModel { *; }

# Keep all repository classes
-keep class * extends *Repository { *; }

# Keep all use case classes
-keep class * extends *UseCase { *; }

# Keep all utility classes
-keep class * extends *Util { *; }
-keep class * extends *Utils { *; }

# Keep all helper classes
-keep class * extends *Helper { *; }

# Keep all manager classes
-keep class * extends *Manager { *; }

# Keep all controller classes
-keep class * extends *Controller { *; }

# Keep all adapter classes
-keep class * extends *Adapter { *; }

# Keep all holder classes
-keep class * extends *Holder { *; }

# Keep all listener classes
-keep class * extends *Listener { *; }
-keep class * extends *Callback { *; }

# Keep all interface implementations
-keep class * implements * { *; }

# Keep all abstract classes
-keep abstract class * { *; }

# Keep all public methods
-keepclassmembers class * {
    public <methods>;
}

# Keep all protected methods
-keepclassmembers class * {
    protected <methods>;
}

# Keep all private methods that might be called via reflection
-keepclassmembers class * {
    private <methods>;
}

# Keep all fields
-keepclassmembers class * {
    <fields>;
}

# Keep all constructors
-keepclassmembers class * {
    <init>(...);
}

# Keep all static methods
-keepclassmembers class * {
    static <methods>;
}

# Keep all static fields
-keepclassmembers class * {
    static <fields>;
}

# Keep all final classes
-keep class final * { *; }

# Keep all public classes
-keep public class * { *; }

# Remove all unused warnings
-dontwarn java.lang.**
-dontwarn javax.lang.**
-dontwarn javax.annotation.**
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn com.google.gson.**
-dontwarn dagger.**
-dontwarn hilt.**
-dontwarn com.edham.logistics.**
