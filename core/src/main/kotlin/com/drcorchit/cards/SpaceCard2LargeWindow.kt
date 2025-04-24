package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.SpaceCard2.Companion.border
import com.drcorchit.cards.graphics.*
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass
import java.io.File

class SpaceCard2LargeWindow(val text: String, val file: String, val textColor: Color) : Drawable {
    var image: AnimatedSprite? = updateGraphic()

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)
        this.image?.draw(batch, imageX, imageY)
        largeWindow.draw(batch, BORDER, BORDER, W, H)
        border.draw(batch, 0f, 0f)
        Draw.drawText(textX, textY, font, text, W, Compass.CENTER, textColor)
    }

    override fun updateGraphic(): AnimatedSprite? {
        val png = "assets/images/space_cards/card_art/$file"
        val texture = if (File(png).exists()) Texture(png) else null

        if (texture == null) {
            println("Could not load $png")
        } else {
            image = texture.asSprite().setOffset(Compass.CENTER)
        }
        return image
    }

    companion object {
        val disasterNames = listOf(
            "Damaged",
            "Hacked",
            "EMP"
        )

        val largeWindow = Textures.card2LargeWindow.asSprite()
        val centerX = BORDER + W / 2
        val imageX = centerX + 7
        val imageY = BORDER + H / 2 - 67
        val textX = centerX
        val textY = BORDER + H - 140f
        val font = SpaceCard2.nameTextFont
        val color = Color.RED

        val cardbacks by lazy {
            SpaceCard.Type.entries.map {
                SpaceCard2LargeWindow(it.text, "${it.name.lowercase()}.png", Color.WHITE)
            }
        }
        val disasters by lazy {
            disasterNames.map {
                SpaceCard2LargeWindow(it, "${it.lowercase()}.png", Color.RED)
            }
        }
    }
}
