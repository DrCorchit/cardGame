package com.drcorchit.cards

import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class CardType(file: String?) {
    Unit(null),
    Instant("instant.png"),
    Equipment("equipment.png"),
    Emplacement("emplacement.png");

    val image = file?.let { Textures.initTexture(it).asSprite().setOffset(Compass.CENTER) }
}
