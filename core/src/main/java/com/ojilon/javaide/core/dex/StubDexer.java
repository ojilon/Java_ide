package com.ojilon.javaide.core.dex;

import androidx.annotation.NonNull;

/**
 * Placeholder until a real d8/r8 integration is added.
 */
public final class StubDexer implements Dexer {

    @NonNull
    @Override
    public DexResult dex(@NonNull DexRequest request) {
        return DexResult.notImplemented();
    }
}
