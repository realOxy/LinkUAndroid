package com.linku.core.fs.crypto

import java.io.InputStream
import java.io.OutputStream

interface CryptoManager {
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray
    fun decrypt(inputStream: InputStream): ByteArray
}