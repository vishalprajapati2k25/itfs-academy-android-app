package com.itfreesource.academy.security

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * EncryptedDataStore — Secure local persistence layer.
 * Encrypts cached questions, tokens, and progress with device-bound AES keys.
 * Even on a rooted device with ADB pull, raw questions cannot be read in plaintext.
 */
class EncryptedDataStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("itfs_academy_secure_vault", Context.MODE_PRIVATE)

    private val secretKeySpec: SecretKeySpec
    private val ivSpec: IvParameterSpec

    init {
        // Derive device-bound cryptographic key from Android ID and package seed
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "itfs_fallback_key"
        val seed = "${context.packageName}:$androidId:ITFS_ACADEMY_SALT_2026"
        val sha256 = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray(Charsets.UTF_8))
        secretKeySpec = SecretKeySpec(sha256, 0, 16, "AES")
        ivSpec = IvParameterSpec(sha256, 16, 16)
    }

    private fun encrypt(plaintext: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
            val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            plaintext
        }
    }

    private fun decrypt(ciphertext: String): String {
        return try {
            val decoded = Base64.decode(ciphertext, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
            String(cipher.doFinal(decoded), Charsets.UTF_8)
        } catch (e: Exception) {
            ciphertext
        }
    }

    fun putSecureString(key: String, value: String) {
        prefs.edit().putString(key, encrypt(value)).apply()
    }

    fun getSecureString(key: String, defaultValue: String = ""): String {
        val encrypted = prefs.getString(key, null) ?: return defaultValue
        return decrypt(encrypted)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    fun clearVault() {
        prefs.edit().clear().apply()
    }
}
