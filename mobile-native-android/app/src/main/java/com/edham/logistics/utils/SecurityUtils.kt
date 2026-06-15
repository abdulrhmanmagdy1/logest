package com.edham.logistics.utils

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Security Utils - Utility functions for encryption, hashing, and security operations
 */
object SecurityUtils {
    
    private const val AES_KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    
    /**
     * Generate a secure random key
     */
    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(AES_KEY_SIZE)
        return keyGenerator.generateKey()
    }
    
    /**
     * Encrypt data using AES-GCM
     */
    fun encrypt(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        
        val cipherText = cipher.doFinal(data.toByteArray())
        
        val ivAndCipherText = iv + cipherText
        return Base64.encodeToString(ivAndCipherText, Base64.DEFAULT)
    }
    
    /**
     * Decrypt data using AES-GCM
     */
    fun decrypt(encryptedData: String, key: SecretKey): String {
        val ivAndCipherText = Base64.decode(encryptedData, Base64.DEFAULT)
        
        val iv = ivAndCipherText.copyOfRange(0, GCM_IV_LENGTH)
        val cipherText = ivAndCipherText.copyOfRange(GCM_IV_LENGTH, ivAndCipherText.size)
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)
        
        val decryptedData = cipher.doFinal(cipherText)
        return String(decryptedData)
    }
    
    /**
     * Hash data using SHA-256
     */
    fun hash(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data.toByteArray())
        return Base64.encodeToString(hashBytes, Base64.DEFAULT)
    }
    
    /**
     * Convert string to SecretKey
     */
    fun stringToKey(keyString: String): SecretKey {
        val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
        return SecretKeySpec(keyBytes, "AES")
    }
    
    /**
     * Convert SecretKey to string
     */
    fun keyToString(key: SecretKey): String {
        return Base64.encodeToString(key.encoded, Base64.DEFAULT)
    }
}
