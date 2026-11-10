# Proguard / R8 rules for Rosarium

# Preserve line numbers and source file names for Play Console deobfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlinx Serialization
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer();
}

# Preserve Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve Firebase / Firestore data models
-keepclassmembers class com.trivium.rosarium.data.model.** {
    <init>(...);
    *;
}
-keep class com.trivium.rosarium.data.model.** { *; }

# Google Play In-App Updates
-keep class com.google.android.play.core.appupdate.** { *; }
-keep class com.google.android.play.core.install.** { *; }
