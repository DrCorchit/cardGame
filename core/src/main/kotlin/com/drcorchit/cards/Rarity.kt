package com.drcorchit.cards

import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite

enum class Rarity {
    Common, Rare, Legendary;

    val texture = Textures.initTexture("${name.lowercase()}.png")
    val image = texture.asSprite()
}
