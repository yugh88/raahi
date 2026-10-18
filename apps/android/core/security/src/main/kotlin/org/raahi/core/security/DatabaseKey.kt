package org.raahi.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * The database key: 32 random bytes, generated once on this phone. It is stored only wrapped
 * (AES-GCM) by a non-exportable Android Keystore key, so a copied app folder cannot be read
 * elsewhere. SQLCipher gets it as a raw key, which skips the slow passphrase derivation —
 * important on a 2 GB phone at every app start.
 */
object DatabaseKey {
    private const val KEYSTORE = "AndroidKeyStore"
    private const val ALIAS = "raahi_db_wrap"
    private const val FILE = "db.key"

    /** SQLCipher raw-key form: x'<64 hex chars>'. */
    fun sqlcipherRawKey(context: Context): ByteArray {
        val hex = rawKey(context).joinToString("") { "%02x".format(it) }
        return "x'$hex'".toByteArray(Charsets.US_ASCII)
    }

    @Synchronized
    fun rawKey(context: Context): ByteArray {
        val file = File(context.noBackupFilesDir, FILE)
        if (file.exists()) return unwrap(file.readBytes())
        val key = ByteArray(32).also { SecureRandom().nextBytes(it) }
        file.writeBytes(wrap(key))
        return key
    }

    private fun wrappingKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (ks.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        gen.init(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build(),
        )
        return gen.generateKey()
    }

    private fun wrap(plain: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, wrappingKey()) }
        val iv = cipher.iv
        return byteArrayOf(iv.size.toByte()) + iv + cipher.doFinal(plain)
    }

    private fun unwrap(blob: ByteArray): ByteArray {
        val ivLen = blob[0].toInt()
        val iv = blob.copyOfRange(1, 1 + ivLen)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.DECRYPT_MODE, wrappingKey(), GCMParameterSpec(128, iv)) }
        return cipher.doFinal(blob, 1 + ivLen, blob.size - 1 - ivLen)
    }
}
