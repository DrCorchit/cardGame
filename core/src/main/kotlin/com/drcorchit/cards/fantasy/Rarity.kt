package com.drcorchit.cards.fantasy

import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.google.gson.JsonElement

enum class Rarity {
    Common, Rare, Legendary;

    val texture by lazy { Textures.initTexture("${name.lowercase()}.png") }
    val image by lazy { texture.asSprite() }

    companion object {
        val shinyBorder by lazy { Textures.initTexture("leader.png").asSprite() }

        fun findRarity(tags: List<String>): Rarity {
            return tags.firstNotNullOfOrNull {
                try {
                    Rarity.valueOf(it)
                } catch (_: Exception) {
                    null
                }
            } ?: Common
        }

        fun deserialize(json: JsonElement): Rarity {
            return try {
                Rarity.valueOf(json.asString)
            } catch (e: Exception) {
                e.printStackTrace()
                Common
            }
        }
    }
}
