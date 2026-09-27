package com.ojilon.javaide.core.dex;

import androidx.annotation.NonNull;

/**
 * Dexer backend contract (d8 / r8 later).
 */
public interface Dexer {

    @NonNull
    DexResult dex(@NonNull DexRequest request);
}
