package com.drcorchit.cards.space

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
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

open class SpaceCard(
    override val name: String,
    val cost: Int,
    val power: Int,
    val type: Type,
    val abilities: List<String>,
    val count: Int
) : Drawable {
    var image: AnimatedSprite? = updateGraphic()
    val costText = "${cost}0K"
    val powerText = "$power\u0010"
    val abilityText = abilities
        .joinToString("\n") { it.trim() }
        .replace("_", " ")
        .replace("#", "\n • ")

    override val outputLocation =
        "output/images/temporary/space_cards/${type.name.normalize()}/${name.normalize()}.png"

    override val multiplicity = 1

    enum class Type(file: String?, val text: String, val aiHelp: String) {
        Computer("computer", "Computer Module", "mainframe computer"),
        Crew("crew", "Crew", "crewmember"),
        Defense("defense", "Defense Module", "defensive module for a spaceship"),
        Disaster(null, "Disaster Card", "disaster happening to a spaceship"),
        Engine("engine", "Engine Module", "a spaceship engine"),
        Life_Support("life_support", "Life Support Module", "a life support module for a spaceship"),
        Special("special", "Special Card", "anything"),
        Weapon("weapon", "Weapon Module", "weapon fire from a spaceship");

        val file = file?.let { File("assets/txt/space_cards/$it.txt") }
    }

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)

        card.draw(batch, BORDER, BORDER, W, H)
        textArea.draw(batch, BORDER, BORDER, W, H)
        //Draw card art
        //this.image?.draw(batch, imageX, imageY)
        image?.let { Draw.drawCardImage(it, imageX, imageY, imageW, imageH) }
        artBorder.draw(batch, imageX, imageY)

        titleBar.draw(batch, nameTextX, nameTextY - 20)
        Draw.drawText(nameTextX, nameTextY, Fonts.nameFont2, name, W, Compass.CENTER, nameTextColor)

        //typeBar.draw(batch, labelTextX, labelTextY)
        Draw.drawText(
            typeTextX,
            typeTextY,
            Fonts.abilityFont2,
            type.text,
            W,
            Compass.CENTER,
            nameTextColor
        )

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
        val base = "assets/images/space_cards/cards/ChatGPT/Realistic/${type.name.lowercase()}"
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
        val card = Textures.cardWithText.asSprite()
        val titleBar = Textures.titleBar.asSprite().setOffset(Compass.CENTER)
        val scoreArea = Textures.scoreArea.asSprite().setOffset(Compass.CENTER)
        val artBorder = Textures.artBorder.asSprite().setOffset(Compass.CENTER)
        val textArea = Textures.textArea.asSprite()

        val nameTextX = BORDER + (W / 2f)
        val nameTextY = BORDER + H - 75f
        val nameTextColor = Color.valueOf("40c0ff")
        val typeTextX = nameTextX
        val typeTextY = nameTextY - 40

        //image from 100-720x, 500-920y
        val imageX = 410f
        val imageY = 710f
        val imageW = artBorder.getFrames().width.toFloat()
        val imageH = artBorder.getFrames().height.toFloat()

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
