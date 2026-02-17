# Proguard optimizations

-optimizationpasses 5
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-overloadaggressively

# Gson

-keepattributes Annotation, Signature

-keep class * extends java.util.ArrayList { *; }

# keep Compose Material/Material3 classes otherwise certain components may break.
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.material3.** { *; }

# exclude WorkManager from ProGuard optimization
-keep class androidx.work.** { *; }
-keepclassmembers class * extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}

-keep class androidx.work.impl.** { *; }
-keep class androidx.work.Worker { *; }
-keep class androidx.work.CoroutineWorker { *; }

-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keepclassmembers class * {
    @androidx.room.* *;
}

# Keep anything that Room generates or reflects on
-keep class **_Impl { *; }           # Room generated impls
-keep class **_Entity { *; }         # if you have custom naming

# OkHttp3

## JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

## OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
## May be used with robolectric or deliberate use of Bouncy Castle on Android
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**

# Suppress IntelliJ warnings
-dontwarn com.intellij.**