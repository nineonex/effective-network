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

-ignorewarnings
-keep class **.R$* {*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# 保护泛型与反射
-keepattributes Signature
# 保护注解　
-keepattributes *Annotation*

# Application classes that will be serialized/deserialized over Gson
-keep public class * extends cc.seedland.inf.network.BaseBean{*;}
-keep class cc.seedland.inf.network.BaseBean{*;}

-keep class cc.seedland.inf.network.Networkit{*;}
-keep class cc.seedland.inf.network.JsonCallback{*;}