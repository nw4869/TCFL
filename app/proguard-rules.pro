# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\programming\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------



  -dontwarn com.gc.**
  -keep class com.gc.** {
    *;
  }

  -dontwarn it.**
  -keep class it.** {
    *;
  }

  -dontwarn com.nostra13.**
  -keep class com.nostra13.** {
    *;
  }

  -dontwarn cn.jpush.**
  -keep class cn.jpush.** { *; }

  -dontwarn com.aliyun.**
  -keep class com.aliyun.** { *; }

  -dontwarn com.baidu.**
  -keep class com.baidu.** { *; }

  -dontwarn vi.com.**
  -keep class vi.com.** { *; }

  -dontwarn com.google.**
  -keep class com.google.** { *; }

  -dontwarn com.nineoldandroids.**
  -keep class com.nineoldandroids.** { *; }

  -dontwarn com.android.**
  -keep class com.android.** { *; }

  -dontwarn android.**
  -keep class android.** { *; }

  -dontwarn cn.smssdk.**
  -keep class cn.smssdk.** { *; }


#  -keepclassmembers public class com.nightwind.tcfl.bean.** {
#   void set*(***);
#   *** get*();
#   *** is*();
#  }
  -keep public class com.nightwind.tcfl.bean.** { *; }
