package com.ojilon.javaide.run;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

/**
 * Receives PackageInstaller commit status.
 */
public final class InstallResultReceiver extends BroadcastReceiver {

    private static final String TAG = "InstallResultReceiver";

    public interface Listener {
        void onInstallFinished(boolean success, String message, String packageName);
    }

    private static volatile Listener listener;

    public static void setListener(Listener l) {
        listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        String packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME);

        boolean success = status == PackageInstaller.STATUS_SUCCESS;
        Log.i(TAG, "Install status=" + status + " pkg=" + packageName + " msg=" + message);

        // If the system needs user confirmation, forward the intent
        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            Intent confirm = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (confirm != null) {
                confirm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(confirm);
            }
            return;
        }

        Listener l = listener;
        if (l != null) {
            l.onInstallFinished(success,
                    message != null ? message : (success ? "Installed" : "Install failed"),
                    packageName);
        }
    }
}
