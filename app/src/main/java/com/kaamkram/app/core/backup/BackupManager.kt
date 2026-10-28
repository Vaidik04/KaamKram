package com.kaamkram.app.core.backup

import android.content.Context
import com.kaamkram.app.data.local.db.KaamKramDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupManager {

    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val SALT_SIZE = 16
    private const val IV_SIZE = 12
    private const val TAG_LENGTH_BITS = 128

    fun createEncryptedBackup(context: Context, passphrase: CharArray, outputFile: File): Boolean {
        val dbFile = context.getDatabasePath(KaamKramDatabase.DATABASE_NAME)
        if (!dbFile.exists()) return false

        val dbBytes = dbFile.readBytes()

        val salt = ByteArray(SALT_SIZE)
        val iv = ByteArray(IV_SIZE)
        SecureRandom().apply {
            nextBytes(salt)
            nextBytes(iv)
        }

        val key = deriveKey(passphrase, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BITS, iv))
        val encryptedData = cipher.doFinal(dbBytes)

        FileOutputStream(outputFile).use { out ->
            out.write("KAAMKRAM_V1".toByteArray(Charsets.UTF_8)) // Header
            out.write(salt)
            out.write(iv)
            out.write(encryptedData)
        }
        return true
    }

    fun restoreEncryptedBackup(context: Context, passphrase: CharArray, backupFile: File): Boolean {
        if (!backupFile.exists()) return false

        FileInputStream(backupFile).use { input ->
            val headerBytes = ByteArray(11)
            val readHeader = input.read(headerBytes)
            if (readHeader != 11 || String(headerBytes, Charsets.UTF_8) != "KAAMKRAM_V1") {
                throw IllegalArgumentException("Invalid or unsupported KaamKram backup file format")
            }

            val salt = ByteArray(SALT_SIZE)
            input.read(salt)

            val iv = ByteArray(IV_SIZE)
            input.read(iv)

            val encryptedData = input.readBytes()

            val key = deriveKey(passphrase, salt)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BITS, iv))

            val decryptedDbBytes = cipher.doFinal(encryptedData)

            val dbFile = context.getDatabasePath(KaamKramDatabase.DATABASE_NAME)
            // Staging file
            val tempStagingFile = File(context.cacheDir, "staging_restore.db")
            tempStagingFile.writeBytes(decryptedDbBytes)

            // Atomic replace
            tempStagingFile.copyTo(dbFile, overwrite = true)
            tempStagingFile.delete()
        }
        return true
    }

    private fun deriveKey(passphrase: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(passphrase, salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secret = factory.generateSecret(spec)
        return SecretKeySpec(secret.encoded, "AES")
    }
}
