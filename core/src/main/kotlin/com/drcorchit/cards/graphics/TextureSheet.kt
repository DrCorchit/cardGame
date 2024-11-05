package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.google.common.collect.ImmutableList

class TextureSheet @JvmOverloads constructor(
    texture: Texture?,
    framesHoriz: Int,
    framesVert: Int,
    frameCount: Int = framesHoriz * framesVert
) :
    SpriteSheet {
    private val texture: Texture?
    private val frames: ImmutableList<TextureRegion>

    override val width: Int
    override val height: Int

    //Ratio above 1.0 means taller than wide. Below means wider than tall. 1.0 means perfect square.
    override val ratio: Float

    init {
        if (texture == null) throw NullPointerException("Missing texture.")
        this.texture = texture
        width = texture.width / framesHoriz
        height = texture.height / framesVert

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        val regions = TextureRegion.split(texture, width, height)
        val framesBuilder = ImmutableList.builder<TextureRegion>()
        for (j in 0 until framesVert) {
            for (i in 0 until framesHoriz) {
                val frameIndex = j * framesHoriz + i
                if (frameIndex >= frameCount) break
                framesBuilder.add(regions[j][i])
            }
        }

        frames = framesBuilder.build()
        ratio = frames[0].regionHeight * 1.0f / frames[0].regionWidth
    }

    override val isLoaded: Boolean
        get() = true

    override fun get(index: Double): TextureRegion {
        return frames[index.toInt()]
    }

    override val frameCount: Int
        get() = frames.size

    override fun dispose() {
        texture?.dispose()
    }

    override fun toString(): String {
        return "TextureSheet@" + width + "x" + height
    }
}
