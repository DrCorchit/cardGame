package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass
import com.google.gson.JsonObject

class Card(
    val name: String,
    val type: String,
    val rarity: String,
    val power: Int,
    val cost: Int,
    val tags: List<String>,
    val abilities: List<String>,
    val quote: String
) {
    val motive = tags.firstNotNullOfOrNull {
        try {
            Motives.valueOf(it)
        } catch (e: Exception) {
            null
        }
    } ?: Motives.Neutral

    val tagsText = tags.joinToString(", ")
    val abilityText = abilities.joinToString("\n")
    val image = LocalAssets.getInstance()
        .getTexture(name.normalize() + ".png")
        ?.asSprite()
        ?.setOffset(Compass.CENTER)

    constructor(json: JsonObject) : this(
        json["name"].asString,
        json["type"].asString,
        json["rarity"].asString,
        json["power"]?.asInt ?: 0,
        json["cost"].asInt,
        json.getAsJsonArray("tags").map { it.asString },
        loadAbility(json),
        json["quote"].asString
    )

    companion object {
        @JvmStatic
        fun loadAbility(json: JsonObject): List<String> {
            val ability = json["ability"]

            return if (ability == null) {
                listOf()
            } else if (ability.isJsonArray) {
                ability.asJsonArray.map { it.asString }
            } else if (ability.isJsonPrimitive) {
                listOf(ability.asString)
            } else {
                throw IllegalArgumentException("Cannot deserialize ability text: $ability")
            }
        }

        val diamondOffsetX = 50f
        val diamondOffsetY = 100f

        val starSize = 70f

        val abilityBufferX = 20f
        val abilityBufferY = 20f
        val abilityBufferW = W - 120f
        val abilityBufferH = 350f
        val abilityBufferMargin = 10f
        val abilityTextW = abilityBufferW - 2 * abilityBufferMargin

        val imageX = W / 2f
        val imageY = H - 240f
    }

    fun draw(minimal: Boolean) {
        ScreenUtils.clear(1f, 1f, 1f, 1f)

        val textColor = if (minimal) Color.BLACK else Color.WHITE

        if (!minimal && image != null) {
            val scale = W.toFloat() / image.getFrames().width
            image.draw(Draw.batch, imageX, imageY, scale, scale, 0f)
        }

        if (minimal) {
            Draw.drawLine(
                abilityBufferX,
                abilityBufferY + abilityBufferH,
                abilityBufferX + abilityBufferW,
                abilityBufferY + abilityBufferH,
                1f,
                Color.BLACK,
            )

            //Draw.drawRectangle(0, 0, W, H, Color.BLACK)
        } else {
            val slate = Textures.slate.asSprite()
            slate.draw(Draw.batch, 0f, 0f)
            val border = Textures.slateBorder.asSprite()
            border.draw(Draw.batch, 0f, 0f)
        }

        //Power/provision diamonds
        if (!minimal) {
            val diamond = Textures.diamond.asSprite().setOffset(Compass.CENTER)
            diamond.blend = motive.color
            diamond.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY)
            diamond.draw(Draw.batch, W - diamondOffsetX, diamondOffsetY)
        }

        val diamondBorder = Textures.diamondBorder.asSprite().setOffset(Compass.CENTER)
        diamondBorder.blend = motive.color
        diamondBorder.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY)
        diamondBorder.draw(Draw.batch, W - diamondOffsetX, diamondOffsetY)

        if (!minimal) {
            Draw.drawRectangle(
                abilityBufferX,
                abilityBufferY,
                abilityBufferW,
                abilityBufferH,
                Compass.NORTHEAST,
                Color.BLACK
            )
        }

        Draw.drawText(20f, 440f, Fonts.nameFont, name, W - 50f, Compass.EAST, textColor)
        if (power == 0) {
            val star = Textures.star.asSprite().setOffset(Compass.CENTER)
            star.blend = if (minimal) Color.BLACK else Color.WHITE
            star.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
        } else {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY,
                Fonts.numberFont,
                power.toString(),
                100f,
                Compass.CENTER,
                textColor
            )
        }

        Draw.drawText(
            W - diamondOffsetX,
            diamondOffsetY,
            Fonts.numberFont,
            cost.toString(),
            100f,
            Compass.CENTER,
            textColor
        )

        val textX = abilityBufferX + abilityBufferMargin
        Draw.drawText(textX, 390f, Fonts.tagFont, tagsText, 1000f, Compass.EAST, textColor)

        //Ability Text
        val abilityTextY = abilityBufferY + abilityBufferH - abilityBufferMargin
        Draw.drawText(
            textX,
            abilityTextY,
            Fonts.textFont,
            abilityText,
            abilityTextW,
            Compass.SOUTHEAST,
            textColor
        )
        val quoteTextY = abilityBufferY + abilityBufferMargin
        val quoteTextColor = if (minimal) Color.GRAY else Color.LIGHT_GRAY
        Draw.drawText(
            textX,
            quoteTextY,
            Fonts.quoteFont,
            quote,
            abilityTextW,
            Compass.NORTHEAST,
            quoteTextColor
        )
    }

    override fun toString(): String {
        return "$name $power/${cost}p $abilityText $quote"
    }
}
