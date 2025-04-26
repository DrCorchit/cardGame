package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.*
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass

class SpaceCardBack(override val name: String) : Drawable {
    override val outputLocation = "output/images/full/cards/large/${name.normalize()}.png"

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)
        raw.draw(Draw.batch, BORDER, BORDER, W, H)
        textBack.draw(Draw.batch, textX, textY)
        Draw.drawText(textX, textY, Fonts.cardTypeFont, name, W, Compass.CENTER, Color.RED)
    }

    override fun updateGraphic(): AnimatedSprite? {
        //No-op
        return null
    }

    companion object {

        val texts =
            listOf(
                "Life Support Module",
                "Engine Module",
                "Computer Module",
                "Weapon Module",
                "Defense Module",
                "Crewmember",
                "Special Card",
                "Disaster Card"
            )

        val textBack = Textures.titleBar.asSprite().setOffset(Compass.CENTER)
        val raw = Textures.cardWithInset.asSprite()
        val textX = BORDER + W / 2
        val textY = BORDER + H / 2
    }
}
