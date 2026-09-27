// Task 14: minimal native stub for libjavaide_core.so.
//
// No native methods yet — those land in task 15 (NativeBridge).
// This file only proves the CMake/NDK wiring works and the .so loads.
// Keep it dependency-free so every configured ABI builds cleanly.

#include <jni.h>

// Called by the VM when System.loadLibrary("javaide_core") runs.
// Returning JNI_VERSION_1_6 keeps minSdk 26 compatible.
jint JNI_OnLoad(JavaVM* vm, void* /*reserved*/) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_VERSION_1_6;
    }
    return JNI_VERSION_1_6;
}
