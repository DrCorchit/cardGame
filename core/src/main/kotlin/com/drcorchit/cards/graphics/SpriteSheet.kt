package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.g2d.TextureRegion

interface SpriteSheet {
    val isLoaded: Boolean

    fun get(index: Double): TextureRegion

    val frameCount: Int

    val width: Int

    val height: Int

    val ratio: Float
        //Ratio above 1.0 means taller than wide. Below means wider than tall. 1.0 means perfect square.
        get() = height * 1.0f / width

    fun dispose()
}
