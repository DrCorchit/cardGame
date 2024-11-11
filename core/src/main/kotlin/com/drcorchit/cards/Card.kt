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
    val power: Int,
    val cost: Int,
    val tags: List<String>,
    val abilities: List<String>,
    val quote: String
) {
    //val cardType
    //val rarity

    val motive = tags.firstNotNullOfOrNull {
        try {
            Motives.valueOf(it)
        } catch (e: Exception) {
            null
        }
    } ?: Motives.Neutral

    val tagsText = tags.joinToString(", ")
    val abilityText = abilities.joinToString("\n") {
        it.replace("fire mana", "\u0010")
            .replace("water mana", "\u0011")
            .replace("earth mana", "\u0012")
            .replace("air mana", "\u0013")
            .replace("light mana", "\u0014")
            .replace("dark mana", "\u0015")

    }
    val image = (
        LocalAssets.getInstance()
            .getTexture(name.normalize() + ".png")
            ?: LocalAssets.getInstance()
                .getTexture(name.normalize() + ".jpg"))
        ?.asSprite()
        ?.setOffset(Compass.NORTH)

    constructor(json: JsonObject) : this(
        json["name"].asString,
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

        val midWidth = W / 2f
        val midHeight = H / 2f
        val cardRatio = H * 1f / W

        val diamondOffsetX = 50f
        val diamondOffsetY = 100f
        val starSize = 70f

        val nameX = 20f
        val nameY = midHeight - 40f

        val abilityBufferX = 20f
        val abilityBufferY = 20f
        val abilityBufferW = W - 120f
        val abilityBufferH = midHeight - 130f
        val abilityBufferMargin = 10f
        val abilityTextW = abilityBufferW - 2 * abilityBufferMargin
    }

    fun draw(minimal: Boolean) {
        ScreenUtils.clear(1f, 1f, 1f, 1f)

        val textColor = if (minimal) Color.BLACK else Color.WHITE

        if (!minimal && image != null) {
            val imageRatio = image.getFrames().ratio
            val scale =
                if (imageRatio > cardRatio / 2f) {
                    W.toFloat() / image.getFrames().width
                    //H.toFloat() / image.getFrames().height
                } else {
                    //W.toFloat() / image.getFrames().width
                    (H / 2f) / image.getFrames().height
                }

            image.draw(Draw.batch, midWidth, H.toFloat(), scale, scale, 0f)
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
            val background = Textures.slate.asSprite().setOffset(Compass.NORTH)
            val border = Textures.slateBorder.asSprite().setOffset(Compass.NORTH)

            val bgRatio = background.getFrames().ratio
            val scale = if (bgRatio > cardRatio) {
                W.toFloat() / background.getFrames().width
            } else {
                H.toFloat() / background.getFrames().height
            }

            background.draw(Draw.batch, midWidth, midHeight, scale, scale, 0f)
            border.draw(Draw.batch, midWidth, midHeight, scale, scale, 0f)
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

        Draw.drawText(nameX, nameY, Fonts.nameFont, name, W - 50f, Compass.EAST, textColor)
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
        Draw.drawText(textX, nameY - 50f, Fonts.tagFont, tagsText, 1000f, Compass.EAST, textColor)

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
