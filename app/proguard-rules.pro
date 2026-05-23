# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\YourUser\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# SQLCipher
-keep class net.zetetic.database.sqlcipher.** { *; }
-keep class net.zetetic.database.** { *; }

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Mercado Pago SDK
-keep class com.mercadopago.** { *; }
-dontwarn com.mercadopago.**
