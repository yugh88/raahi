# Vosk talks to libvosk.so through JNA, which finds Java classes and methods by name at runtime.
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keep class org.vosk.** { *; }
-dontwarn java.awt.**

# SQLCipher's native library calls back into these classes by name.
-keep class net.zetetic.database.** { *; }
