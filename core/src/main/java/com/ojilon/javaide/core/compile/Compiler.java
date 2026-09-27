package com.ojilon.javaide.core.compile;

import androidx.annotation.NonNull;

/**
 * Compiler backend contract.
 * Implementations must be side-effect free with respect to the request
 * (they may write temporary files, but the public API stays pure).
 *
 * Future: a native (C++) implementation can be swapped in behind the same interface.
 */
public interface Compiler {

    @NonNull
    CompileResult compile(@NonNull CompileRequest request);
}
