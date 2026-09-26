package com.ojilon.javaide.core;

/**
 * Narrow bridge from Android UI (OOP) into the functional / JNI-ready core.
 *
 * Design goals:
 * - Pure / functional style where possible (static methods, immutable data).
 * - Easy to replace implementation with native (C++) via JNI later.
 * - No Android UI types leak into this layer.
 */
public final class CoreBridge {

    private CoreBridge() {
        // no instances
    }

    /** Simple health-check / entry point. Later this can become a JNI call. */
    public static String hello() {
        return "Java IDE core ready (functional layer)";
    }

    // Future: load native library and declare native methods here, e.g.
    // static {
    //     System.loadLibrary("javaide_core");
    // }
    // public static native String nativeHello();
}
