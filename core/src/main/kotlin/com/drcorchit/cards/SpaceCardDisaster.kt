package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.*
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass
import java.io.File

class SpaceCardDisaster(val text: String, val file: String) : Drawable {
    var image: AnimatedSprite? = null

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)
        disasterCutaway.draw(Draw.batch, BORDER, BORDER, W, H)

        image?.draw(Draw.batch, centerX, centerY)

        textBack.draw(Draw.batch, textX, textY)
        Draw.drawText(textX, textY, Fonts.cardTypeFont, text, W, Compass.CENTER, Color.RED)
    }

    override fun updateGraphic(): AnimatedSprite? {
        val png = "assets/images/space_cards/card_art/$file"
        val texture = if (File(png).exists()) Texture(png) else null

        if (texture == null) {
            //println("Could not load $png or $jpg")
        } else {
            image = texture.asSprite().setOffset(Compass.CENTER)
        }
        return image
    }

    companion object {
        val disasters = listOf(
            "Damaged" to "damaged.png",
            "Hacked" to "hacked.png",
            "EMP" to "emp.png"
        )

        val textBack = Textures.titleBar.asSprite().setOffset(Compass.CENTER)
        val disasterCutaway = Textures.cardWithCutaway.asSprite()
        val centerX = BORDER + W / 2
        val centerY = BORDER + H / 2
        val textX = centerX
        val textY = BORDER + 105
    }
}
