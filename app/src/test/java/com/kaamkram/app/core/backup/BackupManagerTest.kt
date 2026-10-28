package com.kaamkram.app.core.backup

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class BackupManagerTest {

    @Test
    fun testAesGcmEncryptionAndDecryption() {
        val originalData = "SQLite format 3\u0000KaamKram Test Database Content".toByteArray(Charsets.UTF_8)
        val password = "StrongTechnicianPassword123!".toCharArray()

        // Salt and IV
        val salt = ByteArray(16)
        val iv = ByteArray(12)
        SecureRandom().apply {
            nextBytes(salt)
            nextBytes(iv)
        }

        // Key Derivation
        val spec = PBEKeySpec(password, salt, 1000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

        // Encrypt
        val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val ciphertext = encryptCipher.doFinal(originalData)

        // Decrypt with correct password
        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        val decryptedData = decryptCipher.doFinal(ciphertext)

        assertArrayEquals(originalData, decryptedData)
    }

    @Test
    fun testDecryptionFailsWithWrongPassword() {
        val originalData = "Confidential customer database".toByteArray(Charsets.UTF_8)
        val correctPassword = "CorrectPassword".toCharArray()
        val wrongPassword = "WrongPassword".toCharArray()

        val salt = ByteArray(16)
        val iv = ByteArray(12)
        SecureRandom().apply {
            nextBytes(salt)
            nextBytes(iv)
        }

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val correctKey = SecretKeySpec(factory.generateSecret(PBEKeySpec(correctPassword, salt, 1000, 256)).encoded, "AES")
        val wrongKey = SecretKeySpec(factory.generateSecret(PBEKeySpec(wrongPassword, salt, 1000, 256)).encoded, "AES")

        val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, correctKey, GCMParameterSpec(128, iv))
        val ciphertext = encryptCipher.doFinal(originalData)

        // Attempting to decrypt with wrong key must fail with AEADBadTagException
        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, wrongKey, GCMParameterSpec(128, iv))

        assertThrows(AEADBadTagException::class.java) {
            decryptCipher.doFinal(ciphertext)
        }
    }
}
