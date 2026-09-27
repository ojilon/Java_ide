package com.ojilon.javaide.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ojilon.javaide.R;
import com.ojilon.javaide.ui.editor.EditorFragment;

/**
 * Android UI entry point — classic OOP style.
 * Hosts the editor (and later other screens).
 */
public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, EditorFragment.newInstance())
                    .commit();
        }
    }
}
