-keepattributes Annotation, Signature

# Gson

-keep class com.google.gson.** { *; }

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# This is to keep ```class Foo : ArrayList<Bar>()``` which is used to turn the model
# into an array which then is easily fed to Gson without any weirdness of nested classes
-keep class * extends java.util.ArrayList { *; }

# OkHttp

-keep class okhttp3.** { *; }

# Compose

-keep class androidx.compose.** { *; }
-keep class kotlin.reflect.** { *; }

# Voyager

-keep class cafe.adriel.voyager.** { *; }

# Misc Ignores

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.intellij.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn androidx.compose.**
-dontwarn androidx.ui.**