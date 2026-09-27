package com.ojilon.javaide.core.run;

/**
 * Stages of the build-and-run pipeline.
 */
public enum RunStage {
    SAVE,
    COMPILE,
    DEX,
    PACKAGE,
    INSTALL,
    LAUNCH,
    DONE,
    FAILED
}
