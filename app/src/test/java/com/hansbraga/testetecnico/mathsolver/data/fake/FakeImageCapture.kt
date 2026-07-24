package com.hansbraga.testetecnico.mathsolver.data.fake

import android.net.Uri
import com.hansbraga.testetecnico.mathsolver.data.ImageCapture
import io.mockk.mockk

class FakeImageCapture : ImageCapture {

    var captureUri: Uri = mockk(relaxed = true)
    var compressedBytes: ByteArray? = byteArrayOf(1, 2, 3)
    var lastCompressedUri: Uri? = null

    override fun createCaptureUri(): Uri = captureUri

    override fun compress(uri: Uri): ByteArray? {
        lastCompressedUri = uri
        return compressedBytes
    }
}
