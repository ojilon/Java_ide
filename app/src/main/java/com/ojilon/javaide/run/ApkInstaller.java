package com.ojilon.javaide.run;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Installs an APK using the modern PackageInstaller API (OOP / platform layer).
 */
public final class ApkInstaller {

    public static final String ACTION_INSTALL_COMPLETE =
            "com.ojilon.javaide.INSTALL_COMPLETE";

    private ApkInstaller() {}

    /**
     * Starts a PackageInstaller session and commits the given APK bytes.
     * Result is delivered via a broadcast with action ACTION_INSTALL_COMPLETE.
     *
     * @return session id, or -1 on failure to create the session
     */
    public static int install(@NonNull Context context, @NonNull byte[] apkBytes,
                              @NonNull String suggestedName) throws IOException {
        PackageInstaller installer = context.getPackageManager().getPackageInstaller();

        PackageInstaller.SessionParams params =
                new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(null); // let the APK declare it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED);
        }

        int sessionId = installer.createSession(params);
        PackageInstaller.Session session = installer.openSession(sessionId);

        try (OutputStream out = session.openWrite(suggestedName, 0, apkBytes.length)) {
            out.write(apkBytes);
            session.fsync(out);
        } catch (IOException e) {
            session.abandon();
            throw e;
        }

        Intent callback = new Intent(ACTION_INSTALL_COMPLETE);
        callback.setPackage(context.getPackageName());
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pending = PendingIntent.getBroadcast(context, sessionId, callback, flags);
        session.commit(pending.getIntentSender());
        session.close();
        return sessionId;
    }

    /** Launch the main activity of an installed package, if any. */
    public static boolean launch(@NonNull Context context, @NonNull String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent launch = pm.getLaunchIntentForPackage(packageName);
        if (launch == null) return false;
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
        return true;
    }
}
