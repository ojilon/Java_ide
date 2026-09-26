package com.ojilon.javaide.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ojilon.javaide.R;
import com.ojilon.javaide.databinding.ActivityMainBinding;
import com.ojilon.javaide.core.CoreBridge;

/**
 * Android UI entry point — classic OOP style.
 * Talks to the functional / JNI-ready core only through narrow interfaces.
 */
public final class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example: call into the functional core
        String status = CoreBridge.hello();
        binding.statusText.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
