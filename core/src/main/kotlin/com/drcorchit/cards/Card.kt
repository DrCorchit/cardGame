package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.IMAGE_H
import com.drcorchit.cards.Main.Companion.IMAGE_W
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.math.Compass
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File
import kotlin.math.min

class Card(
    val name: String,
    val power: Int,
    val cost: Int,
    val armor: Int,
    val tags: List<String>,
    val abilities: List<String>,
    val quote: String,
    val strategyTags: List<String>
) {
    val type = if (tags.contains("Instant")) {
        CardType.Instant
    } else if (tags.contains("Equipment")) {
        CardType.Equipment
    } else if (tags.contains("Emplacement")) {
        CardType.Emplacement
    } else if (power > 0) {
        CardType.Unit
    } else {
        throw Exception("Unknown card type: $this")
    }

    val motive = tags.firstNotNullOfOrNull {
        try {
            Motive.valueOf(it)
        } catch (e: Exception) {
            null
        }
    } ?: Motive.Neutral

    val rarity = tags.firstNotNullOfOrNull {
        try {
            Rarity.valueOf(it)
        } catch (e: Exception) {
            null
        }
    } ?: Rarity.Common

    val tagsText =
        tags.subtract(setOf("Common", "Rare", "Legendary", "Instant", "Equipment", "Emplacement"))
            .joinToString(", ")
    val abilityText = abilities
        .joinToString("\n") { it.trim() }
        .replace("_", " ")
        .replace("#", "\n • ")
    val styledAbilityText = abilityText
        .replace("fire land", "\u0010")
        .replace("water land", "\u0011")
        .replace("earth land", "\u0012")
        .replace("air land", "\u0013")
        .replace("light land", "\u0014")
        .replace("dark land", "\u0015")

    val keywords = abilities
        .flatMap { it.split(Regex("[ #]")) }
        .map { it.normalize() }
        .mapNotNull { Keyword.keywordsDictionary[it] }
        .distinct()
    //.sortedBy { it.id }

    val keywordText = keywords.joinToString("\n") { "${it.name}: ${it.description}" }

    var image: AnimatedSprite? = updateGraphic()

    constructor(json: JsonObject) : this(
        json["name"].asString,
        json["power"]?.asInt ?: 0,
        json["cost"].asInt,
        json["armor"]?.asInt ?: 0,
        json.getAsJsonArray("tags").map { it.asString },
        loadAbility(json),
        json["quote"].asString,
        json["strategy"]?.let { it.asJsonArray.map { ele -> ele.asString } } ?: listOf()
    )

    fun serialize(): JsonObject {
        val output = JsonObject()
        output.addProperty("name", name)
        if (power > 0) {
            output.addProperty("power", power)
        }
        output.addProperty("cost", cost)
        output.add("tags", tags.map { JsonPrimitive(it) }.toJsonArray())
        output.add("ability", abilities.map { JsonPrimitive(it) }.toJsonArray())
        output.addProperty("quote", quote)
        output.add("strategy", strategyTags.map { JsonPrimitive(it) }.toJsonArray())
        return output
    }

    companion object {
        @JvmStatic
        fun List<Card>.saveTo(file: String) {
            val output = this.joinToString("\n")
            File(file).writeText(output)
        }

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



        val textColor = Color.valueOf("#603000ff")
        val helpTextColor = Color.valueOf("#806040ff") //was 405060

        val stroke = Textures.brushStroke.asSprite().setOffset(Compass.CENTER)
        val tray = Textures.tray.asSprite().setOffset(Compass.SOUTH)
        val armorBack = Textures.armorBack.asSprite().setOffset(Compass.CENTER)
        val costBack = Textures.costBack.asSprite().setOffset(200f, 150f)
        val line = Textures.line.asSprite().setOffset(Compass.CENTER)
        val border = Textures.border.asSprite()

        val scale = W / tray.getFrames().width
        val trayHeight = tray.getFrames().height * scale
        val imageW = W
        val imageH = H + 10 - trayHeight
        val imageRatio = imageH / imageW

        val midWidth = IMAGE_W / 2f
        val midHeight = tray.getFrames().height - 22f

        val diamondW = 80f
        val diamondH = 2 * diamondW
        val diamondOffsetX = 80f + BORDER
        val diamondOffsetY = 120f - BORDER
        val starSize = 48f

        val costBackSize = 150f
        val costBackOffset = 80f
        val costX = W + BORDER - costBackOffset
        val costY = BORDER + costBackOffset

        val armorBackW = 80f
        val armorBackH = 100f
        val armorX = BORDER + costBackOffset
        val armorY = BORDER + costBackOffset

        val strokeY = trayHeight + BORDER - 80
        val strokeH = 120f
        val strokeMargin = 200f
        val strokeMaxW = W - 50f

        val abilityTextX = BORDER + 30f
        val abilityTextY = strokeY - 60
        val abilityTextW = W - 60f

        val lineY = 120f + BORDER
        val quoteTextX = midWidth
        val quoteTextY = (lineY + 20 + BORDER) / 2
        val quoteTextW = W - 320f

        val keywordTextX = abilityTextX
        val keywordTextY = lineY + 20
        val totalAbilityTextH = abilityTextY - keywordTextY
    }

    fun draw() {
        ScreenUtils.clear(Color.BLACK)
        //Draw card art
        val image = this.image
        if (image != null) {
            val sourceImageRatio = image.getFrames().ratio
            val destImageRatio = imageRatio
            val imageScale =
                if (sourceImageRatio > destImageRatio) {
                    imageW / image.getFrames().width
                } else {
                    imageH / image.getFrames().height
                }

            image.draw(batch, midWidth, H + BORDER - 10f, imageScale, imageScale, 0f)
        }
        Draw.drawRectangle(0f, 0f, IMAGE_W.toFloat(), BORDER, Color.BLACK)
        Draw.drawRectangle(0f, H + BORDER, IMAGE_W.toFloat(), IMAGE_H.toFloat(), Color.BLACK)
        Draw.drawRectangle(0f, 0f, BORDER, IMAGE_H.toFloat(), Color.BLACK)
        Draw.drawRectangle(W + BORDER, 0f, IMAGE_W.toFloat(), IMAGE_H.toFloat(), Color.BLACK)

        //Draw border and ability text tray

        tray.draw(batch, midWidth, BORDER, W, trayHeight)
        border.draw(batch, BORDER, BORDER, W, H)
        rarity.image.draw(batch, BORDER, BORDER, W, H)

        //Power diamond
        val diamond = motive.image
        diamond.draw(batch, diamondOffsetX, H - diamondOffsetY, diamondW, diamondH)
        if (power == 0) {
            type.image!!.draw(batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
        } else if (type == CardType.Unit) {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY + 5,
                Fonts.numberFont,
                power.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
        } else {
            val y = H - diamondOffsetY
            Draw.drawText(
                diamondOffsetX,
                y + 28,
                Fonts.numberFontXS,
                power.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
            type.image!!.draw(batch, diamondOffsetX + 3, y - 18, starSize, starSize)
        }

        //Armor
        if (armor > 0) {
            armorBack.draw(batch, armorX, armorY, armorBackW, armorBackH)
            Draw.drawText(
                armorX, armorY + 2,
                Fonts.numberFontSmall,
                armor.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
        }

        //Cost
        costBack.draw(batch, costX, costY, costBackSize, costBackSize)
        Draw.drawText(
            costX, costY + 2,
            Fonts.numberFontSmall,
            cost.toString(),
            100f,
            Compass.CENTER,
            Color.WHITE
        )

        //Card Name
        val dims = Draw.calculateDimensions(Fonts.nameFont, name, W - 50f)
        stroke.blend = motive.secondaryColor
        val strokeW = min(dims.first + strokeMargin, strokeMaxW)
        stroke.draw(batch, midWidth, strokeY, strokeW, strokeH)
        Draw.drawText(
            midWidth,
            strokeY + 27,
            Fonts.nameFont,
            name,
            W - 50f,
            Compass.CENTER,
            motive.color
        )

        //Tags
        Draw.drawText(
            midWidth,
            strokeY - 27,
            Fonts.tagFont,
            tagsText,
            1000f,
            Compass.CENTER,
            motive.color
        )

        //Ability Text
        val text = styledAbilityText
        Draw.drawText(
            abilityTextX,
            abilityTextY,
            Fonts.abilityFont,
            text,
            abilityTextW,
            Compass.SOUTHEAST,
            Color.WHITE
        )

        //Keyword text
        Draw.drawText(
            keywordTextX,
            keywordTextY,
            Fonts.keywordFont,
            keywordText,
            abilityTextW,
            Compass.NORTHEAST,
            helpTextColor
        )

        line.draw(batch, midWidth, lineY, 3f, 1f, 0f)

        //Quote text
        Draw.drawText(
            quoteTextX,
            quoteTextY,
            Fonts.quoteFont,
            quote,
            quoteTextW,
            Compass.CENTER,
            textColor
        )
    }

    override fun toString(): String {
        val statsStr = if (power > 0) "$power/${cost}p" else "${cost}p"
        val tagsStr = tags.joinToString(", ")
        val abilitiesStr = abilities.joinToString("; ")
        return "$name: $statsStr [$tagsStr] [$abilitiesStr] [$quote]"
    }

    fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()
        val base = "assets/images/cards/used/${motive.name}"
        val png = "$base/$normalized.png"
        val jpg = "$base/$normalized.jpg"
        val texture = if (File(png).exists()) Texture(png)
        else if (File(jpg).exists()) Texture(jpg)
        else null

        if (texture == null) {
            //println("Could not load $png or $jpg")
        } else {
            image = texture.asSprite().setOffset(Compass.NORTH)
        }
        return image
    }
}
