# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\User\Documents\sdk-update/tools/proguard/proguard-android.txt
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

-dontwarn android.test.**

# org.apache
-dontwarn org.apache.**
# Jackson
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
# OkHttp
-dontwarn com.squareup.okhttp.**
# Picasso
-dontwarn com.squareup.picasso.**
#Okio
-dontwarn okio.**
#Joda Time Convert
-dontwarn org.joda.convert.**

-keep class org.junit.** { *; }
-dontwarn org.junit.**

