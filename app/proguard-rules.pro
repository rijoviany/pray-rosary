# Proguard rules for Rosarium
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
