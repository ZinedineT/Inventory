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

# Reglas para Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Mantener las clases de Glide y sus métodos
-keep class com.bumptech.glide.** { *; }
-keep class com.bumptech.glide.load.resource.bitmap.** { *; }
-keep class com.bumptech.glide.load.resource.gif.** { *; }
-keep class com.bumptech.glide.load.resource.drawable.** { *; }

# Mantener las anotaciones de Glide
-keep class com.bumptech.glide.annotation.** { *; }

# Mantener las clases de soporte de Glide
-keep class com.bumptech.glide.support.** { *; }

# Mantener las clases de transformaciones de Glide
-keep class com.bumptech.glide.load.resource.transcode.** { *; }

# Mantener las clases de decodificación de Glide
-keep class com.bumptech.glide.load.resource.bitmap.** { *; }
-keep class com.bumptech.glide.load.resource.gif.** { *; }
-keep class com.bumptech.glide.load.resource.drawable.** { *; }

# Mantener las clases de caché de Glide
-keep class com.bumptech.glide.load.engine.cache.** { *; }

# Mantener las clases de utilidades de Glide
-keep class com.bumptech.glide.util.** { *; }

# Mantener las clases de solicitudes de Glide
-keep class com.bumptech.glide.request.** { *; }

# Mantener las clases de gestión de recursos de Glide
-keep class com.bumptech.glide.load.engine.** { *; }

# Mantener las clases de gestión de errores de Glide
-keep class com.bumptech.glide.load.engine.error.** { *; }

# Mantener las clases de gestión de memoria de Glide
-keep class com.bumptech.glide.load.engine.bitmap_recycle.** { *; }

# Mantener las clases de gestión de hilos de Glide
-keep class com.bumptech.glide.load.engine.executor.** { *; }