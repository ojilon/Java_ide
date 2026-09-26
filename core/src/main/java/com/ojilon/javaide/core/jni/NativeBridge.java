package com.ojilon.javaide.core.jni;

/**
 * Future JNI seam.
 * Keep all native method declarations and System.loadLibrary calls here
 * so the rest of the codebase stays pure Java / functional.
 *
 * When the C++ backend is ready:
 * 1. Add the .so via CMake in core/
 * 2. Uncomment the static block and native methods
 * 3. Implement matching functions on the native side
 */
public final class NativeBridge {

    private NativeBridge() {}

    /*
    static {
        System.loadLibrary("javaide_core");
    }

    public static native String nativeHello();
    public static native long parseLogLineNative(String raw);
    */
}
