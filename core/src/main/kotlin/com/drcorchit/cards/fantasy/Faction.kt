package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.AnimatedSprite

interface Faction {
    val color: Color
    val secondaryColor: Color
    val image: AnimatedSprite
}
