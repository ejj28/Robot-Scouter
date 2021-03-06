package com.supercilex.robotscouter.core

import android.app.ActivityManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityManagerCompat
import androidx.core.content.getSystemService

val fullVersionName: String by lazy {
    // Get it from the package manager instead of the BuildConfig to support injected version names
    // from the automated build system.
    RobotScouter.packageManager.getPackageInfo(RobotScouter.packageName, 0).versionName
}
val fullVersionCode by lazy {
    // See fullVersionName
    RobotScouter.packageManager.getPackageInfo(RobotScouter.packageName, 0).run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }
    }
}
val providerAuthority: String by lazy { "${RobotScouter.packageName}.provider" }
val isLowRamDevice: Boolean by lazy {
    ActivityManagerCompat.isLowRamDevice(
            checkNotNull(RobotScouter.getSystemService<ActivityManager>()))
}
val isInTestMode: Boolean by lazy {
    Settings.System.getString(RobotScouter.contentResolver, "firebase.test.lab") == "true"
}
