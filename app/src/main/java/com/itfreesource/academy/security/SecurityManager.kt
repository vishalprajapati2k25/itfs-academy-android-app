package com.itfreesource.academy.security

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowManager

/**
 * SecurityManager — Enforces DRM, Screen-Scraping Protection, and Content Security.
 *
 * Prevents:
 * 1. Screenshots and Screen Recording (FLAG_SECURE)
 * 2. Recent Apps / Task Switcher snapshot leakage (blacks out previews)
 * 3. External display mirroring over untrusted screencast
 */
object SecurityManager {

    private const val PREFS_SECURITY = "itfs_security_prefs"
    private const val KEY_FLAG_SECURE_ENFORCED = "flag_secure_enforced"

    /**
     * Applies WindowManager.LayoutParams.FLAG_SECURE to the current window.
     * When active:
     * - The OS rejects hardware screenshots (Volume Down + Power).
     * - Screen recording apps capture a completely black frame.
     * - The Android task switcher preview displays a blank or generic silhouette.
     */
    fun applyScreenshotProtection(activity: Activity, enable: Boolean = true) {
        activity.runOnUiThread {
            if (enable) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    /**
     * Verifies whether FLAG_SECURE is currently active on the window.
     */
    fun isScreenshotProtectionActive(activity: Activity): Boolean {
        val flags = activity.window.attributes.flags
        return (flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }

    /**
     * Checks user security preference (defaults to TRUE for maximum protection).
     */
    fun isProtectionPreferenceEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_SECURITY, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FLAG_SECURE_ENFORCED, true)
    }

    fun setProtectionPreference(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_SECURITY, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FLAG_SECURE_ENFORCED, enabled).apply()
    }
}
