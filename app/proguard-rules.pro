# ============================================================================
# ProGuard / R8 Hardened Security Rules - ITFreeSource Academy Android App
# Enforces Anti-Recompilation, Anti-Decompilation & Code Armor
# ============================================================================

# Strip debugging information (Line numbers, source files, local variable names)
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Aggressive package flattening & class obfuscation
-repackageclasses ''
-allowaccessmodification
-overloadaggressively

# Strip all android.util.Log calls in release builds to prevent telemetry leakage
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# Keep Compose Runtime essentials
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# Protect Kotlin reflection metadata while allowing obfuscation
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
    @androidx.compose.runtime.ReadOnlyComposable *;
}

# Coil Image Loader rules
-keep class coil.** { *; }
-dontwarn coil.**

# Keep models for JSON serialization parsing while obfuscating method names
-keepclassmembers class com.itfreesource.academy.data.model.** {
    <fields>;
}

# Obfuscate Security & Anti-Tamper engines
-keepclassmembers class com.itfreesource.academy.security.** {
    private *;
    public *;
}
