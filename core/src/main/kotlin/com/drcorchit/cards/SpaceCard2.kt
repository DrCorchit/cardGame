package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.cards.graphics.Fonts
import com.drcorchit.cards.graphics.Fonts.addTexture
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass
import java.io.File

class SpaceCard2(
    name: String,
    cost: Int,
    power: Int,
    type: Type,
    abilities: List<String>,
    count: Int
) :
    SpaceCard(name, cost, power, type, abilities, count) {

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)

        //Draw card art
        this.image?.draw(batch, imageX, imageY)
        card.draw(batch, BORDER, BORDER, W, H)
        border.draw(batch, 0f, 0f)

        Draw.drawText(
            nameTextX, nameTextY,
            nameTextFont, name, W, Compass.CENTER, nameTextColor
        )

        Draw.drawText(
            typeTextX, typeTextY,
            typeTextFont, type.text, W, Compass.CENTER, typeTextColor
        )

        Draw.drawText(
            abilityTextX, abilityTextY,
            abilityTextFont, abilityText, abilityTextW, Compass.SOUTHEAST, abilityTextColor
        )

        if (power > 0) {
            numberBox.draw(batch, powerTextX, powerTextY)
            Draw.drawText(
                powerTextX, powerTextY,
                costTextFont, powerText, W, Compass.CENTER, costTextColor
            )
        }

        numberBox.draw(batch, costTextX, costTextY)
        Draw.drawText(
            costTextX, costTextY,
            costTextFont, costText, W, Compass.CENTER, costTextColor
        )
    }

    override fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()
        val base = "assets/images/space_cards/card_art/${type.name.lowercase()}"
        val png = "$base/$normalized.png"
        val jpg = "$base/$normalized.jpg"
        val texture = if (File(png).exists()) Texture(png)
        else if (File(jpg).exists()) Texture(jpg)
        else null

        if (texture == null) {
            println("Could not load $png or $jpg")
        } else {
            image = texture.asSprite().setOffset(Compass.CENTER)
        }
        return image
    }

    companion object {
        val card = Textures.card2SmallWindow.asSprite()
        val border = Textures.border2.asSprite()
        val numberBox = Textures.numberBox.asSprite().setOffset(Compass.CENTER)

        val nameTextX = BORDER + (W / 2f)
        val nameTextY = BORDER + H - 120f
        val nameTextFont = Fonts.initFontSizeAndStroke("conthrax.otf", 36)
        val nameTextColor = Color.WHITE //Color.valueOf("40c0ff")

        val typeTextX = nameTextX
        val typeTextY = nameTextY - 40
        val typeTextFont = Fonts.initFontSize("exo_medium.ttf", 28)
        val typeTextColor = nameTextColor

        //image from 100-720x, 500-920y
        val imageX = nameTextX
        val imageY = BORDER + 640f

        val margin = 100f
        val abilityTextX = BORDER + margin
        val abilityTextY = BORDER + 370f
        val abilityTextW = W - 2 * margin
        val abilityTextFont = Fonts.initFontSize("exo_medium.ttf", 28)
        val abilityTextColor = Color.WHITE

        val margin2 = 165
        val powerTextX = BORDER + margin2
        val powerTextY = BORDER + 110
        val powerTextFont = Fonts.initFontSize("conthrax.otf", 48)
        val powerTextColor = Color.YELLOW

        val costTextX = BORDER + W - margin2
        val costTextY = powerTextY
        val costTextFont = powerTextFont
        val costTextColor = powerTextColor

        init {
            powerTextFont.addTexture(Textures.power, '\u0010')
        }
    }
}
