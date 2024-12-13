package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Draw.batch
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

    var image: AnimatedSprite? = updateGraphic()

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
        val powerRegex = "(?<power>\\d+)"
        val costRegex = "(?<cost>\\d+)"
        val armorRegex = "(?<armor>\\d+)a "
        val statsRegex = "($armorRegex)?($powerRegex\\/)?${costRegex}p"
        val tagsRegex = "(?<tags>.*?)"
        val abilityRegex = "(?<abilities>.*?)"
        val quoteRegex = "(?<quote>.*?)"

        val regex =
            Regex("$nameRegex: $statsRegex \\[$tagsRegex] \\[$abilityRegex] \\[$quoteRegex]")

        @JvmStatic
        fun parse(str: String): Card? {
            if (str.isBlank()) {
                return null
            }

            try {
                val match = regex.matchEntire(str)!!.groups
                val name = match["name"]!!.value
                val power = match["power"]?.value?.toInt() ?: 0
                val cost = match["cost"]!!.value.toInt()
                val tags = match["tags"]!!.value.split(",").map { it.trim() }
                val abilities = match["abilities"]!!.value.split(";").map { it.trim() }
                val quote = match["quote"]!!.value

                return Card(name, power, cost, tags, abilities, quote)
            } catch (e: Exception) {
                println("Error parsing line: $str")
                return null
            }
        }

        @JvmStatic
        fun List<Card>.saveTo(file: String) {
            val output = this.joinToString("\n")
            File(file).writeText(output)
        }

        fun readFrom(filename: String): List<Card> {
            return File(filename).listFiles()!!
                .flatMap { file -> file.readLines().mapNotNull { parse(it) } }
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
            readFrom("assets/cards")
            /*
            parseFromFile("assets/json/cards.json")!!
                .first.asJsonArray!!
                .map { Card(it.asJsonObject) }
            */
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

            val cardsByRarity = cards.groupBy { it.rarity }
            cardsByRarity.forEach { (rarity, cards) -> println("$rarity ${cards.size}") }
            println("Total cards: ${cards.size}")

            fun factionCount(motive: Motive): Int {
                val cards = cardsByMotive[motive]!!

                fun rarityCount(rarity: Rarity): Int {
                    val count = cards[rarity]?.size ?: 0
                    return if (rarity == Rarity.Common) count * 2 else count
                }
                return Rarity.entries.sumOf { rarityCount(it) }
            }

            val count = Motive.entries.sumOf { factionCount(it) } + factionCount(Motive.Neutral)
            println("Total projected cards: $count")

            /*
            cardsByMotive.mapValues { it.value.values.flatten() }
                .values.map { list -> list.sortedBy { it.cost } }
                .forEach { it.saveTo("assets/cards/${it.first().motive.name}.txt") }

             */
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
        val diamondOffsetY = 135f
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

        val borderY = 20
        val lineY = 120f
        val quoteTextX = midWidth
        val quoteTextY = (lineY + borderY) / 2
        val quoteTextW = W - 320f
    }

    fun draw() {
        ScreenUtils.clear(Color.BLACK)

        //Draw card art
        val image = this.image
        if (image != null) {
            val sourceImageRatio = image.getFrames().ratio
            val destImageRatio = (H + 20 - midHeight) / W
            val scale =
                if (sourceImageRatio > destImageRatio) {
                    W.toFloat() / image.getFrames().width
                } else {
                    (H + 20 - midHeight) / image.getFrames().height
                }

            image.draw(batch, midWidth, H.toFloat(), scale, scale, 0f)
        }

        //Draw border and ability text tray
        tray.draw(batch, midWidth, 0f)
        border.draw(batch, 0f, 0f)
        rarity.image.draw(batch, 0f, 0f, W.toFloat(), H.toFloat())

        //Power diamond
        val diamond = motive.image
        diamond.draw(batch, diamondOffsetX, H - diamondOffsetY, diamondW, diamondH)
        if (power == 0) {
            star.draw(batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
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

        //line.draw(batch, midWidth, abilityBufferY + abilityBufferH, 3f, 1f, 0f)


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
            println("Could not load $png or $jpg")
        } else {
            image = texture.asSprite().setOffset(Compass.NORTH)
        }
        return image
    }
}
