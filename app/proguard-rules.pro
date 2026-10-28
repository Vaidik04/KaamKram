# KaamKram Proguard / R8 Rules

# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class **$$serializer {
    public static final **$$serializer INSTANCE;
}
-keepclassmembers class * {
    public static final *** Companion;
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt / Dependency Injection
-keep class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class * extends androidx.hilt.work.HiltWorkerFactory

# Keep domain models and entities from aggressive obfuscation
-keep class com.kaamkram.app.domain.model.** { *; }
-keep class com.kaamkram.app.data.local.entity.** { *; }
