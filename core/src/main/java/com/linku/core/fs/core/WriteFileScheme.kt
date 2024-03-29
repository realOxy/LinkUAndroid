@file:Suppress("unused")

package com.linku.core.fs.core

import android.net.Uri
import androidx.core.net.toUri
import java.io.File

interface WriteFileScheme : FileScheme {
    fun put(uri: Uri?, fixedFormat: String? = null): File?
    fun encrypt(text: String): String
}

fun WriteFileScheme.cache(uri: Uri?): Uri? {
    return put(uri)?.toUri()
}
