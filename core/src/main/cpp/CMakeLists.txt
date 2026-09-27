cmake_minimum_required(VERSION 3.22.1)

# Task 14: first CMake wiring for :core.
# Builds libjavaide_core.so (currently JNI_OnLoad stub only).
# Native methods land in task 15; hot-path ports in task 16.
project("javaide_core")

add_library(
        javaide_core
        SHARED
        javaide_core.cpp
)

# Prefer C++23 (matches cppFlags -std=c++23 in core/build.gradle);
# target_compile_features keeps it explicit even if flags change.
target_compile_features(javaide_core PRIVATE cxx_std_23)

# Android log library (for future __android_log_print; harmless now).
find_library(log-lib log)

target_link_libraries(
        javaide_core
        PRIVATE
        ${log-lib}
)
