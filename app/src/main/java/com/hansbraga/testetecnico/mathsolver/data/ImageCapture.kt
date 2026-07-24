package com.hansbraga.testetecnico.mathsolver.data

import android.net.Uri

interface ImageCapture {
    fun createCaptureUri(): Uri
    fun compress(uri: Uri): ByteArray?
}
