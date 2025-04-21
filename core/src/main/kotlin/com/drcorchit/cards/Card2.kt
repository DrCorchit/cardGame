package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.*
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass
import java.io.File

class Card2(
    val name: String,
    val cost: Int,
    val power: Int,
    val type: Type,
    val abilities: List<String>,
    val count: Int
) : Drawable {
    var image: AnimatedSprite? = updateGraphic()
    val costText = "${cost}K"
    val powerText = "$power\u0010"
    val abilityText = abilities
        .joinToString("\n") { it.trim() }
        .replace("_", " ")
        .replace("#", "\n • ")

    enum class Type(file: String) {
        Computer("computers"),
        Crew("crew"),
        Defense("defense"),
        Engine("engines"),
        LifeSupport("life_support"),
        Special("specials"),
        Weapon("weapons");

        val file = File("assets/txt/space_cards/$file.txt")
    }

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)

        card.draw(batch, BORDER, BORDER, W, H)
        //Draw card art
        this.image?.draw(batch, imageX, imageY)
        artBorder.draw(batch, imageX, imageY)

        titleBar.draw(batch, nameTextX, nameTextY)
        Draw.drawText(nameTextX, nameTextY, Fonts.nameFont2, name, W, Compass.CENTER, nameTextColor)

        Draw.drawText(
            abilityTextX,
            abilityTextY,
            Fonts.abilityFont2,
            abilityText,
            abilityTextW,
            Compass.SOUTHEAST,
            abilityTextColor
        )

        if (power > 0) {
            scoreArea.draw(batch, powerTextX, powerTextY)
            Draw.drawText(
                powerTextX,
                powerTextY,
                Fonts.numberFont2,
                powerText,
                W,
                Compass.CENTER,
                Color.YELLOW
            )
        }

        scoreArea.draw(batch, costTextX, costTextY)
        Draw.drawText(
            costTextX,
            costTextY,
            Fonts.numberFont2,
            costText,
            W,
            Compass.CENTER,
            Color.YELLOW
        )

    }

    override fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()
        val base = "assets/images/space_cards/card_art"
        val png = "$base/$normalized.png"
        val jpg = "$base/$normalized.jpg"
        val texture = if (File(png).exists()) Texture(png)
        else if (File(jpg).exists()) Texture(jpg)
        else null

        if (texture == null) {
            //println("Could not load $png or $jpg")
        } else {
            val w = Textures.artBorder.width
            val h = Textures.artBorder.height
            val newTex = Draw.textureRegionToTexture(TextureRegion(texture, w, h))
            image = newTex.asSprite().setOffset(Compass.CENTER)
        }
        return image
    }

    companion object {
        val card = Textures.card.asSprite()
        val titleBar = Textures.titleBar.asSprite().setOffset(Compass.CENTER)
        val scoreArea = Textures.scoreArea.asSprite().setOffset(Compass.CENTER)
        val artBorder = Textures.artBorder.asSprite().setOffset(Compass.CENTER)

        val nameTextX = BORDER + (W / 2f)
        val nameTextY = BORDER + H - 80f
        val nameTextColor = Color.valueOf("40c0ff")

        //image from 100-720x, 500-920y
        val imageX = 410f
        val imageY = 710f

        val margin = 80f
        val abilityTextX = BORDER + margin
        val abilityTextY = BORDER + 370f
        val abilityTextW = W - 2 * margin
        val abilityTextColor = nameTextColor

        val margin2 = 165
        val powerTextX = BORDER + margin2
        val powerTextY = BORDER + 130

        val costTextX = BORDER + W - margin2
        val costTextY = powerTextY
    }
}
