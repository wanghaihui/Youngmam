# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/xiaobu1/Android Studio SDK/Sdk/tools/proguard/proguard-android.txt
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

-keepattributes Signature
-keepattributes EnclosingMethod

-keep class com.xiaobukuaipao.youngmam.domain.** { *; }

-keep public class * implements java.io.Serializable {
        public *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-dontwarn com.alibaba.fastjson.**

-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-keep class com.alibaba.fastjson.** { *; }

-keepclassmembers class * {
    public <methods>;
}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep class com.baidu.** { *; }
-dontwarn com.baidu.**

-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

-keep class com.github.bumptech.glide.** { *; }
-dontwarn com.github.bumptech.glide.**

-keep class org.apache.http.** {*;}
-dontwarn org.apache.http.**

-keep class com.umeng.** { *; }
-dontwarn com.umeng.**

-keep class com.tencent.** { *; }
-dontwarn com.tencent.**

-keep class com.sina.** { *; }
-dontwarn com.sina.**













