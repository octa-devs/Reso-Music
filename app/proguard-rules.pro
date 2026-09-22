# ==============================================================================
# RESO MUSIC - PROGUARD / R8 RULES
# ==============================================================================

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic attributes
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# ==============================================================================
# ROOM DATABASE
# ==============================================================================
-keep class com.octadevs.resomusic.data.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}

# ==============================================================================
# GSON & DATA CLASSES (Cache & Backup)
# ==============================================================================
-keep class com.octadevs.resomusic.tools.Song { *; }
-keep class com.octadevs.resomusic.tools.PlaylistExportData { *; }
-keep class com.octadevs.resomusic.tools.PlaylistData { *; }
-keep class com.octadevs.resomusic.tools.SongMetadata { *; }

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==============================================================================
# TOOLS (PlaybackManager, MusicService, SettingsManager, audio pipeline)
# ==============================================================================
-keep class com.octadevs.resomusic.tools.** { *; }

# ==============================================================================
# AUDIO EFFECTS (reflection in DynamicsEffect)
# ==============================================================================
-keep class com.octadevs.resomusic.audio.** { *; }

# ==============================================================================
# UI ACTIVITIES (EqualizerActivity, etc. — Compose state)
# ==============================================================================
-keep class com.octadevs.resomusic.ui.** { *; }

# ==============================================================================
# JAUDIOTAGGER (Metadata extraction)
# ==============================================================================
-keep class org.jaudiotagger.** { *; }
-dontwarn org.jaudiotagger.**

# ==============================================================================
# KOTLIN COROUTINES
# ==============================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ==============================================================================
# OKIO / OKHTTP (transitive deps via Coil)
# ==============================================================================
-dontwarn okio.**
-dontwarn okhttp3.**
