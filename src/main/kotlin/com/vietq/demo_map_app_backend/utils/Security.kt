package com.vietq.demo_map_app_backend.utils
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun sha256(input: String): String {
    val bytes = MessageDigest
        .getInstance("SHA-256")
        .digest(input.toByteArray(Charsets.UTF_8))

    return bytes.joinToString("") { "%02x".format(it) }
}
fun hexToBytes(hex: String): ByteArray {
    return hex.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
fun decrypt3DES(encryptedHex: String, encodeKey: String): String {
    val key24 = encodeKey.substring(0, 24)

    val secretKey = SecretKeySpec(
        key24.toByteArray(Charsets.UTF_8),
        "DESede"
    )

    val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)

    val encryptedBytes = hexToBytes(encryptedHex)
    val decryptedBytes = cipher.doFinal(encryptedBytes)

    return String(decryptedBytes, Charsets.UTF_8)
}