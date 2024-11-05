package com.drcorchit.cards

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.TextureSheet
import com.drcorchit.justice.utils.logging.Logger
import java.io.IOException

object Textures {

    var log: Logger = Logger.getLogger(Textures::class.java)

    val white = initTexture("white.png")

    val diamond = initTexture("diamond.png")
    val diamondBorder = initTexture("diamond_border.png")
    val slate = initTexture("slate2.png")
    val star = initTexture("star.png")

    //Arrow
    val arrowLeft = initTexture("arrow_left.png")
    val arrowRight = initTexture("arrow_right.png")
    val arrowHoriz = initTexture("arrow_horiz.png")
    val arrowVert = initTexture("arrow_vert.png")
    val arrowCorner1 = initTexture("arrow_corner_1.png")
    val arrowCorner2 = initTexture("arrow_corner_2.png")
    val arrowCorner3 = initTexture("arrow_corner_3.png")
    val arrowCorner4 = initTexture("arrow_corner_4.png")

    fun Texture.asSprite(width: Int = 1, height: Int = 1, frames: Int = width * height): AnimatedSprite {
        return AnimatedSprite(TextureSheet(this, width, height, frames))
    }

    fun Texture.toNinepatch(left: Int, right: Int, top: Int, bottom: Int): NinePatch {
        return NinePatch(TextureRegion(this), left, right, top, bottom)
    }

    private fun initTexture(name: String): Texture {
        val output = LocalAssets.getInstance().getTexture(name)
        if (output == null) {
            val error = IOException("Missing Texture")
            log.error("Texture $name failed to load", error)
            throw error
        }
        return output
    }
}
