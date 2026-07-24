package com.hansbraga.testetecnico.mathsolver.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidImageCaptureTest {

    @Test
    fun `wide image larger than the max is scaled down preserving aspect ratio`() {
        val (width, height) = scaledDimensions(width = 2000, height = 1000, maxDimension = 1024)

        assertEquals(1024, width)
        assertEquals(512, height)
    }

    @Test
    fun `tall image larger than the max is scaled down preserving aspect ratio`() {
        val (width, height) = scaledDimensions(width = 500, height = 2000, maxDimension = 1024)

        assertEquals(256, width)
        assertEquals(1024, height)
    }

    @Test
    fun `dimensions already within the max are left unchanged`() {
        val (width, height) = scaledDimensions(width = 800, height = 600, maxDimension = 1024)

        assertEquals(800, width)
        assertEquals(600, height)
    }

    @Test
    fun `dimensions exactly at the max are left unchanged`() {
        val (width, height) = scaledDimensions(width = 1024, height = 1024, maxDimension = 1024)

        assertEquals(1024, width)
        assertEquals(1024, height)
    }
}
