package com.drcorchit.cards

import com.drcorchit.cards.Textures.asSprite

enum class Rarity {
    Common, Rare, Legendary;

    val texture = Textures.initTexture("${name.lowercase()}_4.png")
    val image = texture.asSprite()
}
