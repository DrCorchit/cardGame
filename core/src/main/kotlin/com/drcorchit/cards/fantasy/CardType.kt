package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class CardType(file: String?) {
    Unit(null),
    Tactic("tactic.png"),
    Equipment("equipment.png"),
    Emplacement("emplacement.png");

    val image by lazy { file?.let { Textures.initTexture(it).asSprite().setOffset(Compass.CENTER) } }
    val imageBlack by lazy { image?.copy()?.let { it.blend = Color.BLACK; it } }
}
