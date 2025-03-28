package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Main.Companion.H
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
        val nameRegex = "(?<name>.*)"
        val powerRegex = "(?<power>\\d+)"
        val costRegex = "(?<cost>\\d+)"
        val armorRegex = "(?<armor>\\d+)a"
        val statsRegex = "($armorRegex)? *($powerRegex\\/)?${costRegex}p"
        val tagsRegex = "(?<tags>.*?)"
        val abilityRegex = "(?<abilities>.*?)"
        val quoteRegex = "(?<quote>.*?)"
        val strategyRegex = "(?<strategy>.*?)"

        val regex =
            Regex("$nameRegex: *$statsRegex *\\[$tagsRegex] *\\[$abilityRegex] *\\[$quoteRegex]( *\\[$strategyRegex])?")

        @JvmStatic
        fun parse(str: String): Card? {
            if (str.isBlank() || str.startsWith("#")) {
                return null
            }

            try {
                val match = regex.matchEntire(str)!!.groups
                val name = match["name"]!!.value
                val armor = match["armor"]?.value?.toInt() ?: 0
                val power = match["power"]?.value?.toInt() ?: 0
                val cost = match["cost"]!!.value.toInt()
                val tags = match["tags"]!!.value.split(",").map { it.trim() }
                val abilities = match["abilities"]!!.value
                    .replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
                    .replace("\"", "”")
                    .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
                    .replace("'", "’")
                    .split(";")
                val quote = match["quote"]!!.value
                    .replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
                    .replace("\"", "”")
                    .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
                    .replace("'", "’")
                val strategyTags = match["strategy"]
                    ?.let { it.value.split(",").map { tag -> tag.trim() } }
                    ?: listOf()

                return Card(name, power, cost, armor, tags, abilities, quote, strategyTags)
            } catch (e: Exception) {
                println("Error parsing line: $str")
                e.printStackTrace()
                return null
            }
        }

        @JvmStatic
        fun List<Card>.saveTo(file: String) {
            val output = this.joinToString("\n")
            File(file).writeText(output)
        }

        val factions = setOf(Motive.Peace, Motive.Greed, Motive.Justice, Motive.Vice, Motive.Neutral, Motive.Wisdom)
            .map { "$it.txt" }

        fun readFrom(filename: String): List<Card> {
            return File(filename).listFiles()!!
                .flatMap { file ->
                    if (factions.contains(file.name)) {
                        file.readLines().mapNotNull { parse(it) }
                    } else listOf()
                }
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
            println("Using regex: $regex")

            val folder =
                File("assets/images/cards/used").listFiles()!!
                    .filter { it.isDirectory }
                    .flatMap { it.listFiles()!!.asList() }
                    .map { it.nameWithoutExtension.normalize() }
                    .toMutableSet()
            folder.removeAll(cards.map { it.name.normalize() }.toSet())
            if (folder.isNotEmpty()) {
                //println("Unused card arts {\n  ${folder.joinToString("\n  ")}\n}")
            }

            val cardsByMotive = cards.groupBy { card -> card.motive }
                .mapValues { it.value.groupBy { card -> card.rarity } }

            cardsByMotive.entries
                .forEach { entry ->
                    fun count(rarity: Rarity): Int {
                        return entry.value[rarity]?.size ?: 0
                    }

                    val str = "%-10s %3d %3d %3d".format(
                        entry.key,
                        count(Rarity.Common),
                        count(Rarity.Rare),
                        count(Rarity.Legendary)
                    )
                    println(str)

                }

            val cardsByRarity = cards.groupBy { it.rarity }
            cardsByRarity.forEach { (rarity, cards) -> println("$rarity ${cards.size}") }
            println("Total cards: ${cards.size}")

            fun factionCount(motive: Motive): Int {
                val cards = cardsByMotive[motive]

                fun rarityCount(rarity: Rarity): Int {
                    val count = cards?.get(rarity)?.size ?: 0
                    return if (rarity == Rarity.Common) count * 2 else count
                }
                return Rarity.entries.sumOf { rarityCount(it) }
            }

            val count = Motive.entries.sumOf { factionCount(it) }
            println("Total printable cards: $count")

            val tagsCount = cards.flatMap { it.tags }.groupBy { it }.mapValues { it.value.size }
            tagsCount.forEach { (tag, count) -> println("Tag [$tag]: $count") }

            val keywordsCount =
                cards.flatMap { it.keywords }.groupBy { it }.mapValues { it.value.size }
            keywordsCount.forEach { (keyword, count) -> println("Keyword [${keyword.name}]: $count") }

            //print card issues here
            cards.forEach {
                if (it.image == null) {
                    println("Card ${it.name} has no art!")
                }
                if (it.quote.isBlank()) {
                    println("Card ${it.name} has no quote!")
                }
            }
        }

        val textColor = Color.valueOf("#603000ff")
        val helpTextColor = Color.valueOf("#806040ff") //was 405060

        val stroke = Textures.brushStroke.asSprite().setOffset(Compass.CENTER)
        val tray = Textures.tray.asSprite().setOffset(Compass.SOUTH)
        val armorBack = Textures.armorBack.asSprite().setOffset(Compass.CENTER)
        val armorBlack = Textures.armorBlack.asSprite().setOffset(Compass.CENTER)
        val costBack = Textures.costBack.asSprite().setOffset(200f, 150f)
        val provisionsBlack = Textures.provisionsBlack.asSprite().setOffset(200f, 150f)
        val line = Textures.line.asSprite().setOffset(Compass.CENTER)
        val border = Textures.border.asSprite()

        val midWidth = W / 2f
        val midHeight = tray.getFrames().height - 22f
        val cardRatio = H * 1f / W

        val imageW = 950f
        val imageH = 744f
        val imageRatio = imageH / imageW

        val diamondOffsetX = 85f
        val diamondOffsetY = 140f
        val diamondW = 100f
        val diamondH = 2 * diamondW
        val starSize = 60f

        val armorBackW = 80f
        val armorBackH = 100f
        val armorX = 80f
        val armorY = 80f

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

        val borderY = 20
        val lineY = 120f
        val quoteTextX = midWidth
        val quoteTextY = (lineY + borderY) / 2
        val quoteTextW = W - 320f

        val abilityBufferMargin = 20f
        val abilityBufferX = 40f
        val abilityBufferY = lineY + 30
        val abilityBufferW = W - 2 * abilityBufferX
        val abilityBufferH = midHeight - (strokeH + abilityBufferY)
    }

    fun drawSimple() {
        ScreenUtils.clear(Color.WHITE)

        //Draw border and ability text tray
        Draw.drawLine(50f, midHeight, W - 50f, midHeight, 1f, Color.BLACK)
        border.draw(batch, 0f, 0f)

        //Power diamond
        val diamond = Textures.diamondBlack.asSprite().setOffset(Compass.CENTER)
        diamond.draw(batch, diamondOffsetX, H - diamondOffsetY, diamondW, diamondH)
        if (power == 0) {
            type.imageBlack!!.draw(batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
        } else if (type == CardType.Unit) {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY,
                Fonts.numberFont,
                power.toString(),
                100f,
                Compass.CENTER,
                Color.BLACK
            )
        } else {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY + 24,
                Fonts.numberFontSmall,
                power.toString(),
                100f,
                Compass.CENTER,
                Color.BLACK
            )
        }

        //Armor
        if (armor > 0) {
            armorBlack.draw(batch, armorX, armorY, armorBackW, armorBackH)
            Draw.drawText(
                armorX, armorY + 2,
                Fonts.numberFontSmall,
                armor.toString(),
                100f,
                Compass.CENTER,
                Color.BLACK
            )
        }

        //Cost
        provisionsBlack.draw(batch, costX, costY, costBackSize, costBackSize)
        Draw.drawText(
            costX, costY + 5,
            Fonts.numberFontSmall,
            cost.toString(),
            100f,
            Compass.CENTER,
            Color.BLACK
        )

        //Card Name
        Draw.drawText(midWidth, nameY, Fonts.nameFont, name, W - 50f, Compass.CENTER, Color.BLACK)

        //Tags
        Draw.drawText(
            midWidth,
            tagsY,
            Fonts.tagFont,
            tagsText,
            1000f,
            Compass.CENTER,
            Color.BLACK
        )

        //Ability Text
        val text = styledAbilityText
        val abilityTextY = abilityBufferY + abilityBufferH - abilityBufferMargin
        Draw.drawText(
            abilityBufferX,
            abilityTextY,
            Fonts.abilityFontColorless,
            text,
            abilityBufferW,
            Compass.SOUTHEAST,
            Color.BLACK
        )

        //Keyword text
        val keywordTextY = abilityBufferY + abilityBufferMargin
        Draw.drawText(
            abilityBufferX,
            keywordTextY,
            Fonts.tagFont,
            keywordText,
            abilityBufferW,
            Compass.NORTHEAST,
            Color.BLACK
        )

        Draw.drawLine(midWidth - 150, lineY, midWidth + 150, lineY, 1f, Color.BLACK)
        //line.draw(batch, midWidth, lineY, 3f, 1f, 0f)

        //Quote text
        Draw.drawText(
            quoteTextX,
            quoteTextY,
            Fonts.quoteFont,
            quote,
            quoteTextW,
            Compass.CENTER,
            Color.BLACK
        )
    }

    fun draw() {
        ScreenUtils.clear(Color.BLACK)

        //Draw card art
        val image = this.image
        if (image != null) {
            val sourceImageRatio = image.getFrames().ratio
            val destImageRatio = imageRatio
            val scale =
                if (sourceImageRatio > destImageRatio) {
                    imageW / image.getFrames().width
                } else {
                    imageH / image.getFrames().height
                }

            image.draw(batch, midWidth, H - 10f, scale, scale, 0f)
        }

        //Draw border and ability text tray
        tray.draw(batch, midWidth, 0f)
        border.draw(batch, 0f, 0f)
        rarity.image.draw(batch, 0f, 0f, W.toFloat(), H.toFloat())

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
                y + 30,
                Fonts.numberFontXS,
                power.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
            type.image!!.draw(batch, diamondOffsetX + 3, y - 20, starSize, starSize)
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
        Draw.drawText(midWidth, nameY, Fonts.nameFont, name, W - 50f, Compass.CENTER, motive.color)

        //Tags
        Draw.drawText(
            midWidth,
            tagsY,
            Fonts.tagFont,
            tagsText,
            1000f,
            Compass.CENTER,
            motive.color
        )

        //Ability Text
        val text = styledAbilityText
        val abilityTextY = abilityBufferY + abilityBufferH - abilityBufferMargin
        Draw.drawText(
            abilityBufferX,
            abilityTextY,
            Fonts.abilityFont,
            text,
            abilityBufferW,
            Compass.SOUTHEAST,
            Color.WHITE
        )

        //Keyword text
        val keywordTextY = abilityBufferY + abilityBufferMargin
        Draw.drawText(
            abilityBufferX,
            keywordTextY,
            Fonts.tagFont,
            keywordText,
            abilityBufferW,
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
            println("Could not load $png or $jpg")
        } else {
            image = texture.asSprite().setOffset(Compass.NORTH)
        }
        return image
    }
}
