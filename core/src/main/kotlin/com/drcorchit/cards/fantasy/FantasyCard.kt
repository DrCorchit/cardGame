package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Keyword
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.IMAGE_H
import com.drcorchit.cards.Main.Companion.IMAGE_W
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.*
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.drcorchit.justice.utils.math.Compass
import com.drcorchit.justice.utils.math.MathUtils
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File
import kotlin.math.max

class FantasyCard(
    override val name: String,
    val power: Int,
    val cost: Int,
    val armor: Int,
    val tags: List<String>,
    val abilities: List<String>,
    val quote: String,
    val strategyTags: List<String>
) : Drawable {
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

    val city = tags.firstNotNullOfOrNull {
        try {
            City.valueOf(it)
        } catch (e: Exception) {
            //println("No city for card $name")
            null
        }
    } ?: City.Unaffiliated

    val faction: Faction = city

    val rarity = tags.firstNotNullOfOrNull {
        try {
            Rarity.valueOf(it)
        } catch (e: Exception) {
            null
        }
    } ?: Rarity.Common

    val tagsText = run {
        val miscTags = tags.toSet()
            .subtract(Rarity.entries.map { it.name }.toSet())
            .subtract(setOf(city.name))
            .subtract(setOf("Unit", "Instant", "Equipment", "Emplacement"))
            .subtract(Race.entries.map { it.name }.toSet())

        val race = Race.detectRacialTag(tags, type)

        val prefix = if (city == City.Unaffiliated) "$rarity $race" else "$rarity ${city.adj} $race"
        val suffix = miscTags.joinToString(", ")
        if (miscTags.isEmpty()) prefix else "$prefix — $suffix"
    }

    val abilityText = abilities
        .joinToString("\n") { it.trim() }
        .replace("#", "\n > ")

    val abilityTextHandler = AbilityTextHandler(abilityText, Keyword.keywordsDictionary)

    val keywords = abilities
        .flatMap { it.split(Regex("[ #]")) }
        .map { it.normalize() }
        .mapNotNull { Keyword.keywordsDictionary[it] }
        .filter { it.description != null }
        .distinct()
    //.sortedBy { it.id }

    val keywordText = keywords.joinToString("\n") { "${it.name}: ${it.description}" }

    var image: AnimatedSprite? = updateGraphic()

    //We sort by rarity to so we can auto add cards as much as possible when using the MakePlayingCards.com website
    override val outputLocation =
        "output/images/full/cards/${rarity.name.normalize()}/${name.normalize()}.png"

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
        fun List<FantasyCard>.saveTo(file: String) {
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
        val keywordColor = Color.valueOf("#502800ff")
        val keywordHelpColor = Color.valueOf("#806040ff") //was 405060

        val stroke = Textures.brushStroke.asSprite().setOffset(Compass.CENTER)
        val tray = Textures.tray.asSprite().setOffset(Compass.SOUTH)
        val armorBack = Textures.armorBack.asSprite().setOffset(Compass.CENTER)
        val costBack = Textures.costBack.asSprite().setOffset(200f, 150f)
        val line = Textures.line.asSprite().setOffset(Compass.CENTER)
        val border = Textures.fantasyBorder.asSprite()

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

        //8x10 aspect ratio preferred
        val armorBackW = 72f
        val armorBackH = 90f
        val armorX = BORDER + costBackOffset
        val armorY = BORDER + costBackOffset

        val strokeY = trayHeight + BORDER - 80
        val strokeH = 120f
        val strokeMargin = 200f
        val strokeMinW = 200f
        val strokeMaxW = W - 50f

        val abilityTextX = BORDER + 30f
        val abilityTextY = strokeY - 60
        val abilityTextW = W - 60f

        val lineY = 118f + BORDER
        val quoteTextX = midWidth
        val quoteTextY = (lineY + 20 + BORDER) / 2
        val quoteTextW = W - 350f

        val keywordTextX = abilityTextX
        val keywordTextY = lineY + 15
        val keywordHelpW = abilityTextW - 30
        val totalAbilityTextH = abilityTextY - keywordTextY
    }

    override fun draw() {
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
        val diamond = faction.image
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
                Fonts.numberFont,
                armor.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
        }

        //Cost
        if (cost > 0) {
            costBack.draw(batch, costX, costY, costBackSize, costBackSize)
            Draw.drawText(
                costX, costY + 2,
                Fonts.numberFont,
                cost.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
        }

        //Card Name
        val dims1 = Draw.calculateDimensions(Fonts.nameFont, name, W - 50f)
        val dims2 = Draw.calculateDimensions(Fonts.tagFont, tagsText, W - 50f)
        stroke.blend = faction.secondaryColor


        val strokeW = MathUtils.clamp(
            strokeMinW,
            max(
                dims1.first + strokeMargin,
                dims2.first + strokeMargin / 2
            ), strokeMaxW
        )

        stroke.draw(batch, midWidth, strokeY, strokeW, strokeH)
        Draw.drawText(
            midWidth,
            strokeY + 27,
            Fonts.nameFont,
            name,
            W - 50f,
            Compass.CENTER,
            faction.color
        )

        //Tags
        Draw.drawText(
            midWidth,
            strokeY - 27,
            Fonts.tagFont,
            tagsText,
            1000f,
            Compass.CENTER,
            faction.color
        )

        //Ability Text
//        Draw.drawText(
//            abilityTextX,
//            abilityTextY,
//            Fonts.abilityFont,
//            styledAbilityText,
//            abilityTextW,
//            Compass.SOUTHEAST,
//            Color.WHITE
//        )

        //Draw.drawRectangle(abilityTextX, abilityTextY, abilityTextW, 10f, Color.RED)
        abilityTextHandler.render(abilityTextX, abilityTextY, abilityTextW)

        //Keyword text
        Draw.drawText(
            keywordTextX,
            keywordTextY,
            Fonts.keywordHelpFont,
            keywordText,
            keywordHelpW,
            Compass.NORTHEAST,
            keywordHelpColor
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

    override fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()
        val base = "assets/images/fantasy_cards/${city.name}"
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
