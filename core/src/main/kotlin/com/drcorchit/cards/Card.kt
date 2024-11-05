package com.drcorchit.cards

import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils
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
    }

}
