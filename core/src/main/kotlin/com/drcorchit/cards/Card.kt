package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils.parseFromFile
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
    val tags: List<String>,
    val abilities: List<String>,
    val quote: String
) {
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

    val tagsText = tags.joinToString(", ")
    val abilityText = abilities.joinToString("\n")
    val styledAbilityText = abilityText
        .replace("fire mana", "\u0010")
        .replace("water mana", "\u0011")
        .replace("earth mana", "\u0012")
        .replace("air mana", "\u0013")
        .replace("light mana", "\u0014")
        .replace("dark mana", "\u0015")

    val image = (
        LocalAssets.getInstance()
            .getTexture(name.normalize() + ".png")
            ?: LocalAssets.getInstance()
                .getTexture(name.normalize() + ".jpg"))
        ?.asSprite()
        ?.setOffset(Compass.NORTH)

    init {
        if (image == null) {
            //println("No card art found for: $name")
        }
    }

    constructor(json: JsonObject) : this(
        json["name"].asString,
        json["power"]?.asInt ?: 0,
        json["cost"].asInt,
        json.getAsJsonArray("tags").map { it.asString },
        loadAbility(json),
        json["quote"].asString
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
        return output
    }

    companion object {

        val nameRegex = "(?<name>.*)"
        val statsRegex = "((?<power>\\d+)/)?(?<cost>\\d+)p"
        val tagsRegex = "(?<tags>\\.+(,\\.+)*)"
        val abilityRegex = "(?<abilities>\\.+(;\\.+)*)"
        val quoteRegex = "(?<quote>.*)"

        val regex = Regex("$nameRegex $statsRegex [$tagsRegex] [$abilityRegex] [$quoteRegex]")

        @JvmStatic
        fun parse(str: String): Card {
            val match = regex.matchEntire(str)!!.groups
            val name = match["name"]!!.value
            val power = match["power"]?.value?.toInt() ?: 0
            val cost = match["cost"]!!.value.toInt()
            val tags = match["tags"]!!.value.split(",").map { it.trim() }
            val abilities = match["abilities"]!!.value.split(",").map { it.trim() }
            val quote = match["quote"]!!.value

            return Card(name, power, cost, tags, abilities, quote)
        }

        @JvmStatic
        fun List<Card>.saveTo(file: String) {
            val output = this.joinToString("\n")
            File(file).writeText(output)
        }

        fun readFrom(file: String): List<Card> {
            return File(file).readLines().map { parse(it) }
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

        val cards by lazy {
            //readFrom("assets/cards.txt")
            //*
            parseFromFile("assets/json/cards.json")!!
                .first.asJsonArray!!
                .map { Card(it.asJsonObject) }
            //*/
        }

        init {
            val cardsByMotive = cards.groupBy { card -> card.motive }
                .mapValues { it.value.groupBy { card -> card.rarity } }

            cardsByMotive.entries
                .forEach { entry ->
                    entry.value.forEach {
                        println("${entry.key} ${it.key}: ${it.value.size}")
                    }
                }

            cardsByMotive.mapValues { it.value.values.flatten() }
                .values.map { list -> list.sortedBy { it.cost } }
                .forEach { it.saveTo("assets/cards/${it.first().motive.name}.txt") }
        }

        val textColor = Color.valueOf("#603000ff")
        val trayColor = Color.valueOf("#00000080")

        val stroke = Textures.brushStroke.asSprite().setOffset(Compass.CENTER)
        val star = Textures.star.asSprite().setOffset(Compass.CENTER)
        val tray = Textures.tray.asSprite().setOffset(Compass.SOUTH)
        val costBack = Textures.costBack.asSprite().setOffset(200f, 150f)
        val line = Textures.line.asSprite().setOffset(Compass.CENTER)
        val border = Textures.border.asSprite()

        val midWidth = W / 2f
        val midHeight = tray.getFrames().height - 20f
        val cardRatio = H * 1f / W

        val diamondOffsetX = 80f
        val diamondOffsetY = 120f
        val diamondW = 100f
        val diamondH = 2 * diamondW
        val starSize = 60f

        val costBackSize = 150f
        val costBackOffset = 80f
        val costX = W - costBackOffset
        val costY = costBackOffset

        val nameY = midHeight - 50f
        val tagsY = nameY - 65f
        val strokeH = 140f
        val strokeMargin = 200f
        val strokeMaxW = W - 50f
        val strokeY = nameY - 32f

        val abilityBufferX = 20f
        val abilityBufferY = 20f
        val abilityBufferW = W - 2 * abilityBufferX
        val abilityBufferH = midHeight - 180f
        val abilityBufferMargin = 20f
        val abilityTextW = abilityBufferW - 2 * abilityBufferMargin

        val lineY = 150f
        val quoteTextX = midWidth
        val quoteTextY = 80f
        val quoteTextW = W - 320f
    }

    fun draw() {
        ScreenUtils.clear(Color.BLACK)

        //Draw card art
        if (image != null) {
            val imageRatio = image.getFrames().ratio
            val scale =
                if (imageRatio > cardRatio / 2f) {
                    W.toFloat() / image.getFrames().width
                } else {
                    (H - midHeight) / image.getFrames().height
                }

            image.draw(Draw.batch, midWidth, H.toFloat(), scale, scale, 0f)
        }

        //Draw border and ability text tray
        tray.draw(Draw.batch, midWidth, 0f)
        border.draw(Draw.batch, 0f, 0f)
        rarity.image.draw(Draw.batch, 0f, 0f, W.toFloat(), H.toFloat())

        //Power diamond
        val diamond = motive.image
        diamond.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY, diamondW, diamondH)
        if (power == 0) {
            star.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
        } else {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY,
                Fonts.numberFont,
                power.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
        }

        //Cost
        costBack.draw(Draw.batch, costX, costY, costBackSize, costBackSize)
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
        stroke.draw(Draw.batch, midWidth, strokeY, strokeW, strokeH)
        Draw.drawText(midWidth, nameY, Fonts.nameFont, name, W - 50f, Compass.CENTER, motive.color)

        //Tags
        val textX = abilityBufferX + abilityBufferMargin
        Draw.drawText(
            midWidth,
            tagsY,
            Fonts.tagFont,
            tagsText,
            1000f,
            Compass.CENTER,
            motive.color
        )

        //line.draw(Draw.batch, midWidth, abilityBufferY + abilityBufferH, 3f, 1f, 0f)


        //Ability Text
        val text = styledAbilityText
        val abilityTextY = abilityBufferY + abilityBufferH - abilityBufferMargin
        Draw.drawText(
            textX,
            abilityTextY,
            Fonts.textFont,
            text,
            abilityTextW,
            Compass.SOUTHEAST,
            Color.WHITE
        )

        line.draw(Draw.batch, midWidth, 2 * quoteTextY, 3f, 1f, 0f)

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
}
